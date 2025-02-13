package net.ltxprogrammer.changed.mixin.compatibility.Parcool;

import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.common.capability.Animation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.client.PoseStackExtender;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AdvancedHumanoidRenderer.class, remap = false)
@RequiredMods("parcool")
public abstract class AdvancedHumanoidRendererMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>, A extends LatexHumanoidArmorModel<T, ?>> extends MobRenderer<T, M> {
    private AdvancedHumanoidRendererMixin(EntityRendererProvider.Context p_174304_, M p_174305_, float p_174306_) {
        super(p_174304_, p_174305_, p_174306_);
    }

    @Unique
    private PlayerModelRotator parCool$rotator = null;

    @Inject(method = "setupRotations(Lnet/ltxprogrammer/changed/entity/ChangedEntity;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At("TAIL"))
    protected void onSetupRotationsTail(ChangedEntity entity, PoseStack stack, float xRot, float yRot, float zRot, CallbackInfo ci) {
        var player = entity.getUnderlyingPlayer();
        if (!(player instanceof AbstractClientPlayer clientPlayer))
            return;

        Animation animation = Animation.get(clientPlayer);
        if (animation == null) {
            return;
        }
        if (parCool$rotator != null) {
            animation.rotatePost(clientPlayer, parCool$rotator);
            parCool$rotator = null;
        }
    }

    @Inject(method = "setupRotations(Lnet/ltxprogrammer/changed/entity/ChangedEntity;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V", at = @At("HEAD"), cancellable = true)
    protected void onSetupRotationsHead(ChangedEntity entity, PoseStack stack, float xRot, float yRot, float zRot, CallbackInfo ci) {
        var player = entity.getUnderlyingPlayer();
        if (!(player instanceof AbstractClientPlayer clientPlayer))
            return;

        Animation animation = Animation.get(clientPlayer);
        if (animation == null) {
            return;
        }
        parCool$rotator = new PlayerModelRotator(stack, player, Minecraft.getInstance().getFrameTime(), xRot, yRot, zRot);
        if (animation.rotatePre(clientPlayer, parCool$rotator)) {
            parCool$rotator = null;
            ci.cancel();
        }
    }
}
