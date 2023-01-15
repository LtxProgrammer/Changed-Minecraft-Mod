package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class SimpleAbilityInstance extends AbstractAbilityInstance {
    public SimpleAbilityInstance(AbstractAbility<SimpleAbilityInstance> ability, Player player, LatexVariant<?> variant) {
        super(ability, player, variant);
    }

    @Override
    public boolean canUse() {
        return ability.canUse(player, variant);
    }

    @Override
    public boolean canKeepUsing() {
        return ability.canKeepUsing(player, variant);
    }

    @Override
    public void startUsing() {
        ability.startUsing(player, variant);
    }

    @Override
    public void tick() {
        ability.tick(player, variant);
    }

    @Override
    public void stopUsing() {
        ability.stopUsing(player, variant);
    }

    @Override
    public void onRemove() {
        ability.onRemove(player, variant);
    }

    @Override
    public void saveData(CompoundTag tag) {
        ability.onRemove(player, variant);
    }

    @Override
    public void readData(CompoundTag tag) {
        ability.onRemove(player, variant);
    }
}
