package net.ltxprogrammer.changed.init;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.entity.CardboardBoxBlockEntity;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.beast.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import oshi.util.tuples.Pair;

import java.util.*;
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
        return ENTITY_COLOR_MAP.computeIfAbsent(location, loc -> new Pair<>(0xF0F0F0, 0xF0F0F0));
    }

    public static int getEntityColorBack(ResourceLocation location) {
        return getEntityColor(location).getA();
    }

    public static int getEntityColorFront(ResourceLocation location) {
        return getEntityColor(location).getB();
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
    public static boolean mesaSpawning(Biome.BiomeCategory category) {
        return category == Biome.BiomeCategory.MESA;
    }
    public static boolean oceanSpawning(Biome.BiomeCategory category) {
        return category == Biome.BiomeCategory.OCEAN || category == Biome.BiomeCategory.RIVER;
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
            EntityType.Builder.of(LightLatexWolfFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LightLatexWolfMale>> LIGHT_LATEX_WOLF_MALE = register("light_latex_wolf_male", 0xFFFFFF, 0xFF927F,
            EntityType.Builder.of(LightLatexWolfMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LightLatexKnight>> LIGHT_LATEX_KNIGHT = register("light_latex_knight", 0xFFFFFF, 0x1E1E1E,
            EntityType.Builder.of(LightLatexKnight::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LightLatexCentaur>> LIGHT_LATEX_CENTAUR = register("light_latex_centaur", 0xFFFFFF, 0x1E1E1E,
            EntityType.Builder.of(LightLatexCentaur::new, MobCategory.MONSTER).clientTrackingRange(10).sized(1.1F, 1.85F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LightLatexKnightFusion>> LIGHT_LATEX_KNIGHT_FUSION = register("light_latex_knight_fusion", 0xFFFFFF, 0x0072ff,
            EntityType.Builder.of(LightLatexKnightFusion::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LatexCrystalWolf>> LATEX_CRYSTAL_WOLF = register("latex_crystal_wolf", 0x393939, 0xFF014E,
            EntityType.Builder.of(LatexCrystalWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<LatexSilverFox>> LATEX_SILVER_FOX = register("latex_silver_fox", 0x959CA5, 0x272727,
            EntityType.Builder.of(LatexSilverFox::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<AerosolLatexWolf>> AEROSOL_LATEX_WOLF = register("aerosol_latex_wolf", 0x5D4743, 0xFFFFFF,
            EntityType.Builder.of(AerosolLatexWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<DarkLatexDragonFemale>> DARK_LATEX_DRAGON_FEMALE = register("dark_latex_dragon_female", 0x393939, 0x909090,
            EntityType.Builder.of(DarkLatexDragonFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::mountainSpawning);
    public static final RegistryObject<EntityType<DarkLatexDragonMale>> DARK_LATEX_DRAGON_MALE = register("dark_latex_dragon_male", 0x393939, 0x909090,
            EntityType.Builder.of(DarkLatexDragonMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::mountainSpawning);
    public static final RegistryObject<EntityType<DarkLatexWolfMale>> DARK_LATEX_WOLF_MALE = register("dark_latex_wolf_male", 0x393939, 0x303030,
            EntityType.Builder.of(DarkLatexWolfMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::noSpawning);
    public static final RegistryObject<EntityType<DarkLatexWolfFemale>> DARK_LATEX_WOLF_FEMALE = register("dark_latex_wolf_female", 0x393939, 0x303030,
            EntityType.Builder.of(DarkLatexWolfFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::noSpawning);
    /*public static final RegistryObject<EntityType<DarkLatexPup>> DARK_LATEX_PUP = register("dark_latex_pup", 0x454545, 0x303030,
            EntityType.Builder.of(DarkLatexPup::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.6F, 0.9F));*/
    public static final RegistryObject<EntityType<DarkLatexYufeng>> DARK_LATEX_YUFENG = register("dark_latex_yufeng", 0x393939, 0x0,
            EntityType.Builder.of(DarkLatexYufeng::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::mountainSpawning);
    public static final RegistryObject<EntityType<LatexBeifeng>> LATEX_BEIFENG = register("latex_beifeng", 0x51659D, 0xFFE852,
            EntityType.Builder.of(LatexBeifeng::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::undergroundSpawning);
    public static final RegistryObject<EntityType<LatexBlueDragon>> LATEX_BLUE_DRAGON = register("latex_blue_dragon", 0xCDFEFF, 0xFFFFFF,
            EntityType.Builder.of(LatexBlueDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::taigaSpawning);
    public static final RegistryObject<EntityType<LatexHypnoCat>> LATEX_HYPNO_CAT = register("latex_hypno_cat", 0x52596D, 0xD7FF46,
            EntityType.Builder.of(LatexHypnoCat::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::savannaSpawning);
    public static final RegistryObject<EntityType<LatexLeaf>> LATEX_LEAF = register("latex_leaf", 0xBFF298, 0x76C284,
            EntityType.Builder.of(LatexLeaf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::forestSpawning);
    public static final RegistryObject<EntityType<LatexRedDragon>> LATEX_RED_DRAGON = register("latex_red_dragon", 0xa54f58, 0xd8d43e,
            EntityType.Builder.of(LatexRedDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::mesaSpawning);
    public static final RegistryObject<EntityType<LatexSharkMale>> LATEX_SHARK_MALE = register("latex_shark_male", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(LatexSharkMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexSharkFemale>> LATEX_SHARK_FEMALE = register("latex_shark_female", 0x969696, 0xFFFFFF,
            EntityType.Builder.of(LatexSharkFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexSnowLeopardMale>> LATEX_SNOW_LEOPARD_MALE = register("latex_snow_leopard_male", 0x9C9C9C, 0x272727,
            EntityType.Builder.of(LatexSnowLeopardMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::taigaSpawning);
    public static final RegistryObject<EntityType<LatexSnowLeopardFemale>> LATEX_SNOW_LEOPARD_FEMALE = register("latex_snow_leopard_female", 0x9C9C9C, 0x272727,
            EntityType.Builder.of(LatexSnowLeopardFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::taigaSpawning);
    public static final RegistryObject<EntityType<LatexSquidDog>> LATEX_SQUID_DOG = register("latex_squid_dog", 0xFFFFFF, 0x0,
            EntityType.Builder.of(LatexSquidDog::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexTigerShark>> LATEX_TIGER_SHARK = register("latex_tiger_shark", 0x969696, 0x0,
            EntityType.Builder.of(LatexTigerShark::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexTrafficConeDragon>> LATEX_TRAFFIC_CONE_DRAGON = register("latex_traffic_cone_dragon", 0xFFD201, 0x0,
            EntityType.Builder.of(LatexTrafficConeDragon::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::undergroundSpawning);
    public static final RegistryObject<EntityType<LatexOrca>> LATEX_ORCA = register("latex_orca", 0x393939, 0xFFFFFF,
            EntityType.Builder.of(LatexOrca::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::oceanSpawning, SpawnPlacements.Type.IN_WATER);
    public static final RegistryObject<EntityType<LatexWatermelonCatMale>> LATEX_WATERMELON_CAT_MALE = register("latex_watermelon_cat_male", 0x545454, 0xC7FF5A,
            EntityType.Builder.of(LatexWatermelonCatMale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::jungleSpawning);
    public static final RegistryObject<EntityType<LatexWatermelonCatFemale>> LATEX_WATERMELON_CAT_FEMALE = register("latex_watermelon_cat_female", 0x545454, 0xC7FF5A,
            EntityType.Builder.of(LatexWatermelonCatFemale::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::jungleSpawning);
    public static final RegistryObject<EntityType<MilkPudding>> MILK_PUDDING = register("milk_pudding", 0xFFFFFF, 0xF0F0F0,
            EntityType.Builder.of(MilkPudding::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.6F, 0.5F),
            ChangedEntities::plainsSpawning);
    public static final RegistryObject<EntityType<WhiteLatexWolf>> WHITE_LATEX_WOLF = register("white_latex_wolf", 0xFFFFFF, 0xFAFAFA,
            EntityType.Builder.of(WhiteLatexWolf::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F),
            ChangedEntities::noSpawning);

    public static final RegistryObject<EntityType<CardboardBoxBlockEntity.EntityContainer>> ENTITY_CONTAINER = REGISTRY.register("entity_container",
            () -> EntityType.Builder.of(CardboardBoxBlockEntity.EntityContainer::new, MobCategory.MISC).sized(0.01f, 0.01f).build("entity_container"));

    public static final RegistryObject<EntityType<SpecialLatex>> SPECIAL_LATEX = registerNoEgg("special_latex",
            EntityType.Builder.of(SpecialLatex::new, MobCategory.MONSTER).clientTrackingRange(10).sized(0.7F, 1.85F));

    public static class Category {
        public static final ImmutableList<RegistryObject<? extends EntityType<? extends DarkLatexEntity>>> DARK_LATEX = ImmutableList.of(
            DARK_LATEX_DRAGON_MALE,
            DARK_LATEX_DRAGON_FEMALE,
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

    public static <T extends LatexEntity> RegistryObject<EntityType<T>> register(
            String name,
            int eggBack,
            int eggHighlight,
            EntityType.Builder<T> builder,
            Predicate<Biome.BiomeCategory> category,
            SpawnPlacements.Type spawnType) {
        ENTITY_COLOR_MAP.put(Changed.modResource(name), new Pair<>(eggBack, eggHighlight));
        String regName = Changed.modResource(name).toString();
        RegistryObject<EntityType<T>> entityType = REGISTRY.register(name, () -> builder.build(regName));
        INIT_FUNC_REGISTRY.add(LatexEntity.getInit(entityType, spawnType));
        ATTR_FUNC_REGISTRY.add(new Pair<>(entityType::get, T::createMonsterAttributes));
        RegistryObject<Item> spawnEggItem = ChangedItems.register(name + "_spawn_egg", () -> new ForgeSpawnEggItem(entityType, eggBack, eggHighlight,
                new Item.Properties().tab(ChangedTabs.TAB_CHANGED_ENTITIES)));
        SPAWN_EGGS.add(() -> (ForgeSpawnEggItem) spawnEggItem.get());
        SPAWNING_ENTITY.add((event, spawner) -> {
            if (category.test(event.getCategory()))
                spawner.add(new MobSpawnSettings.SpawnerData(entityType.get(), 12, 1, 3));
        });
        return entityType;
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> INIT_FUNC_REGISTRY.forEach(VoidConsumer::accept));
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        ATTR_FUNC_REGISTRY.forEach((pair) -> event.put(pair.getA().get(), pair.getB().get().build()));
    }
}
