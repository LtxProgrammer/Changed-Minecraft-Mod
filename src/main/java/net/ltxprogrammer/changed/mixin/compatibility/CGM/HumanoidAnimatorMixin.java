package net.ltxprogrammer.changed.mixin.compatibility.CGM;

import com.mojang.math.Vector3f;
import com.mrcrayfish.guns.client.handler.AimingHandler;
import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.item.GunItem;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HumanoidAnimator.class, remap = false)
public abstract class HumanoidAnimatorMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> {
    @Shadow public abstract void applyPropertyModel(HumanoidModel<?> propertyModel);

    @Shadow @Final public M entityModel;

    @Inject(method = "setupAnim", at = @At("RETURN"))
    public void setupAnimEND(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
        Player player = entity.getUnderlyingPlayer();
        if (player == null) return;

        HumanoidModel<?> model = this.entityModel;
        this.entityModel.syncPropertyModel();
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() instanceof GunItem gunItem) {
            if (player.isLocalPlayer() && limbSwing == 0.0F) {
                model.rightArm.xRot = 0.0F;
                model.rightArm.yRot = 0.0F;
                model.rightArm.zRot = 0.0F;
                model.leftArm.xRot = 0.0F;
                model.leftArm.yRot = 0.0F;
                model.leftArm.zRot = 0.0F;
                this.applyPropertyModel(model);
                return;
            }

            Gun gun = gunItem.getModifiedGun(heldItem);
            Vector3f rightArmPosO = getPartPos(model.rightArm);
            Vector3f leftArmPosO = getPartPos(model.leftArm);
            Vector3f headPosO = getPartPos(model.head);
            model.rightArm.setPos(-5.0f, 2.0f, 0.0f);
            model.leftArm.setPos(5.0f, 2.0f, 0.0f);
            model.head.setPos(0.0f, 0.0f, 0.0f);
            gun.getGeneral().getGripType().getHeldAnimation().applyPlayerModelRotation(player, model.rightArm, model.leftArm, model.head, InteractionHand.MAIN_HAND, AimingHandler.get().getAimProgress(player, Minecraft.getInstance().getFrameTime()));
            offsetPart(model.rightArm, rightArmPosO, new Vector3f(-5.0f, 2.0f, 0.0f));
            offsetPart(model.leftArm, leftArmPosO, new Vector3f(5.0f, 2.0f, 0.0f));
            offsetPart(model.head, headPosO, Vector3f.ZERO);
            this.applyPropertyModel(model);
        }
    }

    @Unique
    private static Vector3f getPartPos(ModelPart part) {
        return new Vector3f(part.x, part.y, part.z);
    }

    @Unique
    protected void offsetPart(ModelPart part, Vector3f oldPos, Vector3f expectedPos) {
        part.x = oldPos.x() + (expectedPos.x() - part.x);
        part.y = oldPos.y() + (expectedPos.y() - part.y);
        part.z = oldPos.z() + (expectedPos.z() - part.z);
    }
}
