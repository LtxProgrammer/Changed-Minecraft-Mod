package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class AbstractLatexWolf extends LatexEntity {
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
    public MobType getMobType() {
        return MobType.UNDEFINED;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.death"));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_20052_) {
        super.readAdditionalSaveData(p_20052_);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return super.getArmorSlots();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot p_21127_) {
        return super.getItemBySlot(p_21127_);
    }

    @Override
    public void setItemSlot(EquipmentSlot p_21036_, ItemStack p_21037_) {
        super.setItemSlot(p_21036_, p_21037_);
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_20139_) {
        super.addAdditionalSaveData(p_20139_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new FloatGoal(this));
    }
}
