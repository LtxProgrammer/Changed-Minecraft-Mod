package net.ltxprogrammer.changed.mixin.compatibility.CarryOn;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.animate.upperbody.AbstractUpperBodyAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.upperbody.HoldEntityAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.common.config.Configs;
import tschipp.carryon.common.handler.RegistrationHandler;
import tschipp.carryon.common.helper.ScriptParseHelper;
import tschipp.carryon.common.item.ItemCarryonBlock;
import tschipp.carryon.common.item.ItemCarryonEntity;
import tschipp.carryon.common.scripting.CarryOnOverride;
import tschipp.carryon.common.scripting.ScriptChecker;

@Mixin(value = HoldEntityAnimator.class, remap = false)
public abstract class HoldEntityAnimatorMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public HoldEntityAnimatorMixin(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
    }

    @Inject(method = "setupAnim", at = @At("RETURN"))
    public void maybeSetupCarryOn(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        // Copied from RenderEvents
        if (Configs.Settings.renderArms.get() && entity.getUnderlyingPlayer() instanceof AbstractClientPlayer player) {
            if (!ModList.get().isLoaded("obfuscate")) {
                net.minecraft.world.entity.Pose pose = player.getPose();
                ItemStack stack = player.getMainHandItem();
                if (pose != net.minecraft.world.entity.Pose.SWIMMING && pose != net.minecraft.world.entity.Pose.FALL_FLYING && !stack.isEmpty() && (stack.getItem() == RegistrationHandler.itemTile && ItemCarryonBlock.hasTileData(stack) || stack.getItem() == RegistrationHandler.itemEntity && ItemCarryonEntity.hasEntityData(stack))) {
                    CarryOnOverride overrider = ScriptChecker.getOverride(player);
                    if (overrider == null) {
                        this.rightArm.xRot = Mth.PI + 2.0F + (this.core.crouching ? 0.0F : 0.2F) - (stack.getItem() == RegistrationHandler.itemEntity ? 0.3F : 0.0F);
                        this.rightArm.yRot = 0.0F;
                        this.rightArm.zRot = stack.getItem() == RegistrationHandler.itemEntity ? 0.15F : 0.0F;

                        this.leftArm.xRot = Mth.PI + 2.0F + (this.core.crouching ? 0.0F : 0.2F) - (stack.getItem() == RegistrationHandler.itemEntity ? 0.3F : 0.0F);
                        this.leftArm.yRot = 0.0F;
                        this.leftArm.zRot = stack.getItem() == RegistrationHandler.itemEntity ? -0.15F : 0.0F;
                    } else {
                        float[] rotLeft = null;
                        float[] rotRight = null;
                        if (!overrider.getRenderRotationLeftArm().isEmpty()) {
                            rotLeft = ScriptParseHelper.getXYZArray(overrider.getRenderRotationLeftArm());
                        }

                        if (!overrider.getRenderRotationRightArm().isEmpty()) {
                            rotRight = ScriptParseHelper.getXYZArray(overrider.getRenderRotationRightArm());
                        }

                        boolean renderRight = overrider.isRenderRightArm();
                        boolean renderLeft = overrider.isRenderLeftArm();
                        if (renderLeft && rotLeft != null) {
                            this.leftArm.xRot = Mth.PI + rotLeft[0];
                            this.leftArm.yRot = 0.0F;
                            this.leftArm.zRot = -rotLeft[2];
                        } else if (renderLeft) {
                            this.leftArm.xRot = Mth.PI + 2.0F + (this.core.crouching ? 0.0F : 0.2F) - (stack.getItem() == RegistrationHandler.itemEntity ? 0.3F : 0.0F);
                            this.leftArm.yRot = 0.0F;
                            this.leftArm.zRot = stack.getItem() == RegistrationHandler.itemEntity ? -0.15F : 0.0F;
                        }

                        if (renderRight && rotRight != null) {
                            this.rightArm.xRot = Mth.PI + rotRight[0];
                            this.rightArm.yRot = 0.0F;
                            this.rightArm.zRot = -rotRight[2];
                        } else if (renderRight) {
                            this.rightArm.xRot = Mth.PI + 2.0F + (this.core.crouching ? 0.0F : 0.2F) - (stack.getItem() == RegistrationHandler.itemEntity ? 0.3F : 0.0F);
                            this.rightArm.yRot = 0.0F;
                            this.rightArm.zRot = stack.getItem() == RegistrationHandler.itemEntity ? 0.15F : 0.0F;
                        }
                    }
                }
            }

        }
    }
}
