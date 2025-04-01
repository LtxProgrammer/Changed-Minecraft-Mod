package net.ltxprogrammer.changed.mixin.forge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ForgeHooks.class, remap = false)
public abstract class ForgeHooksMixin {
    @WrapOperation(method = "onShieldBlock", at = @At(value = "NEW", target = "net/minecraftforge/event/entity/living/ShieldBlockEvent"))
    private static ShieldBlockEvent correctBlocker(LivingEntity blocker, DamageSource source, float blocked, Operation<ShieldBlockEvent> op) {
        LivingEntity controller = GrabEntityAbility.getControllingEntity(blocker);
        if (controller == blocker)
            return op.call(blocker, source, blocked);
        else {
            ShieldBlockEvent ev = op.call(controller, source, blocked);
            ev.setShieldTakesDamage(false);
            return ev;
        }
    }
}
