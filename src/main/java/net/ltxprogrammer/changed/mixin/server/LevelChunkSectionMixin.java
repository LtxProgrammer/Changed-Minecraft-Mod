package net.ltxprogrammer.changed.mixin.server;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.ltxprogrammer.changed.block.LatexCoveringSource;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.world.LatexCoverCounter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.ltxprogrammer.changed.world.LevelChunkSectionExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunkSection.class)
public abstract class LevelChunkSectionMixin implements LevelChunkSectionExtension {
    @Shadow @Final private PalettedContainer<BlockState> states;
    @Shadow private short nonEmptyBlockCount;
    @Unique private short tickingLatexCoverCount;
    @Unique private PalettedContainer<LatexCoverState> coverStates
            = new PalettedContainer<>(ChangedLatexTypes.getLatexCoverStateIDMap(), ChangedLatexTypes.NONE.get().defaultCoverState(), PalettedContainer.Strategy.SECTION_STATES);

    @Override
    public LatexCoverState getLatexCoverState(int x, int y, int z) {
        BlockState blockState = this.states.get(x, y, z);
        if (blockState.getBlock() instanceof LatexCoveringSource source)
            return source.getLatexCoverState(blockState, new BlockPos(x, y, z));
        return coverStates.get(x, y, z);
    }

    @Override
    public LatexCoverState setLatexCoverState(int x, int y, int z, LatexCoverState state, boolean unchecked) {
        LatexCoverState oldState;
        if (unchecked) {
            oldState = this.coverStates.getAndSet(x, y, z, state);
        } else {
            oldState = this.coverStates.getAndSetUnchecked(x, y, z, state);
        }

        if (!oldState.isAir()) {
            --this.nonEmptyBlockCount;
            if (oldState.isRandomlyTicking()) {
                --this.tickingLatexCoverCount;
            }
        }

        if (!state.isAir()) {
            ++this.nonEmptyBlockCount;
            if (state.isRandomlyTicking()) {
                ++this.tickingLatexCoverCount;
            }
        }

        return oldState;
    }

    @Override
    public void acceptLatexStates(PalettedContainer<LatexCoverState> container) {
        coverStates = container;
    }

    @Override
    public PalettedContainer<LatexCoverState> getLatexStates() {
        return coverStates;
    }

    @Override
    public void recalcLatexCoverCounts() {
        LatexCoverCounter coverCounter = new LatexCoverCounter();
        this.coverStates.count(coverCounter);
        this.nonEmptyBlockCount += (short)coverCounter.nonEmptyBlockCount;
        this.tickingLatexCoverCount = (short)coverCounter.tickingLatexCoverCount;
    }

    @WrapMethod(method = "isRandomlyTicking")
    public boolean isLatexCoverRandomlyTicking(Operation<Boolean> original) {
        return this.tickingLatexCoverCount > 0 || original.call();
    }

    @WrapMethod(method = "write")
    public void writeChangedData(FriendlyByteBuf buffer, Operation<Void> original) {
        coverStates.write(buffer);
        original.call(buffer);
    }

    @WrapMethod(method = "read")
    public void readChangedData(FriendlyByteBuf buffer, Operation<Void> original) {
        coverStates.read(buffer);
        original.call(buffer);
    }

    @WrapMethod(method = "getSerializedSize")
    public int appendChangedData(Operation<Integer> original) {
        return original.call() + coverStates.getSerializedSize();
    }
}
