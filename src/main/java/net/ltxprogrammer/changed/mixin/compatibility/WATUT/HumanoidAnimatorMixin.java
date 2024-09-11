package net.ltxprogrammer.changed.mixin.compatibility.WATUT;

import com.corosus.watut.WatutMod;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModelInterface;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AdvancedHumanoidModel.class, remap = false)
public abstract class HumanoidAnimatorMixin<T extends ChangedEntity> extends PlayerModel<T> {
    public HumanoidAnimatorMixin(ModelPart p_170821_, boolean p_170822_) {
        super(p_170821_, p_170822_);
    }

    @Shadow public abstract void syncPropertyModel();

    @Inject(method = "setupAnim", at = @At("TAIL"))
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo ci) {
        Player player = pEntity.getUnderlyingPlayer();
        if (player == null) return;
        if (!(this instanceof AdvancedHumanoidModelInterface<?,?> modelInterface)) return;

        this.syncPropertyModel();
        WatutMod.getPlayerStatusManagerClient().setupRotationsHook(this, player, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        modelInterface.getAnimator().applyPropertyModel(this);
    }
}
