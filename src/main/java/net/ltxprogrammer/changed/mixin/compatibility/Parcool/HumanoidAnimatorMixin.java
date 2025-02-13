package net.ltxprogrammer.changed.mixin.compatibility.Parcool;

import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.config.ParCoolConfig;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
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
@RequiredMods("parcool")
public abstract class HumanoidAnimatorMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> {
    @Shadow public abstract void applyPropertyModel(HumanoidModel<?> propertyModel);

    @Shadow public float forwardOffset;
    @Shadow public float hipOffset;

    @Shadow public abstract float calculateTorsoPositionY();

    @Shadow @Final public M entityModel;

    @Shadow protected abstract void setupAnimStage(HumanoidAnimator.AnimateStage stage, @NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch);

    @Unique
    private PlayerModelTransformer parCool$transformer = null;

    @Inject(method = "setupAnim", at = @At("HEAD"), cancellable = true)
    public void onSetupAnimHead(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
        Player player = entity.getUnderlyingPlayer();
        if (player == null)
            return;
        if (player.isLocalPlayer()
                && Minecraft.getInstance().options.getCameraType().isFirstPerson()
                && !ParCoolConfig.Client.Booleans.EnableFPVAnimation.get()
        ) return;

        parCool$transformer = new PlayerModelTransformer(
                player,
                this.entityModel,
                false,
                ageInTicks,
                limbSwing,
                limbSwingAmount,
                netHeadYaw,
                headPitch
        );
        parCool$transformer.reset();

        // Our equivalent of ^^^
        this.setupAnimStage(HumanoidAnimator.AnimateStage.INIT, entity, 0f, 0f, 0f, 0f, 0f);
        this.setupAnimStage(HumanoidAnimator.AnimateStage.STAND, entity, 0f, 0f, 0f, 0f, 0f);
        this.entityModel.syncPropertyModel(entity);

        Animation animation = Animation.get(player);
        if (animation == null) return;

        boolean shouldCancel = animation.animatePre(player, parCool$transformer);
        parCool$transformer.copyFromBodyToWear();
        if (shouldCancel) {
            parCool$transformer = null;
            info.cancel();

            this.applyPropertyModel(this.entityModel);
        }
    }

    @Inject(method = "setupAnim", at = @At("TAIL"))
    protected void onSetupAnimTail(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
        Player player = entity.getUnderlyingPlayer();
        if (player == null)
            return;
        if (player.isLocalPlayer()
                && Minecraft.getInstance().options.getCameraType().isFirstPerson()
                && !ParCoolConfig.Client.Booleans.EnableFPVAnimation.get()
        ) return;

        Animation animation = Animation.get(player);
        if (animation == null) {
            parCool$transformer = null;
            return;
        }

        if (parCool$transformer != null) {
            this.entityModel.syncPropertyModel(entity);
            animation.animatePost(player, parCool$transformer);
            parCool$transformer.copyFromBodyToWear();
            parCool$transformer = null;

            this.applyPropertyModel(this.entityModel);
        }
    }
}
