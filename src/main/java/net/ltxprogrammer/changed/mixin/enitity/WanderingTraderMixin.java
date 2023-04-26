package net.ltxprogrammer.changed.mixin.enitity;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderMixin extends AbstractVillager {
    public WanderingTraderMixin(EntityType<? extends AbstractVillager> p_35267_, Level p_35268_) {
        super(p_35267_, p_35268_);
    }

    @Inject(method = "registerGoals", at = @At("RETURN"))
    public void registerGoals(CallbackInfo callback) {
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, LatexEntity.class, latex -> !latex.getType().is(ChangedTags.EntityTypes.ORGANIC_LATEX),
                8.0F, 0.5D, 0.5D, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test));
    }
}
