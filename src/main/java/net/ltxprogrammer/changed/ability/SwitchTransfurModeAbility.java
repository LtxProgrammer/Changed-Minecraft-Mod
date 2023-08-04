package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.TransfurMode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class SwitchTransfurModeAbility extends SimpleAbility {
    @Override
    public boolean canUse(IAbstractLatex entity) {
        return entity.getTransfurMode() != TransfurMode.NONE;
    }

    @Override
    public boolean canKeepUsing(IAbstractLatex entity) { return false; }

    @Override
    public void startUsing(IAbstractLatex entity) {
        if (entity.getLevel().isClientSide)
            return;

        if (entity.getTransfurMode() == TransfurMode.NONE)
            return;

        if (entity.getTransfurMode() == TransfurMode.ABSORPTION)
            entity.setTransfurMode(TransfurMode.REPLICATION);
        else
            entity.setTransfurMode(TransfurMode.ABSORPTION);

        setDirty(entity);
        entity.displayClientMessage(new TranslatableComponent("ability.changed.switch_transfur_mode.select", entity.getTransfurMode().toString()), true);
    }

    @Override
    public void saveData(CompoundTag tag, IAbstractLatex entity) {
        super.saveData(tag, entity);
        tag.putString("TransfurMode", entity.getTransfurMode().toString());
    }

    @Override
    public void readData(CompoundTag tag, IAbstractLatex entity) {
        super.readData(tag, entity);
        entity.setTransfurMode(TransfurMode.valueOf(tag.getString("TransfurMode")));
    }

    @Override
    public void tick(IAbstractLatex entity) {}

    @Override
    public void stopUsing(IAbstractLatex entity) {}

    @Override
    public ResourceLocation getTexture(IAbstractLatex entity) {
        if (entity.getTransfurMode() == TransfurMode.NONE)
            return new ResourceLocation(getRegistryName().getNamespace(), "textures/abilities/" + getRegistryName().getPath() + "_replication.png");

        return new ResourceLocation(getRegistryName().getNamespace(), "textures/abilities/" + getRegistryName().getPath() + "_" +
                entity.getTransfurMode().toString().toLowerCase() + ".png");
    }

    @Override
    public int getCoolDown(IAbstractLatex entity) {
        return 10;
    }
}
