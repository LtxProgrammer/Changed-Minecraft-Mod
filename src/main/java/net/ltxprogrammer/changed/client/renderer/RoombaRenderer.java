package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.RoombaModel;
import net.ltxprogrammer.changed.entity.robot.Roomba;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class RoombaRenderer extends MobRenderer<Roomba, RoombaModel> {
    private final ResourceLocation TEXTURE = Changed.modResource("textures/roomba.png");

    public RoombaRenderer(EntityRendererProvider.Context context) {
        super(context, new RoombaModel(context.bakeLayer(RoombaModel.LAYER_LOCATION)), 0.4f);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Roomba roomba) {
        return TEXTURE;
    }

    @Override
    public void render(Roomba roomba, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        float f = (float)roomba.getHurtTime() - partialTicks;
        float f1 = roomba.getDamage() - partialTicks;
        if (f1 < 0.0F) {
            f1 = 0.0F;
        }

        if (f > 0.0F) {
            poseStack.mulPose(Vector3f.YP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * (float)roomba.getHurtDir()));
        }

        super.render(roomba, yRot, partialTicks, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
