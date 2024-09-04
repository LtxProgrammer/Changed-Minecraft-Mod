package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.FirstPersonLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModelInterface;
import net.ltxprogrammer.changed.client.renderer.model.CorrectorType;
import net.ltxprogrammer.changed.client.tfanimations.TransfurAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class FormRenderHandler {
    public static void renderForm(Player player, PoseStack stack, MultiBufferSource buffer, int light, float partialTick) {
        ProcessTransfur.ifPlayerTransfurred(player, variant -> {
            ChangedCompatibility.freezeIsFirstPersonRendering();
            variant.sync(player);
            variant.getChangedEntity().setCustomNameVisible(true);

            if (variant.getTransfurProgression(partialTick) < 1f && !variant.isTemporaryFromSuit()) {
                TransfurAnimator.startCapture();

                renderLiving(player, stack, buffer, light, partialTick);
                renderLiving(variant.getChangedEntity(), stack, buffer, light, partialTick);

                TransfurAnimator.endCapture();

                ChangedCompatibility.forceIsFirstPersonRenderingToFrozen();

                TransfurAnimator.renderTransfurringPlayer(player, variant, stack, buffer, light, partialTick);
            } else {
                if (!RenderOverride.renderOverrides(player, variant, stack, buffer, light, partialTick))
                    renderLiving(variant.getChangedEntity(), stack, buffer, light, partialTick);
            }

            ChangedCompatibility.thawIsFirstPersonRendering();
        });
    }

    public static void renderLiving(LivingEntity living, PoseStack stack, MultiBufferSource buffer, int light, float partialTick) {
        if (living == null) return;
        EntityRenderer<? super LivingEntity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(living);
        renderer.render(living, living.getYRot(), partialTick, stack, buffer, light);
    }

    public static float lastPartialTick;

    public static void renderHand(LivingEntity living, HumanoidArm arm, PoseStack stack, MultiBufferSource buffer, int light, float partialTick) {
        renderHand(living, arm, stack, buffer, light, partialTick, true);
    }

    private static boolean renderingHand = false;
    public static boolean isRenderingHand() {
        return renderingHand;
    }

    public static void renderHand(LivingEntity living, HumanoidArm arm, PoseStack stack, MultiBufferSource buffer, int light, float partialTick, boolean layers) {
        EntityRenderer<? super LivingEntity> entRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(living);
        if (!(entRenderer instanceof LivingEntityRenderer<?,?> livingRenderer)) return;

        if (livingRenderer instanceof PlayerRenderer playerRenderer && living instanceof AbstractClientPlayer clientPlayer) {
            renderingHand = true;
            switch (arm) {
                case RIGHT -> playerRenderer.renderRightHand(stack, buffer, light, clientPlayer);
                case LEFT -> playerRenderer.renderLeftHand(stack, buffer, light, clientPlayer);
            }
            renderingHand = false;
            return;
        }

        if (livingRenderer instanceof AdvancedHumanoidRenderer<?,?,?> advRenderer && living instanceof ChangedEntity changedEntity) {
            renderingHand = true;

            AdvancedHumanoidModel entModel = advRenderer.getModel(changedEntity);
            var modelInterface = (AdvancedHumanoidModelInterface<?,?>)entModel;

            var controller = modelInterface.getAnimator();

            controller.resetVariables();
            ModelPart handPart = entModel.getArm(arm);
            entModel.setupAnim(changedEntity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
            modelInterface.setupHand();

            PoseStack stackCorrector = modelInterface.getPlacementCorrectors(CorrectorType.fromArm(arm));
            ResourceLocation texture = entRenderer.getTextureLocation(changedEntity);

            renderModelPartWithTexture(handPart, stackCorrector, stack, buffer.getBuffer(RenderType.entityCutout(texture)), light, 1F);

            if (layers) {
                for (var layer : advRenderer.layers)  {
                    if (layer instanceof FirstPersonLayer firstPersonLayer)
                        firstPersonLayer.renderFirstPersonOnArms(stack, buffer, light, changedEntity, arm, stackCorrector, partialTick);
                }
            }

            renderingHand = false;
        }
    }

    public static boolean maybeRenderHand(PlayerRenderer playerRenderer, PoseStack stack, MultiBufferSource buffer, int light, AbstractClientPlayer player, ModelPart arm, ModelPart armwear) {
        if (renderingHand) return false;

        return ProcessTransfur.ifPlayerTransfurred(player, variant -> {
            if (player == Minecraft.getInstance().getCameraEntity()) {
                float partialTick = Minecraft.getInstance().getDeltaFrameTime();
                HumanoidArm handSide = playerRenderer.getModel().rightArm != arm ? HumanoidArm.LEFT : HumanoidArm.RIGHT;

                ChangedCompatibility.freezeIsFirstPersonRendering();
                variant.sync(player);
                variant.getChangedEntity().setCustomNameVisible(true);

                if (variant.getTransfurProgression(partialTick) < 1f && !variant.isTemporaryFromSuit()) {
                    TransfurAnimator.startCapture();

                    renderHand(player, handSide, stack, buffer, light, partialTick);
                    renderHand(variant.getChangedEntity(), handSide, stack, buffer, light, partialTick);

                    TransfurAnimator.endCapture();

                    ChangedCompatibility.forceIsFirstPersonRenderingToFrozen();

                    TransfurAnimator.renderTransfurringArm(player, handSide, variant, stack, buffer, light, partialTick, null);
                } else {
                    renderHand(variant.getChangedEntity(), handSide, stack, buffer, light, partialTick);
                }

                ChangedCompatibility.thawIsFirstPersonRendering();

                return true;
            }

            return false;
        }, () -> false);
    }

    public static void renderModelPartWithTexture(ModelPart part, PoseStack stackCorrector, PoseStack stack, VertexConsumer buffer, int light, float alpha) {
        if(part == null) return;

        float prevX = part.xRot;
        part.xRot = 0F;
        float prevY = part.yRot;
        part.yRot = 0F;
        float prevZ = part.zRot;
        part.zRot = 0.05F;

        //taken from ModelRenderer.render
        if(part.visible) {
            stack.pushPose();

            part.translateAndRotate(stack);

            stack.mulPoseMatrix(stackCorrector.last().pose());

            part.render(stack, buffer, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, alpha);

            stack.popPose();
        }

        part.xRot = prevX;
        part.yRot = prevY;
        part.zRot = prevZ;
    }

    public static void renderModelPartWithTexture(ModelPart part, PoseStack stackCorrector, PoseStack stack, VertexConsumer buffer, int light, float red, float green, float blue, float alpha) {
        if(part == null) return;

        float prevX = part.xRot;
        part.xRot = 0F;
        float prevY = part.yRot;
        part.yRot = 0F;
        float prevZ = part.zRot;
        part.zRot = 0.05F;

        //taken from ModelRenderer.render
        if(part.visible) {
            stack.pushPose();

            part.translateAndRotate(stack);

            stack.mulPoseMatrix(stackCorrector.last().pose());

            part.render(stack, buffer, light, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);

            stack.popPose();
        }

        part.xRot = prevX;
        part.yRot = prevY;
        part.zRot = prevZ;
    }
}
