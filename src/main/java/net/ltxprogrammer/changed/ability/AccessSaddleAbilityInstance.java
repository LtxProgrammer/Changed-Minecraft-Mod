package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.extension.curios.CurioSlots;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.util.ItemUtil;
import net.ltxprogrammer.changed.world.inventory.TaurSaddleMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AccessSaddleAbilityInstance extends AbstractAbilityInstance {
    //@NotNull public ItemStack saddle = ItemStack.EMPTY;
    @NotNull public ItemStack chest = ItemStack.EMPTY;

    public AccessSaddleAbilityInstance(AbstractAbility<AccessSaddleAbilityInstance> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    @Override
    public boolean canUse() {
        return ability.canUse(entity);
    }

    @Override
    public boolean canKeepUsing() {
        return ability.canKeepUsing(entity);
    }

    @Override
    public void startUsing() {
        ability.startUsing(entity);
    }

    @Override
    public void tick() {
        if (entity.getContainerMenu() instanceof TaurSaddleMenu taurSaddleMenu)
            if (taurSaddleMenu.tick(this))
                ability.setDirty(this);
    }

    @Override
    public void stopUsing() {
        ability.stopUsing(entity);
    }

    @Override
    public void saveData(CompoundTag tag) {
        super.saveData(tag);
        tag.put("chest", chest.serializeNBT());
    }

    @Override
    public void readData(CompoundTag tag) {
        super.readData(tag);
        if (tag.contains("saddle")) {
            ItemStack stack = ItemStack.of(tag.getCompound("saddle"));
            if (!ItemUtil.tryEquipCurio(entity.getEntity(), stack, CurioSlots.LOWER_BODY))
                if (!entity.addItem(stack))
                    entity.drop(stack, true);
        }
        if (tag.contains("chest"))
            chest = ItemStack.of(tag.getCompound("chest"));
    }

    @Override
    public void onRemove() {
        super.onRemove();
        if (entity.isDeadOrDying() && entity.getLevel().getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM))
            return;

        if (!entity.addItem(chest))
            entity.drop(chest, true);
        chest = ItemStack.EMPTY;
    }
}
