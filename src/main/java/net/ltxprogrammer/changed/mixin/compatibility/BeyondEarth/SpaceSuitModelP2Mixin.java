package net.ltxprogrammer.changed.mixin.compatibility.BeyondEarth;

import com.st0x0ef.beyond_earth.client.renderers.armors.SpaceSuitModel;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModelInterface;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = SpaceSuitModel.SpaceSuitP2.class, remap = false)
public abstract class SpaceSuitModelP2Mixin {
    @Redirect(method = "renderToBuffer", remap = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getModel()Lnet/minecraft/client/model/EntityModel;", remap = true))
    public EntityModel<? extends LivingEntity> getHumanoidModel(LivingEntityRenderer<LivingEntity, ?> instance) {
        var model = instance.getModel();
        if (model instanceof AdvancedHumanoidModelInterface<?, ?> changedModel) {
            return changedModel.getAnimator().getPropertyModel(null);
        }

        return model;
    }
}
