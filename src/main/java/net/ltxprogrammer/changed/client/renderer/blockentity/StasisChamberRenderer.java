package net.ltxprogrammer.changed.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.*;
import net.ltxprogrammer.changed.block.entity.StasisChamberBlockEntity;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.client.ForgeHooksClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class StasisChamberRenderer<T extends StasisChamberBlockEntity> implements BlockEntityRenderer<T> {
    public StasisChamberRenderer(BlockEntityRendererProvider.Context context) {
        //this.goo = context.bakeLayer(LAYER_LOCATION);
    }

    private static void makeQuad(Consumer<ModelPart.Vertex> out, float x0, float x1, float y0, float y1, float u0, float u1, float v0, float v1) {
        out.accept(new ModelPart.Vertex(x0, 0f, y0, u0, v0));
        out.accept(new ModelPart.Vertex(x1, 0f, y0, u1, v0));
        out.accept(new ModelPart.Vertex(x1, 0f, y1, u1, v1));
        out.accept(new ModelPart.Vertex(x0, 0f, y1, u0, v1));

        out.accept(new ModelPart.Vertex(x0, 0f, y0, u0, v0));
        out.accept(new ModelPart.Vertex(x0, 0f, y1, u0, v1));
        out.accept(new ModelPart.Vertex(x1, 0f, y1, u1, v1));
        out.accept(new ModelPart.Vertex(x1, 0f, y0, u1, v0));
    }

    private static void makeQuad(Consumer<ModelPart.Vertex> out, float x0, float x1, float y0, float y1, float z0, float z1, float u0, float u1, float v0, float v1) {
        out.accept(new ModelPart.Vertex(x0, y0, z0, u0, v0));
        out.accept(new ModelPart.Vertex(x1, y0, z1, u1, v0));
        out.accept(new ModelPart.Vertex(x1, y1, z1, u1, v1));
        out.accept(new ModelPart.Vertex(x0, y1, z0, u0, v1));

        out.accept(new ModelPart.Vertex(x0, y0, z0, u0, v0));
        out.accept(new ModelPart.Vertex(x0, y1, z0, u0, v1));
        out.accept(new ModelPart.Vertex(x1, y1, z1, u1, v1));
        out.accept(new ModelPart.Vertex(x1, y0, z1, u1, v0));
    }

    private static void makeTri(Consumer<ModelPart.Vertex> out, float x0, float x1, float y0, float y1, float u0, float u1, float v0, float v1) {
        out.accept(new ModelPart.Vertex(x0, 0f, y1, u0, v1));
        out.accept(new ModelPart.Vertex(x0, 0f, y0, u0, v0));
        out.accept(new ModelPart.Vertex(x1, 0f, y0, u1, v0));
        out.accept(new ModelPart.Vertex(x1, 0f, y0, u1, v0));

        out.accept(new ModelPart.Vertex(x1, 0f, y0, u1, v0));
        out.accept(new ModelPart.Vertex(x0, 0f, y0, u0, v0));
        out.accept(new ModelPart.Vertex(x0, 0f, y1, u0, v1));
        out.accept(new ModelPart.Vertex(x0, 0f, y1, u0, v1));
    }

    private static final List<ModelPart.Vertex> TOP_SURFACE_VERTICES = Util.make(new ArrayList<>(), list -> {
        float v0 = 0.925f;
        float v1 = 15.075f;
        float o0 = -9.3f;
        float o1 = 25.3f;

        makeQuad(list::add, v0, v1, v0, v1, v0, v1, v0, v1);

        makeQuad(list::add, o0, v0, v0, v1, 6.7f, v1, v0, v1);
        makeQuad(list::add, v1, o1, v0, v1, v0, 9.3f, v0, v1);
        makeQuad(list::add, v0, v1, o0, v0, v0, v1, 6.7f, v1);
        makeQuad(list::add, v0, v1, v1, o1, v0, v1, v0, 9.3f);

        makeTri(list::add, v0, o0, v0, o0, 16f, 6.7f, 16f, 6.7f);
        makeTri(list::add, v1, o1, v0, o0, 16f, 6.7f, 16f, 6.7f);
        makeTri(list::add, v0, o0, v1, o1, 16f, 6.7f, 16f, 6.7f);
        makeTri(list::add, v1, o1, v1, o1, 16f, 6.7f, 16f, 6.7f);
    });
    private void renderTopSurface(PoseStack.Pose pose, VertexConsumer buffer, int packedLight, int packedOverlay, float r, float g, float b, float a, TextureAtlasSprite sprite) {
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        var up = Vector3f.YP.copy();
        up.transform(matrix3f);

        for (var vertex : TOP_SURFACE_VERTICES) {
            float x = vertex.pos.x() / 16.0F;
            float y = vertex.pos.y() / 16.0F;
            float z = vertex.pos.z() / 16.0F;
            Vector4f vector4f = new Vector4f(x, y, z, 1.0F);
            vector4f.transform(matrix4f);
            buffer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), r, g, b, a,
                    sprite.getU(vertex.u),
                    sprite.getV(vertex.v),
                    packedOverlay, packedLight, up.x(), up.y(), up.z());
        }
    }

    private void renderFrontSurface(PoseStack.Pose pose, VertexConsumer buffer, int packedLight, int packedOverlay, float r, float g, float b, float a, TextureAtlasSprite sprite, float blockSize) {
        float v0 = 0.925f;
        float v1 = 15.075f;
        float o0 = -9.3f;
        float o1 = 25.3f;

        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        var front = Vector3f.ZP.copy();
        front.transform(matrix3f);
        var left = Vector3f.XP.copy();
        left.add(Vector3f.ZP);
        left.normalize();
        left.transform(matrix3f);
        var right = Vector3f.XN.copy();
        right.add(Vector3f.ZP);
        right.normalize();
        right.transform(matrix3f);

        float thisPos = 0f;
        while (blockSize > 0f) {
            float thisSize = Math.min(blockSize, 1f) * 16f;

            makeQuad(vertex -> {
                float x = vertex.pos.x() / 16.0F;
                float y = vertex.pos.y() / 16.0F;
                float z = vertex.pos.z() / 16.0F;
                Vector4f vector4f = new Vector4f(x, y, z, 1.0F);
                vector4f.transform(matrix4f);

                buffer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), r, g, b, a,
                        sprite.getU(vertex.u),
                        sprite.getV(vertex.v),
                        packedOverlay, packedLight, front.x(), front.y(), front.z());
            }, v0, v1, thisPos, thisPos - thisSize, o0, o0, v0, v1, 0f, thisSize);

            makeQuad(vertex -> {
                float x = vertex.pos.x() / 16.0F;
                float y = vertex.pos.y() / 16.0F;
                float z = vertex.pos.z() / 16.0F;
                Vector4f vector4f = new Vector4f(x, y, z, 1.0F);
                vector4f.transform(matrix4f);

                buffer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), r, g, b, a,
                        sprite.getU(vertex.u),
                        sprite.getV(vertex.v),
                        packedOverlay, packedLight, left.x(), left.y(), left.z());
            }, o1, v1, thisPos, thisPos - thisSize, v0, o0, v0, v1, 0f, thisSize);

            makeQuad(vertex -> {
                float x = vertex.pos.x() / 16.0F;
                float y = vertex.pos.y() / 16.0F;
                float z = vertex.pos.z() / 16.0F;
                Vector4f vector4f = new Vector4f(x, y, z, 1.0F);
                vector4f.transform(matrix4f);

                buffer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), r, g, b, a,
                        sprite.getU(vertex.u),
                        sprite.getV(vertex.v),
                        packedOverlay, packedLight, right.x(), right.y(), right.z());
            }, v0, o0, thisPos, thisPos - thisSize, o0, v0, v0, v1, 0f, thisSize);

            blockSize -= 1f;
            thisPos -= thisSize;
        }
    }

    @Override
    public void render(T blockEntity, float partialTicks, PoseStack pose, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        blockEntity.getFluidType().ifPresent(fluid -> {
            float fillPercent = blockEntity.getFluidLevel(partialTicks);
            if (fillPercent <= 0f)
                return;

            pose.pushPose();
            pose.translate(0, -1f, 0f);
            pose.translate(0.5f, 0f, 0.5f);
            pose.mulPose(switch (blockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING)) {
                case EAST -> Vector3f.YP.rotationDegrees(-90.0F);
                case WEST -> Vector3f.YP.rotationDegrees(90.0F);
                case SOUTH -> Vector3f.YP.rotationDegrees(180.0F);
                default -> Quaternion.ONE;
            });
            pose.translate(-0.5f, 0f, -0.5f);

            var fluidState = fluid.defaultFluidState();
            var sprites = ForgeHooksClient.getFluidSprites(blockEntity.getLevel(), blockEntity.getBlockPos(), fluidState);

            var color = fluid.getAttributes().getColor(blockEntity.getLevel(), blockEntity.getBlockPos());
            var rgb = Color3.fromInt(color);
            var alpha = ((float)(color >> 24)) / 255f;

            float fillYLevel = fillPercent * 2.75f; // Fill percent -> fill in blocks
            pose.translate(0, fillYLevel + 0.125f, 0);

            if (fillYLevel + 0.125f >= 1f) {
                fluid.defaultFluidState().animateTick(blockEntity.getLevel(), blockEntity.getBlockPos().below(), blockEntity.getLevel().getRandom());
            }

            if (fillYLevel + 0.125f >= 2f) {
                fluid.defaultFluidState().animateTick(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getLevel().getRandom());
            }

            if (fillYLevel + 0.25f >= 3f) {
                fluid.defaultFluidState().animateTick(blockEntity.getLevel(), blockEntity.getBlockPos().above(), blockEntity.getLevel().getRandom());
            }

            for (RenderType rendertype : RenderType.chunkBufferLayers()) {
                if (!fluidState.isEmpty() && ItemBlockRenderTypes.canRenderInLayer(fluidState, rendertype)) {
                    final PoseStack.Pose renderPose = pose.last();
                    ChangedClient.recordTranslucentRender(buffers, rendertype, buffer -> {
                        if (fillPercent < 1f)
                            renderTopSurface(renderPose, buffer, packedLight, packedOverlay, rgb.red(), rgb.green(), rgb.blue(), alpha, sprites[0]);
                        renderFrontSurface(renderPose, buffer, packedLight, packedOverlay, rgb.red(), rgb.green(), rgb.blue(), alpha, sprites[1], fillYLevel);
                    });
                }
            }

            pose.popPose();
        });
    }
}
