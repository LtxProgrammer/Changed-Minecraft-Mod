package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class SlitherAbility extends SimpleAbility {
    @Override
    public boolean canUse(IAbstractLatex entity) {
        return true;
    }

    @Override
    public boolean canKeepUsing(IAbstractLatex entity) {
        return !entity.isCrouching() && !entity.isSleeping();
    }

    @Override
    public void startUsing(IAbstractLatex entity) {
        super.startUsing(entity);
        entity.getLatexEntity().overrideVisuallySwimming = true;
        setDirty(entity);
    }

    @Override
    public void stopUsing(IAbstractLatex entity) {
        super.stopUsing(entity);
        entity.getLatexEntity().overrideVisuallySwimming = false;
        setDirty(entity);
    }

    @Override
    public void saveData(CompoundTag tag, IAbstractLatex entity) {
        super.saveData(tag, entity);
        tag.putBoolean("overrideSwimming", entity.getLatexEntity().overrideVisuallySwimming);
    }

    @Override
    public void readData(CompoundTag tag, IAbstractLatex entity) {
        super.readData(tag, entity);
        entity.getLatexEntity().overrideVisuallySwimming = tag.getBoolean("overrideSwimming");
    }

    @Override
    public UseType getUseType(IAbstractLatex entity) {
        return UseType.HOLD;
    }
}
