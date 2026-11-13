package net.ltxprogrammer.changed.mixin.compatibility.Sodium;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.material.DefaultMaterials;
import me.jellysquid.mods.sodium.client.render.chunk.terrain.material.Material;
import me.jellysquid.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import me.jellysquid.mods.sodium.client.util.task.CancellationToken;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.ltxprogrammer.changed.block.LatexCoveringSource;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.extension.sodium.OptimizedVertexBuilder;
import net.ltxprogrammer.changed.extension.sodium.WorldSliceExtension;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = ChunkBuilderMeshingTask.class, remap = false)
@RequiredMods("rubidium")
public abstract class ChunkRenderRebuildTaskMixin {
    @Shadow @Final private RandomSource random;

    @Shadow @Final private RenderSection render;

    @Unique
    public LatexCoverState getLatexCoverState(WorldSlice slice, BlockPos blockPos) {
        if (!((Object)slice instanceof WorldSliceExtension ext))
            throw new IllegalStateException("WorldSlice not extended");
        return ext.getLatexCoverState(blockPos);
    }

    @Inject(method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
            at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Reference2ReferenceOpenHashMap;<init>()V"))
    public void addChangedSteps(ChunkBuildContext buildContext, CancellationToken cancellationToken, CallbackInfoReturnable<Object> cir,
                                @Local VisGraph occluder) {
        ChunkBuildBuffers buffers = buildContext.buffers;
        WorldSlice slice = buildContext.cache.getWorldSlice();
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos modelOffset = new BlockPos.MutableBlockPos();

        ChunkVertexEncoder.Vertex[] vertices = ChunkVertexEncoder.Vertex.uninitializedQuad();
        Map<RenderType, OptimizedVertexBuilder> builderCache = new HashMap<>();

        int minX = this.render.getOriginX();
        int minY = this.render.getOriginY();
        int minZ = this.render.getOriginZ();
        int maxX = minX + 16;
        int maxY = minY + 16;
        int maxZ = minZ + 16;

        PoseStack poseStack = new PoseStack();

        for(int y = minY; y < maxY; ++y) {
            if (cancellationToken.isCancelled()) {
                return;
            }

            for (int z = minZ; z < maxZ; ++z) {
                for (int x = minX; x < maxX; ++x) {
                    blockPos.set(x, y, z);

                    BlockState blockState = slice.getBlockState(blockPos);
                    if (blockState.getBlock() instanceof LatexCoveringSource source)
                        source.getLatexCoverState(blockState);
                    LatexCoverState latexCoverState = getLatexCoverState(slice, blockPos);

                    if (!latexCoverState.isPresent())
                        continue;

                    RenderType rendertype = ChangedClient.latexCoveredBlocksRenderer.get().getRenderType(latexCoverState);
                    Material material = DefaultMaterials.forRenderLayer(rendertype);

                    int blockX0 = blockPos.getX() & 15;
                    int blockY0 = blockPos.getY() & 15;
                    int blockZ0 = blockPos.getZ() & 15;
                    poseStack.pushPose();
                    poseStack.translate(blockX0, blockY0, blockZ0);

                    boolean rendered = ChangedClient.latexCoveredBlocksRenderer.get().tesselate(
                            slice,
                            LatexCoverGetter.extend(slice, fetchPos -> this.getLatexCoverState(slice, fetchPos)),
                            blockPos,
                            poseStack,
                            builderCache.computeIfAbsent(rendertype, type -> new OptimizedVertexBuilder(
                                    vertices,
                                    buffers.get(material),
                                    material)),
                            blockState,
                            latexCoverState,
                            this.random);

                    poseStack.popPose();

                    if (rendered) {
                        blockPos.set(x & 15, y & 15, z & 15);
                        occluder.setOpaque(blockPos);
                    }
                }
            }
        }
    }
}
