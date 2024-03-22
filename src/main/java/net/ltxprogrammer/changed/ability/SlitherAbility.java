package net.ltxprogrammer.changed.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Pose;

public class SlitherAbility extends SimpleAbility {
    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return true;
    }

    @Override
    public boolean canKeepUsing(IAbstractChangedEntity entity) {
        return !entity.isCrouching() && !entity.isSleeping();
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);
        entity.getChangedEntity().overridePose = Pose.SWIMMING;
        setDirty(entity);
    }

    @Override
    public void stopUsing(IAbstractChangedEntity entity) {
        super.stopUsing(entity);
        entity.getChangedEntity().overridePose = null;
        setDirty(entity);
    }

    @Override
    public void saveData(CompoundTag tag, IAbstractChangedEntity entity) {
        super.saveData(tag, entity);
        if (entity.getChangedEntity().overridePose != null)
            tag.putString("overridePose", entity.getChangedEntity().overridePose.name());
    }

    @Override
    public void readData(CompoundTag tag, IAbstractChangedEntity entity) {
        super.readData(tag, entity);
        if (tag.contains("overridePose"))
            entity.getChangedEntity().overridePose = Pose.valueOf(tag.getString("overridePose"));
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.HOLD;
    }
}
