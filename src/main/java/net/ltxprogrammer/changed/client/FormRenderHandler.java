package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.LatexHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomCoatLayer;
import net.ltxprogrammer.changed.client.renderer.layers.EmissiveBodyLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexTranslucentLayer;
import net.ltxprogrammer.changed.client.renderer.model.CorrectorType;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModelInterface;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
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
        ProcessTransfur.ifPlayerLatex(player, variant -> {
            variant.sync(player);
            variant.getLatexEntity().setCustomNameVisible(true);

            if (!RenderOverride.renderOverrides(player, variant, stack, buffer, light, partialTick))
                renderLiving(variant.getLatexEntity(), stack, buffer, light, partialTick);
        });
    }

    public static void renderLiving(LivingEntity living, PoseStack stack, MultiBufferSource buffer, int light, float partialTick) {
        if (living == null) return;
        EntityRenderer<? super LivingEntity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(living);
        renderer.render(living, living.getYRot(), partialTick, stack, buffer, light);
    }

    public static float lastPartialTick;

    public static boolean renderHand(PlayerRenderer playerRenderer, PoseStack stack, MultiBufferSource buffer, int light, AbstractClientPlayer player, ModelPart arm, ModelPart armwear) {
        return ProcessTransfur.ifPlayerLatex(player, variant -> {
            //Check if this is the player
            if(player == Minecraft.getInstance().getCameraEntity()) {
                ModelPart handPart = null;
                PoseStack stackCorrector = null;
                ResourceLocation texture = null;

                HumanoidArm handSide = playerRenderer.getModel().rightArm != arm ? HumanoidArm.LEFT : HumanoidArm.RIGHT; //default to right arm instead any mods override the player model

                LatexEntity livingInstance = variant.getLatexEntity();
                if (livingInstance == null) return false;
                EntityRenderer entRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(livingInstance);
                LatexHumanoidRenderer<?, ?, ?> latexRenderer = null;

                if (entRenderer instanceof LatexHumanoidRenderer<?,?,?> tmp) {
                    latexRenderer = tmp;

                    LatexHumanoidModel entityModel = latexRenderer.getModel(livingInstance);
                    if (entityModel == null)
                        return true;

                    LatexHumanoidModelInterface latexHumanoidModel = (LatexHumanoidModelInterface)entityModel;

                    var controller = latexHumanoidModel.getAnimator();

                    entityModel.attackTime = 0.0F;
                    controller.resetVariables();
                    entityModel.setupAnim(livingInstance, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
                    latexHumanoidModel.setupHand();

                    handPart = latexHumanoidModel.getArm(handSide);
                    stackCorrector = latexHumanoidModel.getPlacementCorrectors(CorrectorType.fromArm(handSide));
                    texture = entRenderer.getTextureLocation(livingInstance);
                }

                if(handPart != null && texture != null) {
                    renderModelPartWithTexture(handPart, stackCorrector, stack, buffer.getBuffer(RenderType.entityCutout(texture)), light, 1F);
                    for (var layer : latexRenderer.layers)  {
                        if (layer instanceof EmissiveBodyLayer<?, ?> emissiveBodyLayer)
                            renderModelPartWithTexture(handPart, stackCorrector, stack, buffer.getBuffer(emissiveBodyLayer.renderType()), LightTexture.FULL_BRIGHT, 1F);
                        if (layer instanceof CustomCoatLayer<?,?> customCoatLayer) {
                            var info = livingInstance.getBasicPlayerInfo();
                            var coatColor = info.getHairColor();
                            renderModelPartWithTexture(handPart, stackCorrector, stack, buffer.getBuffer(customCoatLayer.getRenderTypeForColor(coatColor)), light,
                                    coatColor.red(), coatColor.green(), coatColor.blue(), 1F);
                        }
                        if (layer instanceof LatexTranslucentLayer<?,?> gelLayer)
                            renderModelPartWithTexture(handPart, stackCorrector, stack, buffer.getBuffer(RenderType.entityTranslucent(gelLayer.getTexture())), light, 1F);
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
