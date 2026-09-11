package net.ltxprogrammer.changed.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.client.LatexCoveredBlocksRenderer;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Set;

@Mixin(ChunkRenderDispatcher.RenderChunk.RebuildTask.class)
public abstract class ChunkRenderDispatcherMixin {
    @Shadow @Final ChunkRenderDispatcher.RenderChunk this$1;

    @Unique
    public LatexCoverState getLatexCoverState(RenderChunkRegion region, BlockPos blockPos) {
        int i = SectionPos.blockToSectionCoord(blockPos.getX()) - region.centerX;
        int j = SectionPos.blockToSectionCoord(blockPos.getZ()) - region.centerZ;
        if (i < 0 || i >= region.chunks.length)
            return ChangedLatexTypes.NONE.get().defaultCoverState();
        if (j < 0 || j >= region.chunks[i].length)
            return ChangedLatexTypes.NONE.get().defaultCoverState();
        return LatexCoverState.getAt(region.chunks[i][j].wrapped, blockPos);
    }

    @Unique
    public void beginLayer(BufferBuilder builder) {
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
    }

    @Inject(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getBlockRenderer()Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;"))
    public void addCompileSteps(float cameraX, float cameraY, float cameraZ, ChunkBufferBuilderPack bufferBuilderPack, CallbackInfoReturnable<?> cir,
                                @Local RenderChunkRegion region,
                                @Local PoseStack poseStack,
                                @Local Set<RenderType> renderTypes,
                                @Local RandomSource random) {
        BlockPos start = this$1.getOrigin().immutable();
        BlockPos end = start.offset(15, 15, 15);

        for (BlockPos blockPos : BlockPos.betweenClosed(start, end)) {
            BlockState blockState = region.getBlockState(blockPos);
            LatexCoverState latexCoverState = getLatexCoverState(region, blockPos);

            if (!latexCoverState.isPresent())
                continue;

            RenderType rendertype = ChangedClient.latexCoveredBlocksRenderer.get().getRenderType(latexCoverState);
            BufferBuilder bufferbuilder = bufferBuilderPack.builder(rendertype);
            if (renderTypes.add(rendertype)) {
                beginLayer(bufferbuilder);
            }

            int blockX0 = blockPos.getX() & 15;
            int blockY0 = blockPos.getY() & 15;
            int blockZ0 = blockPos.getZ() & 15;
            poseStack.pushPose();
            poseStack.translate(blockX0, blockY0, blockZ0);

            ChangedClient.latexCoveredBlocksRenderer.get().tesselate(
                    region,
                    LatexCoverGetter.extend(region, fetchPos -> this.getLatexCoverState(region, fetchPos)),
                    blockPos,
                    poseStack,
                    bufferbuilder,
                    blockState,
                    latexCoverState,
                    random);

            poseStack.popPose();
        }
    }
}
