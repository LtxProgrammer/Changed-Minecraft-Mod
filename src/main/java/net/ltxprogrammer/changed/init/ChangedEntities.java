package net.ltxprogrammer.changed.init;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.SeatEntity;
import net.ltxprogrammer.changed.entity.beast.*;
import net.ltxprogrammer.changed.entity.beast.boss.BehemothHandLeft;
import net.ltxprogrammer.changed.entity.beast.boss.BehemothHandRight;
import net.ltxprogrammer.changed.entity.beast.boss.BehemothHead;
import net.ltxprogrammer.changed.entity.projectile.GasParticle;
import net.ltxprogrammer.changed.entity.projectile.GooInkball;
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
    public static final RegistryObject<EntityType<WhiteGooWolfFemale>> WHITE_GOO_WOLF_FEMALE = register("light_latex_wolf_female", 0xFFFFFF, 0xFF927F,
            EntityType.Builder.of(WhiteGooWolfFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<WhiteGooWolfMale>> WHITE_GOO_WOLF_MALE = register("light_latex_wolf_male", 0xFFFFFF, 0xFF927F,
            EntityType.Builder.of(WhiteGooWolfMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<WhiteWolf>> WHITE_WOLF = registerReducedSpawn("light_latex_wolf_organic", 0xFFFFFF, 0xFAE9E4,
            EntityType.Builder.of(WhiteWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<WhiteGooKnight>> WHITE_GOO_KNIGHT = register("light_latex_knight", 0xFFFFFF, 0x1E1E1E,
            EntityType.Builder.of(WhiteGooKnight::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<WhiteGooCentaur>> WHITE_GOO_CENTAUR = register("light_latex_centaur", 0xFFFFFF, 0x1E1E1E,
            EntityType.Builder.of(WhiteGooCentaur::new, MobCategory.MONSTER).clientTrackingRange(10).sized(1.1F, 2.2F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<HeadlessKnight>> HEADLESS_KNIGHT = register("headless_knight", 0xFFFFFF, 0x1E1E1E,
            EntityType.Builder.of(HeadlessKnight::new, MobCategory.MONSTER).clientTrackingRange(10).sized(1.1F, 1.1F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<WhiteGooKnightFusion>> WHITE_GOO_KNIGHT_FUSION = register("light_latex_knight_fusion", 0xFFFFFF, 0x0072ff,
            EntityType.Builder.of(WhiteGooKnightFusion::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LatexCrystalWolf>> LATEX_CRYSTAL_WOLF = registerReducedSpawn("latex_crystal_wolf", 0x393939, 0xCF003E,
            EntityType.Builder.of(LatexCrystalWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LatexCrystalWolfHorned>> LATEX_CRYSTAL_WOLF_HORNED = registerReducedSpawn("latex_crystal_wolf_horned", 0x393939, 0xFF014E,
            EntityType.Builder.of(LatexCrystalWolfHorned::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LatexDeer>> LATEX_DEER = register("latex_deer", 0xD8BC99, 0xFBE5BC,
            EntityType.Builder.of(LatexDeer::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::forestSpawning);
    public static final RegistryObject<EntityType<GooBee>> GOO_BEE = register("latex_bee", 0xFFBF75, 0xFF9E58,
            EntityType.Builder.of(GooBee::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::forestSpawning);
    public static final RegistryObject<EntityType<LatexPinkDeer>> LATEX_PINK_DEER = register("latex_pink_deer", 0xF2AFBC, 0xCA636A,
            EntityType.Builder.of(LatexPinkDeer::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::forestSpawning);
    public static final RegistryObject<EntityType<GooKeonWolf>> LATEX_KEON_WOLF = register("latex_keon_wolf", 0x959CA5, 0x272727,
            EntityType.Builder.of(GooKeonWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<AerosolLatexWolf>> AEROSOL_LATEX_WOLF = registerReducedSpawn("aerosol_latex_wolf", 0x5D4743, 0xFFFFFF,
            EntityType.Builder.of(AerosolLatexWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<DarkLatexDragon>> DARK_LATEX_DRAGON = registerReducedSpawn("dark_latex_dragon", 0x393939, 0x909090,
            EntityType.Builder.of(DarkLatexDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::mountainSpawning);
    public static final RegistryObject<EntityType<BlackGooWolfMale>> BLACK_GOO_WOLF_MALE = registerReducedSpawn("dark_latex_wolf_male", 0x393939, 0x303030,
            EntityType.Builder.of(BlackGooWolfMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<BlackGooWolfFemale>> BLACK_GOO_WOLF_FEMALE = registerReducedSpawn("dark_latex_wolf_female", 0x393939, 0x303030,
            EntityType.Builder.of(BlackGooWolfFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<BlackGooPup>> BLACK_GOO_PUP = register("dark_latex_pup", 0x454545, 0x303030,
            EntityType.Builder.of(BlackGooPup::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.6F, 0.85F),
            ChangedEntities::noSpawning);
    public static final RegistryObject<EntityType<BlackGooYufeng>> BLACK_GOO_YUFENG = register("dark_latex_yufeng", 0x393939, 0x0,
            EntityType.Builder.of(BlackGooYufeng::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::mountainSpawning);
    public static final RegistryObject<EntityType<PhageLatexWolfMale>> PHAGE_LATEX_WOLF_MALE = registerReducedSpawn("phage_latex_wolf_male", 0x393939, 0x3499ff,
            EntityType.Builder.of(PhageLatexWolfMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<PhageLatexWolfFemale>> PHAGE_LATEX_WOLF_FEMALE = registerReducedSpawn("phage_latex_wolf_female", 0x393939, 0x3499ff,
            EntityType.Builder.of(PhageLatexWolfFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<Beifeng>> BEIFENG = registerReducedSpawn("latex_beifeng", 0x51659D, 0xFFE852,
            EntityType.Builder.of(Beifeng::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::undergroundSpawning);
    public static final RegistryObject<EntityType<BenignGooWolf>> BENIGN_GOO_WOLF = register("latex_benign_wolf", 0x282828, 0x292929,
            EntityType.Builder.of(BenignGooWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::noSpawning);
    public static final RegistryObject<EntityType<BlueGooDragon>> BLUE_GOO_DRAGON = register("latex_blue_dragon", 0xCDFEFF, 0x5c72ab,
            EntityType.Builder.of(BlueGooDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::taigaSpawning);
    public static final RegistryObject<EntityType<BlueGooWolf>> BLUE_GOO_WOLF = register("latex_blue_wolf", 0x8ad6e7, 0x7395c0,
            EntityType.Builder.of(BlueGooWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::mountainSpawning);
    public static final RegistryObject<EntityType<GooCrocodile>> GOO_CROCODILE = register("latex_crocodile", 0x216d50, 0x43b058,
            EntityType.Builder.of(GooCrocodile::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.8F, 2.5F),
            ChangedEntities::swampSpawning);
    public static final RegistryObject<EntityType<GooFennecFox>> GOO_FENNEC_FOX = register("latex_fennec_fox", 0xffe195, 0x84484b,
            EntityType.Builder.of(GooFennecFox::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::desertSpawning);
    public static final RegistryObject<EntityType<GooHypnoCat>> LATEX_HYPNO_CAT = register("latex_hypno_cat", 0x52596D, 0xD7FF46,
            EntityType.Builder.of(GooHypnoCat::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::savannaSpawning);
    public static final RegistryObject<EntityType<LatexLeaf>> LATEX_LEAF = register("latex_leaf", 0xBFF298, 0x76C284,
            EntityType.Builder.of(LatexLeaf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::forestSpawning);
    public static final RegistryObject<EntityType<GooSquirrel>> GOO_SQUIRREL = register("latex_squirrel", 0xFFE8A5, 0xAC8F64,
            EntityType.Builder.of(GooSquirrel::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::forestSpawning);
    public static final RegistryObject<EntityType<GooMantaRayFemale>> LATEX_MANTA_RAY_FEMALE = register("latex_manta_ray_female", 0x6f7696, 0xd2d9e1,
            EntityType.Builder.of(GooMantaRayFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<GooMantaRayMale>> LATEX_MANTA_RAY_MALE = register("latex_manta_ray_male", 0x6f7696, 0xd2d9e1,
            EntityType.Builder.of(GooMantaRayMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexMedusaCat>> LATEX_MEDUSA_CAT = register("latex_medusa_cat", 0xFFDB4F, 0xF398B7,
            EntityType.Builder.of(LatexMedusaCat::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::savannaSpawning);
    public static final RegistryObject<EntityType<LatexMingCat>> LATEX_MING_CAT = register("latex_ming_cat", 0xD2A87F, 0x75483F,
            EntityType.Builder.of(LatexMingCat::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<GooMermaidShark>> LATEX_MERMAID_SHARK = register("latex_mermaid_shark", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(GooMermaidShark::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.58625F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexMoth>> LATEX_MOTH = registerReducedSpawn("latex_moth", 0xFBE5BC, 0xD8BC99,
            EntityType.Builder.of(LatexMoth::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::forestSpawning);
    public static final RegistryObject<EntityType<GooSiren>> LATEX_SIREN = registerReducedSpawn("latex_siren", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(GooSiren::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.58625F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<GooSnake>> GOO_SNAKE = register("latex_snake", 0xFFFFFF, 0x7E7E7E,
            EntityType.Builder.of(GooSnake::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::desertSpawning);
    public static final RegistryObject<EntityType<LatexMimicPlant>> LATEX_MIMIC_PLANT = register("latex_mimic_plant", 0x446d5d, 0x729c6a,
            EntityType.Builder.of(LatexMimicPlant::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::jungleAndForestSpawning);
    public static final RegistryObject<EntityType<LatexPinkWyvern>> LATEX_PINK_WYVERN = register("latex_pink_wyvern", 0xf2aaba, 0xd1626d,
            EntityType.Builder.of(LatexPinkWyvern::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::mountainSpawning);
    public static final RegistryObject<EntityType<LatexPinkYuinDragon>> LATEX_PINK_YUIN_DRAGON = register("latex_pink_yuin_dragon", 0xFFFFFF, 0xF2AABA,
            EntityType.Builder.of(LatexPinkYuinDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::mountainSpawning);
    public static final RegistryObject<EntityType<GooPurpleFox>> LATEX_PURPLE_FOX = register("latex_purple_fox", 0xcebbe8, 0xf1e3f1,
            EntityType.Builder.of(GooPurpleFox::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::taigaSpawning);
    public static final RegistryObject<EntityType<LatexRaccoon>> LATEX_RACCOON = registerReducedSpawn("latex_raccoon", 0x949494, 0x535353,
            EntityType.Builder.of(LatexRaccoon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::forestSpawning);
    public static final RegistryObject<EntityType<LatexRedDragon>> LATEX_RED_DRAGON = register("latex_red_dragon", 0xa54f58, 0xfcfa4a,
            EntityType.Builder.of(LatexRedDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::mesaSpawning);
    public static final RegistryObject<EntityType<GooRedPanda>> GOO_RED_PANDA = register("latex_red_panda", 0xbd4040, 0x663d53,
            EntityType.Builder.of(GooRedPanda::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::jungleSpawning);
    public static final RegistryObject<EntityType<GooShark>> GOO_SHARK = register("latex_shark", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(GooShark::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<GooSharkMale>> LATEX_SHARK_MALE = register("latex_shark_male", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(GooSharkMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.8F, 2.45F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<GooSharkFemale>> LATEX_SHARK_FEMALE = register("latex_shark_female", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(GooSharkFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.8F, 2.45F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexSniperDog>> LATEX_SNIPER_DOG = registerReducedSpawn("latex_sniper_dog", 0xEF8F44, 0x894633,
            EntityType.Builder.of(LatexSniperDog::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LatexAlien>> GOO_ALIEN = register("latex_alien", 0x1983A9, 0x2DAAB9,
            EntityType.Builder.of(LatexAlien::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::desertSpawning);
    public static final RegistryObject<EntityType<GooSnowLeopardMale>> GOO_SNOW_LEOPARD_MALE = register("latex_snow_leopard_male", 0x9C9C9C, 0x272727,
            EntityType.Builder.of(GooSnowLeopardMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::taigaSpawning);
    public static final RegistryObject<EntityType<GooSnowLeopardFemale>> GOO_SNOW_LEOPARD_FEMALE = register("latex_snow_leopard_female", 0x9C9C9C, 0x272727,
            EntityType.Builder.of(GooSnowLeopardFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::taigaSpawning);
    public static final RegistryObject<EntityType<GooSquidDogFemale>> GOO_SQUID_DOG_FEMALE = register("latex_squid_dog_female", 0xFFFFFF, 0x0,
            EntityType.Builder.of(GooSquidDogFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.8F, 2.00F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER, GooSquidDogFemale::createAttributes);
    public static final RegistryObject<EntityType<GooSquidDogMale>> GOO_SQUID_DOG_MALE = register("latex_squid_dog_male", 0xFFFFFF, 0x0,
            EntityType.Builder.of(GooSquidDogMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.8F, 2.00F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER, GooSquidDogMale::createAttributes);
    public static final RegistryObject<EntityType<GooStiger>> GOO_STIGER = register("latex_stiger", 0x7b4251, 0xe0cfd9,
            EntityType.Builder.of(GooStiger::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::undergroundSpawning);
    public static final RegistryObject<EntityType<GooTigerShark>> GOO_TIGER_SHARK = registerReducedSpawn("latex_tiger_shark", 0x969696, 0x0,
            EntityType.Builder.of(GooTigerShark::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexTrafficConeDragon>> LATEX_TRAFFIC_CONE_DRAGON = register("latex_traffic_cone_dragon", 0xFFD201, 0x0,
            EntityType.Builder.of(LatexTrafficConeDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::undergroundSpawning);
    public static final RegistryObject<EntityType<LatexTranslucentLizard>> LATEX_TRANSLUCENT_LIZARD = register("latex_translucent_lizard", 0xffb84b, 0xFF904C,
            EntityType.Builder.of(LatexTranslucentLizard::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::undergroundSpawning);
    public static final RegistryObject<EntityType<GooOrca>> GOO_ORCA = register("latex_orca", 0x393939, 0xFFFFFF,
            EntityType.Builder.of(GooOrca::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<GooOtter>> GOO_OTTER = register("latex_otter", 0x5D4743, 0xB6957C,
            EntityType.Builder.of(GooOtter::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::beachSpawning);
    public static final RegistryObject<EntityType<GooWatermelonCat>> GOO_WATERMELON_CAT = register("latex_watermelon_cat", 0x545454, 0xC7FF5A,
            EntityType.Builder.of(GooWatermelonCat::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::jungleSpawning);
    public static final RegistryObject<EntityType<LatexWhiteTiger>> LATEX_WHITE_TIGER = register("latex_white_tiger", 0xFFFFFF, 0xACACAC,
            EntityType.Builder.of(LatexWhiteTiger::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::taigaSpawning);
    public static final RegistryObject<EntityType<GooYuin>> GOO_YUIN = register("latex_yuin", 0xFFFFFF, 0x7442cc,
            EntityType.Builder.of(GooYuin::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::mountainSpawning);
    public static final RegistryObject<EntityType<MilkPudding>> MILK_PUDDING = register("milk_pudding", 0xFFFFFF, 0xF0F0F0,
            EntityType.Builder.of(MilkPudding::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.6F, 0.5F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<Shark>> SHARK = register("shark", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(Shark::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.9F, 0.6F),
            ChangedEntities::oceanSpawning);
    public static final RegistryObject<EntityType<PureWhiteGooWolf>> PURE_WHITE_GOO_WOLF = register("white_latex_wolf", 0xFFFFFF, 0xFAFAFA,
            EntityType.Builder.of(PureWhiteGooWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F),
            ChangedEntities::noSpawning);

    public static final RegistryObject<EntityType<SeatEntity>> SEAT_ENTITY = REGISTRY.register("seat_entity",
            () -> EntityType.Builder.of(SeatEntity::new, MobCategory.MISC).sized(0.01f, 0.01f).build("seat_entity"));

    public static final RegistryObject<EntityType<BlackGooWolfPartial>> BLACK_GOO_WOLF_PARTIAL = registerNoEgg("dark_latex_wolf_partial", 0x393939, 0x303030,
            EntityType.Builder.of(BlackGooWolfPartial::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F));
    public static final RegistryObject<EntityType<GooHuman>> GOO_HUMAN = registerNoEgg("latex_human", 0x8B8B8B, 0xC6C6C6,
            EntityType.Builder.of(GooHuman::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.6F, 1.8F));
    public static final RegistryObject<EntityType<SpecialLatex>> SPECIAL_LATEX = registerNoEgg("special_latex",
            EntityType.Builder.of(SpecialLatex::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.93F));

    public static final RegistryObject<EntityType<BehemothHead>> BEHEMOTH_HEAD = registerNoEgg("behemoth_head",
            EntityType.Builder.of(BehemothHead::new, MobCategory.MONSTER).clientTrackingRange(10).sized(3.0f, 3.0f));
    public static final RegistryObject<EntityType<BehemothHandLeft>> BEHEMOTH_HAND_LEFT = registerNoEgg("behemoth_hand_left",
            EntityType.Builder.of(BehemothHandLeft::new, MobCategory.MONSTER).clientTrackingRange(10).sized(2.0f, 2.0f));
    public static final RegistryObject<EntityType<BehemothHandRight>> BEHEMOTH_HAND_RIGHT = registerNoEgg("behemoth_hand_right",
            EntityType.Builder.of(BehemothHandRight::new, MobCategory.MONSTER).clientTrackingRange(10).sized(2.0f, 2.0f));

    public static final RegistryObject<EntityType<GooInkball>> GOO_INKBALL = REGISTRY.register("latex_inkball",
            () -> EntityType.Builder.<GooInkball>of(GooInkball::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("latex_inkball"));
    public static final RegistryObject<EntityType<GasParticle>> GAS_PARTICLE = REGISTRY.register("gas_particle",
            () -> EntityType.Builder.of(GasParticle::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(4).updateInterval(10).build("gas_particle"));

    // TODO make register function for non `LatexEntity`

    public static class Category {
        public static final ImmutableList<RegistryObject<? extends EntityType<? extends DarkLatexEntity>>> DARK_LATEX = ImmutableList.of(
            DARK_LATEX_DRAGON,
                BLACK_GOO_WOLF_MALE,
                BLACK_GOO_WOLF_FEMALE,
                BLACK_GOO_WOLF_PARTIAL,
                BLACK_GOO_YUFENG
        );
        public static final ImmutableList<RegistryObject<? extends EntityType<? extends PureWhiteGooEntity>>> WHITE_LATEX = ImmutableList.of(
                PURE_WHITE_GOO_WOLF
        );
    }

    public static <T extends ChangedEntity> RegistryObject<EntityType<T>> registerNoEgg(
            String name,
            int eggBack,
            int eggHighlight,
            EntityType.Builder<T> builder) {
        String regName = Changed.modResource(name).toString();
        ENTITY_COLOR_MAP.put(Changed.modResource(name), new Pair<>(eggBack, eggHighlight));
        RegistryObject<EntityType<T>> entityType = REGISTRY.register(name, () -> builder.build(regName));
        ATTR_FUNC_REGISTRY.add(new Pair<>(entityType::get, T::createMonsterAttributes));
        return entityType;
    }

    public static <T extends ChangedEntity> RegistryObject<EntityType<T>> registerNoEgg(
            String name,
            EntityType.Builder<T> builder) {
        String regName = Changed.modResource(name).toString();
        RegistryObject<EntityType<T>> entityType = REGISTRY.register(name, () -> builder.build(regName));
        ATTR_FUNC_REGISTRY.add(new Pair<>(entityType::get, T::createMonsterAttributes));
        return entityType;
    }

    public static <T extends ChangedEntity> RegistryObject<EntityType<T>> register(
            String name,
            int eggBack,
            int eggHighlight,
            EntityType.Builder<T> builder,
            Predicate<Biome.BiomeCategory> category) {
        return register(name, eggBack, eggHighlight, builder, category, SpawnPlacements.Type.ON_GROUND);
    }

    public static <T extends ChangedEntity> RegistryObject<EntityType<T>> registerReducedSpawn(
            String name,
            int eggBack,
            int eggHighlight,
            EntityType.Builder<T> builder,
            Predicate<Biome.BiomeCategory> category) {
        return registerReducedSpawn(name, eggBack, eggHighlight, builder, category, SpawnPlacements.Type.ON_GROUND);
    }

    public static <T extends ChangedEntity> RegistryObject<EntityType<T>> register(
            String name,
            int eggBack,
            int eggHighlight,
            EntityType.Builder<T> builder,
            Predicate<Biome.BiomeCategory> category,
            SpawnPlacements.Type spawnType) {
        return register(name, eggBack, eggHighlight, builder, category, spawnType, T::createMonsterAttributes);
    }

    public static <T extends ChangedEntity> RegistryObject<EntityType<T>> registerReducedSpawn(
            String name,
            int eggBack,
            int eggHighlight,
            EntityType.Builder<T> builder,
            Predicate<Biome.BiomeCategory> category,
            SpawnPlacements.Type spawnType) {
        return registerReducedSpawn(name, eggBack, eggHighlight, builder, category, spawnType, T::createMonsterAttributes);
    }

    public static <T extends ChangedEntity> RegistryObject<EntityType<T>> register(
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
        INIT_FUNC_REGISTRY.add(ChangedEntity.getInit(entityType, spawnType));
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

    public static <T extends ChangedEntity> RegistryObject<EntityType<T>> registerReducedSpawn(
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
        INIT_FUNC_REGISTRY.add(ChangedEntity.getInit(entityType, spawnType));
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
