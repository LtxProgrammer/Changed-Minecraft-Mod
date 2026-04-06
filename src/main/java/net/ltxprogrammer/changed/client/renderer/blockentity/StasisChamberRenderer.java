package net.ltxprogrammer.changed.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.*;
import net.ltxprogrammer.changed.block.entity.StasisChamberBlockEntity;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.Util;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.ForgeHooksClient;
import org.joml.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
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

    private static final List<ModelPart.Vertex> TOP_SURFACE_VERTICES = Util.make(new ArrayList<>(), list -> {
        float v0 = 0.0f;
        float v1 = 16.0f;
        float o0 = -8.975f;
        float o1 = 24.975f;

        makeQuad(list::add, v0, v1, v0, v1, v0, v1, v0, v1); // Center

        makeQuad(list::add, o0, v0, v0, v1, 7.0f, v1, v0, v1); // l
        makeQuad(list::add, v1, o1, v0, v1, v0, 9.0f, v0, v1); // r
        makeQuad(list::add, v0, v1, o0, v0, v0, v1, 7.0f, v1); // b
        makeQuad(list::add, v0, v1, v1, o1, v0, v1, v0, 9.0f); // f

        makeQuad(list::add, o0, v0, o0, v0, 7.0f, v1, 7.0f, v1); // bl
        makeQuad(list::add, o0, v0, v1, o1, 7.0f, v1, v0, 9.0f); // fl
        makeQuad(list::add, v1, o1, o0, v0, v0, 9.0f, 7.0f, v1); // br
        makeQuad(list::add, v1, o1, v1, o1, v0, 9.0f, v0, 9.0f); // fr
    });
    private void renderTopSurface(PoseStack.Pose pose, VertexConsumer buffer, int packedLight, int packedOverlay, float r, float g, float b, float a, TextureAtlasSprite sprite) {
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        var up = new Vector3f(0f, 1f, 0f);
        up.mul(matrix3f);

        for (var vertex : TOP_SURFACE_VERTICES) {
            float x = vertex.pos.x() / 16.0F;
            float y = vertex.pos.y() / 16.0F;
            float z = vertex.pos.z() / 16.0F;
            Vector4f vector4f = new Vector4f(x, y, z, 1.0F);
            vector4f.mul(matrix4f);
            buffer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), r, g, b, a,
                    sprite.getU(vertex.u),
                    sprite.getV(vertex.v),
                    packedOverlay, packedLight, up.x(), up.y(), up.z());
        }
    }

    private static final float FLOWING_FLUID_TEX_SCALE = 0.5F;
    private void renderFrontSurface(PoseStack.Pose pose, VertexConsumer buffer, int packedLight, int packedOverlay, float r, float g, float b, float a, TextureAtlasSprite sprite, float blockSize) {
        float v0 = 0.0f;
        float v1 = 16.0f;
        float o0 = -8.975f;
        float o1 = 24.975f;

        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        var front = new Vector3f(0f, 0f, 1f);
        front.mul(matrix3f);
        var left = new Vector3f(1f, 0f, 0f);
        left.mul(matrix3f);
        var right = new Vector3f(-1f, 0f, 0f);
        right.mul(matrix3f);

        BiConsumer<ModelPart.Vertex, Vector3f> intermediate = (vertex, normal) -> {
            float x = vertex.pos.x() / 16.0F;
            float y = vertex.pos.y() / 16.0F;
            float z = vertex.pos.z() / 16.0F;
            Vector4f vector4f = new Vector4f(x, y, z, 1.0F);
            vector4f.mul(matrix4f);

            buffer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), r, g, b, a,
                    sprite.getU(vertex.u),
                    sprite.getV(vertex.v),
                    packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
        };

        float thisPos = 0f;
        while (blockSize > 0f) {
            float thisSize = Math.min(blockSize, 1f) * 16f;

            makeQuad(vertex -> intermediate.accept(vertex, front), 
                    o0, v0, thisPos, thisPos - thisSize, o0, o0, 7.0F * FLOWING_FLUID_TEX_SCALE, v1 * FLOWING_FLUID_TEX_SCALE, 0f, thisSize * FLOWING_FLUID_TEX_SCALE);
            makeQuad(vertex -> intermediate.accept(vertex, front), 
                    v0, v1, thisPos, thisPos - thisSize, o0, o0, v0 * FLOWING_FLUID_TEX_SCALE, v1 * FLOWING_FLUID_TEX_SCALE, 0f, thisSize * FLOWING_FLUID_TEX_SCALE);
            makeQuad(vertex -> intermediate.accept(vertex, front), 
                    v1, o1, thisPos, thisPos - thisSize, o0, o0, v0 * FLOWING_FLUID_TEX_SCALE, (9.0f) * FLOWING_FLUID_TEX_SCALE, 0f, thisSize * FLOWING_FLUID_TEX_SCALE);
            
            makeQuad(vertex -> intermediate.accept(vertex, left),
                    o1, o1, thisPos, thisPos - thisSize, v0, o0, v0 * FLOWING_FLUID_TEX_SCALE, (9.0f) * FLOWING_FLUID_TEX_SCALE, 0f, thisSize * FLOWING_FLUID_TEX_SCALE);
            makeQuad(vertex -> intermediate.accept(vertex, right), 
                    o0, o0, thisPos, thisPos - thisSize, o0, v0, 7.0F * FLOWING_FLUID_TEX_SCALE, v1 * FLOWING_FLUID_TEX_SCALE, 0f, thisSize * FLOWING_FLUID_TEX_SCALE);

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
                case EAST -> Axis.YP.rotationDegrees(-90.0F);
                case WEST -> Axis.YP.rotationDegrees(90.0F);
                case SOUTH -> Axis.YP.rotationDegrees(180.0F);
                default -> new Quaternionf();
            });
            pose.translate(-0.5f, 0f, -0.5f);

            var fluidState = fluid.defaultFluidState();
            var sprites = ForgeHooksClient.getFluidSprites(blockEntity.getLevel(), blockEntity.getBlockPos(), fluidState);

            var color = net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions.of(fluid).getTintColor(fluidState, blockEntity.getLevel(), blockEntity.getBlockPos());
            var rgb = Color3.fromInt(color);
            float alpha = ((float)((color >> 24) & 0xFF)) / 255f;
            if (fluid.isSame(Fluids.WATER))
                alpha *= 0.5f;

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

            final float renderAlpha = alpha;

            RenderType rendertype = ItemBlockRenderTypes.getRenderLayer(fluidState);
            final PoseStack.Pose renderPose = pose.last();
            ChangedClient.recordTranslucentRender(buffers, rendertype, buffer -> {
                if (fillPercent < 1f)
                    renderTopSurface(renderPose, buffer, packedLight, packedOverlay, rgb.red(), rgb.green(), rgb.blue(), renderAlpha, sprites[0]);
                renderFrontSurface(renderPose, buffer, packedLight, packedOverlay, rgb.red(), rgb.green(), rgb.blue(), renderAlpha, sprites[1], fillYLevel);
            });

            pose.popPose();
        });
    }
}
