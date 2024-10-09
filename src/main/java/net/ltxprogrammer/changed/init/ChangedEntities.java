package net.ltxprogrammer.changed.init;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.SeatEntity;
import net.ltxprogrammer.changed.entity.beast.*;
import net.ltxprogrammer.changed.entity.beast.boss.BehemothHandLeft;
import net.ltxprogrammer.changed.entity.beast.boss.BehemothHandRight;
import net.ltxprogrammer.changed.entity.beast.boss.BehemothHead;
import net.ltxprogrammer.changed.entity.projectile.GasParticle;
import net.ltxprogrammer.changed.entity.projectile.LatexInkball;
import net.ltxprogrammer.changed.entity.robot.Roomba;
import net.ltxprogrammer.changed.world.biome.ChangedBiomeInterface;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
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
                if (Registry.ITEM.get(new ResourceLocation(loc.getNamespace(), loc.getPath() + "_spawn_egg")) instanceof ForgeSpawnEggItem item)
                    return new Pair<>(item.getColor(0), item.getColor(1));
                else
                    return new Pair<>(0xF0F0F0, 0xF0F0F0);
            } catch (Exception ex) {
                return new Pair<>(0xF0F0F0, 0xF0F0F0);
            }
        });
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
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, Changed.MODID);
    public static final Map<RegistryObject<? extends EntityType<?>>, RegistryObject<ForgeSpawnEggItem>> SPAWN_EGGS = new HashMap<>();
    public static final RegistryObject<EntityType<WhiteLatexWolfFemale>> WHITE_LATEX_WOLF_FEMALE = registerSpawning("white_latex_wolf_female", 0xFFFFFF, 0xFF927F,
            EntityType.Builder.of(WhiteLatexWolfFemale::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, WhiteLatexWolfFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<WhiteLatexWolfMale>> WHITE_LATEX_WOLF_MALE = registerSpawning("white_latex_wolf_male", 0xFFFFFF, 0xFF927F,
            EntityType.Builder.of(WhiteLatexWolfMale::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, WhiteLatexWolfMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<WhiteWolfMale>> WHITE_WOLF_MALE = registerSpawning("white_wolf_male", 0xFFFFFF, 0xFAE9E4,
            EntityType.Builder.of(WhiteWolfMale::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, WhiteWolfMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<WhiteWolfFemale>> WHITE_WOLF_FEMALE = registerSpawning("white_wolf_female", 0xFFFFFF, 0xFAE9E4,
            EntityType.Builder.of(WhiteWolfFemale::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, WhiteWolfFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<WhiteLatexKnight>> WHITE_LATEX_KNIGHT = registerSpawning("white_latex_knight", 0xFFFFFF, 0x1E1E1E,
            EntityType.Builder.of(WhiteLatexKnight::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, WhiteLatexKnight::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<WhiteLatexCentaur>> WHITE_LATEX_CENTAUR = registerSpawning("white_latex_centaur", 0xFFFFFF, 0x1E1E1E,
            EntityType.Builder.of(WhiteLatexCentaur::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(1.1F, 2.0F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, WhiteLatexCentaur::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<HeadlessKnight>> HEADLESS_KNIGHT = registerSpawning("headless_knight", 0xFFFFFF, 0x1E1E1E,
            EntityType.Builder.of(HeadlessKnight::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(1.1F, 1.1F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, HeadlessKnight::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<WhiteLatexKnightFusion>> WHITE_LATEX_KNIGHT_FUSION = registerSpawning("white_latex_knight_fusion", 0xFFFFFF, 0x0072ff,
            EntityType.Builder.of(WhiteLatexKnightFusion::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, WhiteLatexKnightFusion::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<CrystalWolf>> CRYSTAL_WOLF = registerSpawning("crystal_wolf", 0x393939, 0xCF003E,
            EntityType.Builder.of(CrystalWolf::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, CrystalWolf::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<CrystalWolfHorned>> CRYSTAL_WOLF_HORNED = registerSpawning("crystal_wolf_horned", 0x393939, 0xFF014E,
            EntityType.Builder.of(CrystalWolfHorned::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, CrystalWolfHorned::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexDeer>> LATEX_DEER = registerSpawning("latex_deer", 0xD8BC99, 0xFBE5BC,
            EntityType.Builder.of(LatexDeer::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexDeer::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexBee>> LATEX_BEE = registerSpawning("latex_bee", 0xFFBF75, 0xFF9E58,
            EntityType.Builder.of(LatexBee::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexBee::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexPinkDeer>> LATEX_PINK_DEER = registerSpawning("latex_pink_deer", 0xF2AFBC, 0xCA636A,
            EntityType.Builder.of(LatexPinkDeer::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexPinkDeer::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexKeonWolf>> LATEX_KEON_WOLF = registerSpawning("latex_keon_wolf", 0x959CA5, 0x272727,
            EntityType.Builder.of(LatexKeonWolf::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexKeonWolf::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<GasWolf>> GAS_WOLF = registerSpawning("gas_wolf", 0x5D4743, 0xFFFFFF,
            EntityType.Builder.of(GasWolf::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, GasWolf::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<DarkDragon>> DARK_DRAGON = registerSpawning("dark_dragon", 0x393939, 0x909090,
            EntityType.Builder.of(DarkDragon::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, DarkDragon::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<DarkLatexWolfMale>> DARK_LATEX_WOLF_MALE = registerSpawning("dark_latex_wolf_male", 0x393939, 0x303030,
            EntityType.Builder.of(DarkLatexWolfMale::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, DarkLatexWolfMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<DarkLatexWolfFemale>> DARK_LATEX_WOLF_FEMALE = registerSpawning("dark_latex_wolf_female", 0x393939, 0x303030,
            EntityType.Builder.of(DarkLatexWolfFemale::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, DarkLatexWolfFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<DarkLatexWolfPup>> DARK_LATEX_WOLF_PUP = registerSpawning("dark_latex_wolf_pup", 0x454545, 0x303030,
            EntityType.Builder.of(DarkLatexWolfPup::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.6F, 0.85F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, DarkLatexWolfPup::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<DarkLatexYufeng>> DARK_LATEX_YUFENG = registerSpawning("dark_latex_yufeng", 0x393939, 0x0,
            EntityType.Builder.of(DarkLatexYufeng::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, DarkLatexYufeng::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<PhageLatexWolfMale>> PHAGE_LATEX_WOLF_MALE = registerSpawning("phage_latex_wolf_male", 0x393939, 0x3499ff,
            EntityType.Builder.of(PhageLatexWolfMale::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, PhageLatexWolfMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<PhageLatexWolfFemale>> PHAGE_LATEX_WOLF_FEMALE = registerSpawning("phage_latex_wolf_female", 0x393939, 0x3499ff,
            EntityType.Builder.of(PhageLatexWolfFemale::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, PhageLatexWolfFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<Beifeng>> BEIFENG = registerSpawning("beifeng", 0x51659D, 0xFFE852,
            EntityType.Builder.of(Beifeng::new, ChangedMobCategories.UNDERGROUND).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, Beifeng::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexBenignWolf>> BENIGN_LATEX_WOLF = registerSpawning("latex_benign_wolf", 0x282828, 0x292929,
            EntityType.Builder.of(LatexBenignWolf::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexBenignWolf::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexBlueDragon>> BLUE_LATEX_DRAGON = registerSpawning("latex_blue_dragon", 0xCDFEFF, 0x5c72ab,
            EntityType.Builder.of(LatexBlueDragon::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexBlueDragon::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexBlueWolf>> BLUE_LATEX_WOLF = registerSpawning("latex_blue_wolf", 0x8ad6e7, 0x7395c0,
            EntityType.Builder.of(LatexBlueWolf::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexBlueWolf::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexCrocodile>> LATEX_CROCODILE = registerSpawning("latex_crocodile", 0x216d50, 0x43b058,
            EntityType.Builder.of(LatexCrocodile::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.8F, 2.3F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexCrocodile::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexFennecFox>> LATEX_FENNEC_FOX = registerSpawning("latex_fennec_fox", 0xffe195, 0x84484b,
            EntityType.Builder.of(LatexFennecFox::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexFennecFox::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<GreenLizard>> GREEN_LIZARD = registerSpawning("green_lizard", 0xB3e53A, 0xFBE5BC,
            EntityType.Builder.of(GreenLizard::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, GreenLizard::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexHypnoCat>> LATEX_HYPNO_CAT = registerSpawning("latex_hypno_cat", 0x52596D, 0xD7FF46,
            EntityType.Builder.of(LatexHypnoCat::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexHypnoCat::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexLeaf>> LATEX_LEAF = registerSpawning("latex_leaf", 0xBFF298, 0x76C284,
            EntityType.Builder.of(LatexLeaf::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexLeaf::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexSquirrel>> LATEX_SQUIRREL = registerSpawning("latex_squirrel", 0xFFE8A5, 0xAC8F64,
            EntityType.Builder.of(LatexSquirrel::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexSquirrel::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexMantaRayFemale>> LATEX_MANTA_RAY_FEMALE = registerSpawning("latex_manta_ray_female", 0x6f7696, 0xd2d9e1,
            EntityType.Builder.of(LatexMantaRayFemale::new, ChangedMobCategories.AQUATIC).clientTrackingRange(10).sized(0.7F, 1.58625F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexMantaRayFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexMantaRayMale>> LATEX_MANTA_RAY_MALE = registerSpawning("latex_manta_ray_male", 0x6f7696, 0xd2d9e1,
            EntityType.Builder.of(LatexMantaRayMale::new, ChangedMobCategories.AQUATIC).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexMantaRayMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexMedusaCat>> LATEX_MEDUSA_CAT = registerSpawning("latex_medusa_cat", 0xFFDB4F, 0xF398B7,
            EntityType.Builder.of(LatexMedusaCat::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexMedusaCat::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexMingCat>> LATEX_MING_CAT = registerSpawning("latex_ming_cat", 0xD2A87F, 0x75483F,
            EntityType.Builder.of(LatexMingCat::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexMingCat::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexMermaidShark>> LATEX_MERMAID_SHARK = registerSpawning("latex_mermaid_shark", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(LatexMermaidShark::new, ChangedMobCategories.AQUATIC).clientTrackingRange(10).sized(0.7F, 1.58625F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexMermaidShark::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexMoth>> LATEX_MOTH = registerSpawning("latex_moth", 0xFBE5BC, 0xD8BC99,
            EntityType.Builder.of(LatexMoth::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexMoth::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexMutantBloodcellWolf>> LATEX_MUTANT_BLOODCELL_WOLF = registerSpawning("latex_mutant_bloodcell_wolf", 0xD7D7D7, 0x8A8A8A,
            EntityType.Builder.of(LatexMutantBloodcellWolf::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexMutantBloodcellWolf::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexSiren>> LATEX_SIREN = registerSpawning("latex_siren", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(LatexSiren::new, ChangedMobCategories.AQUATIC).clientTrackingRange(10).sized(0.7F, 1.58625F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexSiren::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexSnake>> LATEX_SNAKE = registerSpawning("latex_snake", 0xFFFFFF, 0x7E7E7E,
            EntityType.Builder.of(LatexSnake::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexSnake::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexMimicPlant>> LATEX_MIMIC_PLANT = registerSpawning("latex_mimic_plant", 0x446d5d, 0x729c6a,
            EntityType.Builder.of(LatexMimicPlant::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexMimicPlant::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexPinkWyvern>> LATEX_PINK_WYVERN = registerSpawning("latex_pink_wyvern", 0xf2aaba, 0xd1626d,
            EntityType.Builder.of(LatexPinkWyvern::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexPinkWyvern::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexPinkYuinDragon>> LATEX_PINK_YUIN_DRAGON = registerSpawning("latex_pink_yuin_dragon", 0xFFFFFF, 0xF2AABA,
            EntityType.Builder.of(LatexPinkYuinDragon::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexPinkYuinDragon::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexPurpleFox>> LATEX_PURPLE_FOX = registerSpawning("latex_purple_fox", 0xcebbe8, 0xf1e3f1,
            EntityType.Builder.of(LatexPurpleFox::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexPurpleFox::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexRaccoon>> LATEX_RACCOON = registerSpawning("latex_raccoon", 0x949494, 0x535353,
            EntityType.Builder.of(LatexRaccoon::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexRaccoon::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexRedDragon>> LATEX_RED_DRAGON = registerSpawning("latex_red_dragon", 0xa54f58, 0xfcfa4a,
            EntityType.Builder.of(LatexRedDragon::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexRedDragon::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexRedPanda>> LATEX_RED_PANDA = registerSpawning("latex_red_panda", 0xbd4040, 0x663d53,
            EntityType.Builder.of(LatexRedPanda::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexRedPanda::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexShark>> LATEX_SHARK = registerSpawning("latex_shark", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(LatexShark::new, ChangedMobCategories.AQUATIC).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexShark::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<BuffLatexSharkMale>> LATEX_SHARK_MALE = registerSpawning("latex_shark_male", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(BuffLatexSharkMale::new, ChangedMobCategories.AQUATIC).clientTrackingRange(10).sized(0.8F, 2.2F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, BuffLatexSharkMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<BuffLatexSharkFemale>> LATEX_SHARK_FEMALE = registerSpawning("latex_shark_female", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(BuffLatexSharkFemale::new, ChangedMobCategories.AQUATIC).clientTrackingRange(10).sized(0.8F, 2.25F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, BuffLatexSharkFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<SniperDog>> SNIPER_DOG = registerSpawning("sniper_dog", 0xEF8F44, 0x894633,
            EntityType.Builder.of(SniperDog::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, SniperDog::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexAlien>> LATEX_ALIEN = registerSpawning("latex_alien", 0x1983A9, 0x2DAAB9,
            EntityType.Builder.of(LatexAlien::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexAlien::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexSnowLeopardMale>> LATEX_SNOW_LEOPARD_MALE = registerSpawning("latex_snow_leopard_male", 0x9C9C9C, 0x272727,
            EntityType.Builder.of(LatexSnowLeopardMale::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexSnowLeopardMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexSnowLeopardFemale>> LATEX_SNOW_LEOPARD_FEMALE = registerSpawning("latex_snow_leopard_female", 0x9C9C9C, 0x272727,
            EntityType.Builder.of(LatexSnowLeopardFemale::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexSnowLeopardFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexSquidDogFemale>> LATEX_SQUID_DOG_FEMALE = registerSpawning("latex_squid_dog_female", 0xFFFFFF, 0x0,
            EntityType.Builder.of(LatexSquidDogFemale::new, ChangedMobCategories.AQUATIC).clientTrackingRange(10).sized(0.8F, 2.1F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexSquidDogFemale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexSquidDogMale>> LATEX_SQUID_DOG_MALE = registerSpawning("latex_squid_dog_male", 0xFFFFFF, 0x0,
            EntityType.Builder.of(LatexSquidDogMale::new, ChangedMobCategories.AQUATIC).clientTrackingRange(10).sized(0.8F, 2.1F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexSquidDogMale::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexStiger>> LATEX_STIGER = registerSpawning("latex_stiger", 0x7b4251, 0xe0cfd9,
            EntityType.Builder.of(LatexStiger::new, ChangedMobCategories.UNDERGROUND).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexStiger::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexTigerShark>> LATEX_TIGER_SHARK = registerSpawning("latex_tiger_shark", 0x969696, 0x0,
            EntityType.Builder.of(LatexTigerShark::new, ChangedMobCategories.AQUATIC).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexTigerShark::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexTrafficConeDragon>> LATEX_TRAFFIC_CONE_DRAGON = registerSpawning("latex_traffic_cone_dragon", 0xFFD201, 0x0,
            EntityType.Builder.of(LatexTrafficConeDragon::new, ChangedMobCategories.UNDERGROUND).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexTrafficConeDragon::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexTranslucentLizard>> LATEX_TRANSLUCENT_LIZARD = registerSpawning("latex_translucent_lizard", 0xffb84b, 0xFF904C,
            EntityType.Builder.of(LatexTranslucentLizard::new, ChangedMobCategories.UNDERGROUND).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexTranslucentLizard::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexOrca>> LATEX_ORCA = registerSpawning("latex_orca", 0x393939, 0xFFFFFF,
            EntityType.Builder.of(LatexOrca::new, ChangedMobCategories.AQUATIC).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, LatexOrca::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexOtter>> LATEX_OTTER = registerSpawning("latex_otter", 0x5D4743, 0xB6957C,
            EntityType.Builder.of(LatexOtter::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexOtter::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexWatermelonCat>> LATEX_WATERMELON_CAT = registerSpawning("latex_watermelon_cat", 0x545454, 0xC7FF5A,
            EntityType.Builder.of(LatexWatermelonCat::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexWatermelonCat::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexWhiteTiger>> LATEX_WHITE_TIGER = registerSpawning("latex_white_tiger", 0xFFFFFF, 0xACACAC,
            EntityType.Builder.of(LatexWhiteTiger::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexWhiteTiger::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<LatexYuin>> LATEX_YUIN = registerSpawning("latex_yuin", 0xFFFFFF, 0x7442cc,
            EntityType.Builder.of(LatexYuin::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, LatexYuin::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<MilkPudding>> MILK_PUDDING = registerSpawning("milk_pudding", 0xFFFFFF, 0xF0F0F0,
            EntityType.Builder.of(MilkPudding::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.6F, 0.5F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, MilkPudding::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<Shark>> SHARK = registerSpawning("shark", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(Shark::new, ChangedMobCategories.AQUATIC).clientTrackingRange(10).sized(0.9F, 0.6F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.IN_WATER, Shark::checkEntitySpawnRules);
    public static final RegistryObject<EntityType<PureWhiteLatexWolf>> PURE_WHITE_LATEX_WOLF = registerSpawning("pure_white_latex_wolf", 0xFFFFFF, 0xFAFAFA,
            EntityType.Builder.of(PureWhiteLatexWolf::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::overworldOnly, SpawnPlacements.Type.ON_GROUND, PureWhiteLatexWolf::checkEntitySpawnRules);

    public static final RegistryObject<EntityType<SeatEntity>> SEAT_ENTITY = REGISTRY.register("seat_entity",
            () -> EntityType.Builder.of(SeatEntity::new, MobCategory.MISC).sized(0.01f, 0.01f).build("seat_entity"));

    public static final RegistryObject<EntityType<DarkLatexWolfPartial>> DARK_LATEX_WOLF_PARTIAL = registerNoEgg("dark_latex_wolf_partial", 0x393939, 0x303030,
            EntityType.Builder.of(DarkLatexWolfPartial::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F));
    public static final RegistryObject<EntityType<LatexHuman>> LATEX_HUMAN = registerNoEgg("latex_human", 0x8B8B8B, 0xC6C6C6,
            EntityType.Builder.of(LatexHuman::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.6F, 1.8F));
    public static final RegistryObject<EntityType<SpecialLatex>> SPECIAL_LATEX = registerNoEgg("special_latex",
            EntityType.Builder.of(SpecialLatex::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(0.7F, 1.93F));

    public static final RegistryObject<EntityType<BehemothHead>> BEHEMOTH_HEAD = registerNoEgg("behemoth_head",
            EntityType.Builder.of(BehemothHead::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(3.0f, 3.0f));
    public static final RegistryObject<EntityType<BehemothHandLeft>> BEHEMOTH_HAND_LEFT = registerNoEgg("behemoth_hand_left",
            EntityType.Builder.of(BehemothHandLeft::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(2.0f, 2.0f));
    public static final RegistryObject<EntityType<BehemothHandRight>> BEHEMOTH_HAND_RIGHT = registerNoEgg("behemoth_hand_right",
            EntityType.Builder.of(BehemothHandRight::new, ChangedMobCategories.CHANGED).clientTrackingRange(10).sized(2.0f, 2.0f));

    public static final RegistryObject<EntityType<Roomba>> ROOMBA = REGISTRY.register("roomba",
            () -> EntityType.Builder.of(Roomba::new, MobCategory.MISC).clientTrackingRange(10).sized(0.6F, 0.125f).build("roomba"));

    public static final RegistryObject<EntityType<LatexInkball>> LATEX_INKBALL = REGISTRY.register("latex_inkball",
            () -> EntityType.Builder.<LatexInkball>of(LatexInkball::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("latex_inkball"));
    public static final RegistryObject<EntityType<GasParticle>> GAS_PARTICLE = REGISTRY.register("gas_particle",
            () -> EntityType.Builder.of(GasParticle::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(4).updateInterval(10).build("gas_particle"));

    // TODO make register function for non `ChangedEntity`

    private static <T extends ChangedEntity> RegistryObject<EntityType<T>> registerNoEgg(
            String name,
            int eggBack,
            int eggHighlight,
            EntityType.Builder<T> builder) {
        String regName = Changed.modResource(name).toString();
        ENTITY_COLOR_MAP.put(Changed.modResource(name), new Pair<>(eggBack, eggHighlight));
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
        ENTITY_COLOR_MAP.put(Changed.modResource(name), new Pair<>(eggBack, eggHighlight));
        String regName = Changed.modResource(name).toString();
        RegistryObject<EntityType<T>> entityType = REGISTRY.register(name, () -> builder.build(regName));
        INIT_FUNC_REGISTRY.add(ChangedEntity.getInit(entityType, spawnType, spawnPredicate));
        ATTR_FUNC_REGISTRY.add(new Pair<>(entityType::get, attributes));
        RegistryObject<ForgeSpawnEggItem> spawnEggItem = ChangedItems.register(name + "_spawn_egg", () -> new ForgeSpawnEggItem(entityType, eggBack, eggHighlight,
                new Item.Properties().tab(ChangedTabs.TAB_CHANGED_ENTITIES)));
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
    }

    @Mod.EventBusSubscriber
    public static class EventListener {
        @SubscribeEvent
        public static void addSpawners(BiomeLoadingEvent event) { // Inject spawns into vanilla / modded biomes (not including changed)
            if (event.getName() != null && event.getName().getNamespace().equals(Changed.MODID))
                return;

            final var spawns = event.getSpawns();

            /* Cave spawning entities */

            ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.UNDERGROUND, LATEX_STIGER, 100, 1, 3, 0.7, 0.15);
            ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.UNDERGROUND, LATEX_TRAFFIC_CONE_DRAGON, 100, 1, 3, 0.7, 0.15);
            ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.UNDERGROUND, LATEX_TRANSLUCENT_LIZARD, 100, 1, 3, 0.7, 0.15);

            // Passive
            ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.UNDERGROUND, BEIFENG, 10, 1, 1, 0.7, 0.15);

            /* Surface spawning entities */

            if (event.getCategory() == Biome.BiomeCategory.PLAINS) {
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, WHITE_LATEX_WOLF_MALE, 100, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, WHITE_LATEX_WOLF_FEMALE, 100, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, WHITE_LATEX_KNIGHT, 50, 1, 1, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, WHITE_LATEX_CENTAUR, 20, 1, 1, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, HEADLESS_KNIGHT, 40, 1, 1, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, WHITE_LATEX_KNIGHT_FUSION, 20, 1, 1, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_KEON_WOLF, 10, 1, 1, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_MING_CAT, 10, 1, 1, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, MILK_PUDDING, 80, 1, 1, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, DARK_LATEX_WOLF_MALE, 10, 1, 1, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, DARK_LATEX_WOLF_FEMALE, 10, 1, 1, 0.7, 0.15);

                // Passive
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, WHITE_WOLF_MALE, 10, 1, 2, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, WHITE_WOLF_FEMALE, 10, 1, 2, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, CRYSTAL_WOLF, 10, 1, 2, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, CRYSTAL_WOLF_HORNED, 10, 1, 2, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, GAS_WOLF, 10, 1, 2, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, SNIPER_DOG, 10, 1, 2, 0.7, 0.15);
            }

            if (event.getCategory() == Biome.BiomeCategory.MOUNTAIN) {
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, BLUE_LATEX_WOLF, 70, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_PINK_WYVERN, 60, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_PINK_YUIN_DRAGON, 50, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_YUIN, 100, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, DARK_LATEX_YUFENG, 20, 1, 1, 0.7, 0.15);

                // Passive
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, DARK_DRAGON, 5, 1, 1, 0.7, 0.15);
            }

            if (event.getCategory() == Biome.BiomeCategory.FOREST) {
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_DEER, 100, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_PINK_DEER, 50, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_BEE, 80, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_LEAF, 100, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_SQUIRREL, 80, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_MOTH, 40, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_MIMIC_PLANT, 50, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_RACCOON, 30, 1, 3, 0.7, 0.15);
            }

            if (event.getCategory() == Biome.BiomeCategory.JUNGLE) {
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_RED_PANDA, 100, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_WATERMELON_CAT, 30, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_MIMIC_PLANT, 70, 1, 3, 0.7, 0.15);
            }

            if (event.getCategory() == Biome.BiomeCategory.DESERT) {
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_FENNEC_FOX, 100, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_SNAKE, 100, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_ALIEN, 20, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, GREEN_LIZARD, 10, 1, 2, 0.7, 0.15);
            }

            if (event.getCategory() == Biome.BiomeCategory.BEACH && !event.getName().equals(Biomes.STONY_SHORE.getRegistryName())) {
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_OTTER, 100, 1, 3, 0.7, 0.15);
            }

            if (event.getCategory() == Biome.BiomeCategory.MESA) {
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_RED_DRAGON, 80, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_FENNEC_FOX, 50, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_ALIEN, 10, 1, 3, 0.7, 0.15);
            }

            if (event.getCategory() == Biome.BiomeCategory.SWAMP) {
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_CROCODILE, 100, 1, 3, 0.7, 0.5);
            }

            if (event.getCategory() == Biome.BiomeCategory.SAVANNA) {
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_HYPNO_CAT, 60, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_MEDUSA_CAT, 40, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, GREEN_LIZARD, 20, 1, 2, 0.7, 0.15);
            }

            if (event.getCategory() == Biome.BiomeCategory.TAIGA || event.getCategory() == Biome.BiomeCategory.ICY) {
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, BLUE_LATEX_DRAGON, 50, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_PURPLE_FOX, 30, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_SNOW_LEOPARD_MALE, 100, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_SNOW_LEOPARD_FEMALE, 100, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.CHANGED, LATEX_WHITE_TIGER, 80, 1, 3, 0.7, 0.15);
            }

            /* Water spawning entities */

            if (event.getCategory() == Biome.BiomeCategory.OCEAN || event.getCategory() == Biome.BiomeCategory.RIVER) {
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.AQUATIC, LATEX_MANTA_RAY_MALE, 30, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.AQUATIC, LATEX_MANTA_RAY_FEMALE, 30, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.AQUATIC, LATEX_MERMAID_SHARK, 20, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.AQUATIC, LATEX_SIREN, 20, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.AQUATIC, LATEX_SHARK, 100, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.AQUATIC, LATEX_SHARK_MALE, 30, 1, 3, 0.7, 0.5);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.AQUATIC, LATEX_SHARK_FEMALE, 30, 1, 3, 0.7, 0.5);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.AQUATIC, LATEX_TIGER_SHARK, 20, 1, 3, 0.7, 0.35);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.AQUATIC, LATEX_ORCA, 80, 1, 3, 0.7, 0.15);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.AQUATIC, LATEX_SQUID_DOG_MALE, 80, 1, 3, 0.7, 0.35);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.AQUATIC, LATEX_SQUID_DOG_FEMALE, 80, 1, 3, 0.7, 0.35);
                ChangedBiomeInterface.addSpawn(spawns, ChangedMobCategories.AQUATIC, SHARK, 100, 1, 3, 0.7, 0.15);
            }
        }
    }
}
