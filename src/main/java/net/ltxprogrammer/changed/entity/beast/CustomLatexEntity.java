package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.EntityShape;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CustomLatexEntity extends ChangedEntity {
    public enum TorsoType {
        GENERIC,
        CHISELED,
        FEMALE,
        HEAVY;

        public TorsoType cycle() {
            return values()[this.ordinal() + 1 >= values().length ? 0 : this.ordinal() + 1];
        }

        public static TorsoType fromFlags(int flags) {
            var type = (flags) & 0xf;
            if (type >= TorsoType.values().length) return TorsoType.GENERIC;
            else return TorsoType.values()[type];
        }

        public int setFlags(int flags) {
            flags ^= (flags) & 0x0000000f;
            flags |= ordinal();
            return flags;
        }
    }

    public enum HairType {
        BALD,
        SHORT,
        LONG;

        public HairType cycle() {
            return values()[this.ordinal() + 1 >= values().length ? 0 : this.ordinal() + 1];
        }

        public static HairType fromFlags(int flags) {
            var type = (flags >> 4) & 0xf;
            if (type >= HairType.values().length) return HairType.BALD;
            else return HairType.values()[type];
        }

        public int setFlags(int flags) {
            flags ^= (flags) & 0x000000f0;
            flags |= ordinal() << 4;
            return flags;
        }
    }

    public enum EarType {
        WOLF,
        CAT,
        DRAGON,
        SHARK;

        public EarType cycle() {
            return values()[this.ordinal() + 1 >= values().length ? 0 : this.ordinal() + 1];
        }

        public static EarType fromFlags(int flags) {
            var type = (flags >> 8) & 0xf;
            if (type >= EarType.values().length) return EarType.WOLF;
            else return EarType.values()[type];
        }

        public int setFlags(int flags) {
            flags ^= (flags) & 0x00000f00;
            flags |= ordinal() << 8;
            return flags;
        }
    }

    public enum TailType {
        WOLF,
        CAT,
        DRAGON,
        SHARK;

        public TailType cycle() {
            return values()[this.ordinal() + 1 >= values().length ? 0 : this.ordinal() + 1];
        }

        public static TailType fromFlags(int flags) {
            var type = (flags >> 12) & 0xf;
            if (type >= TailType.values().length) return TailType.WOLF;
            else return TailType.values()[type];
        }

        public int setFlags(int flags) {
            flags ^= (flags) & 0x0000f000;
            flags |= ordinal() << 12;
            return flags;
        }
    }

    public enum LegType {
        BIPEDAL,
        CENTAUR,
        MERMAID;

        public LegType cycle() {
            return values()[this.ordinal() + 1 >= values().length ? 0 : this.ordinal() + 1];
        }

        public static LegType fromFlags(int flags) {
            var type = (flags >> 16) & 0xf;
            if (type >= LegType.values().length) return LegType.BIPEDAL;
            else return LegType.values()[type];
        }

        public int setFlags(int flags) {
            flags ^= (flags) & 0x000f0000;
            flags |= ordinal() << 16;
            return flags;
        }
    }

    public enum ArmType {
        GENERIC,
        WYVERN,
        SHARK;

        public ArmType cycle() {
            return values()[this.ordinal() + 1 >= values().length ? 0 : this.ordinal() + 1];
        }

        public static ArmType fromFlags(int flags) {
            var type = (flags >> 20) & 0xf;
            if (type >= ArmType.values().length) return ArmType.GENERIC;
            else return ArmType.values()[type];
        }

        public int setFlags(int flags) {
            flags ^= (flags) & 0x00f00000;
            flags |= ordinal() << 20;
            return flags;
        }
    }

    public enum ScaleType {
        NORMAL(1.0F, 1.0F, 1.0F),
        BUFF(1.125F, 1.0F, 1.15F),
        SMALL(0.85F, 1.0F, 0.85F);

        public final float bodyScale;
        public final float headScale;
        public final float bbScale;

        ScaleType(float bodyScale, float headScale, float bbScale) {
            this.bodyScale = bodyScale;
            this.headScale = headScale;
            this.bbScale = bbScale;
        }

        public ScaleType cycle() {
            return values()[this.ordinal() + 1 >= values().length ? 0 : this.ordinal() + 1];
        }

        public static ScaleType fromFlags(int flags) {
            var type = (flags >> 24) & 0xf;
            if (type >= TailType.values().length) return ScaleType.NORMAL;
            else return ScaleType.values()[type];
        }

        public int setFlags(int flags) {
            flags ^= (flags) & 0x0f000000;
            flags |= ordinal() << 24;
            return flags;
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
        return TorsoType.fromFlags(this.entityData.get(DATA_FORM_FLAGS));
    }

    public HairType getHairType() {
        return HairType.fromFlags(this.entityData.get(DATA_FORM_FLAGS));
    }

    public EarType getEarType() {
        return EarType.fromFlags(this.entityData.get(DATA_FORM_FLAGS));
    }

    public TailType getTailType() {
        return TailType.fromFlags(this.entityData.get(DATA_FORM_FLAGS));
    }

    public LegType getLegType() {
        return LegType.fromFlags(this.entityData.get(DATA_FORM_FLAGS));
    }

    public ArmType getArmType() {
        return ArmType.fromFlags(this.entityData.get(DATA_FORM_FLAGS));
    }

    public ScaleType getScaleType() {
        return ScaleType.fromFlags(this.entityData.get(DATA_FORM_FLAGS));
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

    public void setScaleType(ScaleType type) {
        int flags = this.entityData.get(DATA_FORM_FLAGS);
        flags ^= (flags) & 0x0f000000;
        flags &= type.ordinal() << 24;
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

    public void cycleScaleType() {
        setScaleType(getScaleType().cycle());
    }

    public CustomLatexEntity(EntityType<? extends ChangedEntity> type, Level level) {
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
        if (tag.contains("RawFormFlags"))
            this.setRawFormFlags(tag.getInt("RawFormFlags"));
    }

    @Override
    public CompoundTag savePlayerVariantData() {
        final var tag = super.savePlayerVariantData();
        tag.putInt("RawFormFlags", this.getRawFormFlags());
        return tag;
    }

    @Override
    public void readPlayerVariantData(CompoundTag tag) {
        super.readPlayerVariantData(tag);
        if (tag.contains("RawFormFlags"))
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
    public float getScale() {
        return getScaleType().bbScale * super.getScale();
    }

    @Override
    public @NotNull EntityShape getEntityShape() {
        return switch (getLegType()) {
            case BIPEDAL -> EntityShape.ANTHRO;
            case CENTAUR -> EntityShape.TAUR;
            case MERMAID -> EntityShape.MER;
        };
    }
}
