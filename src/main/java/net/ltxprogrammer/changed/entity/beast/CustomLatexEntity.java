package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class CustomLatexEntity extends LatexEntity {
    public enum TorsoType {
        GENERIC,
        CHISELED,
        FEMALE;

        TorsoType cycle() {
            return values()[this.ordinal() + 1 >= values().length ? 0 : this.ordinal() + 1];
        }
    }

    public enum HairType {
        BALD,
        SHORT,
        LONG;

        HairType cycle() {
            return values()[this.ordinal() + 1 >= values().length ? 0 : this.ordinal() + 1];
        }
    }

    public enum EarType {
        WOLF,
        CAT,
        DRAGON,
        SHARK;

        EarType cycle() {
            return values()[this.ordinal() + 1 >= values().length ? 0 : this.ordinal() + 1];
        }
    }

    public enum TailType {
        WOLF,
        CAT,
        DRAGON,
        SHARK;

        TailType cycle() {
            return values()[this.ordinal() + 1 >= values().length ? 0 : this.ordinal() + 1];
        }
    }

    public enum LegType {
        BIPEDAL,
        SNAKE;

        LegType cycle() {
            return values()[this.ordinal() + 1 >= values().length ? 0 : this.ordinal() + 1];
        }
    }

    public enum ArmType {
        GENERIC,
        DRAGON,
        SHARK;

        ArmType cycle() {
            return values()[this.ordinal() + 1 >= values().length ? 0 : this.ordinal() + 1];
        }
    }

    // Integers have 32 bits for flags
    // 0000 0000 0000 0000 0000 0000 0000 0000
    // 8 Fields can be serialized, each having 16 different possible values
    public static final EntityDataAccessor<Integer> DATA_FORM_FLAGS = SynchedEntityData.defineId(CustomLatexEntity.class, EntityDataSerializers.INT);

    public int getRawFormFlags() {
        return this.entityData.get(DATA_FORM_FLAGS);
    }

    public void setRawFormFlags(int flags) {
        this.entityData.set(DATA_FORM_FLAGS, flags);
    }

    public TorsoType getTorsoType() {
        var type = this.entityData.get(DATA_FORM_FLAGS) & 0xf;
        if (type >= TorsoType.values().length) return TorsoType.GENERIC;
        else return TorsoType.values()[type];
    }

    public HairType getHairType() {
        var type = (this.entityData.get(DATA_FORM_FLAGS) >> 4) & 0xf;
        if (type >= HairType.values().length) return HairType.BALD;
        else return HairType.values()[type];
    }

    public EarType getEarType() {
        var type = (this.entityData.get(DATA_FORM_FLAGS) >> 8) & 0xf;
        if (type >= EarType.values().length) return EarType.WOLF;
        else return EarType.values()[type];
    }

    public TailType getTailType() {
        var type = (this.entityData.get(DATA_FORM_FLAGS) >> 12) & 0xf;
        if (type >= TailType.values().length) return TailType.WOLF;
        else return TailType.values()[type];
    }

    public LegType getLegType() {
        var type = (this.entityData.get(DATA_FORM_FLAGS) >> 16) & 0xf;
        if (type >= TailType.values().length) return LegType.BIPEDAL;
        else return LegType.values()[type];
    }

    public ArmType getArmType() {
        var type = (this.entityData.get(DATA_FORM_FLAGS) >> 20) & 0xf;
        if (type >= TailType.values().length) return ArmType.GENERIC;
        else return ArmType.values()[type];
    }

    public void setTorsoType(TorsoType type) {
        int flags = this.entityData.get(DATA_FORM_FLAGS);
        flags ^= (flags) & 0x0000000f;
        flags &= type.ordinal();
        this.entityData.set(DATA_FORM_FLAGS, flags);
    }

    public void setHairType(HairType type) {
        int flags = this.entityData.get(DATA_FORM_FLAGS);
        flags ^= (flags) & 0x000000f0;
        flags &= type.ordinal() << 4;
        this.entityData.set(DATA_FORM_FLAGS, flags);
    }

    public void setEarType(EarType type) {
        int flags = this.entityData.get(DATA_FORM_FLAGS);
        flags ^= (flags) & 0x00000f00;
        flags &= type.ordinal() << 8;
        this.entityData.set(DATA_FORM_FLAGS, flags);
    }

    public void setTailType(TailType type) {
        int flags = this.entityData.get(DATA_FORM_FLAGS);
        flags ^= (flags) & 0x0000f000;
        flags &= type.ordinal() << 12;
        this.entityData.set(DATA_FORM_FLAGS, flags);
    }

    public void setLegType(LegType type) {
        int flags = this.entityData.get(DATA_FORM_FLAGS);
        flags ^= (flags) & 0x000f0000;
        flags &= type.ordinal() << 16;
        this.entityData.set(DATA_FORM_FLAGS, flags);
    }

    public void setArmType(ArmType type) {
        int flags = this.entityData.get(DATA_FORM_FLAGS);
        flags ^= (flags) & 0x00f00000;
        flags &= type.ordinal() << 20;
        this.entityData.set(DATA_FORM_FLAGS, flags);
    }

    public void cycleTorsoType() {
        setTorsoType(getTorsoType().cycle());
    }

    public void cycleHairType() {
        setHairType(getHairType().cycle());
    }

    public void cycleEarType() {
        setEarType(getEarType().cycle());
    }

    public void cycleTailType() {
        setTailType(getTailType().cycle());
    }

    public void cycleLegType() {
        setLegType(getLegType().cycle());
    }

    public void cycleArmType() {
        setArmType(getArmType().cycle());
    }

    public CustomLatexEntity(EntityType<? extends LatexEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FORM_FLAGS, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("RawFormFlags", this.getRawFormFlags());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setRawFormFlags(tag.getInt("RawFormFlags"));
    }

    @Override
    public Color3 getHairColor(int layer) {
        return null;
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public Color3 getDripColor() {
        return Color3.WHITE;
    }
}
