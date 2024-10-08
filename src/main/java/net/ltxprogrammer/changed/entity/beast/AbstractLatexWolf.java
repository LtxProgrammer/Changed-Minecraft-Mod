package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.AttributePresets;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class AbstractLatexWolf extends ChangedEntity {
    public AbstractLatexWolf(EntityType<? extends AbstractLatexWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public int getTicksRequiredToFreeze() { return 240; }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() { return TransfurMode.REPLICATION; }

    @Override
    public void addAdditionalSaveData(CompoundTag p_20139_) {
        super.addAdditionalSaveData(p_20139_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.wolfLike(attributes);
    }
}
