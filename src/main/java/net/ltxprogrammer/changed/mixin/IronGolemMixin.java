package net.ltxprogrammer.changed.mixin;


import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronGolem.class)
public abstract class IronGolemMixin extends AbstractGolem implements NeutralMob {
    protected IronGolemMixin(EntityType<? extends AbstractGolem> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"))
    protected void registerGoals(CallbackInfo info) {
        IronGolem self = (IronGolem)(Object)this;
        self.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(self, Player.class, true, livingEntity -> {
            if (self.isPlayerCreated())
                return false;
            if (livingEntity instanceof Player player)
                return !ProcessTransfur.isPlayerOrganic(player);
            else return false;
        }));
    }
}
