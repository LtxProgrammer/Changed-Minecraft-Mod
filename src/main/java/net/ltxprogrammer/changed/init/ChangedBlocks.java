package net.ltxprogrammer.changed.init;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.*;
import net.ltxprogrammer.changed.entity.beast.DarkLatexEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, Changed.MODID);
    public static final Map<RegistryObject<Block>, Consumer<Block>> REGISTRY_CRL = new HashMap<>();
    public static final RegistryObject<Block> AEROSOL_LATEX = registerNoItem("aerosol_latex", AerosolLatex::new);
    public static final RegistryObject<Block> BEDSIDE_IV_RACK = register("bedside_iv_rack", BedsideIVRack::new, AbstractLatexCrystal::translucentRenderer);
    public static final RegistryObject<Block> BOOK_STACK = register("book_stack", () -> new BookStack(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_RED).sound(SoundType.WOOD).strength(0.5F, 6.0F)));
    public static final RegistryObject<Block> CLIPBOARD = register("clipboard", () -> new BookStack(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).sound(SoundType.CANDLE).strength(0.2F, 3.0F)));
    public static final RegistryObject<Block> COMPUTER = register("computer", () -> new Computer(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).sound(SoundType.METAL).strength(3.0F, 18.0F).lightLevel((state) -> 8)));
    public static final RegistryObject<Block> CARDBOARD_BOX = register("cardboard_box", CardboardBox::new);
    public static final RegistryObject<Block> IRON_CRATE = register("iron_crate", () -> new IronCrate(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).sound(SoundType.COPPER).strength(5.0F, 40.0F)));
    public static final RegistryObject<Block> DARK_LATEX_FLUID = registerNoItem("dark_latex_fluid", DarkLatexFluid::new);
    public static final RegistryObject<Block> LAB_DOOR = register("lab_door", () -> new LabDoor(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).sound(SoundType.METAL).strength(3.0F, 18.0F)));
    public static final RegistryObject<Block> LAB_LIGHT = register("lab_light", () -> new LabLight(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WOOL).sound(SoundType.METAL).strength(3.0F, 18.0F).lightLevel(
            (state) -> state.getValue(LabLight.POWERED) ? 15 : 0)));
    public static final RegistryObject<Block> LAB_LIGHT_SMALL = register("lab_light_small", () -> new LabLightSmall(BlockBehaviour.Properties.copy(LAB_LIGHT.get())));
    public static final RegistryObject<Block> LAB_TABLE_2X1 = register("lab_table_2x1", () -> new LabTable2x1(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK).sound(SoundType.METAL).strength(3.0F, 18.0F)));
    public static final RegistryObject<Block> LATEX_CRYSTAL = register("latex_crystal", () -> new LatexCrystal(new ImmutableList.Builder<Supplier<EntityType<? extends DarkLatexEntity>>>()
            .add(ChangedEntities.DARK_LATEX_WOLF_MALE::get)
            .add(ChangedEntities.DARK_LATEX_WOLF_FEMALE::get)
            .add(ChangedEntities.DARK_LATEX_DRAGON::get)
            .add(ChangedEntities.DARK_LATEX_YUFENG::get).build(), ChangedItems.DARK_LATEX_CRYSTAL_FRAGMENT, BlockBehaviour.Properties.of(ChangedMaterials.LATEX_CRYSTAL).sound(SoundType.AMETHYST).noOcclusion().noCollission().dynamicShape().strength(3.0F, 18.0F)), AbstractLatexCrystal::cutoutRenderer);
    public static final RegistryObject<Block> LATEX_TRAFFIC_CONE = register("latex_traffic_cone", LatexTrafficCone::new);
    public static final RegistryObject<Block> LATEX_BEIFENG_CRYSTAL = register("latex_beifeng_crystal", () -> new LatexBeifengCrystal(BlockBehaviour.Properties.copy(LATEX_CRYSTAL.get()).strength(4.0F, 30.0F)), AbstractLatexCrystal::cutoutRenderer);
    public static final RegistryObject<Block> DARK_LATEX_DRAGON_CRYSTAL = register("dark_latex_dragon_crystal", () -> new DarkLatexDragonCrystal(BlockBehaviour.Properties.copy(LATEX_CRYSTAL.get()).strength(4.0F, 30.0F)), AbstractLatexCrystal::cutoutRenderer);
    public static final RegistryObject<Block> LATEX_BEIFENG_CRYSTAL_SMALL = register("latex_beifeng_crystal_small", () -> new LatexCrystal(new ImmutableList.Builder<Supplier<EntityType<? extends DarkLatexEntity>>>()
            .add(ChangedEntities.LATEX_BEIFENG::get).build(), ChangedItems.LATEX_BEIFENG_CRYSTAL_FRAGMENT, BlockBehaviour.Properties.copy(LATEX_CRYSTAL.get())), AbstractLatexCrystal::cutoutRenderer);
    public static final RegistryObject<Block> LATEX_WOLF_CRYSTAL = register("latex_wolf_crystal", () -> new LatexWolfCrystal(BlockBehaviour.Properties.copy(LATEX_CRYSTAL.get()).strength(4.0F, 30.0F)), AbstractLatexCrystal::cutoutRenderer);
    public static final RegistryObject<Block> DARK_LATEX_CRYSTAL_LARGE = register("dark_latex_crystal_large", () -> new DarkLatexCrystalLarge(BlockBehaviour.Properties.copy(LATEX_CRYSTAL.get()).strength(4.0F, 30.0F)), AbstractLatexCrystal::cutoutRenderer);
    public static final RegistryObject<Block> LATEX_WOLF_CRYSTAL_SMALL = register("latex_wolf_crystal_small", () -> new LatexCrystal(new ImmutableList.Builder<Supplier<EntityType<? extends DarkLatexEntity>>>()
            .add(ChangedEntities.LATEX_CRYSTAL_WOLF::get).build(), ChangedItems.LATEX_WOLF_CRYSTAL_FRAGMENT, BlockBehaviour.Properties.copy(LATEX_CRYSTAL.get())), AbstractLatexCrystal::cutoutRenderer);
    public static final RegistryObject<Block> LIGHT_LATEX_PUDDLE_FEMALE = register("light_latex_puddle_female", () -> new AbstractPuddle(BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.WOOL).sound(SoundType.SLIME_BLOCK), LatexVariant.LIGHT_LATEX_WOLF.female()));
    public static final RegistryObject<Block> LIGHT_LATEX_PUDDLE_MALE = register("light_latex_puddle_male", () -> new AbstractPuddle(BlockBehaviour.Properties.copy(LIGHT_LATEX_PUDDLE_FEMALE.get()), LatexVariant.LIGHT_LATEX_WOLF.male()));
    public static final RegistryObject<Block> RETINAL_SCANNER = register("retinal_scanner", () -> new RetinalScanner(BlockBehaviour.Properties.copy(COMPUTER.get()).lightLevel((state) -> 0)));
    public static final RegistryObject<Block> TILES_CAUTION = register("tiles_caution", () -> new LabBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_YELLOW).sound(SoundType.STONE).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> TILES_GREENHOUSE = register("tiles_greenhouse", () -> new LabBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN).sound(SoundType.WOOD).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> WALL_GREENHOUSE = register("wall_greenhouse", () -> new LabBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN).sound(SoundType.WOOD).strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> TILES_GRAYBLUE = register("tiles_grayblue", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get()).color(MaterialColor.COLOR_LIGHT_BLUE)));
    public static final RegistryObject<Block> TILES_GRAYBLUE_STAIRS = register("tiles_grayblue_stairs", () -> new LabStairBlock(TILES_GRAYBLUE.get()::defaultBlockState, BlockBehaviour.Properties.copy(TILES_GRAYBLUE.get())));
    public static final RegistryObject<Block> TILES_GRAY = register("tiles_gray", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get()).color(MaterialColor.COLOR_GRAY)));
    public static final RegistryObject<Block> TILES_GRAYBLUE_BOLTED = register("tiles_grayblue_bolted", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get()).color(MaterialColor.COLOR_GRAY)));
    public static final RegistryObject<Block> TILES_GRAY_STAIRS = register("tiles_gray_stairs", () -> new LabStairBlock(TILES_GRAY.get()::defaultBlockState, BlockBehaviour.Properties.copy(TILES_GRAY.get())));
    public static final RegistryObject<Block> TILES_LIBRARY_BROWN = register("tiles_library_brown", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get()).color(MaterialColor.COLOR_BROWN)));
    public static final RegistryObject<Block> TILES_LIBRARY_TAN = register("tiles_library_tan", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get()).color(MaterialColor.WOOD)));
    public static final RegistryObject<Block> TILES_WHITE = register("tiles_white", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get()).color(MaterialColor.WOOL)));
    public static final RegistryObject<Block> TILES_WHITE_STAIRS = register("tiles_white_stairs", () -> new LabStairBlock(TILES_WHITE.get()::defaultBlockState, BlockBehaviour.Properties.copy(TILES_WHITE.get())));
    public static final RegistryObject<Block> TILES_WHITE_CONNECTED = register("tiles_white_connected", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_WHITE.get())));
    public static final RegistryObject<Block> VENT_HATCH = register("vent_hatch", () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR).sound(SoundType.METAL)), AbstractLatexCrystal::cutoutRenderer);
    public static final RegistryObject<Block> WALL_CAUTION = register("wall_caution", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    public static final RegistryObject<Block> WALL_LIBRARY_UPPER = register("wall_library_upper", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get()).color(MaterialColor.COLOR_BROWN)));
    public static final RegistryObject<Block> WALL_LIBRARY_LOWER = register("wall_library_lower", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get()).color(MaterialColor.COLOR_BROWN)));
    public static final RegistryObject<Block> WALL_LIGHTRED = register("wall_lightred", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get()).color(MaterialColor.COLOR_RED)));
    public static final RegistryObject<Block> WALL_LIGHTRED_STRIPED = register("wall_lightred_striped", () -> new LabBlock(BlockBehaviour.Properties.copy(WALL_LIGHTRED.get())));
    public static final RegistryObject<Block> WALL_GRAY = register("wall_gray", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get()).color(MaterialColor.COLOR_GRAY)));
    public static final RegistryObject<Block> WALL_GRAY_STRIPED = register("wall_gray_striped", () -> new LabBlock(BlockBehaviour.Properties.copy(WALL_GRAY.get())));
    public static final RegistryObject<Block> WALL_GREEN = register("wall_green", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    public static final RegistryObject<Block> WALL_GREEN_STRIPED = register("wall_green_striped", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    public static final RegistryObject<Block> WALL_VENT = register("wall_vent", () -> new LabBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).sound(SoundType.METAL).strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> WALL_WHITE = register("wall_white", () -> new LabBlock(BlockBehaviour.Properties.copy(WALL_GRAY.get()).color(MaterialColor.WOOL)));

    public static final RegistryObject<Block> WHITE_LATEX_FLUID = registerNoItem("white_latex_fluid", WhiteLatexFluid::new);
    public static final RegistryObject<Block> WHITE_LATEX_PILLAR = register("white_latex_pillar", () -> new WhiteLatexPillar(BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.WOOL).sound(SoundType.SLIME_BLOCK).strength(1.0F, 6.0F).noOcclusion()));

    public static final RegistryObject<Block> INFUSER = register("infuser", () -> new Infuser(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WOOL).sound(SoundType.METAL).strength(8.0f, 24.0f)));
    public static final RegistryObject<Block> PURIFIER = register("purifier", () -> new Purifier(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WOOL).sound(SoundType.METAL).strength(8.0f, 24.0f)));

    public static final RegistryObject<Block> DARK_LATEX_BLOCK = register("dark_latex_block", () -> new DarkLatexBlock(BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.COLOR_BLACK).sound(SoundType.SLIME_BLOCK).strength(1.0F, 6.0F)));
    public static final RegistryObject<Block> DARK_LATEX_ICE = register("dark_latex_ice", () -> new AbstractLatexIceBlock(BlockBehaviour.Properties.of(Material.ICE_SOLID, MaterialColor.COLOR_BLACK).friction(0.98F).sound(SoundType.GLASS).strength(1.0F, 0.2F)));
    public static final RegistryObject<Block> WHITE_LATEX_BLOCK = register("white_latex_block", () -> new WhiteLatexBlock(BlockBehaviour.Properties.copy(DARK_LATEX_BLOCK.get()).color(MaterialColor.WOOL).noOcclusion()
            .isViewBlocking(Blocks::never).isSuffocating(Blocks::never)));
    public static final RegistryObject<Block> ORANGE_TREE_SAPLING = register("orange_tree_sapling", () -> new SaplingBlock(new AbstractTreeGrower() {
        @Override
        protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random random, boolean p_204308_) {
            return ChangedFeatures.ORANGE_TREE;
        }
    }, BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)), AbstractLatexCrystal::cutoutRenderer);
    public static final RegistryObject<Block> ORANGE_TREE_LEAVES = register("orange_tree_leaves", () -> new LeavesBlock(
            BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(ChangedBlocks::ocelotOrParrot).isSuffocating(Blocks::never).isViewBlocking(Blocks::never)));

    private static Boolean ocelotOrParrot(BlockState p_50822_, BlockGetter p_50823_, BlockPos p_50824_, EntityType<?> p_50825_) {
        return p_50825_ == EntityType.OCELOT || p_50825_ == EntityType.PARROT;
    }

    private static RegistryObject<Block> registerNoItem(String name, Supplier<Block> block) {
        return registerNoItem(name, block, null);
    }

    private static RegistryObject<Block> registerNoItem(String name, Supplier<Block> blockConstructor, @Nullable Consumer<Block> renderLayer) {
        RegistryObject<Block> block = REGISTRY.register(name, blockConstructor);
        if (renderLayer != null)
            REGISTRY_CRL.put(block, renderLayer);
        return block;
    }

    private static RegistryObject<Block> register(String name, Supplier<Block> block) {
        return register(name, block, null);
    }

    private static RegistryObject<Block> register(String name, Supplier<Block> blockConstructor, @Nullable Consumer<Block> renderLayer) {
        RegistryObject<Block> block = REGISTRY.register(name, blockConstructor);
        if (renderLayer != null)
            REGISTRY_CRL.put(block, renderLayer);
        ChangedItems.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(ChangedTabs.TAB_CHANGED_BLOCKS)));
        return block;
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Block> event) {
        REGISTRY_CRL.forEach((block, blockConsumer) -> blockConsumer.accept(block.get()));
    }

    public static ResourceLocation textureOf(RegistryObject<Block> block) {
        return Changed.modResource("blocks/" + block.getId().getPath());
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientInitializer {
        @SubscribeEvent
        public static void onBlockColorsInit(ColorHandlerEvent.Block event) {
            event.getBlockColors().register((state, level, pos, layer) ->
                            switch (layer) {
                                case 0 ->
                                        level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor();
                                case 1 -> 0xFFFFFF;
                                default -> -1;
                            },
                    ORANGE_TREE_LEAVES.get());
        }
        @SubscribeEvent
        public static void onItemColorsInit(ColorHandlerEvent.Item event) {
            event.getItemColors().register((stack, layer) ->
                            switch (layer) {
                                case 0 -> FoliageColor.getDefaultColor();
                                case 1 -> 0xFFFFFF;
                                default -> -1;
                            },
                    ORANGE_TREE_LEAVES.get());
        }
    }
}
