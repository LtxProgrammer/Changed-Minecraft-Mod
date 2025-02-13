package net.ltxprogrammer.changed.mixin.compatibility.TaCZ;

import com.tacz.guns.client.animation.third.InnerThirdPersonManager;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.model.HumanoidModel;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HumanoidAnimator.class, remap = false)
@RequiredMods("tacz")
public abstract class HumanoidAnimatorMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> {
    @Shadow public abstract void applyPropertyModel(HumanoidModel<?> propertyModel);

    @Shadow @Final public M entityModel;

    @Inject(method = "setupAnim", at = @At("RETURN"))
    public void setupAnimEND(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
        HumanoidModel<?> model = this.entityModel;
        this.entityModel.syncPropertyModel(entity);
        InnerThirdPersonManager.setRotationAnglesHead(entity.maybeGetUnderlying(), model.rightArm, model.leftArm, model.body, model.head, limbSwingAmount);
        this.applyPropertyModel(model);
    }
}
