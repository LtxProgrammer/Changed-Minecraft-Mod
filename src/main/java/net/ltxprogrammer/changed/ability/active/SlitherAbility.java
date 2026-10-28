package net.ltxprogrammer.changed.ability.active;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.init.ChangedPoseExtensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Pose;

import java.util.Collection;
import java.util.Collections;

public class SlitherAbility extends SimpleAbility {
    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return entity.getChangedEntity().canEnterPose(ChangedPoseExtensions.SLITHERING.get());
    }

    @Override
    public boolean canKeepUsing(IAbstractChangedEntity entity) {
        return !entity.isCrouching() && !entity.isSleeping();
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);
        entity.getChangedEntity().setPoseExtension(ChangedPoseExtensions.SLITHERING.get());
        entity.getEntity().refreshDimensions();
        setDirty(entity);
    }

    @Override
    public void stopUsing(IAbstractChangedEntity entity) {
        super.stopUsing(entity);
        entity.getChangedEntity().clearPoseExtension(ChangedPoseExtensions.SLITHERING.get());
        entity.getEntity().refreshDimensions();
        setDirty(entity);
    }

    @Override
    public void saveData(CompoundTag tag, IAbstractChangedEntity entity) {
        super.saveData(tag, entity);
        if (entity.getChangedEntity().getPoseExtension() == ChangedPoseExtensions.SLITHERING.get())
            tag.putBoolean("slithering", true);
    }

    @Override
    public void readData(CompoundTag tag, IAbstractChangedEntity entity) {
        super.readData(tag, entity);
        if (tag.getBoolean("slithering")) {
            entity.getChangedEntity().setPoseExtension(ChangedPoseExtensions.SLITHERING.get());
            entity.getEntity().refreshDimensions();
        }
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.HOLD;
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.slither.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }
}
