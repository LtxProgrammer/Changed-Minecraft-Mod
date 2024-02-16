package net.ltxprogrammer.changed.mixin.compatibility.NotEnoughAnimations;

import dev.tr7zw.notenoughanimations.NEAnimationsLoader;
import dev.tr7zw.notenoughanimations.access.PlayerData;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = HumanoidAnimator.class, remap = false)
public abstract class HumanoidAnimatorMixin<T extends ChangedEntity> {
    @Shadow public abstract PlayerModel<?> getPropertyModelPlayer(@Nullable EquipmentSlot slot);
    @Shadow public abstract void applyPropertyModel(HumanoidModel<?> model);

    @Inject(method = "setupAnim", at = @At("HEAD"))
    public void setupAnimHEAD(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
        final Player player = entity.getUnderlyingPlayer();
        if (!(player instanceof AbstractClientPlayer clientPlayer)) return;

        final PlayerModel<?> propertyModel = this.getPropertyModelPlayer(null);
        NEAnimationsLoader.INSTANCE.playerTransformer.preUpdate(clientPlayer, (PlayerModel)propertyModel, limbSwing, info);

        this.applyPropertyModel(propertyModel);
    }

    @Inject(method = "setupAnim", at = @At("RETURN"))
    public void setupAnimEND(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
        final Player player = entity.getUnderlyingPlayer();
        if (!(player instanceof AbstractClientPlayer clientPlayer)) return;

        final PlayerModel<?> propertyModel = this.getPropertyModelPlayer(null);
        NEAnimationsLoader.INSTANCE.playerTransformer.updateModel(clientPlayer, (PlayerModel)propertyModel, limbSwing, info);

        if (player instanceof PlayerData data) {
            if (data.getPoseOverwrite() != null) {
                clientPlayer.setPose(data.getPoseOverwrite());
                data.setPoseOverwrite((Pose)null);
            }
        }

        this.applyPropertyModel(propertyModel);
    }
}
