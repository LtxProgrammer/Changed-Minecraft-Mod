package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.client.renderer.layers.DarkLatexMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurProgressLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidMobRenderer.class)
public abstract class HumanoidMobRendererMixin<T extends Mob, M extends HumanoidModel<T>> extends MobRenderer<T, M> {
    public HumanoidMobRendererMixin(EntityRendererProvider.Context p_174169_, M p_174170_, float p_174171_) {
        super(p_174169_, p_174170_, p_174171_);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lnet/minecraft/client/model/HumanoidModel;FFFF)V", at = @At("RETURN"))
    public void PlayerRenderer(EntityRendererProvider.Context context, M p_174174_, float p_174175_, float p_174176_, float p_174177_, float p_174178_, CallbackInfo callback) {
        this.addLayer(new DarkLatexMaskLayer<>(this, context.getModelSet()));
    }
}
