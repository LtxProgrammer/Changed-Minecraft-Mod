package net.ltxprogrammer.changed.init;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.entity.CardboardBoxBlockEntity;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.beast.*;
import net.ltxprogrammer.changed.entity.beast.boss.BehemothHandLeft;
import net.ltxprogrammer.changed.entity.beast.boss.BehemothHandRight;
import net.ltxprogrammer.changed.entity.beast.boss.BehemothHead;
import net.ltxprogrammer.changed.entity.projectile.LatexInkball;
import net.ltxprogrammer.changed.entity.projectile.GasParticle;
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
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.ltxprogrammer.changed.entity.variant.LatexVariant.getNextEntId;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedEntities {
    public interface VoidConsumer { void accept(); }

    private static final Map<ResourceLocation, Pair<Integer, Integer>> ENTITY_COLOR_MAP = new HashMap<>();
    private static final List<Pair<Supplier<EntityType<? extends LatexEntity>>, Supplier<AttributeSupplier.Builder>>> ATTR_FUNC_REGISTRY = new ArrayList<>();
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

    public static boolean overworldSpawning(Biome.BiomeCategory category) {
        return category != Biome.BiomeCategory.NETHER && category != Biome.BiomeCategory.THEEND;
    }

    public static boolean plainsSpawning(Biome.BiomeCategory category) {
        return category == Biome.BiomeCategory.PLAINS;
    }

    public static boolean mountainSpawning(Biome.BiomeCategory category) {
        return category == Biome.BiomeCategory.MOUNTAIN;
    }

    public static boolean forestSpawning(Biome.BiomeCategory category) {
        return category == Biome.BiomeCategory.FOREST;
    }

    public static boolean desertSpawning(Biome.BiomeCategory category) {
        return category == Biome.BiomeCategory.DESERT;
    }

    public static boolean beachSpawning(Biome.BiomeCategory category) {
        return category == Biome.BiomeCategory.BEACH || category == Biome.BiomeCategory.RIVER;
    }

    public static boolean mesaSpawning(Biome.BiomeCategory category) {
        return category == Biome.BiomeCategory.MESA;
    }

    public static boolean oceanSpawning(Biome.BiomeCategory category) {
        return category == Biome.BiomeCategory.OCEAN || category == Biome.BiomeCategory.RIVER;
    }

    public static boolean swampSpawning(Biome.BiomeCategory category) {
        return category == Biome.BiomeCategory.SWAMP;
    }

    public static boolean undergroundSpawning(Biome.BiomeCategory category) {
        return category == Biome.BiomeCategory.UNDERGROUND;
    }

    public static boolean savannaSpawning(Biome.BiomeCategory category) {
        return category == Biome.BiomeCategory.SAVANNA;
    }

    public static boolean taigaSpawning(Biome.BiomeCategory category) {
        return category == Biome.BiomeCategory.TAIGA || category == Biome.BiomeCategory.ICY;
    }

    public static boolean jungleSpawning(Biome.BiomeCategory category) {
        return category == Biome.BiomeCategory.JUNGLE;
    }

    public static boolean jungleAndForestSpawning(Biome.BiomeCategory category) {
        return category == Biome.BiomeCategory.JUNGLE || category == Biome.BiomeCategory.FOREST;
    }

    public static boolean noSpawning(Biome.BiomeCategory category) {
        return false;
    }

    public static final List<BiConsumer<BiomeLoadingEvent, List<MobSpawnSettings.SpawnerData>>> SPAWNING_ENTITY = new ArrayList<>();
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, Changed.MODID);
    public static final List<Supplier<ForgeSpawnEggItem>> SPAWN_EGGS = new ArrayList<>();
    public static final RegistryObject<EntityType<LightLatexWolfFemale>> LIGHT_LATEX_WOLF_FEMALE = register("light_latex_wolf_female", 0xFFFFFF, 0xFF927F,
            EntityType.Builder.of(LightLatexWolfFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LightLatexWolfMale>> LIGHT_LATEX_WOLF_MALE = register("light_latex_wolf_male", 0xFFFFFF, 0xFF927F,
            EntityType.Builder.of(LightLatexWolfMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LightLatexWolfOrganic>> LIGHT_LATEX_WOLF_ORGANIC = registerReducedSpawn("light_latex_wolf_organic", 0xFFFFFF, 0xFAE9E4,
            EntityType.Builder.of(LightLatexWolfOrganic::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LightLatexKnight>> LIGHT_LATEX_KNIGHT = register("light_latex_knight", 0xFFFFFF, 0x1E1E1E,
            EntityType.Builder.of(LightLatexKnight::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LightLatexCentaur>> LIGHT_LATEX_CENTAUR = register("light_latex_centaur", 0xFFFFFF, 0x1E1E1E,
            EntityType.Builder.of(LightLatexCentaur::new, MobCategory.MONSTER).clientTrackingRange(10).sized(1.1F, 2.2F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<HeadlessKnight>> HEADLESS_KNIGHT = register("headless_knight", 0xFFFFFF, 0x1E1E1E,
            EntityType.Builder.of(HeadlessKnight::new, MobCategory.MONSTER).clientTrackingRange(10).sized(1.1F, 1.1F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LightLatexKnightFusion>> LIGHT_LATEX_KNIGHT_FUSION = register("light_latex_knight_fusion", 0xFFFFFF, 0x0072ff,
            EntityType.Builder.of(LightLatexKnightFusion::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LatexCrystalWolf>> LATEX_CRYSTAL_WOLF = registerReducedSpawn("latex_crystal_wolf", 0x393939, 0xFF014E,
            EntityType.Builder.of(LatexCrystalWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LatexDeer>> LATEX_DEER = register("latex_deer", 0xCFBC9B, 0xF4E5BE,
            EntityType.Builder.of(LatexDeer::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::forestSpawning);
    public static final RegistryObject<EntityType<LatexBee>> LATEX_BEE = register("latex_bee", 0xFFBF75, 0xFF9E58,
            EntityType.Builder.of(LatexBee::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::forestSpawning);
    public static final RegistryObject<EntityType<LatexPinkDeer>> LATEX_PINK_DEER = register("latex_pink_deer", 0xF2AFBC, 0xCA636A,
            EntityType.Builder.of(LatexPinkDeer::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::forestSpawning);
    public static final RegistryObject<EntityType<LatexSilverFox>> LATEX_SILVER_FOX = register("latex_silver_fox", 0x959CA5, 0x272727,
            EntityType.Builder.of(LatexSilverFox::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<AerosolLatexWolf>> AEROSOL_LATEX_WOLF = registerReducedSpawn("aerosol_latex_wolf", 0x5D4743, 0xFFFFFF,
            EntityType.Builder.of(AerosolLatexWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<DarkLatexDragon>> DARK_LATEX_DRAGON = registerReducedSpawn("dark_latex_dragon", 0x393939, 0x909090,
            EntityType.Builder.of(DarkLatexDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::mountainSpawning);
    public static final RegistryObject<EntityType<DarkLatexWolfMale>> DARK_LATEX_WOLF_MALE = register("dark_latex_wolf_male", 0x393939, 0x303030,
            EntityType.Builder.of(DarkLatexWolfMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::noSpawning);
    public static final RegistryObject<EntityType<DarkLatexWolfFemale>> DARK_LATEX_WOLF_FEMALE = register("dark_latex_wolf_female", 0x393939, 0x303030,
            EntityType.Builder.of(DarkLatexWolfFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::noSpawning);
    /*public static final RegistryObject<EntityType<DarkLatexPup>> DARK_LATEX_PUP = register("dark_latex_pup", 0x454545, 0x303030,
            EntityType.Builder.of(DarkLatexPup::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.6F, 0.9F));*/
    public static final RegistryObject<EntityType<DarkLatexYufeng>> DARK_LATEX_YUFENG = register("dark_latex_yufeng", 0x393939, 0x0,
            EntityType.Builder.of(DarkLatexYufeng::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::mountainSpawning);
    public static final RegistryObject<EntityType<LatexBeifeng>> LATEX_BEIFENG = registerReducedSpawn("latex_beifeng", 0x51659D, 0xFFE852,
            EntityType.Builder.of(LatexBeifeng::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::undergroundSpawning);
    public static final RegistryObject<EntityType<LatexBenignWolf>> LATEX_BENIGN_WOLF = register("latex_benign_wolf", 0x282828, 0x292929,
            EntityType.Builder.of(LatexBenignWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::noSpawning);
    public static final RegistryObject<EntityType<LatexBlueDragon>> LATEX_BLUE_DRAGON = register("latex_blue_dragon", 0xCDFEFF, 0x5c72ab,
            EntityType.Builder.of(LatexBlueDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::taigaSpawning);
    public static final RegistryObject<EntityType<LatexBlueWolf>> LATEX_BLUE_WOLF = register("latex_blue_wolf", 0x8ad6e7, 0x7395c0,
            EntityType.Builder.of(LatexBlueWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::mountainSpawning);
    public static final RegistryObject<EntityType<LatexCrocodile>> LATEX_CROCODILE = register("latex_crocodile", 0x216d50, 0x43b058,
            EntityType.Builder.of(LatexCrocodile::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.8F, 2.5F),
            ChangedEntities::swampSpawning);
    public static final RegistryObject<EntityType<LatexHypnoCat>> LATEX_HYPNO_CAT = register("latex_hypno_cat", 0x52596D, 0xD7FF46,
            EntityType.Builder.of(LatexHypnoCat::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::savannaSpawning);
    public static final RegistryObject<EntityType<LatexLeaf>> LATEX_LEAF = register("latex_leaf", 0xBFF298, 0x76C284,
            EntityType.Builder.of(LatexLeaf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::forestSpawning);
    public static final RegistryObject<EntityType<LatexSquirrel>> LATEX_SQUIRREL = register("latex_squirrel", 0xFFE8A5, 0xAC8F64,
            EntityType.Builder.of(LatexSquirrel::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::forestSpawning);
    public static final RegistryObject<EntityType<LatexMantaRayFemale>> LATEX_MANTA_RAY_FEMALE = register("latex_manta_ray_female", 0x6f7696, 0xd2d9e1,
            EntityType.Builder.of(LatexMantaRayFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexMantaRayMale>> LATEX_MANTA_RAY_MALE = register("latex_manta_ray_male", 0x6f7696, 0xd2d9e1,
            EntityType.Builder.of(LatexMantaRayMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexMedusaCat>> LATEX_MEDUSA_CAT = register("latex_medusa_cat", 0xFFDB4F, 0xF398B7,
            EntityType.Builder.of(LatexMedusaCat::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::savannaSpawning);
    public static final RegistryObject<EntityType<LatexMermaidShark>> LATEX_MERMAID_SHARK = register("latex_mermaid_shark", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(LatexMermaidShark::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexMoth>> LATEX_MOTH = registerReducedSpawn("latex_moth", 0xFBE5BC, 0xD8BC99,
            EntityType.Builder.of(LatexMoth::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::forestSpawning);
    public static final RegistryObject<EntityType<LatexSiren>> LATEX_SIREN = register("latex_siren", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(LatexSiren::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexSnake>> LATEX_SNAKE = register("latex_snake", 0xFFFFFF, 0x7E7E7E,
            EntityType.Builder.of(LatexSnake::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::desertSpawning);
    public static final RegistryObject<EntityType<LatexMimicPlant>> LATEX_MIMIC_PLANT = register("latex_mimic_plant", 0x446d5d, 0x729c6a,
            EntityType.Builder.of(LatexMimicPlant::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::jungleAndForestSpawning);
    public static final RegistryObject<EntityType<LatexPinkWyvern>> LATEX_PINK_WYVERN = register("latex_pink_wyvern", 0xf2aaba, 0xd1626d,
            EntityType.Builder.of(LatexPinkWyvern::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::mountainSpawning);
    public static final RegistryObject<EntityType<LatexPinkYuinDragon>> LATEX_PINK_YUIN_DRAGON = register("latex_pink_yuin_dragon", 0xFFFFFF, 0xF2AABA,
            EntityType.Builder.of(LatexPinkYuinDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::mountainSpawning);
    public static final RegistryObject<EntityType<LatexPurpleFox>> LATEX_PURPLE_FOX = register("latex_purple_fox", 0xcebbe8, 0xf1e3f1,
            EntityType.Builder.of(LatexPurpleFox::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::taigaSpawning);
    public static final RegistryObject<EntityType<LatexRaccoon>> LATEX_RACCOON = registerReducedSpawn("latex_raccoon", 0x949494, 0x535353,
            EntityType.Builder.of(LatexRaccoon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::forestSpawning);
    public static final RegistryObject<EntityType<LatexRedDragon>> LATEX_RED_DRAGON = register("latex_red_dragon", 0xa54f58, 0xfcfa4a,
            EntityType.Builder.of(LatexRedDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::mesaSpawning);
    public static final RegistryObject<EntityType<LatexRedPanda>> LATEX_RED_PANDA = register("latex_red_panda", 0xbb2a3b, 0x5a2b49,
            EntityType.Builder.of(LatexRedPanda::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::jungleSpawning);
    public static final RegistryObject<EntityType<LatexShark>> LATEX_SHARK = register("latex_shark", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(LatexShark::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexSharkMale>> LATEX_SHARK_MALE = register("latex_shark_male", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(LatexSharkMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.8F, 2.45F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexSharkFemale>> LATEX_SHARK_FEMALE = register("latex_shark_female", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(LatexSharkFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.8F, 2.45F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexSniperDog>> LATEX_SNIPER_DOG = registerReducedSpawn("latex_sniper_dog", 0xEF8F44, 0x894633,
            EntityType.Builder.of(LatexSniperDog::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LatexAlien>> LATEX_ALIEN = register("latex_alien", 0x1983A9, 0x2DAAB9,
            EntityType.Builder.of(LatexAlien::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::desertSpawning);
    public static final RegistryObject<EntityType<LatexSnowLeopardMale>> LATEX_SNOW_LEOPARD_MALE = register("latex_snow_leopard_male", 0x9C9C9C, 0x272727,
            EntityType.Builder.of(LatexSnowLeopardMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::taigaSpawning);
    public static final RegistryObject<EntityType<LatexSnowLeopardFemale>> LATEX_SNOW_LEOPARD_FEMALE = register("latex_snow_leopard_female", 0x9C9C9C, 0x272727,
            EntityType.Builder.of(LatexSnowLeopardFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::taigaSpawning);
    public static final RegistryObject<EntityType<LatexSquidDogFemale>> LATEX_SQUID_DOG_FEMALE = register("latex_squid_dog_female", 0xFFFFFF, 0x0,
            EntityType.Builder.of(LatexSquidDogFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.8F, 2.2F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER, LatexSquidDogFemale::createAttributes);
    public static final RegistryObject<EntityType<LatexSquidDogMale>> LATEX_SQUID_DOG_MALE = register("latex_squid_dog_male", 0xFFFFFF, 0x0,
            EntityType.Builder.of(LatexSquidDogMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.8F, 2.2F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER, LatexSquidDogMale::createAttributes);
    public static final RegistryObject<EntityType<LatexStiger>> LATEX_STIGER = register("latex_stiger", 0x7b4251, 0xe0cfd9,
            EntityType.Builder.of(LatexStiger::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::undergroundSpawning);
    public static final RegistryObject<EntityType<LatexTigerShark>> LATEX_TIGER_SHARK = register("latex_tiger_shark", 0x969696, 0x0,
            EntityType.Builder.of(LatexTigerShark::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexTrafficConeDragon>> LATEX_TRAFFIC_CONE_DRAGON = register("latex_traffic_cone_dragon", 0xFFD201, 0x0,
            EntityType.Builder.of(LatexTrafficConeDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::undergroundSpawning);
    public static final RegistryObject<EntityType<LatexTranslucentLizard>> LATEX_TRANSLUCENT_LIZARD = register("latex_translucent_lizard", 0xffb84b, 0xFF904C,
            EntityType.Builder.of(LatexTranslucentLizard::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::undergroundSpawning);
    public static final RegistryObject<EntityType<LatexOrca>> LATEX_ORCA = register("latex_orca", 0x393939, 0xFFFFFF,
            EntityType.Builder.of(LatexOrca::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexOtter>> LATEX_OTTER = register("latex_otter", 0x5D4743, 0xB6957C,
            EntityType.Builder.of(LatexOtter::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::beachSpawning);
    public static final RegistryObject<EntityType<LatexWatermelonCat>> LATEX_WATERMELON_CAT = register("latex_watermelon_cat", 0x545454, 0xC7FF5A,
            EntityType.Builder.of(LatexWatermelonCat::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::jungleSpawning);
    public static final RegistryObject<EntityType<LatexWhiteTiger>> LATEX_WHITE_TIGER = register("latex_white_tiger", 0xFFFFFF, 0xACACAC,
            EntityType.Builder.of(LatexWhiteTiger::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::taigaSpawning);
    public static final RegistryObject<EntityType<LatexYuin>> LATEX_YUIN = register("latex_yuin", 0xFFFFFF, 0x7442cc,
            EntityType.Builder.of(LatexYuin::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::mountainSpawning);
    public static final RegistryObject<EntityType<MilkPudding>> MILK_PUDDING = register("milk_pudding", 0xFFFFFF, 0xF0F0F0,
            EntityType.Builder.of(MilkPudding::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.6F, 0.5F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<Shark>> SHARK = register("shark", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(Shark::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.9F, 0.6F),
            ChangedEntities::oceanSpawning);
    public static final RegistryObject<EntityType<WhiteLatexWolf>> WHITE_LATEX_WOLF = register("white_latex_wolf", 0xFFFFFF, 0xFAFAFA,
            EntityType.Builder.of(WhiteLatexWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F),
            ChangedEntities::noSpawning);

    public static final RegistryObject<EntityType<CardboardBoxBlockEntity.EntityContainer>> ENTITY_CONTAINER = REGISTRY.register("entity_container",
            () -> EntityType.Builder.of(CardboardBoxBlockEntity.EntityContainer::new, MobCategory.MISC).sized(0.01f, 0.01f).build("entity_container"));

    public static final RegistryObject<EntityType<SpecialLatex>> SPECIAL_LATEX = registerNoEgg("special_latex",
            EntityType.Builder.of(SpecialLatex::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.95F));

    public static final RegistryObject<EntityType<BehemothHead>> BEHEMOTH_HEAD = registerNoEgg("behemoth_head",
            EntityType.Builder.of(BehemothHead::new, MobCategory.MONSTER).clientTrackingRange(10).sized(3.0f, 3.0f));
    public static final RegistryObject<EntityType<BehemothHandLeft>> BEHEMOTH_HAND_LEFT = registerNoEgg("behemoth_hand_left",
            EntityType.Builder.of(BehemothHandLeft::new, MobCategory.MONSTER).clientTrackingRange(10).sized(2.0f, 2.0f));
    public static final RegistryObject<EntityType<BehemothHandRight>> BEHEMOTH_HAND_RIGHT = registerNoEgg("behemoth_hand_right",
            EntityType.Builder.of(BehemothHandRight::new, MobCategory.MONSTER).clientTrackingRange(10).sized(2.0f, 2.0f));

    public static final RegistryObject<EntityType<LatexInkball>> LATEX_INKBALL = REGISTRY.register("latex_inkball",
            () -> EntityType.Builder.<LatexInkball>of(LatexInkball::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("latex_inkball"));
    public static final RegistryObject<EntityType<GasParticle>> GAS_PARTICLE = REGISTRY.register("gas_particle",
            () -> EntityType.Builder.of(GasParticle::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(4).updateInterval(10).build("gas_particle"));

    // TODO make register function for non `LatexEntity`

    public static class Category {
        public static final ImmutableList<RegistryObject<? extends EntityType<? extends DarkLatexEntity>>> DARK_LATEX = ImmutableList.of(
            DARK_LATEX_DRAGON,
            DARK_LATEX_WOLF_MALE,
            DARK_LATEX_WOLF_FEMALE,
            DARK_LATEX_YUFENG
        );
        public static final ImmutableList<RegistryObject<? extends EntityType<? extends WhiteLatexEntity>>> WHITE_LATEX = ImmutableList.of(
            WHITE_LATEX_WOLF
        );
    }

    public static <T extends LatexEntity> RegistryObject<EntityType<T>> registerNoEgg(
            String name,
            EntityType.Builder<T> builder) {
        String regName = Changed.modResource(name).toString();
        RegistryObject<EntityType<T>> entityType = REGISTRY.register(name, () -> builder.build(regName));
        ATTR_FUNC_REGISTRY.add(new Pair<>(entityType::get, T::createMonsterAttributes));
        return entityType;
    }

    public static <T extends LatexEntity> RegistryObject<EntityType<T>> register(
            String name,
            int eggBack,
            int eggHighlight,
            EntityType.Builder<T> builder,
            Predicate<Biome.BiomeCategory> category) {
        return register(name, eggBack, eggHighlight, builder, category, SpawnPlacements.Type.ON_GROUND);
    }

    public static <T extends LatexEntity> RegistryObject<EntityType<T>> registerReducedSpawn(
            String name,
            int eggBack,
            int eggHighlight,
            EntityType.Builder<T> builder,
            Predicate<Biome.BiomeCategory> category) {
        return registerReducedSpawn(name, eggBack, eggHighlight, builder, category, SpawnPlacements.Type.ON_GROUND);
    }

    public static <T extends LatexEntity> RegistryObject<EntityType<T>> register(
            String name,
            int eggBack,
            int eggHighlight,
            EntityType.Builder<T> builder,
            Predicate<Biome.BiomeCategory> category,
            SpawnPlacements.Type spawnType) {
        return register(name, eggBack, eggHighlight, builder, category, spawnType, T::createMonsterAttributes);
    }

    public static <T extends LatexEntity> RegistryObject<EntityType<T>> registerReducedSpawn(
            String name,
            int eggBack,
            int eggHighlight,
            EntityType.Builder<T> builder,
            Predicate<Biome.BiomeCategory> category,
            SpawnPlacements.Type spawnType) {
        return registerReducedSpawn(name, eggBack, eggHighlight, builder, category, spawnType, T::createMonsterAttributes);
    }

    public static <T extends LatexEntity> RegistryObject<EntityType<T>> register(
            String name,
            int eggBack,
            int eggHighlight,
            EntityType.Builder<T> builder,
            Predicate<Biome.BiomeCategory> category,
            SpawnPlacements.Type spawnType,
            Supplier<AttributeSupplier.Builder> attributes) {
        ENTITY_COLOR_MAP.put(Changed.modResource(name), new Pair<>(eggBack, eggHighlight));
        String regName = Changed.modResource(name).toString();
        RegistryObject<EntityType<T>> entityType = REGISTRY.register(name, () -> builder.build(regName));
        INIT_FUNC_REGISTRY.add(LatexEntity.getInit(entityType, spawnType));
        ATTR_FUNC_REGISTRY.add(new Pair<>(entityType::get, attributes));
        RegistryObject<Item> spawnEggItem = ChangedItems.register(name + "_spawn_egg", () -> new ForgeSpawnEggItem(entityType, eggBack, eggHighlight,
                new Item.Properties().tab(ChangedTabs.TAB_CHANGED_ENTITIES)));
        SPAWN_EGGS.add(() -> (ForgeSpawnEggItem) spawnEggItem.get());
        SPAWNING_ENTITY.add((event, spawner) -> {
            if (category.test(event.getCategory()))
                spawner.add(new MobSpawnSettings.SpawnerData(entityType.get(), 15, 1, 3));
        });
        return entityType;
    }

    public static <T extends LatexEntity> RegistryObject<EntityType<T>> registerReducedSpawn(
            String name,
            int eggBack,
            int eggHighlight,
            EntityType.Builder<T> builder,
            Predicate<Biome.BiomeCategory> category,
            SpawnPlacements.Type spawnType,
            Supplier<AttributeSupplier.Builder> attributes) {
        ENTITY_COLOR_MAP.put(Changed.modResource(name), new Pair<>(eggBack, eggHighlight));
        String regName = Changed.modResource(name).toString();
        RegistryObject<EntityType<T>> entityType = REGISTRY.register(name, () -> builder.build(regName));
        INIT_FUNC_REGISTRY.add(LatexEntity.getInit(entityType, spawnType));
        ATTR_FUNC_REGISTRY.add(new Pair<>(entityType::get, attributes));
        RegistryObject<Item> spawnEggItem = ChangedItems.register(name + "_spawn_egg", () -> new ForgeSpawnEggItem(entityType, eggBack, eggHighlight,
                new Item.Properties().tab(ChangedTabs.TAB_CHANGED_ENTITIES)));
        SPAWN_EGGS.add(() -> (ForgeSpawnEggItem) spawnEggItem.get());
        SPAWNING_ENTITY.add((event, spawner) -> {
            if (category.test(event.getCategory()))
                spawner.add(new MobSpawnSettings.SpawnerData(entityType.get(), 4, 1, 1));
        });
        return entityType;
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> INIT_FUNC_REGISTRY.forEach(VoidConsumer::accept));
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        ATTR_FUNC_REGISTRY.forEach((pair) -> event.put(pair.getFirst().get(), pair.getSecond().get().build()));
    }
}
