package net.ltxprogrammer.changed.init;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.EntityColorProvider;
import net.ltxprogrammer.changed.entity.SeatEntity;
import net.ltxprogrammer.changed.entity.beast.*;
import net.ltxprogrammer.changed.entity.beast.boss.BehemothHandLeft;
import net.ltxprogrammer.changed.entity.beast.boss.BehemothHandRight;
import net.ltxprogrammer.changed.entity.beast.boss.BehemothHead;
import net.ltxprogrammer.changed.entity.decoration.*;
import net.ltxprogrammer.changed.entity.projectile.GasParticle;
import net.ltxprogrammer.changed.entity.projectile.LatexInkball;
import net.ltxprogrammer.changed.entity.robot.Exoskeleton;
import net.ltxprogrammer.changed.entity.robot.Roomba;
import net.ltxprogrammer.changed.util.Color3;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.ltxprogrammer.changed.entity.variant.TransfurVariant.getNextEntId;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedEntities {
    public interface VoidConsumer { void accept(); }

    private static final Map<ResourceLocation, Pair<Integer, Integer>> ENTITY_COLOR_MAP = new HashMap<>();
    private static final List<Pair<Supplier<EntityType<? extends ChangedEntity>>, Supplier<AttributeSupplier.Builder>>> ATTR_FUNC_REGISTRY = new ArrayList<>();
    private static final List<VoidConsumer> INIT_FUNC_REGISTRY = new ArrayList<>();
    private static final Map<Level, Map<EntityType<?>, Entity>> CACHED_ENTITIES = new HashMap<>();

    public static Pair<Integer, Integer> getEntityColor(ResourceLocation location) {
        return ENTITY_COLOR_MAP.computeIfAbsent(location, loc -> {
            try {
                if (ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), loc.getPath() + "_spawn_egg")) instanceof ForgeSpawnEggItem item)
                    return new Pair<>(item.getColor(0), item.getColor(1));
                else
                    return new Pair<>(0xF0F0F0, 0xF0F0F0);
            } catch (Exception ex) {
                return new Pair<>(0xF0F0F0, 0xF0F0F0);
            }
        });
    }

    public static Pair<Color3, Color3> getEntityColor(LivingEntity entity) {
        entity = EntityUtil.maybeGetOverlaying(entity);
        if (entity instanceof EntityColorProvider colorProvider) {
            return Pair.of(colorProvider.getFrontColor(), colorProvider.getBackColor());
        } else {
            return getEntityColor(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()))
                    .mapFirst(Color3::fromInt).mapSecond(Color3::fromInt);
        }
    }

    /**
     * Allows addon mods to set their entity's colors without requiring a spawn egg
     * @param location registry location of the entity
     * @param background eggBack
     * @param highlight eggHighlight
     */
    public static void registerEntityColor(ResourceLocation location, int background, int highlight) {
        ENTITY_COLOR_MAP.put(location, Pair.of(background, highlight));
    }

    public static int getEntityColorBack(ResourceLocation location) {
        return getEntityColor(location).getFirst();
    }

    public static int getEntityColorFront(ResourceLocation location) {
        return getEntityColor(location).getSecond();
    }

    public static <T extends Entity> T getCachedEntity(Level level, EntityType<T> type) {
        return (T)CACHED_ENTITIES.computeIfAbsent(level, (ignored) -> new HashMap<>()).computeIfAbsent(type, (entityType) -> {
            var entity = entityType.create(level);
            entity.setId(getNextEntId()); //to prevent ID collision
            entity.setSilent(true);
            return entity;
        });
    }

    public static void clearAllCachedEntities() {
        CACHED_ENTITIES.clear();
    }

    public static boolean overworldOnly(Level level) {
        return level.dimension().equals(Level.OVERWORLD);
    }

    public static boolean anyDimension(DimensionType dimensionType) {
        return true;
    }

    public static final Map<Supplier<? extends EntityType<?>>, Predicate<Level>> DIMENSION_RESTRICTIONS = new HashMap<>();
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Changed.MODID);
    public static final Map<RegistryObject<? extends EntityType<?>>, RegistryObject<ForgeSpawnEggItem>> SPAWN_EGGS = new HashMap<>();
    public static final RegistryObject<EntityType<WhiteLatexWolfFemale>> WHITE_LATEX_WOLF_FEMALE = registerSpawning("white_latex_wolf_female", 0xFFFFFF, 0xFF927F,
            EntityType.Builder.of(WhiteLatexWolfFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, WhiteLatexWolfFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<WhiteLatexWolfMale>> WHITE_LATEX_WOLF_MALE = registerSpawning("white_latex_wolf_male", 0xFFFFFF, 0xFF927F,
            EntityType.Builder.of(WhiteLatexWolfMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, WhiteLatexWolfMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<WhiteWolfMale>> WHITE_WOLF_MALE = registerSpawning("white_wolf_male", 0xFFFFFF, 0xFAE9E4,
            EntityType.Builder.of(WhiteWolfMale::new, MobCategory.CREATURE).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, WhiteWolfMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<WhiteWolfFemale>> WHITE_WOLF_FEMALE = registerSpawning("white_wolf_female", 0xFFFFFF, 0xFAE9E4,
            EntityType.Builder.of(WhiteWolfFemale::new, MobCategory.CREATURE).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, WhiteWolfFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<WhiteLatexKnight>> WHITE_LATEX_KNIGHT = registerSpawning("white_latex_knight", 0xFFFFFF, 0x1E1E1E,
            EntityType.Builder.of(WhiteLatexKnight::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, WhiteLatexKnight::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<WhiteLatexCentaur>> WHITE_LATEX_CENTAUR = registerSpawning("white_latex_centaur", 0xFFFFFF, 0x1E1E1E,
            EntityType.Builder.of(WhiteLatexCentaur::new, MobCategory.MONSTER).clientTrackingRange(10).sized(1.25F, 2.0F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, WhiteLatexCentaur::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexGnollTaur>> LATEX_GNOLL_TAUR = registerSpawning("latex_gnoll_taur", 0xffbf75, 0xc0604d,
            EntityType.Builder.of(LatexGnollTaur::new, MobCategory.MONSTER).clientTrackingRange(10).sized(1.25F, 2.0F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexGnollTaur::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<HeadlessKnight>> HEADLESS_KNIGHT = registerSpawning("headless_knight", 0xFFFFFF, 0x1E1E1E,
            EntityType.Builder.of(HeadlessKnight::new, MobCategory.MONSTER).clientTrackingRange(10).sized(1.1F, 1.1F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, HeadlessKnight::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<WhiteLatexKnightFusion>> WHITE_LATEX_KNIGHT_FUSION = registerSpawning("white_latex_knight_fusion", 0xFFFFFF, 0x0072ff,
            EntityType.Builder.of(WhiteLatexKnightFusion::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, WhiteLatexKnightFusion::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<CrystalWolf>> CRYSTAL_WOLF = registerSpawning("crystal_wolf", 0x393939, 0xCF003E,
            EntityType.Builder.of(CrystalWolf::new, MobCategory.CREATURE).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, CrystalWolf::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<CrystalWolfHorned>> CRYSTAL_WOLF_HORNED = registerSpawning("crystal_wolf_horned", 0x393939, 0xFF014E,
            EntityType.Builder.of(CrystalWolfHorned::new, MobCategory.CREATURE).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, CrystalWolfHorned::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexDeer>> LATEX_DEER = registerSpawning("latex_deer", 0xD8BC99, 0xFBE5BC,
            EntityType.Builder.of(LatexDeer::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexDeer::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexBee>> LATEX_BEE = registerSpawning("latex_bee", 0xFFBF75, 0xFF9E58,
            EntityType.Builder.of(LatexBee::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexBee::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexPinkDeer>> LATEX_PINK_DEER = registerSpawning("latex_pink_deer", 0xF2AFBC, 0xCA636A,
            EntityType.Builder.of(LatexPinkDeer::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexPinkDeer::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexKeonWolf>> LATEX_KEON_WOLF = registerSpawning("latex_keon_wolf", 0x959CA5, 0x272727,
            EntityType.Builder.of(LatexKeonWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexKeonWolf::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<GasSkunk>> GAS_SKUNK = registerSpawning("gas_skunk", 0x334752, 0xFDFDFD,
            EntityType.Builder.of(GasSkunk::new, MobCategory.CREATURE).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, GasSkunk::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<GasTiger>> GAS_TIGER = registerSpawning("gas_tiger", 0xFFFFFF, 0x212121,
            EntityType.Builder.of(GasTiger::new, MobCategory.CREATURE).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, GasTiger::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<GasWolfFemale>> GAS_WOLF_FEMALE = registerSpawning("gas_wolf_female", 0x5D4743, 0xFFFFFF,
            EntityType.Builder.of(GasWolfFemale::new, MobCategory.CREATURE).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, GasWolfFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<GasWolfMale>> GAS_WOLF_MALE = registerSpawning("gas_wolf_male", 0x5D4743, 0xFFFFFF,
            EntityType.Builder.of(GasWolfMale::new, MobCategory.CREATURE).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, GasWolfMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<GasWolfPup>> GAS_WOLF_PUP = registerSpawning("gas_wolf_pup", 0x654D49, 0xFFFFFF,
            EntityType.Builder.of(GasWolfPup::new, MobCategory.CREATURE).clientTrackingRange(10).sized(0.6F, 0.85F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, GasWolfPup::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<PooltoyWolf>> POOLTOY_WOLF = registerSpawning("pooltoy_wolf", 0x50c3ff, 0x57a9ec,
            EntityType.Builder.of(PooltoyWolf::new, MobCategory.CREATURE).clientTrackingRange(10).sized(0.7F * PooltoyWolf.SCALE, 1.93F * PooltoyWolf.SCALE),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, PooltoyWolf::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<DarkDragon>> DARK_DRAGON = registerSpawning("dark_dragon", 0x393939, 0x909090,
            EntityType.Builder.of(DarkDragon::new, MobCategory.CREATURE).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, DarkDragon::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<DarkLatexWolfMale>> DARK_LATEX_WOLF_MALE = registerSpawning("dark_latex_wolf_male", 0x393939, 0x303030,
            EntityType.Builder.of(DarkLatexWolfMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, DarkLatexWolfMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<DarkLatexWolfFemale>> DARK_LATEX_WOLF_FEMALE = registerSpawning("dark_latex_wolf_female", 0x393939, 0x303030,
            EntityType.Builder.of(DarkLatexWolfFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, DarkLatexWolfFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<DarkLatexWolfPup>> DARK_LATEX_WOLF_PUP = registerSpawning("dark_latex_wolf_pup", 0x454545, 0x303030,
            EntityType.Builder.of(DarkLatexWolfPup::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.6F, 0.85F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, DarkLatexWolfPup::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<DarkLatexYufeng>> DARK_LATEX_YUFENG = registerSpawning("dark_latex_yufeng", 0x393939, 0x0,
            EntityType.Builder.of(DarkLatexYufeng::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, DarkLatexYufeng::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<DarkLatexDoubleYufeng>> DARK_LATEX_DOUBLE_YUFENG = registerSpawning("dark_latex_double_yufeng", 0x393939, 0x0,
            EntityType.Builder.of(DarkLatexDoubleYufeng::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, DarkLatexDoubleYufeng::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<PhageLatexWolfMale>> PHAGE_LATEX_WOLF_MALE = registerSpawning("phage_latex_wolf_male", 0x393939, 0x3499ff,
            EntityType.Builder.of(PhageLatexWolfMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, PhageLatexWolfMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<PhageLatexWolfFemale>> PHAGE_LATEX_WOLF_FEMALE = registerSpawning("phage_latex_wolf_female", 0x393939, 0x3499ff,
            EntityType.Builder.of(PhageLatexWolfFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, PhageLatexWolfFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<Beifeng>> BEIFENG = registerSpawning("beifeng", 0x51659D, 0xFFE852,
            EntityType.Builder.of(Beifeng::new, MobCategory.CREATURE).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, Beifeng::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexBenignOrca>> BENIGN_LATEX_ORCA = registerSpawning("latex_benign_orca", 0x282828, 0xdfdfdf,
            EntityType.Builder.of(LatexBenignOrca::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexBenignOrca::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexBenignWolf>> BENIGN_LATEX_WOLF = registerSpawning("latex_benign_wolf", 0x282828, 0x292929,
            EntityType.Builder.of(LatexBenignWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexBenignWolf::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexBlueDragon>> BLUE_LATEX_DRAGON = registerSpawning("latex_blue_dragon", 0xCDFEFF, 0x5c72ab,
            EntityType.Builder.of(LatexBlueDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexBlueDragon::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexBlueWolf>> BLUE_LATEX_WOLF = registerSpawning("latex_blue_wolf", 0x8ad6e7, 0x7395c0,
            EntityType.Builder.of(LatexBlueWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexBlueWolf::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexCrocodile>> LATEX_CROCODILE = registerSpawning("latex_crocodile", 0x216d50, 0x43b058,
            EntityType.Builder.of(LatexCrocodile::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.8F, 2.3F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexCrocodile::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexCrow>> LATEX_CROW = registerSpawning("latex_crow", 0x0e0e0e, 0xffffff,
            EntityType.Builder.of(LatexCrow::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexCrow::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexEel>> LATEX_EEL = registerSpawning("latex_eel", 0xffdb4f, 0xffffffff,
            EntityType.Builder.of(LatexEel::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexFennecFox::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexFennecFox>> LATEX_FENNEC_FOX = registerSpawning("latex_fennec_fox", 0xffe195, 0x84484b,
            EntityType.Builder.of(LatexFennecFox::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexFennecFox::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<GreenLizard>> GREEN_LIZARD = registerSpawning("green_lizard", 0xB3e53A, 0xFBE5BC,
            EntityType.Builder.of(GreenLizard::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, GreenLizard::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexGoldenDragon>> LATEX_GOLDEN_DRAGON = registerSpawning("latex_golden_dragon", 0xffdb4f, 0xf9b44a,
            EntityType.Builder.of(LatexGoldenDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexGoldenDragon::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexHypnoCat>> LATEX_HYPNO_CAT = registerSpawning("latex_hypno_cat", 0x52596D, 0xD7FF46,
            EntityType.Builder.of(LatexHypnoCat::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexHypnoCat::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexKobold>> LATEX_KOBOLD = registerSpawning("latex_kobold", 0x789ac7, 0x5d6891,
            EntityType.Builder.of(LatexKobold::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexKobold::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexLeaf>> LATEX_LEAF = registerSpawning("latex_leaf", 0xBFF298, 0x76C284,
            EntityType.Builder.of(LatexLeaf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexLeaf::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexSquirrel>> LATEX_SQUIRREL = registerSpawning("latex_squirrel", 0xFFE8A5, 0xAC8F64,
            EntityType.Builder.of(LatexSquirrel::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexSquirrel::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexMantaRayFemale>> LATEX_MANTA_RAY_FEMALE = registerSpawning("latex_manta_ray_female", 0x6f7696, 0xd2d9e1,
            EntityType.Builder.of(LatexMantaRayFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.58625F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexMantaRayFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexMantaRayMale>> LATEX_MANTA_RAY_MALE = registerSpawning("latex_manta_ray_male", 0x6f7696, 0xd2d9e1,
            EntityType.Builder.of(LatexMantaRayMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexMantaRayMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexMedusaCat>> LATEX_MEDUSA_CAT = registerSpawning("latex_medusa_cat", 0xFFDB4F, 0xF398B7,
            EntityType.Builder.of(LatexMedusaCat::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexMedusaCat::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexMingCat>> LATEX_MING_CAT = registerSpawning("latex_ming_cat", 0xD2A87F, 0x75483F,
            EntityType.Builder.of(LatexMingCat::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexMingCat::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexMermaidShark>> LATEX_MERMAID_SHARK = registerSpawning("latex_mermaid_shark", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(LatexMermaidShark::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.58625F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexMermaidShark::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexMoth>> LATEX_MOTH = registerSpawning("latex_moth", 0xFBE5BC, 0xD8BC99,
            EntityType.Builder.of(LatexMoth::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexMoth::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexMutantBloodcellWolf>> LATEX_MUTANT_BLOODCELL_WOLF = registerSpawning("latex_mutant_bloodcell_wolf", 0xD7D7D7, 0x8A8A8A,
            EntityType.Builder.of(LatexMutantBloodcellWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexMutantBloodcellWolf::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexSiren>> LATEX_SIREN = registerSpawning("latex_siren", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(LatexSiren::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.58625F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexSiren::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexSnake>> LATEX_SNAKE = registerSpawning("latex_snake", 0xFFFFFF, 0x7E7E7E,
            EntityType.Builder.of(LatexSnake::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.64F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexSnake::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexMimicPlant>> LATEX_MIMIC_PLANT = registerSpawning("latex_mimic_plant", 0x446d5d, 0x729c6a,
            EntityType.Builder.of(LatexMimicPlant::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexMimicPlant::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexPinkWyvern>> LATEX_PINK_WYVERN = registerSpawning("latex_pink_wyvern", 0xf2aaba, 0xd1626d,
            EntityType.Builder.of(LatexPinkWyvern::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexPinkWyvern::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexPinkYuinDragon>> LATEX_PINK_YUIN_DRAGON = registerSpawning("latex_pink_yuin_dragon", 0xFFFFFF, 0xF2AABA,
            EntityType.Builder.of(LatexPinkYuinDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexPinkYuinDragon::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexPurpleFox>> LATEX_PURPLE_FOX = registerSpawning("latex_purple_fox", 0xcebbe8, 0xf1e3f1,
            EntityType.Builder.of(LatexPurpleFox::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexPurpleFox::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexRabbitFemale>> LATEX_RABBIT_FEMALE = registerSpawning("latex_rabbit_female", 0xFEF0E5, 0xFFC4C4,
            EntityType.Builder.of(LatexRabbitFemale::new, MobCategory.CREATURE).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexRabbitFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexRabbitMale>> LATEX_RABBIT_MALE = registerSpawning("latex_rabbit_male", 0xFEF0E5, 0xFFC4C4,
            EntityType.Builder.of(LatexRabbitMale::new, MobCategory.CREATURE).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexRabbitMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexRaccoon>> LATEX_RACCOON = registerSpawning("latex_raccoon", 0x949494, 0x535353,
            EntityType.Builder.of(LatexRaccoon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexRaccoon::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexRedDragon>> LATEX_RED_DRAGON = registerSpawning("latex_red_dragon", 0xa54f58, 0xfcfa4a,
            EntityType.Builder.of(LatexRedDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexRedDragon::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexRedPanda>> LATEX_RED_PANDA = registerSpawning("latex_red_panda", 0xbd4040, 0x663d53,
            EntityType.Builder.of(LatexRedPanda::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexRedPanda::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexShark>> LATEX_SHARK = registerSpawning("latex_shark", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(LatexShark::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexShark::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<BuffLatexSharkMale>> LATEX_SHARK_MALE = registerSpawning("latex_shark_male", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(BuffLatexSharkMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.8F, 2.2F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, BuffLatexSharkMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<BuffLatexSharkFemale>> LATEX_SHARK_FEMALE = registerSpawning("latex_shark_female", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(BuffLatexSharkFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.8F, 2.25F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, BuffLatexSharkFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<SniperDog>> SNIPER_DOG = registerSpawning("sniper_dog", 0xEF8F44, 0x894633,
            EntityType.Builder.of(SniperDog::new, MobCategory.CREATURE).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, SniperDog::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexAlien>> LATEX_ALIEN = registerSpawning("latex_alien", 0x1983A9, 0x2DAAB9,
            EntityType.Builder.of(LatexAlien::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexAlien::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexSiameseCat>> LATEX_SIAMESE_CAT = registerSpawning("latex_siamese_cat", 0xfdeae0, 0x604e61,
            EntityType.Builder.of(LatexSiameseCat::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexSiameseCat::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexSnowLeopardMale>> LATEX_SNOW_LEOPARD_MALE = registerSpawning("latex_snow_leopard_male", 0x9C9C9C, 0x272727,
            EntityType.Builder.of(LatexSnowLeopardMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexSnowLeopardMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexSnowLeopardFemale>> LATEX_SNOW_LEOPARD_FEMALE = registerSpawning("latex_snow_leopard_female", 0x9C9C9C, 0x272727,
            EntityType.Builder.of(LatexSnowLeopardFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexSnowLeopardFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexSquidDogFemale>> LATEX_SQUID_DOG_FEMALE = registerSpawning("latex_squid_dog_female", 0xFFFFFF, 0x0,
            EntityType.Builder.of(LatexSquidDogFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.8F, 2.1F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexSquidDogFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexSquidDogMale>> LATEX_SQUID_DOG_MALE = registerSpawning("latex_squid_dog_male", 0xFFFFFF, 0x0,
            EntityType.Builder.of(LatexSquidDogMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.8F, 2.1F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexSquidDogMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexStiger>> LATEX_STIGER = registerSpawning("latex_stiger", 0x7b4251, 0xe0cfd9,
            EntityType.Builder.of(LatexStiger::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexStiger::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexTigerShark>> LATEX_TIGER_SHARK = registerSpawning("latex_tiger_shark", 0x969696, 0x0,
            EntityType.Builder.of(LatexTigerShark::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexTigerShark::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexTrafficConeDragon>> LATEX_TRAFFIC_CONE_DRAGON = registerSpawning("latex_traffic_cone_dragon", 0xFFD201, 0x0,
            EntityType.Builder.of(LatexTrafficConeDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexTrafficConeDragon::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexTranslucentLizard>> LATEX_TRANSLUCENT_LIZARD = registerSpawning("latex_translucent_lizard", 0xffb84b, 0xFF904C,
            EntityType.Builder.of(LatexTranslucentLizard::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexTranslucentLizard::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexOrca>> LATEX_ORCA = registerSpawning("latex_orca", 0x393939, 0xFFFFFF,
            EntityType.Builder.of(LatexOrca::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexOrca::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexOtter>> LATEX_OTTER = registerSpawning("latex_otter", 0x5D4743, 0xB6957C,
            EntityType.Builder.of(LatexOtter::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexOtter::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexWatermelonCat>> LATEX_WATERMELON_CAT = registerSpawning("latex_watermelon_cat", 0x545454, 0xC7FF5A,
            EntityType.Builder.of(LatexWatermelonCat::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexWatermelonCat::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexWhiteTiger>> LATEX_WHITE_TIGER = registerSpawning("latex_white_tiger", 0xFFFFFF, 0xACACAC,
            EntityType.Builder.of(LatexWhiteTiger::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexWhiteTiger::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexYuin>> LATEX_YUIN = registerSpawning("latex_yuin", 0xFFFFFF, 0x7442cc,
            EntityType.Builder.of(LatexYuin::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexYuin::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<MilkPudding>> MILK_PUDDING = registerSpawning("milk_pudding", 0xFFFFFF, 0xF0F0F0,
            EntityType.Builder.of(MilkPudding::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.6F, 0.5F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, MilkPudding::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<FeralShark>> FERAL_LATEX_SHARK = registerSpawning("latex_shark_feral", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(FeralShark::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.9F, 0.6F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, FeralShark::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<PureWhiteLatexWolf>> PURE_WHITE_LATEX_WOLF = registerSpawning("pure_white_latex_wolf", 0xFFFFFF, 0xFAFAFA,
            EntityType.Builder.of(PureWhiteLatexWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, PureWhiteLatexWolf::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<PureWhiteLatexWolfPup>> PURE_WHITE_LATEX_WOLF_PUP = registerSpawning("pure_white_latex_wolf_pup", 0xFFFFFF, 0xFAFAFA,
            EntityType.Builder.of(PureWhiteLatexWolfPup::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.6F, 0.85F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, PureWhiteLatexWolfPup::checkEntitySpawnRules);

    public static final RegistryObject<EntityType<CustomLatexEntity>> CUSTOM_LATEX = registerSpawning("custom_latex", 0xFFFFFF, 0xFAFAFA,
            EntityType.Builder.of(CustomLatexEntity::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, CustomLatexEntity::checkEntitySpawnRules);

    public static final RegistryObject<EntityType<SeatEntity>> SEAT_ENTITY = REGISTRY.register("seat_entity",
            () -> EntityType.Builder.of(SeatEntity::new, MobCategory.MISC).sized(0.01f, 0.01f).build("seat_entity"));

    public static final RegistryObject<EntityType<DarkLatexWolfPartial>> DARK_LATEX_WOLF_PARTIAL = registerNoEgg("dark_latex_wolf_partial", 0x393939, 0x303030,
            EntityType.Builder.of(DarkLatexWolfPartial::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F));
    public static final RegistryObject<EntityType<LatexHuman>> LATEX_HUMAN = registerNoEgg("latex_human", 0x8B8B8B, 0xC6C6C6,
            EntityType.Builder.of(LatexHuman::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.6F, 1.8F));

    public static final RegistryObject<EntityType<BehemothHead>> BEHEMOTH_HEAD = registerNoEgg("behemoth_head",
            EntityType.Builder.of(BehemothHead::new, MobCategory.MONSTER).clientTrackingRange(10).sized(3.0f, 3.0f));
    public static final RegistryObject<EntityType<BehemothHandLeft>> BEHEMOTH_HAND_LEFT = registerNoEgg("behemoth_hand_left",
            EntityType.Builder.of(BehemothHandLeft::new, MobCategory.MONSTER).clientTrackingRange(10).sized(2.0f, 2.0f));
    public static final RegistryObject<EntityType<BehemothHandRight>> BEHEMOTH_HAND_RIGHT = registerNoEgg("behemoth_hand_right",
            EntityType.Builder.of(BehemothHandRight::new, MobCategory.MONSTER).clientTrackingRange(10).sized(2.0f, 2.0f));

    public static final RegistryObject<EntityType<Roomba>> ROOMBA = REGISTRY.register("roomba",
            () -> EntityType.Builder.of(Roomba::new, MobCategory.MISC).clientTrackingRange(10).sized(0.6F, 0.125f).build("roomba"));
    public static final RegistryObject<EntityType<Exoskeleton>> EXOSKELETON = REGISTRY.register("exoskeleton",
            () -> EntityType.Builder.of(Exoskeleton::new, MobCategory.MISC).clientTrackingRange(10).sized(0.7F, 1.93f).build("exoskeleton"));

    public static final RegistryObject<EntityType<LatexInkball>> LATEX_INKBALL = REGISTRY.register("latex_inkball",
            () -> EntityType.Builder.<LatexInkball>of(LatexInkball::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("latex_inkball"));
    public static final RegistryObject<EntityType<GasParticle>> GAS_PARTICLE = REGISTRY.register("gas_particle",
            () -> EntityType.Builder.of(GasParticle::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(4).updateInterval(10).build("gas_particle"));
    public static final RegistryObject<EntityType<WallSign>> WALL_SIGN = REGISTRY.register("wall_sign",
            () -> EntityType.Builder.of(WallSign::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE).build("wall_sign"));
    public static final RegistryObject<EntityType<EmittedLaser>> EMITTED_LASER = REGISTRY.register("emitted_laser",
            () -> EntityType.Builder.of(EmittedLaser::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE).build("emitted_laser"));
    public static final RegistryObject<EntityType<BipedArmorStand>> BIPED_ARMOR_STAND = REGISTRY.register("biped_armor_stand",
            () -> EntityType.Builder.<BipedArmorStand>of(BipedArmorStand::new, MobCategory.MISC).sized(0.7F, 1.93F).clientTrackingRange(10).build("biped_armor_stand"));
    public static final RegistryObject<EntityType<CentaurArmorStand>> CENTAUR_ARMOR_STAND = REGISTRY.register("centaur_armor_stand",
            () -> EntityType.Builder.<CentaurArmorStand>of(CentaurArmorStand::new, MobCategory.MISC).sized(1.25F, 2.0F).clientTrackingRange(10).build("centaur_armor_stand"));
    public static final RegistryObject<EntityType<LeglessArmorStand>> LEGLESS_ARMOR_STAND = REGISTRY.register("legless_armor_stand",
            () -> EntityType.Builder.<LeglessArmorStand>of(LeglessArmorStand::new, MobCategory.MISC).sized(0.7F, 1.93F).clientTrackingRange(10).build("legless_armor_stand"));

    // TODO make register function for non `ChangedEntity`

    private static <T extends ChangedEntity> RegistryObject<EntityType<T>> registerNoEgg(
            String name,
            int eggBack,
            int eggHighlight,
            EntityType.Builder<T> builder) {
        String regName = Changed.modResource(name).toString();
        registerEntityColor(Changed.modResource(name), eggBack, eggHighlight);
        RegistryObject<EntityType<T>> entityType = REGISTRY.register(name, () -> builder.build(regName));
        ATTR_FUNC_REGISTRY.add(new Pair<>(entityType::get, T::createLatexAttributes));
        return entityType;
    }

    private static <T extends ChangedEntity> RegistryObject<EntityType<T>> registerNoEgg(
            String name,
            EntityType.Builder<T> builder) {
        String regName = Changed.modResource(name).toString();
        RegistryObject<EntityType<T>> entityType = REGISTRY.register(name, () -> builder.build(regName));
        ATTR_FUNC_REGISTRY.add(new Pair<>(entityType::get, T::createLatexAttributes));
        return entityType;
    }

    private static <T extends ChangedEntity> RegistryObject<EntityType<T>> registerSpawning(
            String name,
            int eggBack,
            int eggHighlight,
            EntityType.Builder<T> builder,
            Predicate<Level> dimension,
            SpawnPlacements.Type spawnType,
            SpawnPlacements.SpawnPredicate<T> spawnPredicate) {
        return registerSpawning(name, eggBack, eggHighlight, builder, dimension, spawnType, spawnPredicate, T::createLatexAttributes);
    }

    private static <T extends ChangedEntity> RegistryObject<EntityType<T>> registerSpawning(
            String name,
            int eggBack,
            int eggHighlight,
            EntityType.Builder<T> builder,
            Predicate<Level> dimension,
            SpawnPlacements.Type spawnType,
            SpawnPlacements.SpawnPredicate<T> spawnPredicate,
            Supplier<AttributeSupplier.Builder> attributes) {
        registerEntityColor(Changed.modResource(name), eggBack, eggHighlight);
        String regName = Changed.modResource(name).toString();
        RegistryObject<EntityType<T>> entityType = REGISTRY.register(name, () -> builder.build(regName));
        INIT_FUNC_REGISTRY.add(ChangedEntity.getInit(entityType, spawnType, spawnPredicate));
        ATTR_FUNC_REGISTRY.add(new Pair<>(entityType::get, attributes));
        RegistryObject<ForgeSpawnEggItem> spawnEggItem = ChangedItems.REGISTRY.register(name + "_spawn_egg", () -> new ForgeSpawnEggItem(entityType, eggBack, eggHighlight, new Item.Properties()));
        SPAWN_EGGS.put(entityType, spawnEggItem);
        DIMENSION_RESTRICTIONS.put(entityType, dimension);
        return entityType;
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> INIT_FUNC_REGISTRY.forEach(VoidConsumer::accept));
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        ATTR_FUNC_REGISTRY.forEach((pair) -> event.put(pair.getFirst().get(), pair.getSecond().get().build()));
        event.put(ROOMBA.get(), Roomba.createAttributes().build());
        event.put(EXOSKELETON.get(), Exoskeleton.createAttributes().build());
        event.put(BIPED_ARMOR_STAND.get(), LivingEntity.createLivingAttributes().build());
        event.put(CENTAUR_ARMOR_STAND.get(), LivingEntity.createLivingAttributes().build());
        event.put(LEGLESS_ARMOR_STAND.get(), LivingEntity.createLivingAttributes().build());
    }
}
