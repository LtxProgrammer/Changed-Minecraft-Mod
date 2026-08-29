package net.ltxprogrammer.changed.mixin.compatibility.Vivecraft;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.animations.Limb;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.StackAwareRenderer;
import net.ltxprogrammer.changed.client.renderer.WrappedPlayerRenderer;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedArmedModel;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.TorsoedModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.LatexBenignOrca;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.extension.vivecraft.RendererScaleAccessor;
import net.ltxprogrammer.changed.extension.vivecraft.VivecraftHelper;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.client.ClientVRPlayers;
import org.vivecraft.client.render.VRPlayerModel;
import org.vivecraft.client.render.VRPlayerRenderer;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.render.helpers.VREffectsHelper;

import javax.annotation.Nullable;

@Mixin(value = AdvancedHumanoidModel.class, remap = false)
@RequiredMods("vivecraft")
public abstract class AdvancedHumanoidModelMixin<T extends ChangedEntity> extends PlayerModel<T> implements AdvancedArmedModel<T>, HeadedModel, TorsoedModel {
    @Shadow
    public abstract HumanoidAnimator<T, ?> getAnimator(T t);

    @Shadow public abstract ModelPart getLeg(HumanoidArm leg);

    @Shadow @Nullable public abstract ModelPart getLimb(Limb limb);

    public AdvancedHumanoidModelMixin(ModelPart pRoot, boolean pSlim) {
        super(pRoot, pSlim);
    }

    @Unique
    private float vivecraft$getModelNeckPos(HumanoidAnimator<T,?> animator, float partialTick) {
        float neckPos = 1.501F;

        float torsoPositionY = animator.calculateTorsoPositionY();
        return ((24.0F - torsoPositionY) / 16.0F + 0.001F);
    }

    @Unique
    private float vivecraft$getDeltaNeckPos(HumanoidAnimator<T,?> animator, float partialTick) {
        return 1.501F - vivecraft$getModelNeckPos(animator, partialTick);
    }

    @Unique
    private void vivecraft$applyLimbLengths(T entity, HumanoidAnimator<T,?> animator, PlayerModel<?> playerModel, float partialTick) {
        float torsoYScale = Mth.cos(playerModel.body.xRot);
        float torsoZScale = -Mth.sin(playerModel.body.xRot);

        float deltaTorsoLength = 12.0F - animator.torsoLength;
        if (deltaTorsoLength != 0.0F) {
            playerModel.leftLeg.y -= deltaTorsoLength * torsoYScale;
            playerModel.leftLeg.z -= deltaTorsoLength * torsoZScale;
            playerModel.rightLeg.y -= deltaTorsoLength * torsoYScale;
            playerModel.rightLeg.z -= deltaTorsoLength * torsoZScale;
        }

        float deltaTorsoWidth = 5.0F - animator.torsoWidth;
        if (deltaTorsoWidth != 0.0F && !VREffectsHelper.isFirstPersonEntityPass()) { // Arms will offset the GUI in FP
            playerModel.leftArm.x -= deltaTorsoWidth;
            playerModel.rightArm.x += deltaTorsoWidth;
        }

        var leftLeg = getLeg(HumanoidArm.LEFT);
        if (leftLeg != null) {
            playerModel.leftLeg.x = leftLeg.x;
            playerModel.leftLeg.xRot = leftLeg.xRot;
            playerModel.leftLeg.yRot = leftLeg.yRot;
            playerModel.leftLeg.zRot = leftLeg.zRot;
        }

        var rightLeg = getLeg(HumanoidArm.RIGHT);
        if (rightLeg != null) {
            playerModel.rightLeg.x = rightLeg.x;
            playerModel.rightLeg.xRot = rightLeg.xRot;
            playerModel.rightLeg.yRot = rightLeg.yRot;
            playerModel.rightLeg.zRot = rightLeg.zRot;
        }

        var abdomen = getLimb(Limb.ABDOMEN);
        if (abdomen != null) {
            playerModel.leftLeg.xRot = abdomen.xRot;
            playerModel.leftLeg.yRot = abdomen.yRot;
            playerModel.leftLeg.zRot = abdomen.zRot;
            playerModel.rightLeg.xRot = abdomen.xRot;
            playerModel.rightLeg.yRot = abdomen.yRot;
            playerModel.rightLeg.zRot = abdomen.zRot;
        }

        if (entity.getType().is(ChangedTags.EntityTypes.BENIGN_LATEXES) && !(entity instanceof LatexBenignOrca)) {
            playerModel.rightArm.xRot = playerModel.body.xRot;
            playerModel.rightArm.yRot = playerModel.body.yRot;
            playerModel.rightArm.zRot = playerModel.body.zRot;
            playerModel.leftArm.xRot = playerModel.body.xRot;
            playerModel.leftArm.yRot = playerModel.body.yRot;
            playerModel.leftArm.zRot = playerModel.body.zRot;
        }

        float deltaNeckPos = vivecraft$getDeltaNeckPos(animator, partialTick) * 16.0F;
        playerModel.head.y += deltaNeckPos;
        playerModel.body.y += deltaNeckPos;
        playerModel.leftArm.y += deltaNeckPos;
        playerModel.rightArm.y += deltaNeckPos;
        playerModel.leftLeg.y += deltaNeckPos;
        playerModel.rightLeg.y += deltaNeckPos;
    }

     @Inject(method = "setupAnim(Lnet/ltxprogrammer/changed/entity/ChangedEntity;FFFFF)V", at = @At("TAIL"))
    private void vivecraft$setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        Player player = entity.getUnderlyingPlayer();
        if (player instanceof AbstractClientPlayer clientPlayer) {
            if (!(ClientVRPlayers.getInstance().isVRPlayer(player))) {
                return;
            }

            EntityRenderer<?> renderer =
                    Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(clientPlayer);

            if (renderer instanceof WrappedPlayerRenderer wrappedPlayerRenderer)
                renderer = wrappedPlayerRenderer.getWrapped();
            if (renderer instanceof StackAwareRenderer<?> stackAwareRenderer)
                renderer = stackAwareRenderer.getShadowedRenderer();

            if (renderer instanceof VRPlayerRenderer vrPlayerRenderer) {

                PlayerModel<AbstractClientPlayer> playerModel = vrPlayerRenderer.getModel();

                if (playerModel instanceof VRPlayerModel<AbstractClientPlayer> vrPlayerModel) {
                    // ===== Map self model parts =====
                    ModelPart selfHead = getHead();
                    ModelPart selfBody = getTorso();
                    ModelPart selfLeftArm = getArm(HumanoidArm.LEFT);
                    ModelPart selfRightArm = getArm(HumanoidArm.RIGHT);
                    ModelPart selfLeftLeg = getLeg(HumanoidArm.LEFT);
                    ModelPart selfRightLeg = getLeg(HumanoidArm.RIGHT);

                    // Force the VR model to update its animation state
                    // (includes HMD, controller tracking, body yaw logic, etc.)
                    playerModel.swimAmount = this.swimAmount;
                    playerModel.setupAnim(clientPlayer,
                            limbSwing,
                            limbSwingAmount,
                            ageInTicks,
                            netHeadYaw,
                            headPitch);

                    vivecraft$applyLimbLengths(entity, getAnimator(entity), playerModel,
                            Mth.positiveModulo(ageInTicks, 1.0f));

                    getAnimator(entity).applyPropertyModelLimbs(playerModel);

                    changed$copyPartVisibility(playerModel.head, selfHead);
                    changed$copyPartVisibility(playerModel.body, selfBody);
                    changed$copyPartVisibility(playerModel.leftArm, selfLeftArm);
                    changed$copyPartVisibility(playerModel.rightArm, selfRightArm);
                    changed$copyPartVisibility(playerModel.leftLeg, selfLeftLeg);
                    changed$copyPartVisibility(playerModel.rightLeg, selfRightLeg);
                }

            }

        }
    }

    @Unique
    private static void changed$copyPartVisibility(ModelPart from, ModelPart to) {
        if (from == null || to == null) {
            return;
        }

        to.visible = from.visible;
    }
}
