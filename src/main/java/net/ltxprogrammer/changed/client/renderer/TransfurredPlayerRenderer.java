package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.ltxprogrammer.changed.client.tfanimations.TransfurAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

public class TransfurredPlayerRenderer extends PlayerRenderer implements StackAwareRenderer<AbstractClientPlayer>, HandRenderer<AbstractClientPlayer> {
    protected final EntityRenderDispatcher entityRenderDispatcher;
    protected final Minecraft minecraft;
    protected PlayerRenderer shadowedPlayerRenderer = null;

    public TransfurredPlayerRenderer(EntityRendererProvider.Context context) {
        super(context, false);
        entityRenderDispatcher = context.getEntityRenderDispatcher();
        minecraft = Minecraft.getInstance();
    }

    public static boolean wantsToOverride(AbstractClientPlayer player) {
        return !player.isRemoved() && !player.isSpectator() && ProcessTransfur.isPlayerTransfurred(player);
    }

    @Override
    public @NotNull PlayerModel<AbstractClientPlayer> getModel() {
        return shadowedPlayerRenderer != null ? shadowedPlayerRenderer.getModel() : super.getModel();
    }

    @Override
    public void setShadowedRenderer(EntityRenderer<? super AbstractClientPlayer> renderer) {
        if (renderer instanceof PlayerRenderer playerRenderer)
            this.shadowedPlayerRenderer = playerRenderer;
        else
            this.shadowedPlayerRenderer = null;
    }

    @Override
    public void render(AbstractClientPlayer player, float yRot, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        TransfurVariantInstance<?> variant = ProcessTransfur.getPlayerTransfurVariant(player);
        ChangedCompatibility.freezeIsFirstPersonRendering();
        variant.sync(player);
        variant.getChangedEntity().setCustomNameVisible(true);
        variant.prepareForRender(partialTick);

        ChangedEntity entity = variant.getChangedEntity();
        EntityRenderer<? super ChangedEntity> changedRenderer = entityRenderDispatcher.getRenderer(entity);
        int variantLight = FormRenderHandler.maxPackedLight(packedLight, changedRenderer.getPackedLightCoords(entity, partialTick));

        if (variant.getTransfurProgression(partialTick) < 1f && !variant.isTemporaryFromSuit() && shadowedPlayerRenderer != null) {
            TransfurAnimator.startCapture();

            shadowedPlayerRenderer.render(player, yRot, partialTick, pose, buffer, packedLight);
            changedRenderer.render(entity, yRot, partialTick, pose, buffer, variantLight);

            TransfurAnimator.endCapture();

            ChangedCompatibility.forceIsFirstPersonRenderingToFrozen();

            try {
                TransfurAnimator.renderTransfurringPlayer(
                        shadowedPlayerRenderer,
                        changedRenderer,
                        yRot,
                        packedLight, variantLight,
                        player, variant, pose, buffer, partialTick);
            } catch (Exception e) {
                CrashReport report = CrashReport.forThrowable(e, "Rendering transfurred form");
                CrashReportCategory category = report.addCategory("Transfur details");
                category.setDetail("Transfur Variant", variant.getFormId());
                category.setDetail("Transfur Progress", variant.getTransfurProgression(partialTick));
                category.setDetail("Transfur Morph Progress", variant.getMorphProgression(partialTick));
                throw new ReportedException(report);
            }
        } else {
            changedRenderer.render(entity, yRot, partialTick, pose, buffer, variantLight);
        }

        ChangedCompatibility.thawIsFirstPersonRendering();
    }

    protected PartPose getArmPose(AbstractClientPlayer player, HumanoidArm hand) {
        PlayerModel<AbstractClientPlayer> playermodel = this.getModel();
        this.setModelProperties(player);
        playermodel.attackTime = 0.0F;
        playermodel.crouching = false;
        playermodel.swimAmount = 0.0F;
        playermodel.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        return (hand == HumanoidArm.LEFT ? playermodel.leftArm : playermodel.rightArm).storePose();
    }

    @Override
    public void renderRightHand(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player) {
        if (!net.minecraftforge.client.ForgeHooksClient.renderSpecificFirstPersonArm(poseStack, buffer, packedLight, player, HumanoidArm.RIGHT)) {
            this.renderHand(poseStack, buffer, packedLight, player, HumanoidArm.RIGHT, getArmPose(player, HumanoidArm.RIGHT));
        }
    }

    @Override
    public void renderLeftHand(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player) {
        if (!net.minecraftforge.client.ForgeHooksClient.renderSpecificFirstPersonArm(poseStack, buffer, packedLight, player, HumanoidArm.LEFT)) {
            this.renderHand(poseStack, buffer, packedLight, player, HumanoidArm.LEFT, getArmPose(player, HumanoidArm.LEFT));
        }
    }

    @Override
    public void renderHand(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, HumanoidArm hand, PartPose armPose) {
        TransfurVariantInstance<?> variant = ProcessTransfur.getPlayerTransfurVariant(player);
        ChangedEntity entity = variant.getChangedEntity();
        EntityRenderer<? super ChangedEntity> changedRenderer = entityRenderDispatcher.getRenderer(entity);

        float partialTick = minecraft.getPartialTick();
        int variantLight = FormRenderHandler.maxPackedLight(packedLight, changedRenderer.getPackedLightCoords(entity, partialTick));

        ChangedCompatibility.freezeIsFirstPersonRendering();
        variant.sync(player);
        variant.getChangedEntity().setCustomNameVisible(true);

        if (variant.getTransfurProgression(partialTick) < 1f && !variant.isTemporaryFromSuit() && shadowedPlayerRenderer != null) {
            TransfurAnimator.startCapture();

            if (hand == HumanoidArm.LEFT)
                shadowedPlayerRenderer.renderLeftHand(poseStack, buffer, packedLight, player);
            else
                shadowedPlayerRenderer.renderRightHand(poseStack, buffer, packedLight, player);
            FormRenderHandler.renderHand(changedRenderer, entity, hand, armPose, poseStack, buffer, variantLight, partialTick, true);

            TransfurAnimator.endCapture();

            ChangedCompatibility.forceIsFirstPersonRenderingToFrozen();

            TransfurAnimator.renderTransfurringArm(shadowedPlayerRenderer, changedRenderer, packedLight, variantLight, player, hand, armPose, variant, poseStack, buffer, partialTick, null);
        } else {
            FormRenderHandler.renderHand(changedRenderer, entity, hand, armPose, poseStack, buffer, variantLight, partialTick, true);
        }

        ChangedCompatibility.thawIsFirstPersonRendering();
    }
}
