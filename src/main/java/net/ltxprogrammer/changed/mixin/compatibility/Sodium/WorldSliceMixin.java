package net.ltxprogrammer.changed.mixin.compatibility.Sodium;

import me.jellysquid.mods.sodium.client.world.ReadableContainerExtended;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import me.jellysquid.mods.sodium.client.world.cloned.ChunkRenderContext;
import me.jellysquid.mods.sodium.client.world.cloned.ClonedChunkSection;
import net.ltxprogrammer.changed.block.LatexCoveringSource;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.extension.sodium.ClonedChunkSectionExtension;
import net.ltxprogrammer.changed.extension.sodium.WorldSliceExtension;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Objects;

@Mixin(value = WorldSlice.class, remap = false)
@RequiredMods("rubidium")
public abstract class WorldSliceMixin implements WorldSliceExtension {
    private static final LatexCoverState EMPTY_LATEX_COVER_STATE = ChangedLatexTypes.NONE.get().defaultCoverState();

    @Shadow @Final private static int SECTION_ARRAY_SIZE;

    @Shadow private int originX;
    @Shadow private int originY;
    @Shadow private int originZ;

    @Shadow public abstract BlockState getBlockState(int x, int y, int z);

    @Unique private final LatexCoverState[][] latexCoverStatesArrays = new LatexCoverState[SECTION_ARRAY_SIZE][4096];

    @Unique
    private void unpackLatexCoverData(LatexCoverState[] states, ChunkRenderContext context, ClonedChunkSection section) {
        if (((ClonedChunkSectionExtension)section).getLatexCoverData() == null) {
            Arrays.fill(states, EMPTY_LATEX_COVER_STATE);
        } else {
            ReadableContainerExtended<LatexCoverState> container = ReadableContainerExtended.of(((ClonedChunkSectionExtension)section).getLatexCoverData());
            SectionPos origin = context.getOrigin();
            SectionPos pos = section.getPosition();
            if (origin.equals(pos)) {
                container.sodium$unpack(states);
            } else {
                BoundingBox bounds = context.getVolume();
                int minBlockX = Math.max(bounds.minX(), pos.minBlockX());
                int maxBlockX = Math.min(bounds.maxX(), pos.maxBlockX());
                int minBlockY = Math.max(bounds.minY(), pos.minBlockY());
                int maxBlockY = Math.min(bounds.maxY(), pos.maxBlockY());
                int minBlockZ = Math.max(bounds.minZ(), pos.minBlockZ());
                int maxBlockZ = Math.min(bounds.maxZ(), pos.maxBlockZ());
                container.sodium$unpack(states, minBlockX & 15, minBlockY & 15, minBlockZ & 15, maxBlockX & 15, maxBlockY & 15, maxBlockZ & 15);
            }

        }
    }

    @Override
    public LatexCoverState getLatexCoverState(int x, int y, int z) {
        final BlockState blockState = getBlockState(x, y, z);
        if (blockState.getBlock() instanceof LatexCoveringSource source)
            return source.getLatexCoverState(blockState);

        int relX = x - this.originX;
        int relY = y - this.originY;
        int relZ = z - this.originZ;
        int sectionIndex = WorldSlice.getLocalSectionIndex(relX >> 4, relY >> 4, relZ >> 4);
        int blockIndex = WorldSlice.getLocalBlockIndex(relX & 15, relY & 15, relZ & 15);
        if (sectionIndex < 0 || sectionIndex >= SECTION_ARRAY_SIZE)
            return ChangedLatexTypes.NONE.get().defaultCoverState();
        if (blockIndex < 0 || blockIndex >= 4096)
            return ChangedLatexTypes.NONE.get().defaultCoverState();
        return Objects.requireNonNullElseGet(this.latexCoverStatesArrays[sectionIndex][blockIndex],
                () -> ChangedLatexTypes.NONE.get().defaultCoverState());
    }

    @Inject(method = "copySectionData", at = @At("TAIL"))
    public void copyChangedSectionData(ChunkRenderContext context, int sectionIndex, CallbackInfo ci) {
        ClonedChunkSection section = context.getSections()[sectionIndex];

        try {
            this.unpackLatexCoverData(this.latexCoverStatesArrays[sectionIndex], context, section);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Exception copying block data for section: " + section.getPosition(), e);
        }
    }
}
