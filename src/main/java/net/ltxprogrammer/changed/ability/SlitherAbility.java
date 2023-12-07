package net.ltxprogrammer.changed.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Pose;

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
        entity.getLatexEntity().overridePose = Pose.SWIMMING;
        setDirty(entity);
    }

    @Override
    public void stopUsing(IAbstractLatex entity) {
        super.stopUsing(entity);
        entity.getLatexEntity().overridePose = null;
        setDirty(entity);
    }

    @Override
    public void saveData(CompoundTag tag, IAbstractLatex entity) {
        super.saveData(tag, entity);
        if (entity.getLatexEntity().overridePose != null)
            tag.putString("overridePose", entity.getLatexEntity().overridePose.name());
    }

    @Override
    public void readData(CompoundTag tag, IAbstractLatex entity) {
        super.readData(tag, entity);
        if (tag.contains("overridePose"))
            entity.getLatexEntity().overridePose = Pose.valueOf(tag.getString("overridePose"));
    }

    @Override
    public UseType getUseType(IAbstractLatex entity) {
        return UseType.HOLD;
    }
}
