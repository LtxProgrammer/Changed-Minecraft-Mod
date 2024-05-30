package net.ltxprogrammer.changed.mixin.compatibility.BeyondEarth;

import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModelInterface;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.mrscauthd.beyond_earth.entities.renderer.spacesuit.SpaceSuitModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = SpaceSuitModel.SPACE_SUIT_P1.class, remap = false)
public abstract class SpaceSuitModelP1Mixin {
    @Redirect(method = "renderToBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getModel()Lnet/minecraft/client/model/EntityModel;"))
    public EntityModel<? extends LivingEntity> getHumanoidModel(LivingEntityRenderer<LivingEntity, ?> instance) {
        var model = instance.getModel();
        if (model instanceof AdvancedHumanoidModelInterface<?, ?> changedModel) {
            return changedModel.getAnimator().getPropertyModel(null);
        }

        return model;
    }
}
