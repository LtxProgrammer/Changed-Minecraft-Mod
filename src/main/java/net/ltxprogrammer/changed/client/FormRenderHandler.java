package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.FirstPersonLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.tfanimations.TransfurAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class FormRenderHandler {
    /**
     * Returns the mix between the sky and block light values.
     */
    public static int lerpPackedLight(int packedLight0, int packedLight1, float alpha) {
        int blockLight0 = LightTexture.block(packedLight0);
        int skyLight0 = LightTexture.sky(packedLight0);
        int blockLight1 = LightTexture.block(packedLight1);
        int skyLight1 = LightTexture.sky(packedLight1);

        return LightTexture.pack(
                Mth.lerpInt(alpha, blockLight0, blockLight1),
                Mth.lerpInt(alpha, skyLight0, skyLight1)
        );
    }

    /**
     * Returns the brighter sky and block light values within the two values
     */
    public static int maxPackedLight(int packedLight0, int packedLight1) {
        int blockLight0 = LightTexture.block(packedLight0);
        int skyLight0 = LightTexture.sky(packedLight0);
        int blockLight1 = LightTexture.block(packedLight1);
        int skyLight1 = LightTexture.sky(packedLight1);

        return LightTexture.pack(
                Math.max(blockLight0, blockLight1),
                Math.max(skyLight0, skyLight1)
        );
    }

    public static void renderForm(Player player, PoseStack stack, MultiBufferSource buffer, int light, float partialTick) {
        ProcessTransfur.ifPlayerTransfurred(player, variant -> {
            ChangedCompatibility.freezeIsFirstPersonRendering();
            variant.sync(player);
            variant.getChangedEntity().setCustomNameVisible(true);

            variant.prepareForRender(partialTick);

            if (variant.getTransfurProgression(partialTick) < 1f && !variant.isTemporaryFromSuit()) {
                TransfurAnimator.startCapture();

                renderLiving(player, stack, buffer, light, partialTick);
                renderLiving(variant.getChangedEntity(), stack, buffer, light, partialTick);

                TransfurAnimator.endCapture();

                ChangedCompatibility.forceIsFirstPersonRenderingToFrozen();

                try {
                    TransfurAnimator.renderTransfurringPlayer(player, variant, stack, buffer, light, partialTick);
                } catch (Exception e) {
                    CrashReport report = CrashReport.forThrowable(e, "Rendering transfurred form");
                    CrashReportCategory category = report.addCategory("Transfur details");
                    category.setDetail("Transfur Variant", variant.getFormId());
                    category.setDetail("Transfur Progress", variant.getTransfurProgression(partialTick));
                    category.setDetail("Transfur Morph Progress", variant.getMorphProgression(partialTick));
                    throw new ReportedException(report);
                }
            } else {
                var changedEntity = variant.getChangedEntity();
                EntityRenderer<? super LivingEntity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(changedEntity);
                int variantLight = maxPackedLight(light, renderer.getPackedLightCoords(changedEntity, partialTick));

                if (!RenderOverride.renderOverrides(player, variant, stack, buffer, variantLight, partialTick))
                    renderLiving(changedEntity, stack, buffer, variantLight, partialTick);
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

    public static void renderHand(LivingEntity living, HumanoidArm arm, PartPose armPose, PoseStack stack, MultiBufferSource buffer, int light, float partialTick) {
        renderHand(living, arm, armPose, stack, buffer, light, partialTick, true);
    }

    private static boolean renderingHand = false;
    public static boolean isRenderingHand() {
        return renderingHand;
    }

    public static void renderHand(LivingEntity living, HumanoidArm arm, PartPose armPose, PoseStack stack, MultiBufferSource buffer, int light, float partialTick, boolean layers) {
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

        if (livingRenderer instanceof AdvancedHumanoidRenderer<?,?> advRenderer && living instanceof ChangedEntity changedEntity) {
            renderingHand = true;

            AdvancedHumanoidModel entModel = advRenderer.getModel(changedEntity);

            var controller = entModel.getAnimator(changedEntity);

            controller.resetVariables();
            ModelPart handPart = entModel.getArm(arm);
            /*entModel.setupAnim(changedEntity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
            modelInterface.setupHand(changedEntity);*/
            handPart.loadPose(armPose);

            ResourceLocation texture = entRenderer.getTextureLocation(changedEntity);

            renderModelPartWithTexture(handPart, stack, buffer.getBuffer(RenderType.entityCutout(texture)), light, 1F);

            if (layers) {
                for (var layer : advRenderer.layers)  {
                    if (layer instanceof FirstPersonLayer firstPersonLayer)
                        firstPersonLayer.renderFirstPersonOnArms(stack, buffer, light, changedEntity, arm, armPose, partialTick);
                }
            }

            renderingHand = false;
        }
    }

    public static boolean maybeRenderHand(PlayerRenderer playerRenderer, PoseStack stack, MultiBufferSource buffer, int light, AbstractClientPlayer player, ModelPart arm, ModelPart sleeve) {
        if (renderingHand) return false;

        return ProcessTransfur.ifPlayerTransfurred(player, variant -> {
            if (player == Minecraft.getInstance().getCameraEntity()) {
                float partialTick = Minecraft.getInstance().getPartialTick();
                HumanoidArm handSide = playerRenderer.getModel().rightArm != arm ? HumanoidArm.LEFT : HumanoidArm.RIGHT;

                ChangedCompatibility.freezeIsFirstPersonRendering();
                variant.sync(player);
                variant.getChangedEntity().setCustomNameVisible(true);

                var armPose = arm.storePose();

                if (variant.getTransfurProgression(partialTick) < 1f && !variant.isTemporaryFromSuit()) {
                    TransfurAnimator.startCapture();

                    renderHand(player, handSide, armPose, stack, buffer, light, partialTick);
                    renderHand(variant.getChangedEntity(), handSide, armPose, stack, buffer, light, partialTick);

                    TransfurAnimator.endCapture();

                    ChangedCompatibility.forceIsFirstPersonRenderingToFrozen();

                    TransfurAnimator.renderTransfurringArm(player, handSide, armPose, variant, stack, buffer, light, partialTick, null);
                } else {
                    var changedEntity = variant.getChangedEntity();
                    EntityRenderer<? super LivingEntity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(changedEntity);
                    int variantLight = maxPackedLight(light, renderer.getPackedLightCoords(changedEntity, partialTick));

                    renderHand(variant.getChangedEntity(), handSide, armPose, stack, buffer, variantLight, partialTick);
                }

                ChangedCompatibility.thawIsFirstPersonRendering();

                return true;
            }

            return false;
        }, () -> false);
    }

    public static void renderModelPartWithTexture(ModelPart part, PoseStack stack, VertexConsumer buffer, int light, float alpha) {
        if(part == null) return;

        part.render(stack, buffer, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, alpha);
    }

    public static void renderVanillaModelPartWithTexture(ModelPart part, PoseStack stack, VertexConsumer buffer, int light, float alpha) {
        if(part == null) return;

        part.render(stack, buffer, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, alpha);
    }

    public static void renderModelPartWithTexture(ModelPart part, PoseStack stack, VertexConsumer buffer, int light, float red, float green, float blue, float alpha) {
        if(part == null) return;

        part.render(stack, buffer, light, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
    }

    public static void renderVanillaModelPartWithTexture(ModelPart part, PoseStack stack, VertexConsumer buffer, int light, float red, float green, float blue, float alpha) {
        if(part == null) return;

        part.render(stack, buffer, light, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
    }
}
