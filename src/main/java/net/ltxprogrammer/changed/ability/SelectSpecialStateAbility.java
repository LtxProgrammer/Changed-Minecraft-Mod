package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.beast.SpecialLatex;
import net.ltxprogrammer.changed.world.inventory.SpecialStateRadialMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;

public class SelectSpecialStateAbility extends SimpleAbility {
    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return entity.getChangedEntity() instanceof SpecialLatex;
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);
        entity.openMenu(new SimpleMenuProvider((id, inv, plr) ->
                new SpecialStateRadialMenu(id, inv, null), SpecialStateRadialMenu.CONTAINER_TITLE));
    }

    @Override
    public void saveData(CompoundTag tag, IAbstractChangedEntity entity) {
        super.saveData(tag, entity);
        if (entity.getChangedEntity() instanceof SpecialLatex specialLatex)
            tag.putString("State", specialLatex.wantedState);
    }

    @Override
    public void readData(CompoundTag tag, IAbstractChangedEntity entity) {
        super.readData(tag, entity);
        if (tag.contains("State") && entity.getChangedEntity() instanceof SpecialLatex specialLatex)
            specialLatex.wantedState = tag.getString("State");
    }
}
