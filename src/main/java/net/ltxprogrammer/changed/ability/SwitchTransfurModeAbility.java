package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.TransfurMode;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class SwitchTransfurModeAbility extends SimpleAbility {
    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return entity.getTransfurMode() != TransfurMode.NONE;
    }

    @Override
    public boolean canKeepUsing(IAbstractChangedEntity entity) { return false; }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        if (entity.getLevel().isClientSide)
            return;

        if (entity.getTransfurMode() == TransfurMode.NONE)
            return;

        if (entity.getTransfurMode() == TransfurMode.ABSORPTION)
            entity.setTransfurMode(TransfurMode.REPLICATION);
        else
            entity.setTransfurMode(TransfurMode.ABSORPTION);

        setDirty(entity);
        entity.displayClientMessage(new TranslatableComponent("ability.changed.switch_transfur_mode.select", new TranslatableComponent("ability.changed.switch_transfur_mode." + entity.getTransfurMode().toString().toLowerCase())), true);
    }

    @Override
    public void saveData(CompoundTag tag, IAbstractChangedEntity entity) {
        super.saveData(tag, entity);
        tag.putString("TransfurMode", entity.getTransfurMode().toString());
    }

    @Override
    public void readData(CompoundTag tag, IAbstractChangedEntity entity) {
        super.readData(tag, entity);
        entity.setTransfurMode(TransfurMode.valueOf(tag.getString("TransfurMode")));
    }

    @Override
    public void tick(IAbstractChangedEntity entity) {}

    @Override
    public void stopUsing(IAbstractChangedEntity entity) {}

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 10;
    }

    private static final Map<TransfurMode, Collection<Component>> DESCRIPTION = Util.make(new EnumMap<>(TransfurMode.class), map -> {
        TranslatableComponent baseDesc = new TranslatableComponent("ability.changed.switch_transfur_mode.desc");

        map.put(TransfurMode.NONE, List.of(baseDesc, new TranslatableComponent("ability.changed.switch_transfur_mode.none.desc")));
        map.put(TransfurMode.ABSORPTION, List.of(baseDesc, new TranslatableComponent("ability.changed.switch_transfur_mode.absorption.desc")));
        map.put(TransfurMode.REPLICATION, List.of(baseDesc, new TranslatableComponent("ability.changed.switch_transfur_mode.replication.desc")));
    });

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION.get(entity.getTransfurMode());
    }
}