package net.ltxprogrammer.changed.entity.beast;

import com.google.common.collect.ImmutableMap;
import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.variant.EntityShape;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class CustomLatexEntity extends ChangedEntity implements LatexTaur<CustomLatexEntity>, ModifiableEntity {
    public enum TorsoType implements StringRepresentable {
        GENERIC("generic"),
        CHISELED("chiseled"),
        FEMALE("female"),
        HEAVY("heavy");

        public final String serialName;

        TorsoType(String serialName) {
            this.serialName = serialName;
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

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

    public enum HairType implements StringRepresentable {
        BALD("bald"),
        SHORT("short"),
        LONG("long");

        public final String serialName;

        HairType(String serialName) {
            this.serialName = serialName;
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

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

    public enum EarType implements StringRepresentable {
        WOLF("wolf"),
        CAT("cat"),
        DRAGON("dragon"),
        SHARK("shark");

        public final String serialName;

        EarType(String serialName) {
            this.serialName = serialName;
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

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

    public enum TailType implements StringRepresentable {
        WOLF("wolf"),
        CAT("cat"),
        DRAGON("dragon"),
        SHARK("shark");

        public final String serialName;

        TailType(String serialName) {
            this.serialName = serialName;
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

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

    public enum LegType implements StringRepresentable {
        BIPEDAL("bipedal"),
        CENTAUR("centaur"),
        MERMAID("mermaid");

        public final String serialName;

        LegType(String serialName) {
            this.serialName = serialName;
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

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

    public enum ArmType implements StringRepresentable {
        GENERIC("generic"),
        WYVERN("wyvern"),
        SHARK("shark");

        public final String serialName;

        ArmType(String serialName) {
            this.serialName = serialName;
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

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

    public enum ScaleType implements StringRepresentable {
        NORMAL("normal", 1.0F, 1.0F, 1.0F),
        BUFF("buff", 1.125F, 1.0F, 1.15F),
        SMALL("small", 0.85F, 1.0F, 0.85F);

        public final String serialName;
        public final float bodyScale;
        public final float headScale;
        public final float bbScale;

        ScaleType(String serialName, float bodyScale, float headScale, float bbScale) {
            this.serialName = serialName;
            this.bodyScale = bodyScale;
            this.headScale = headScale;
            this.bbScale = bbScale;
        }

        @Override
        public String getSerializedName() {
            return serialName;
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
    private int formFlagsLast = -1;
    private final Map<String, ModificationVector> modificationVectors;

    public void updateShape() {
        this.setAttributes(getAttributes());
        this.refreshDimensions();

        ProcessTransfur.ifPlayerTransfurred(this.getUnderlyingPlayer(), variant -> {
            variant.breatheMode = switch (this.getTailType()) {
                case SHARK -> TransfurVariant.BreatheMode.ANY;
                default -> TransfurVariant.BreatheMode.NORMAL;
            };

            if (this.getLegType() == LegType.MERMAID)
                variant.breatheMode = TransfurVariant.BreatheMode.ANY;

            variant.refreshAttributes();
        });

        AccessoryEntities.INSTANCE.forceReloadAccessories(this.maybeGetUnderlying());

        this.formFlagsLast = this.getRawFormFlags();
    }

    public EntityType<?> getEntityTypeForAccessories() {
        switch (getLegType()) {
            case CENTAUR -> {
                return ChangedEntities.WHITE_LATEX_CENTAUR.get();
            }
            case MERMAID -> {
                return ChangedEntities.LATEX_SIREN.get();
            }
            default -> {
                return ChangedEntities.WHITE_WOLF_MALE.get();
            }
        }
    }

    protected EntityDimensions getDimensionsForForm() {
        return switch (this.getLegType()) {
            case MERMAID -> EntityDimensions.scalable(0.7F, 1.58625F);
            case CENTAUR -> EntityDimensions.scalable(1.1F, 2.0F);
            default -> EntityDimensions.scalable(0.7F, 1.93F);
        };
    }

    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions core = getDimensionsForForm();

        if (WhiteLatexTransportInterface.isEntityInWhiteLatex(this.maybeGetUnderlying()))
            return EntityDimensions.scalable(core.width, core.width);

        return (switch (Objects.requireNonNullElse(overridePose, pose)) {
            case STANDING -> core;
            case SLEEPING -> SLEEPING_DIMENSIONS;
            case FALL_FLYING, SWIMMING, SPIN_ATTACK -> EntityDimensions.scalable(core.width, core.width);
            case CROUCHING -> EntityDimensions.scalable(core.width, core.height - 0.3f);
            case DYING -> EntityDimensions.fixed(0.2f, 0.2f);
            default -> core;
        }).scale(getBasicPlayerInfo().getSize(this) * this.getScale());
    }

    public int getRawFormFlags() {
        return this.entityData.get(DATA_FORM_FLAGS);
    }

    public void setRawFormFlags(int flags) {
        this.entityData.set(DATA_FORM_FLAGS, flags);
        this.updateShape();
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
        flags |= type.ordinal();
        this.entityData.set(DATA_FORM_FLAGS, flags);
        this.updateShape();
    }

    public void setHairType(HairType type) {
        int flags = this.entityData.get(DATA_FORM_FLAGS);
        flags ^= (flags) & 0x000000f0;
        flags |= type.ordinal() << 4;
        this.entityData.set(DATA_FORM_FLAGS, flags);
        this.updateShape();
    }

    public void setEarType(EarType type) {
        int flags = this.entityData.get(DATA_FORM_FLAGS);
        flags ^= (flags) & 0x00000f00;
        flags |= type.ordinal() << 8;
        this.entityData.set(DATA_FORM_FLAGS, flags);
        this.updateShape();
    }

    public void setTailType(TailType type) {
        int flags = this.entityData.get(DATA_FORM_FLAGS);
        flags ^= (flags) & 0x0000f000;
        flags |= type.ordinal() << 12;
        this.entityData.set(DATA_FORM_FLAGS, flags);
        this.updateShape();
    }

    public void setLegType(LegType type) {
        int flags = this.entityData.get(DATA_FORM_FLAGS);
        flags ^= (flags) & 0x000f0000;
        flags |= type.ordinal() << 16;
        this.entityData.set(DATA_FORM_FLAGS, flags);
        this.updateShape();
    }

    public void setArmType(ArmType type) {
        int flags = this.entityData.get(DATA_FORM_FLAGS);
        flags ^= (flags) & 0x00f00000;
        flags |= type.ordinal() << 20;
        this.entityData.set(DATA_FORM_FLAGS, flags);
        this.updateShape();
    }

    public void setScaleType(ScaleType type) {
        int flags = this.entityData.get(DATA_FORM_FLAGS);
        flags ^= (flags) & 0x0f000000;
        flags |= type.ordinal() << 24;
        this.entityData.set(DATA_FORM_FLAGS, flags);
        this.updateShape();
    }

    protected void buildModificationVectors(ImmutableMap.Builder<String, ModificationVector> builder) {
        builder.put("torsoType", ModificationVector.simpleEnum(TorsoType.class, this::getTorsoType, this::setTorsoType, "changed.stasis.modify.torso"));
        builder.put("hairType", ModificationVector.simpleEnum(HairType.class, this::getHairType, this::setHairType, "changed.stasis.modify.hair"));
        builder.put("earType", ModificationVector.simpleEnum(EarType.class, this::getEarType, this::setEarType, "changed.stasis.modify.ears"));
        builder.put("tailType", ModificationVector.simpleEnum(TailType.class, this::getTailType, this::setTailType, "changed.stasis.modify.tail"));
        builder.put("legType", ModificationVector.simpleEnum(LegType.class, this::getLegType, this::setLegType, "changed.stasis.modify.legs"));
        builder.put("armType", ModificationVector.simpleEnum(ArmType.class, this::getArmType, this::setArmType, "changed.stasis.modify.arms"));
        builder.put("scaleType", ModificationVector.simpleEnum(ScaleType.class, this::getScaleType, this::setScaleType, "changed.stasis.modify.scale"));
    }

    @Override
    public Map<String, ModificationVector> getModificationVectors() {
        return modificationVectors;
    }

    public CustomLatexEntity(EntityType<? extends ChangedEntity> type, Level level) {
        super(type, level);

        var builder = ImmutableMap.<String, ModificationVector>builder();
        this.buildModificationVectors(builder);
        this.modificationVectors = builder.build();
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

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);

        attributes.getInstance(ForgeMod.STEP_HEIGHT_ADDITION.get()).setBaseValue(computeStepHeightOffset(0.7));

        switch (getLegType()) {
            case CENTAUR -> {
                attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.2);
                attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.9);
                attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(30);
                attributes.getInstance(ForgeMod.STEP_HEIGHT_ADDITION.get()).setBaseValue(computeStepHeightOffset(1.1));
                attributes.getInstance(ChangedAttributes.JUMP_STRENGTH.get()).setBaseValue(1.25);
            }
            case MERMAID -> {
                attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.34);
                attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(5.58);
                attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(28);
            }
            default -> {
                switch (getTailType()) {
                    case WOLF -> AttributePresets.wolfLike(attributes);
                    case CAT -> AttributePresets.catLike(attributes);
                    case DRAGON -> AttributePresets.dragonLike(attributes);
                    case SHARK -> AttributePresets.sharkLike(attributes);
                    default -> AttributePresets.playerLike(attributes);
                }
            }
        }
    }

    @Override
    public void variantTick(Level level) {
        super.variantTick(level);

        if (this.getRawFormFlags() != formFlagsLast) {
            this.updateShape();
        }
    }
    @Override
    public void equipSaddle(@Nullable SoundSource source) {
        this.equipSaddle(this, source);
    }

    @Override
    public boolean isSaddled() {
        return this.isSaddled(this);
    }

    protected void doPlayerRide(Player player) {
        this.doPlayerRide(this, player);
    }

    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() + getTorsoYOffset(this) - (2.0 / 16.0);
    }

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (isSaddled()) {
            this.doPlayerRide(player);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return InteractionResult.PASS;
    }
}
