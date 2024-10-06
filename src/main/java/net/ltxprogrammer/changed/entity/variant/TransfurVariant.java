package net.ltxprogrammer.changed.entity.variant;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.beast.*;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransfurVariant<T extends ChangedEntity> extends ForgeRegistryEntry<TransfurVariant<?>> {
    public static final String NBT_PLAYER_ID = "changed:player_id";
    public static final ResourceLocation SPECIAL_LATEX = Changed.modResource("form_special");
    private static final List<ResourceLocation> SPECIAL_LATEX_FORMS = new ArrayList<>();

    public static Stream<TransfurVariant<?>> getPublicTransfurVariants() {
        return ChangedRegistry.TRANSFUR_VARIANT.get().getValues().stream().filter(variant -> !SPECIAL_LATEX_FORMS.contains(variant.getRegistryName()));
    }

    @Deprecated
    public static List<TransfurVariant<?>> getFusionCompatible(TransfurVariant<?> source, TransfurVariant<?> other) {
        List<TransfurVariant<?>> list = new ArrayList<>();
        ChangedRegistry.TRANSFUR_VARIANT.get().forEach(variant -> {
            if (variant.isFusionOf(source, other))
                list.add(variant);
        });
        return list;
    }

    @Deprecated
    public static List<TransfurVariant<?>> getFusionCompatible(TransfurVariant<?> source, Class<? extends LivingEntity> clazz) {
        List<TransfurVariant<?>> list = new ArrayList<>();
        ChangedRegistry.TRANSFUR_VARIANT.get().forEach(variant -> {
            if (variant.isFusionOf(source, clazz))
                list.add(variant);
        });
        return list;
    }

    public ResourceLocation getFormId() {
        return getRegistryName();
    }

    public boolean isReducedFall() {
        return reducedFall;
    }

    public TransfurMode transfurMode() { return transfurMode; }

    public boolean isGendered() {
        for (Gender g : Gender.values())
            if (this.getFormId().getPath().endsWith(g.name().toLowerCase(Locale.ROOT)))
                return true;
        return false;
    }

    public int getTicksRequiredToFreeze(Level level) {
        return ChangedEntities.getCachedEntity(level, ctor.get()).getTicksRequiredToFreeze();
    }

    @Deprecated
    public boolean isFusionOf(TransfurVariant<?> variantA, TransfurVariant<?> variantB) {
        if (variantA == null || variantB == null)
            return false;

        return ChangedFusions.INSTANCE.getFusionsFor(variantA, variantB).anyMatch(fusion -> fusion == this);
    }

    @Deprecated
    public boolean isFusionOf(TransfurVariant<?> variantA, Class<? extends LivingEntity> clazz) {
        if (variantA == null || clazz == null)
            return false;

        return ChangedFusions.INSTANCE.getFusionsFor(variantA, clazz).anyMatch(fusion -> fusion == this);
    }

    @Deprecated(forRemoval = true)
    public float swimMultiplier() {
        return 1.0F;
    }

    @Deprecated(forRemoval = true)
    public float landMultiplier() {
        return 1.0F;
    }

    public boolean is(@Nullable TransfurVariant<?> variant) {
        if (variant == null)
            return false;
        return getEntityType() == variant.getEntityType();
    }

    public boolean is(@Nullable Supplier<? extends TransfurVariant<?>> variant) {
        if (variant == null)
            return false;
        return getEntityType() == variant.get().getEntityType();
    }

    public boolean is(TagKey<TransfurVariant<?>> tagKey) {
        return ChangedRegistry.TRANSFUR_VARIANT.get().tags().getTag(tagKey).contains(this);
    }

    public enum BreatheMode {
        NORMAL,
        WEAK,
        STRONG,
        WATER,
        ANY;

        public boolean canBreatheWater() {
            return this == WATER || this == ANY;
        }

        public boolean canBreatheAir() {
            return this == NORMAL || this == ANY || this == WEAK || this == STRONG;
        }

        public boolean hasAquaAffinity() { return canBreatheWater(); }
    }

    private static final AtomicInteger NEXT_ENTITY_ID = new AtomicInteger(-70000000);

    public static int getNextEntId() {
        return NEXT_ENTITY_ID.getAndDecrement();
    }

    public String toString() {
        return getRegistryName().toString();
    }

    // Variant properties
    public final Supplier<EntityType<T>> ctor;
    public final LatexType type;
    public final float jumpStrength;
    public final BreatheMode breatheMode;
    public final float stepSize;
    public final boolean canGlide;
    public final int extraJumpCharges;
    public final boolean reducedFall;
    public final boolean canClimb;
    public final VisionType visionType;
    public final int legCount;
    public final boolean hasLegs;
    public final UseItemMode itemUseMode;
    public final List<Class<? extends PathfinderMob>> scares;
    public final TransfurMode transfurMode;
    public final ImmutableList<Function<EntityType<?>, ? extends AbstractAbility<?>>> abilities;
    public final float cameraZOffset;
    public final ResourceLocation sound;

    public TransfurVariant(Supplier<EntityType<T>> ctor, LatexType type,
                           float jumpStrength, BreatheMode breatheMode, float stepSize, boolean canGlide, int extraJumpCharges,
                           boolean reducedFall, boolean canClimb,
                           VisionType visionType, int legCount, UseItemMode itemUseMode, List<Class<? extends PathfinderMob>> scares, TransfurMode transfurMode,
                           List<Function<EntityType<?>, ? extends AbstractAbility<?>>> abilities, float cameraZOffset, ResourceLocation sound) {
        this.ctor = ctor;
        this.type = type;
        this.jumpStrength = jumpStrength;
        this.breatheMode = breatheMode;
        this.stepSize = stepSize;
        this.visionType = visionType;
        this.canGlide = canGlide;
        this.extraJumpCharges = extraJumpCharges;
        this.legCount = legCount;
        this.hasLegs = legCount > 0;
        this.itemUseMode = itemUseMode;
        this.abilities = ImmutableList.<Function<EntityType<?>, ? extends AbstractAbility<?>>>builder().addAll(abilities).build();
        this.reducedFall = reducedFall;
        this.canClimb = canClimb;
        this.scares = scares;
        this.transfurMode = transfurMode;
        this.cameraZOffset = cameraZOffset;
        this.sound = sound;
    }

    public LatexType getLatexType() {
        return type;
    }

    private T createChangedEntity(Level level) {
        T entity = ctor.get().create(level);
        entity.setId(getNextEntId()); //to prevent ID collision
        entity.setSilent(true);
        entity.goalSelector.removeAllGoals();
        return entity;
    }

    public EntityType<T> getEntityType() {
        return ctor.get();
    }

    public T generateForm(@NotNull Player player, Level level) {
        T latexForm = createChangedEntity(level);
        latexForm.moveTo((player.getX()), (player.getY()), (player.getZ()), player.getYRot(), 0);
        if (latexForm instanceof SpecialLatex specialLatex)
            specialLatex.setSpecialForm(UUID.fromString(
                    getFormId().toString().substring(Changed.modResourceStr("special/form_").length())));

        latexForm.setCustomName(player.getDisplayName());
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            if (player == Minecraft.getInstance().player)
                latexForm.setCustomNameVisible(false);
        });

        return latexForm;
    }


    public ChangedEntity spawnAtEntity(@NotNull LivingEntity entity) {
        ChangedEntity newEntity = ctor.get().create(entity.level);
        newEntity.finalizeSpawn((ServerLevelAccessor) entity.level, entity.level.getCurrentDifficultyAt(newEntity.blockPosition()), MobSpawnType.MOB_SUMMONED, null,
                null);
        newEntity.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
        entity.level.addFreshEntity(newEntity);
        if (newEntity instanceof SpecialLatex specialLatex) {
            specialLatex.setSpecialForm(UUID.fromString(
                    getFormId().toString().substring(Changed.modResourceStr("special/form_").length())));
        }
        return newEntity;
    }

    public IAbstractChangedEntity replaceEntity(@NotNull LivingEntity entity) {
        return replaceEntity(entity, (LivingEntity) null);
    }

    public IAbstractChangedEntity replaceEntity(@NotNull LivingEntity entity, @Nullable IAbstractChangedEntity cause) {
        return replaceEntity(entity, cause == null ? null : cause.getEntity());
    }

    public IAbstractChangedEntity replaceEntity(@NotNull LivingEntity entity, @Nullable LivingEntity cause) {
        var newEntity = spawnAtEntity(entity);
        if (entity.hasCustomName()) {
            newEntity.setCustomName(entity.getCustomName());
            newEntity.setCustomNameVisible(entity.isCustomNameVisible());
        }

        if (entity instanceof Mob mob) {
            newEntity.setNoAi(mob.isNoAi());
        }

        if (entity instanceof Mob mob) {
            newEntity.setLeftHanded(mob.isLeftHanded());
        }

        if (entity instanceof Player player) {
            newEntity.getBasicPlayerInfo().copyFrom(((PlayerDataExtension)player).getBasicPlayerInfo());
            if (!ProcessTransfur.killPlayerByTransfur(player, cause != null ? cause : newEntity)) {
                newEntity.discard();
                var instance = ProcessTransfur.setPlayerTransfurVariant(player, this, TransfurCause.GRAB_REPLICATE, 1.0f);
                instance.willSurviveTransfur = true;

                ProcessTransfur.forceNearbyToRetarget(player.level, player);

                player.heal(10.0F);
                return IAbstractChangedEntity.forPlayer(player);
            }
        } else if (entity instanceof ChangedEntity changedEntity) {
            newEntity.getBasicPlayerInfo().copyFrom(changedEntity.getBasicPlayerInfo());
            // Take armor and held items
            Arrays.stream(EquipmentSlot.values()).forEach(slot -> {
                newEntity.setItemSlot(slot, entity.getItemBySlot(slot).copy());
            });

            changedEntity.discard();
        } else {
            // Take armor
            Arrays.stream(EquipmentSlot.values()).filter(slot -> slot.getType() == EquipmentSlot.Type.ARMOR).forEach(slot -> {
                newEntity.setItemSlot(slot, entity.getItemBySlot(slot).copy());
            });
            // Drop held items
            Arrays.stream(EquipmentSlot.values()).filter(slot -> slot.getType() == EquipmentSlot.Type.HAND).forEach(slot -> {
                if (entity.getRandom().nextFloat() < 0.05f) // 5% Drop rate
                    Block.popResource(entity.level, entity.blockPosition(), entity.getItemBySlot(slot).copy());
            });

            entity.discard();
        }

        return IAbstractChangedEntity.forEntity(newEntity);
    }

    public BreatheMode getBreatheMode() {
        return breatheMode;
    }

    public boolean canDoubleJump() { return extraJumpCharges > 0; }

    public boolean rideable() { return this.abilities.contains(ChangedAbilities.ACCESS_SADDLE); }

    public static class UniversalAbilitiesEvent extends Event {
        private final List<Function<EntityType<?>, ? extends AbstractAbility<?>>> abilities;

        public UniversalAbilitiesEvent(List<Function<EntityType<?>, ? extends AbstractAbility<?>>> abilities) {
            this.abilities = abilities;
        }

        public void addAbility(Supplier<? extends AbstractAbility<?>> ability) {
            abilities.add(type -> ability.get());
        }

        public void addAbility(Predicate<EntityType<?>> predicate, Supplier<? extends AbstractAbility<?>> ability) {
            abilities.add(type -> predicate.test(type) ? ability.get() : null);
        }

        public void addAbility(Function<EntityType<?>, ? extends AbstractAbility<?>> ability) {
            abilities.add(ability);
        }

        public Predicate<EntityType<?>> isOfTag(TagKey<EntityType<?>> tag) {
            return type -> type.is(tag);
        }

        public Predicate<EntityType<?>> isNotOfTag(TagKey<EntityType<?>> tag) {
            return type -> !type.is(tag);
        }
    }

    public static class Builder<T extends ChangedEntity> {
        final Supplier<EntityType<T>> entityType;
        LatexType type = LatexType.NEUTRAL;
        float jumpStrength = 1.0F;
        BreatheMode breatheMode = BreatheMode.NORMAL;
        float stepSize = 0.6F;
        boolean canGlide = false;
        int extraJumpCharges = 0;
        boolean reducedFall = false;
        boolean canClimb = false;
        VisionType visionType = VisionType.NORMAL;
        int legCount = 2;
        UseItemMode itemUseMode = UseItemMode.NORMAL;
        List<Class<? extends PathfinderMob>> scares = new ArrayList<>(ImmutableList.of(AbstractVillager.class));
        TransfurMode transfurMode = TransfurMode.REPLICATION;
        List<Function<EntityType<?>, ? extends AbstractAbility<?>>> abilities = new ArrayList<>();
        float cameraZOffset = 0.0F;
        ResourceLocation sound = ChangedSounds.POISON.getLocation();

        public Builder(Supplier<EntityType<T>> entityType) {
            this.entityType = entityType;

            var event = new UniversalAbilitiesEvent(this.abilities);
            event.addAbility(event.isOfTag(ChangedTags.EntityTypes.LATEX)
                    .and(event.isNotOfTag(ChangedTags.EntityTypes.PARTIAL_LATEX)), ChangedAbilities.SWITCH_TRANSFUR_MODE);
            event.addAbility(event.isOfTag(ChangedTags.EntityTypes.LATEX)
                    .and(event.isNotOfTag(ChangedTags.EntityTypes.ARMLESS))
                    .and(event.isNotOfTag(ChangedTags.EntityTypes.PARTIAL_LATEX)), ChangedAbilities.GRAB_ENTITY_ABILITY);

            MinecraftForge.EVENT_BUS.post(event);
        }

        public void ignored() {}

        public static <T extends ChangedEntity> Builder<T> of(Supplier<EntityType<T>> entityType) {
            return new Builder<T>(entityType);
        }

        public Builder<T> faction(LatexType type) {
            this.type = type; return this;
        }

        public Builder<T> jumpStrength(float factor) {
            this.jumpStrength = factor; return this;
        }

        public Builder<T> gills() {
            return gills(false);
        }

        public Builder<T> gills(boolean suffocate_on_land) {
            this.breatheMode = suffocate_on_land ? BreatheMode.WATER : BreatheMode.ANY; return this;
        }

        public Builder<T> breatheMode(BreatheMode mode) {
            this.breatheMode = mode; return this;
        }

        public Builder<T> reducedFall() {
            this.reducedFall = true; return this;
        }

        public Builder<T> noVision() {
            this.visionType = VisionType.BLIND; return this;
        }

        public Builder<T> noVision(boolean v) {
            this.visionType = v ? VisionType.BLIND : visionType; return this;
        }

        public Builder<T> reducedFall(boolean v) {
            this.reducedFall = v; return this;
        }

        public Builder<T> canClimb() {
            this.canClimb = true; return this;
        }

        public Builder<T> canClimb(boolean v) {
            this.canClimb = v; return this;
        }

        public <E extends PathfinderMob> Builder<T> scares(Class<E> type) {
            scares.add(type); return this;
        }

        public Builder<T> scares(List<Class<? extends PathfinderMob>> v) {
            scares = v; return this;
        }

        public Builder<T> stepSize(float factor) {
            this.stepSize = factor; return this;
        }

        public Builder<T> glide() {
            return glide(true);
        }

        public Builder<T> glide(boolean enable) {
            this.canGlide = enable; return this;
        }

        public Builder<T> doubleJump() {
            return extraJumps(1);
        }

        public Builder<T> extraJumps(int count) {
            this.extraJumpCharges = count; return this;
        }
        
        public Builder<T> addAbility(Function<EntityType<?>, ? extends AbstractAbility<?>> ability) {
            if (ability != null)
                this.abilities.add(ability);
            return this;
        }
        
        public Builder<T> addAbility(Supplier<? extends AbstractAbility<?>> ability) {
            if (ability != null)
                this.abilities.add(type -> ability.get());
            return this;
        }
        
        public Builder<T> abilities(List<Function<EntityType<?>, ? extends AbstractAbility<?>>> abilities) {
            this.abilities = new ArrayList<>(abilities); return this;
        }

        public Builder<T> extraHands() {
            return addAbility(ChangedAbilities.SWITCH_HANDS);
        }

        public Builder<T> rideable() {
            return addAbility(ChangedAbilities.ACCESS_SADDLE).addAbility(ChangedAbilities.ACCESS_CHEST);
        }

        public Builder<T> absorbing() {
            return transfurMode(TransfurMode.ABSORPTION);
        }
        
        public Builder<T> replicating() {
            return transfurMode(TransfurMode.REPLICATION);
        }

        public Builder<T> nightVision() {
            this.visionType = VisionType.NIGHT_VISION; return this;
        }

        public Builder<T> nightVision(boolean v) {
            this.visionType = v ? VisionType.NIGHT_VISION : visionType; return this;
        }

        public Builder<T> visionType(VisionType type) {
            this.visionType = type; return this;
        }

        public Builder<T> transfurMode(TransfurMode mode) {
            this.transfurMode = mode; return this;
        }

        public Builder<T> noLegs() {
            this.legCount = 0;
            return this;
        }

        public Builder<T> hasLegs(boolean v) {
            this.legCount = 2;
            return this;
        }

        public Builder<T> quadrupedal() {
            this.legCount = 4;
            return this;
        }

        public Builder<T> disableItems() {
            this.itemUseMode = UseItemMode.NONE;
            return this;
        }

        public Builder<T> holdItemsInMouth() {
            this.itemUseMode = UseItemMode.MOUTH;
            return this;
        }

        public Builder<T> itemUseMode(UseItemMode v) {
            this.itemUseMode = v;
            return this;
        }

        public Builder<T> cameraZOffset(float v) {
            this.cameraZOffset = v; return this;
        }

        public Builder<T> sound(ResourceLocation event) {
            this.sound = event; return this;
        }

        public TransfurVariant<T> build() {
            return new TransfurVariant<>(entityType, type, jumpStrength, breatheMode, stepSize, canGlide, extraJumpCharges,
                    reducedFall, canClimb, visionType, legCount, itemUseMode, scares, transfurMode, abilities, cameraZOffset, sound);
        }
    }

    public static <T extends ChangedEntity> TransfurVariant<T> registerSpecial(TransfurVariant<T> variant) {
        SPECIAL_LATEX_FORMS.add(variant.getFormId());
        return variant;
    }

    public static TransfurVariant<?> findEntityTransfurVariant(ChangedEntity entity) {
        for (TransfurVariant<?> variant : ChangedRegistry.TRANSFUR_VARIANT.get().getValues())
            if (variant.ctor != null && variant.ctor.get().equals(entity.getType()))
                return variant;
        return null;
    }

    public static TransfurVariant<?> getEntityTransfur(LivingEntity entity) {
        return ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(entity),
                variant -> variant.getChangedEntity().getTransfurVariant(), () -> {
            if (entity instanceof ChangedEntity changedEntity)
                return changedEntity.getTransfurVariant();
            return null;
        });
    }

    public static TransfurVariant<?> getEntityVariant(LivingEntity entity) {
        return ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(entity),
                TransfurVariantInstance::getParent,
                () -> {
                    if (entity instanceof ChangedEntity changedEntity)
                        return changedEntity.getSelfVariant();
                    return null;
                });
    }

    // Parses variant from JSON, does not register variant
    public static TransfurVariant<?> fromJson(ResourceLocation id, JsonObject root) {
        return fromJson(id, root, List.of());
    }

    public static TransfurVariant<?> fromJson(ResourceLocation id, JsonObject root, List<AbstractAbility<?>> injectAbilities) {
        ResourceLocation entityType = ResourceLocation.tryParse(GsonHelper.getAsString(root, "entity", ChangedEntities.SPECIAL_LATEX.getId().toString()));

        List<Class<? extends PathfinderMob>> scares = new ArrayList<>(ImmutableList.of(AbstractVillager.class));
        GsonHelper.getAsJsonArray(root, "scares", new JsonArray()).forEach(element -> {
            try {
                scares.add((Class<? extends PathfinderMob>) Class.forName(element.getAsString()));
            } catch (Exception e) {
                Changed.LOGGER.error("Invalid class given: {}", element.getAsString());
            }
        });

        List<TransfurVariant<?>> fusionOf = new ArrayList<>();
        GsonHelper.getAsJsonArray(root, "fusionOf", new JsonArray()).forEach(element -> {
            fusionOf.add(ChangedRegistry.TRANSFUR_VARIANT.get().getValue(ResourceLocation.tryParse(element.getAsString())));
        });

        AtomicReference<TransfurVariant<?>> mobFusionLatex = new AtomicReference<>(null);
        AtomicReference<Class<? extends LivingEntity>> mobFusionMob = null;
        GsonHelper.getAsJsonArray(root, "mobFusionOf", new JsonArray()).forEach(element -> {
            if (ChangedRegistry.TRANSFUR_VARIANT.get().containsKey(ResourceLocation.tryParse(element.getAsString())))
                mobFusionLatex.set(ChangedRegistry.TRANSFUR_VARIANT.get().getValue(ResourceLocation.tryParse(element.getAsString())));
            try {
                mobFusionMob.compareAndSet(null, (Class<? extends LivingEntity>)Class.forName(element.getAsString()));
            } catch (ClassNotFoundException ignored) {}
        });

        List<AbstractAbility<?>> abilities = new ArrayList<>(injectAbilities);
        GsonHelper.getAsJsonArray(root, "abilities", new JsonArray()).forEach(element -> {
            abilities.add(ChangedRegistry.ABILITY.get().getValue(ResourceLocation.tryParse(element.getAsString())));
        });

        List<Function<EntityType<?>, ? extends AbstractAbility<?>>> nAbilitiesList = abilities.stream().map(a -> (Function<EntityType<?>, AbstractAbility<?>>) type -> a).collect(Collectors.toList());

        boolean nightVision = GsonHelper.getAsBoolean(root, "nightVision", false);
        float speed = GsonHelper.getAsFloat(root, "groundSpeed", 1.0F) * 0.1f;
        float swimSpeed = GsonHelper.getAsFloat(root, "swimSpeed", 1.0F);
        int additionalHealth = GsonHelper.getAsInt(root, "additionalHealth", 4);

        return new TransfurVariant<>(
                () -> (EntityType<ChangedEntity>) Registry.ENTITY_TYPE.get(entityType),
                LatexType.valueOf(GsonHelper.getAsString(root, "latexType", LatexType.NEUTRAL.toString())),
                GsonHelper.getAsFloat(root, "jumpStrength", 1.0f),
                BreatheMode.valueOf(GsonHelper.getAsString(root, "breatheMode", BreatheMode.NORMAL.toString())),
                GsonHelper.getAsFloat(root, "stepSize", 0.6f),
                GsonHelper.getAsBoolean(root, "canGlide", false),
                GsonHelper.getAsInt(root, "extraJumpCharges", 0),
                GsonHelper.getAsBoolean(root, "reducedFall", false),
                GsonHelper.getAsBoolean(root, "canClimb", false),
                VisionType.fromSerial(GsonHelper.getAsString(root, "visionType", (nightVision ? VisionType.NIGHT_VISION : VisionType.NORMAL).getSerializedName()))
                        .result().orElse(VisionType.NORMAL),
                GsonHelper.getAsInt(root, "legCount", 2),
                UseItemMode.valueOf(GsonHelper.getAsString(root, "itemUseMode", UseItemMode.NORMAL.toString())),
                scares,
                TransfurMode.valueOf(GsonHelper.getAsString(root, "transfurMode", TransfurMode.REPLICATION.toString())),
                nAbilitiesList,
                GsonHelper.getAsFloat(root, "cameraZOffset", 0.0F),
                ResourceLocation.tryParse(GsonHelper.getAsString(root, "sound", ChangedSounds.POISON.getLocation().toString()))).setRegistryName(id);
    }


    public Pair<Color3, Color3> getColors() {
        var ints = ChangedEntities.getEntityColor(getEntityType().getRegistryName());
        return new Pair<>(
                Color3.fromInt(ints.getFirst()),
                Color3.fromInt(ints.getSecond()));
    }
}
