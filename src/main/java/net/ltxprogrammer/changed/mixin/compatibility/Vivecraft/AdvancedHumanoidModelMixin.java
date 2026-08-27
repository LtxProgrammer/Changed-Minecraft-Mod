package net.ltxprogrammer.changed.mixin.compatibility.Vivecraft;

import net.ltxprogrammer.changed.client.renderer.StackAwareRenderer;
import net.ltxprogrammer.changed.client.renderer.WrappedPlayerRenderer;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedArmedModel;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.TorsoedModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.LatexBenignOrca;
import net.ltxprogrammer.changed.entity.variant.EntityShape;
import net.ltxprogrammer.changed.extension.RequiredMods;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.client.ClientVRPlayers;
import org.vivecraft.client.render.VRPlayerModel;
import org.vivecraft.client.render.VRPlayerRenderer;

@Mixin(value = AdvancedHumanoidModel.class, remap = false)
@RequiredMods("vivecraft")
public abstract class AdvancedHumanoidModelMixin<T extends ChangedEntity> extends PlayerModel<T> implements AdvancedArmedModel<T>, HeadedModel, TorsoedModel {
    @Shadow
    public abstract HumanoidAnimator<T, ?> getAnimator(T t);

    @Shadow public abstract ModelPart getLeg(HumanoidArm leg);

    public AdvancedHumanoidModelMixin(ModelPart pRoot, boolean pSlim) {
        super(pRoot, pSlim);
    }

    @Unique
    private void vivecraft$applyLimbLengths(T entity, HumanoidAnimator<T,?> animator, PlayerModel<?> playerModel) {
        float torsoYScale = Mth.cos(playerModel.body.xRot);
        float torsoZScale = -Mth.sin(playerModel.body.xRot);

        float deltaTorsoLength = 12.0F - animator.torsoLength;
        if (deltaTorsoLength != 0.0F) {
            playerModel.head.y += deltaTorsoLength * torsoYScale;
            playerModel.head.z += deltaTorsoLength * torsoZScale;
            playerModel.body.y += deltaTorsoLength * torsoYScale;
            playerModel.body.z += deltaTorsoLength * torsoZScale;
            playerModel.leftArm.y += deltaTorsoLength * torsoYScale;
            playerModel.leftArm.z += deltaTorsoLength * torsoZScale;
            playerModel.rightArm.y += deltaTorsoLength * torsoYScale;
            playerModel.rightArm.z += deltaTorsoLength * torsoZScale;
        }

        float deltaTorsoWidth = 5.0F - animator.torsoWidth;
        if (deltaTorsoWidth != 0.0F) {
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

        if (entity.getType().is(ChangedTags.EntityTypes.BENIGN_LATEXES) && !(entity instanceof LatexBenignOrca)) {
            playerModel.rightArm.xRot = playerModel.body.xRot;
            playerModel.rightArm.yRot = playerModel.body.yRot;
            playerModel.rightArm.zRot = playerModel.body.zRot;
            playerModel.leftArm.xRot = playerModel.body.xRot;
            playerModel.leftArm.yRot = playerModel.body.yRot;
            playerModel.leftArm.zRot = playerModel.body.zRot;
        }
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

                    vivecraft$applyLimbLengths(entity, getAnimator(entity), playerModel);

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
