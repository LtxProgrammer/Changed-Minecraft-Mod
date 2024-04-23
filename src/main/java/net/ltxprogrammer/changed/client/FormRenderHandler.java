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
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FormRenderHandler {
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

    public static boolean renderHand(PlayerRenderer playerRenderer, PoseStack stack, MultiBufferSource buffer, int light, AbstractClientPlayer player, ModelPart arm, ModelPart armwear) {
        return ProcessTransfur.ifPlayerTransfurred(player, variant -> {
            //Check if this is the player
            if(player == Minecraft.getInstance().getCameraEntity()) {
                ModelPart handPart = null;
                PoseStack stackCorrector = null;
                ResourceLocation texture = null;

                HumanoidArm handSide = playerRenderer.getModel().rightArm != arm ? HumanoidArm.LEFT : HumanoidArm.RIGHT; //default to right arm instead any mods override the player model

                ChangedEntity livingInstance = variant.getChangedEntity();
                if (livingInstance == null) return false;
                EntityRenderer entRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(livingInstance);
                AdvancedHumanoidRenderer<?, ?, ?> latexRenderer = null;

                if (entRenderer instanceof AdvancedHumanoidRenderer<?,?,?> tmp) {
                    latexRenderer = tmp;

                    AdvancedHumanoidModel entityModel = latexRenderer.getModel(livingInstance);
                    if (entityModel == null)
                        return true;

                    AdvancedHumanoidModelInterface AdvancedHumanoidModel = (AdvancedHumanoidModelInterface)entityModel;

                    var controller = AdvancedHumanoidModel.getAnimator();

                    controller.resetVariables();
                    entityModel.setupAnim(livingInstance, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
                    AdvancedHumanoidModel.setupHand();

                    handPart = AdvancedHumanoidModel.getArm(handSide);
                    stackCorrector = AdvancedHumanoidModel.getPlacementCorrectors(CorrectorType.fromArm(handSide));
                    texture = entRenderer.getTextureLocation(livingInstance);
                }

                if(handPart != null && texture != null) {
                    renderModelPartWithTexture(handPart, stackCorrector, stack, buffer.getBuffer(RenderType.entityCutout(texture)), light, 1F);
                    for (var layer : latexRenderer.layers)  {
                        if (layer instanceof FirstPersonLayer firstPersonLayer)
                            firstPersonLayer.renderFirstPersonOnArms(stack, buffer, light, livingInstance, handSide, stackCorrector);
                    }
                }

                return true;
            }

            return false;
        }, () -> false);
    }

    public static void renderModelPartWithTexture(ModelPart part, PoseStack stackCorrector, PoseStack stack, VertexConsumer buffer, int light, float alpha) {
        if(part == null) return;

        float prevX = part.xRot;
        part.xRot = 0F;

        //taken from ModelRenderer.render
        if(part.visible) {
            stack.pushPose();

            part.translateAndRotate(stack);

            stack.mulPoseMatrix(stackCorrector.last().pose());

            part.render(stack, buffer, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, alpha);

            stack.popPose();
        }

        part.xRot = prevX;
    }

    public static void renderModelPartWithTexture(ModelPart part, PoseStack stackCorrector, PoseStack stack, VertexConsumer buffer, int light, float red, float green, float blue, float alpha) {
        if(part == null) return;

        float prevX = part.xRot;
        part.xRot = 0F;

        //taken from ModelRenderer.render
        if(part.visible) {
            stack.pushPose();

            part.translateAndRotate(stack);

            stack.mulPoseMatrix(stackCorrector.last().pose());

            part.render(stack, buffer, light, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);

            stack.popPose();
        }

        part.xRot = prevX;
    }
}
