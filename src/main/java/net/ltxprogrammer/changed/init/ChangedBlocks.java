package net.ltxprogrammer.changed.init;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.BeehiveWall;
import net.ltxprogrammer.changed.block.PipeBlock;
import net.ltxprogrammer.changed.block.*;
import net.ltxprogrammer.changed.entity.beast.DarkLatexEntity;
import net.ltxprogrammer.changed.item.BlockEntityRenderedBlockItem;
import net.ltxprogrammer.changed.item.EmptyMug;
import net.ltxprogrammer.changed.item.FluidCanister;
import net.ltxprogrammer.changed.item.GasCanister;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChangedBlocks {
    /*
    setBlock() flags:
    1 -> update neighbors
    2 -> notify clients
     */

    public static boolean always(BlockState blockState, BlockGetter level, BlockPos blockPos) {
        return true;
    }

    public static boolean never(BlockState blockState, BlockGetter level, BlockPos blockPos) {
        return false;
    }
    
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, Changed.MODID);
    private static final Map<RegistryObject<? extends Block>, Consumer<Block>> REGISTRY_CRL = new HashMap<>();

    public static final RegistryObject<FreshAirBlock> FRESH_AIR = registerNoItem("fresh_air", () -> new FreshAirBlock(BlockBehaviour.Properties.of().replaceable().noCollission().noLootTable().air()));

    public static final RegistryObject<AirConditionerBlock> AIR_CONDITIONER = register("air_conditioner", () -> new AirConditionerBlock(BlockBehaviour.Properties.of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    public static final RegistryObject<BarStool> BAR_STOOL = register("bar_stool", BarStool::new);
    public static final RegistryObject<BarTopBlock> BAR_TOP = register("bar_top", () -> new BarTopBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Beaker> BEAKER = register("beaker", Beaker::new);
    public static final RegistryObject<BedsideIVRack> BEDSIDE_IV_RACK = register("bedside_iv_rack", BedsideIVRack::new);
    public static final RegistryObject<BeehiveBed> BEEHIVE_BED = register("beehive_bed", BeehiveBed::new);
    public static final RegistryObject<BeehiveWall> BEEHIVE_WALL = register("beehive_wall", BeehiveWall::new);
    public static final RegistryObject<RailingBlock> BLACK_RAILING = register("black_railing", () -> new RailingBlock(BlockBehaviour.Properties.of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(3.0F, 5.0F)));
    public static final RegistryObject<BookStack> BOOK_STACK = registerNoItem("book_stack", () -> new BookStack(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(0.5F)));
    public static final RegistryObject<BoxPile> BOX_PILE = register("box_pile", BoxPile::new);
    public static final RegistryObject<CDStack> CD_STACK = registerNoItem("cd_stack", () -> new CDStack(BlockBehaviour.Properties.of().sound(SoundType.CANDLE).strength(0.2F)));
    public static final RegistryObject<ClipboardBlock> CLIPBOARD = register("clipboard", ClipboardBlock::new);
    public static final RegistryObject<Note> NOTE = register("note", Note::new);
    public static final RegistryObject<Computer> COMPUTER = register("computer", () -> new Computer(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(3.0F, 3.0F).lightLevel((state) -> 4)));
    public static final RegistryObject<AbstractCanBlock> CANNED_PEACHES = register("canned_peaches", AbstractCanBlock::new);
    public static final RegistryObject<CannedSoup> CANNED_SOUP = register("canned_soup", CannedSoup::new);
    public static final RegistryObject<CardboardBoxSmall> CARDBOARD_BOX_SMALL = register("cardboard_box_small", CardboardBoxSmall::new);
    public static final RegistryObject<CardboardBoxTall> CARDBOARD_BOX_TALL = register("cardboard_box", CardboardBoxTall::new);
    public static final RegistryObject<CardboardBox> CARDBOARD_BOX = register("cardboard_container", CardboardBox::new);
    public static final RegistryObject<AlertingPuddle> DARK_LATEX_PUDDLE = register("dark_latex_puddle", () -> new AlertingPuddle(ChangedLatexTypes.DARK_LATEX));
    public static final RegistryObject<DroppedOrange> DROPPED_ORANGE = registerNoItem("dropped_orange", DroppedOrange::new);
    public static final RegistryObject<DroppedSyringe> DROPPED_SYRINGE = registerNoItem("dropped_syringe", DroppedSyringe::new);
    public static final RegistryObject<DuctBlock> DUCT = register("duct", () -> new DuctBlock(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(3.0F, 3.0F)));
    public static final RegistryObject<ErlenmeyerFlask> ERLENMEYER_FLASK = register("erlenmeyer_flask", ErlenmeyerFlask::new);
    public static final RegistryObject<ExoskeletonCharger> EXOSKELETON_CHARGER = register("exoskeleton_charger", ExoskeletonCharger::new);
    public static final RegistryObject<FloorSignBlock> FLOOR_SIGN_ELECTRICAL = register("floor_sign_electrical", () -> new FloorSignBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD)));
    public static final RegistryObject<FloorSignBlock> FLOOR_SIGN_EXIT = register("floor_sign_exit", () -> new FloorSignBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD)));
    public static final RegistryObject<FloorSignBlock> FLOOR_SIGN_WET = register("floor_sign_wet", () -> new FloorSignBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD)));
    public static final RegistryObject<Generator> GENERATOR = register("generator", Generator::new);
    public static final RegistryObject<IronCrate> IRON_CRATE = register("iron_crate", () -> new IronCrate(BlockBehaviour.Properties.of().sound(SoundType.COPPER).requiresCorrectToolForDrops().strength(5.0F, 6.0F)));
    public static final RegistryObject<DarkLatexFluidBlock> DARK_LATEX_FLUID = registerNoItem("dark_latex_fluid", DarkLatexFluidBlock::new);
    public static final RegistryObject<KeypadBlock> KEYPAD = register("keypad", KeypadBlock::new);
    public static final RegistryObject<LabLight> LAB_LIGHT = register("lab_light", () -> new LabLight(BlockBehaviour.Properties.of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    public static final RegistryObject<LabLightSmall> LAB_LIGHT_SMALL = register("lab_light_small", () -> new LabLightSmall(BlockBehaviour.Properties.copy(LAB_LIGHT.get())));
    public static final RegistryObject<LabTable> LAB_TABLE = register("lab_table", () -> new LabTable(BlockBehaviour.Properties.of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(3.0F, 5.0F)));
    public static final RegistryObject<LaserEmitterBlock> LASER_EMITTER = register("laser_emitter", LaserEmitterBlock::new);
    public static final RegistryObject<LatexCrystal> LATEX_CRYSTAL = register("latex_crystal", () -> new LatexCrystal(new ImmutableList.Builder<Supplier<EntityType<? extends DarkLatexEntity>>>()
            .add(ChangedEntities.DARK_LATEX_WOLF_PUP::get).build(), ChangedItems.DARK_LATEX_CRYSTAL_FRAGMENT, BlockBehaviour.Properties.of().sound(SoundType.AMETHYST_CLUSTER).noOcclusion().noCollission().dynamicShape().strength(1.7F, 0.2F)));
    public static final RegistryObject<LatexPupCrystal> LATEX_PUP_CRYSTAL = register("latex_pup_crystal", () -> new LatexPupCrystal(ChangedTransfurVariants.DARK_LATEX_WOLF_PUP, 6, ChangedItems.DARK_LATEX_CRYSTAL_FRAGMENT, BlockBehaviour.Properties.copy(LATEX_CRYSTAL.get())));
    public static final RegistryObject<LatexContainerBlock> LATEX_CONTAINER = register("latex_container", LatexContainerBlock::new);
    public static final RegistryObject<LatexTrafficCone> LATEX_TRAFFIC_CONE = register("latex_traffic_cone", LatexTrafficCone::new);
    public static final RegistryObject<MugBlock> MUG = register("mug", MugBlock::new, block -> new EmptyMug(block, new Item.Properties()));
    public static final RegistryObject<BeifengCrystal> BEIFENG_CRYSTAL = register("beifeng_crystal", () -> new BeifengCrystal(BlockBehaviour.Properties.copy(LATEX_CRYSTAL.get()).strength(1.7F, 0.2F)));
    public static final RegistryObject<BeifengCrystalSmall> BEIFENG_CRYSTAL_SMALL = register("beifeng_crystal_small", () -> new BeifengCrystalSmall(BlockBehaviour.Properties.copy(LATEX_CRYSTAL.get()).strength(1.7F, 0.2F)));
    public static final RegistryObject<DarkDragonCrystal> DARK_DRAGON_CRYSTAL = register("dark_dragon_crystal", () -> new DarkDragonCrystal(BlockBehaviour.Properties.copy(LATEX_CRYSTAL.get()).strength(1.7F, 0.2F)));
    public static final RegistryObject<WolfCrystal> WOLF_CRYSTAL = register("wolf_crystal", () -> new WolfCrystal(BlockBehaviour.Properties.copy(LATEX_CRYSTAL.get()).strength(1.7F, 0.2F)));
    public static final RegistryObject<SmallWolfCrystal> WOLF_CRYSTAL_SMALL = register("wolf_crystal_small", () -> new SmallWolfCrystal(BlockBehaviour.Properties.copy(LATEX_CRYSTAL.get()).strength(1.7F, 0.2F)));
    public static final RegistryObject<DarkTransfurCrystalLarge> DARK_LATEX_CRYSTAL_LARGE = register("dark_latex_crystal_large", () -> new DarkTransfurCrystalLarge(BlockBehaviour.Properties.copy(LATEX_CRYSTAL.get()).strength(1.7F, 0.2F)));
    public static final RegistryObject<AbstractPuddle> WHITE_LATEX_PUDDLE_FEMALE = register("white_latex_puddle_female", () -> new AbstractPuddle(BlockBehaviour.Properties.of().sound(SoundType.SLIME_BLOCK).strength(0.1F), ChangedTransfurVariants.WHITE_LATEX_WOLF_FEMALE));
    public static final RegistryObject<AbstractPuddle> WHITE_LATEX_PUDDLE_MALE = register("white_latex_puddle_male", () -> new AbstractPuddle(BlockBehaviour.Properties.copy(WHITE_LATEX_PUDDLE_FEMALE.get()), ChangedTransfurVariants.WHITE_LATEX_WOLF_MALE));
    public static final RegistryObject<PipeBlock> PIPE = register("pipe", PipeBlock::new);
    public static final RegistryObject<PenBox> PEN_BOX = register("pen_box", PenBox::new);
    public static final RegistryObject<PetriDishBlock> PETRI_DISH = register("petri_dish", () -> new PetriDishBlock(BlockBehaviour.Properties.of().sound(SoundType.GLASS).instabreak().dynamicShape()));
    public static final RegistryObject<RetinalScanner> RETINAL_SCANNER = register("retinal_scanner", () -> new RetinalScanner(BlockBehaviour.Properties.copy(COMPUTER.get()).lightLevel((state) -> 0)));
    public static final RegistryObject<RoombaCharger> ROOMBA_CHARGER = register("roomba_charger", () -> new RoombaCharger(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(3.0f, 3.0f)));
    public static final RegistryObject<ShippingContainer> SHIPPING_CONTAINER_BLUE = register("shipping_container_blue", ShippingContainer::new);
    public static final RegistryObject<ShippingContainer> SHIPPING_CONTAINER_ORANGE = register("shipping_container_orange", ShippingContainer::new);
    public static final RegistryObject<SpeakerBlock> SPEAKER = register("speaker", () -> new SpeakerBlock(BlockBehaviour.Properties.copy(RETINAL_SCANNER.get())));
    public static final RegistryObject<MicrophoneBlock> MICROPHONE = register("microphone", () -> new MicrophoneBlock(BlockBehaviour.Properties.copy(RETINAL_SCANNER.get())));
    public static final RegistryObject<Microscope> MICROSCOPE = register("microscope", () -> new Microscope(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(1.0F, 4.0F)));
    public static final RegistryObject<OfficeChair> OFFICE_CHAIR = register("office_chair", OfficeChair::new);
    public static final RegistryObject<TapeRecorder> TAPE_RECORDER = register("tape_recorder", () -> new TapeRecorder(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(1.0F, 4.0F)));
    public static final RegistryObject<LabBlock> TILES_BLUE = register("tiles_blue", () -> new LabBlock(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1.5F, 6.5F)));
    public static final RegistryObject<LabBlock> TILES_BLUE_SMALL = register("tiles_blue_small", () -> new LabBlock(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1.5F, 6.5F)));
    public static final RegistryObject<LabBlock> TILES_CAUTION = register("tiles_caution", () -> new LabBlock(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1.5F, 6.5F)));
    public static final RegistryObject<LabSlabBlock> TILES_CAUTION_SLAB = register("tiles_caution_slab", () -> new LabSlabBlock(TILES_CAUTION.get()::defaultBlockState, BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    public static final RegistryObject<LabStairBlock> TILES_CAUTION_STAIRS = register("tiles_caution_stairs", () -> new LabStairBlock(TILES_CAUTION.get()::defaultBlockState, BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    public static final RegistryObject<ConnectedFloorBlock> TILES_GREENHOUSE = register("tiles_greenhouse", () -> new ConnectedFloorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<ChangedBlock> WALL_GREENHOUSE = register("wall_greenhouse", () -> new ChangedBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<ConnectedFloorBlock> TILES_GRAYBLUE = register("tiles_grayblue", () -> new ConnectedFloorBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    public static final RegistryObject<LabSlabBlock> TILES_GRAYBLUE_SLAB = register("tiles_grayblue_slab", () -> new LabSlabBlock(TILES_GRAYBLUE.get()::defaultBlockState, BlockBehaviour.Properties.copy(TILES_GRAYBLUE.get())));
    public static final RegistryObject<LabStairBlock> TILES_GRAYBLUE_STAIRS = register("tiles_grayblue_stairs", () -> new LabStairBlock(TILES_GRAYBLUE.get()::defaultBlockState, BlockBehaviour.Properties.copy(TILES_GRAYBLUE.get())));
    public static final RegistryObject<LabBlock> TILES_GRAY = register("tiles_gray", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    public static final RegistryObject<LabSlabBlock> TILES_GRAY_SLAB = register("tiles_gray_slab", () -> new LabSlabBlock(TILES_GRAY.get()::defaultBlockState, BlockBehaviour.Properties.copy(TILES_GRAY.get())));
    public static final RegistryObject<LabStairBlock> TILES_GRAY_STAIRS = register("tiles_gray_stairs", () -> new LabStairBlock(TILES_GRAY.get()::defaultBlockState, BlockBehaviour.Properties.copy(TILES_GRAY.get())));
    public static final RegistryObject<LabBlock> TILES_GRAYBLUE_BOLTED = register("tiles_grayblue_bolted", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    // TODO rename out of "connected" to distinguish it from TILES_GRAYBLUE_BOLTED
    public static final RegistryObject<ConnectedFloorBlock> TILES_GRAYBLUE_BOLTED_CONNECTED = register("tiles_grayblue_bolted_connected", () -> new ConnectedFloorBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    public static final RegistryObject<LabSlabBlock> TILES_GRAYBLUE_BOLTED_SLAB = register("tiles_grayblue_bolted_slab", () -> new LabSlabBlock(TILES_GRAYBLUE_BOLTED.get()::defaultBlockState, BlockBehaviour.Properties.copy(TILES_GRAYBLUE_BOLTED.get())));
    public static final RegistryObject<LabStairBlock> TILES_GRAYBLUE_BOLTED_STAIRS = register("tiles_grayblue_bolted_stairs", () -> new LabStairBlock(TILES_GRAYBLUE_BOLTED.get()::defaultBlockState, BlockBehaviour.Properties.copy(TILES_GRAYBLUE_BOLTED.get())));
    public static final RegistryObject<LabBlock> TILES_LIBRARY_BROWN = register("tiles_library_brown", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    public static final RegistryObject<LabSlabBlock> TILES_LIBRARY_BROWN_SLAB = register("tiles_library_brown_slab", () -> new LabSlabBlock(TILES_LIBRARY_BROWN.get()::defaultBlockState, BlockBehaviour.Properties.copy(TILES_LIBRARY_BROWN.get())));
    public static final RegistryObject<LabStairBlock> TILES_LIBRARY_BROWN_STAIRS = register("tiles_library_brown_stairs", () -> new LabStairBlock(TILES_LIBRARY_BROWN.get()::defaultBlockState, BlockBehaviour.Properties.copy(TILES_LIBRARY_BROWN.get())));
    public static final RegistryObject<LabBlock> TILES_LIBRARY_TAN = register("tiles_library_tan", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    public static final RegistryObject<LabSlabBlock> TILES_LIBRARY_TAN_SLAB = register("tiles_library_tan_slab", () -> new LabSlabBlock(TILES_LIBRARY_TAN.get()::defaultBlockState, BlockBehaviour.Properties.copy(TILES_LIBRARY_TAN.get())));
    public static final RegistryObject<LabStairBlock> TILES_LIBRARY_TAN_STAIRS = register("tiles_library_tan_stairs", () -> new LabStairBlock(TILES_LIBRARY_TAN.get()::defaultBlockState, BlockBehaviour.Properties.copy(TILES_LIBRARY_TAN.get())));
    public static final RegistryObject<LabBlock> TILES_TEAL = register("tiles_teal", () -> new LabBlock(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1.5F, 6.5F)));
    public static final RegistryObject<ConnectedFloorBlock> TILES_WHITE = register("tiles_white", () -> new ConnectedFloorBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    public static final RegistryObject<LabSlabBlock> TILES_WHITE_SLAB = register("tiles_white_slab", () -> new LabSlabBlock(TILES_WHITE.get()::defaultBlockState, BlockBehaviour.Properties.copy(TILES_WHITE.get())));
    public static final RegistryObject<LabStairBlock> TILES_WHITE_STAIRS = register("tiles_white_stairs", () -> new LabStairBlock(TILES_WHITE.get()::defaultBlockState, BlockBehaviour.Properties.copy(TILES_WHITE.get())));
    public static final RegistryObject<ConnectedFloorBlock> ORANGE_LAB_CARPETING = register("orange_lab_carpeting", () -> new ConnectedFloorBlock(BlockBehaviour.Properties.copy(Blocks.ORANGE_WOOL)));
    public static final RegistryObject<VentFanBlock> VENT_FAN = register("vent_fan", VentFanBlock::new);
    public static final RegistryObject<VentHatchBlock> VENT_HATCH = register("vent_hatch", () -> new VentHatchBlock(BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR).sound(SoundType.METAL).requiresCorrectToolForDrops()));
    public static final RegistryObject<LabBlock> WALL_WHITE_CRACKED = register("wall_white_cracked", () -> new LabBlock(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1.2F, 5.2F)));
    public static final RegistryObject<LabBlock> WALL_BLUE_STRIPED = register("wall_blue_striped", () -> new LabBlock(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1.5F, 6.5F)));
    public static final RegistryObject<LabBlock> WALL_BLUE_TILED = register("wall_blue_tiled", () -> new LabBlock(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(1.5F, 6.5F)));
    public static final RegistryObject<LabBlock> WALL_CAUTION = register("wall_caution", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    public static final RegistryObject<LabBlock> WALL_LIBRARY_UPPER = register("wall_library_upper", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    public static final RegistryObject<LabBlock> WALL_LIBRARY_LOWER = register("wall_library_lower", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    public static final RegistryObject<LabBlock> WALL_LIGHTRED = register("wall_lightred", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    public static final RegistryObject<LabBlock> WALL_LIGHTRED_CRACKED = register("wall_lightred_cracked", () -> new LabBlock(BlockBehaviour.Properties.copy(WALL_WHITE_CRACKED.get())));
    public static final RegistryObject<LabSlabBlock> WALL_LIGHTRED_SLAB = register("wall_lightred_slab", () -> new LabSlabBlock(WALL_LIGHTRED.get()::defaultBlockState, BlockBehaviour.Properties.copy(WALL_LIGHTRED.get())));
    public static final RegistryObject<LabStairBlock> WALL_LIGHTRED_STAIRS = register("wall_lightred_stairs", () -> new LabStairBlock(WALL_LIGHTRED.get()::defaultBlockState, BlockBehaviour.Properties.copy(WALL_LIGHTRED.get())));
    public static final RegistryObject<LabBlock> WALL_LIGHTRED_STRIPED = register("wall_lightred_striped", () -> new LabBlock(BlockBehaviour.Properties.copy(WALL_LIGHTRED.get())));
    public static final RegistryObject<LabBlock> WALL_GRAY = register("wall_gray", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    public static final RegistryObject<LabBlock> WALL_GRAY_CRACKED = register("wall_gray_cracked", () -> new LabBlock(BlockBehaviour.Properties.copy(WALL_WHITE_CRACKED.get())));
    public static final RegistryObject<LabSlabBlock> WALL_GRAY_SLAB = register("wall_gray_slab", () -> new LabSlabBlock(WALL_GRAY.get()::defaultBlockState, BlockBehaviour.Properties.copy(WALL_GRAY.get())));
    public static final RegistryObject<LabStairBlock> WALL_GRAY_STAIRS = register("wall_gray_stairs", () -> new LabStairBlock(WALL_GRAY.get()::defaultBlockState, BlockBehaviour.Properties.copy(WALL_GRAY.get())));
    public static final RegistryObject<LabBlock> WALL_GRAY_STRIPED = register("wall_gray_striped", () -> new LabBlock(BlockBehaviour.Properties.copy(WALL_GRAY.get())));
    public static final RegistryObject<LabBlock> WALL_GREEN = register("wall_green", () -> new LabBlock(BlockBehaviour.Properties.copy(TILES_CAUTION.get())));
    public static final RegistryObject<LabBlock> WALL_GREEN_CRACKED = register("wall_green_cracked", () -> new LabBlock(BlockBehaviour.Properties.copy(WALL_WHITE_CRACKED.get())));
    public static final RegistryObject<LabSlabBlock> WALL_GREEN_SLAB = register("wall_green_slab", () -> new LabSlabBlock(WALL_GREEN.get()::defaultBlockState, BlockBehaviour.Properties.copy(WALL_GREEN.get())));
    public static final RegistryObject<LabStairBlock> WALL_GREEN_STAIRS = register("wall_green_stairs", () -> new LabStairBlock(WALL_GREEN.get()::defaultBlockState, BlockBehaviour.Properties.copy(WALL_GREEN.get())));
    public static final RegistryObject<LabBlock> WALL_GREEN_STRIPED = register("wall_green_striped", () -> new LabBlock(BlockBehaviour.Properties.copy(WALL_GREEN.get())));
    public static final RegistryObject<LabBlock> WALL_VENT = register("wall_vent", () -> new LabBlock(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(2.5F, 6.5F)));
    public static final RegistryObject<LabBlock> WALL_WHITE = register("wall_white", () -> new LabBlock(BlockBehaviour.Properties.copy(WALL_GRAY.get())));
    public static final RegistryObject<LabBlock> WALL_WHITE_GREEN_STRIPED = register("wall_white_green_striped", () -> new LabBlock(BlockBehaviour.Properties.copy(WALL_WHITE.get())));
    public static final RegistryObject<LabBlock> WALL_WHITE_GREEN_TILED = register("wall_white_green_tiled", () -> new LabBlock(BlockBehaviour.Properties.copy(WALL_WHITE.get())));
    public static final RegistryObject<LabSlabBlock> WALL_WHITE_SLAB = register("wall_white_slab", () -> new LabSlabBlock(WALL_WHITE.get()::defaultBlockState, BlockBehaviour.Properties.copy(WALL_WHITE.get())));
    public static final RegistryObject<LabStairBlock> WALL_WHITE_STAIRS = register("wall_white_stairs", () -> new LabStairBlock(WALL_WHITE.get()::defaultBlockState, BlockBehaviour.Properties.copy(WALL_WHITE.get())));
    public static final RegistryObject<LabTable> WHITE_LAB_TABLE = register("white_lab_table", () -> new LabTable(BlockBehaviour.Properties.of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(3.0F, 5.0F)));

    public static final RegistryObject<WhiteLatexFluidBlock> WHITE_LATEX_FLUID = registerNoItem("white_latex_fluid", WhiteLatexFluidBlock::new);
    public static final RegistryObject<WhiteLatexPillar> WHITE_LATEX_PILLAR = register("white_latex_pillar", () -> new WhiteLatexPillar(BlockBehaviour.Properties.of().sound(SoundType.SLIME_BLOCK).strength(1.0F, 4.0F).noOcclusion()));

    public static final RegistryObject<AbstractLabDoor> BLUE_LAB_DOOR = register("blue_lab_door", () -> new AbstractLabDoor(ChangedSounds.LAB_DOOR_OPEN, ChangedSounds.LAB_DOOR_CLOSE, ChangedSounds.LAB_DOOR_LOCKED, false));
    public static final RegistryObject<AbstractLabDoor> MAINTENANCE_DOOR = register("maintenance_door", () -> new AbstractLabDoor(ChangedSounds.MAINTENANCE_DOOR_OPEN, ChangedSounds.MAINTENANCE_DOOR_CLOSE, ChangedSounds.MAINTENANCE_DOOR_LOCKED, false));
    public static final RegistryObject<AbstractLabDoor> LAB_DOOR = register("lab_door", () -> new AbstractLabDoor(ChangedSounds.LAB_DOOR_OPEN, ChangedSounds.LAB_DOOR_CLOSE, ChangedSounds.LAB_DOOR_LOCKED, false));
    public static final RegistryObject<AbstractLabDoor> LIBRARY_DOOR = register("library_door", () -> new AbstractLabDoor(ChangedSounds.LIBRARY_DOOR_OPEN, ChangedSounds.LIBRARY_DOOR_CLOSE, ChangedSounds.LIBRARY_DOOR_LOCKED, true));
    public static final RegistryObject<AbstractLargeLabDoor> LARGE_BLUE_LAB_DOOR = register("large_blue_lab_door", () -> new AbstractLargeLabDoor(ChangedSounds.LAB_DOOR_OPEN, ChangedSounds.LAB_DOOR_CLOSE, ChangedSounds.LAB_DOOR_LOCKED, false));
    public static final RegistryObject<AbstractLargeLabDoor> LARGE_MAINTENANCE_DOOR = register("large_maintenance_door", () -> new AbstractLargeLabDoor(ChangedSounds.MAINTENANCE_DOOR_OPEN, ChangedSounds.MAINTENANCE_DOOR_CLOSE, ChangedSounds.MAINTENANCE_DOOR_LOCKED, false));
    public static final RegistryObject<AbstractLargeLabDoor> LARGE_LAB_DOOR = register("large_lab_door", () -> new AbstractLargeLabDoor(ChangedSounds.LAB_DOOR_OPEN, ChangedSounds.LAB_DOOR_CLOSE, ChangedSounds.LAB_DOOR_LOCKED, false));
    public static final RegistryObject<AbstractLargeLabDoor> LARGE_LIBRARY_DOOR = register("large_library_door", () -> new AbstractLargeLabDoor(ChangedSounds.LIBRARY_DOOR_OPEN, ChangedSounds.LIBRARY_DOOR_CLOSE, ChangedSounds.LIBRARY_DOOR_LOCKED, true));

    public static final RegistryObject<FluidCanisterBlock> EMPTY_CANISTER = register("empty_canister",
            () -> new FluidCanisterBlock(null),
            canister -> new FluidCanister(canister, new Item.Properties(), null));
    public static final RegistryObject<FluidCanisterBlock> OXYGENATED_WATER_CANISTER = register("oxygenated_water_canister",
            () -> new FluidCanisterBlock(null),
            canister -> new FluidCanister(canister, new Item.Properties(), () -> Fluids.WATER));

    public static final RegistryObject<GasFluidBlock> SKUNK_GAS = registerNoItem("skunk_gas", () -> new GasFluidBlock(ChangedFluids.SKUNK_GAS));
    public static final RegistryObject<FluidCanisterBlock> SKUNK_GAS_CANISTER = register("skunk_gas_canister",
            () -> new FluidCanisterBlock(ChangedFluids.SKUNK_GAS),
            canister -> new GasCanister(canister, ChangedFluids.SKUNK_GAS));
    public static final RegistryObject<GasFluidBlock> TIGER_GAS = registerNoItem("tiger_gas", () -> new GasFluidBlock(ChangedFluids.TIGER_GAS));
    public static final RegistryObject<FluidCanisterBlock> TIGER_GAS_CANISTER = register("tiger_gas_canister",
            () -> new FluidCanisterBlock(ChangedFluids.TIGER_GAS),
            canister -> new GasCanister(canister, ChangedFluids.TIGER_GAS));
    public static final RegistryObject<GasFluidBlock> WOLF_GAS = registerNoItem("wolf_gas", () -> new GasFluidBlock(ChangedFluids.WOLF_GAS));
    public static final RegistryObject<FluidCanisterBlock> WOLF_GAS_CANISTER = register("wolf_gas_canister",
            () -> new FluidCanisterBlock(ChangedFluids.WOLF_GAS),
            canister -> new GasCanister(canister, ChangedFluids.WOLF_GAS));

    public static final RegistryObject<StasisChamber> STASIS_CHAMBER = register("stasis_chamber", () -> new StasisChamber(ChangedSounds.STASIS_CHAMBER_DOOR_OPEN, ChangedSounds.STASIS_CHAMBER_DOOR_CLOSE));

    public static final List<RegistryObject<? extends Block>> LAB_DOORS = Util.make(new ArrayList<>(), list -> {
        list.add(BLUE_LAB_DOOR);
        list.add(MAINTENANCE_DOOR);
        list.add(LAB_DOOR);
        list.add(LIBRARY_DOOR);
        list.add(LARGE_BLUE_LAB_DOOR);
        list.add(LARGE_MAINTENANCE_DOOR);
        list.add(LARGE_LAB_DOOR);
        list.add(LARGE_LIBRARY_DOOR);
    });

    public static final RegistryObject<Infuser> INFUSER = register("infuser", () -> new Infuser(BlockBehaviour.Properties.of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(4.5F, 3.0F)));
    public static final RegistryObject<Purifier> PURIFIER = register("purifier", () -> new Purifier(BlockBehaviour.Properties.of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(4.5F, 3.0F)));

    public static final RegistryObject<DarkLatexBlock> DARK_LATEX_BLOCK = register("dark_latex_block", () -> new DarkLatexBlock(BlockBehaviour.Properties.of().sound(SoundType.SLIME_BLOCK).strength(1.0F, 4.0F)));
    public static final RegistryObject<LatexWallSplotch> DARK_LATEX_WALL_SPLOTCH = registerNoItem("dark_latex_wall_splotch", () -> new LatexWallSplotch(ChangedLatexTypes.DARK_LATEX, List.of()));
    public static final RegistryObject<AbstractLatexIceBlock> DARK_LATEX_ICE = register("dark_latex_ice", () -> new AbstractLatexIceBlock(BlockBehaviour.Properties.of().friction(0.98F).sound(SoundType.GLASS).strength(1.5F, 1.0F)));
    public static final RegistryObject<WolfCrystalBlock> WOLF_CRYSTAL_BLOCK = register("wolf_crystal_block", () -> new WolfCrystalBlock(
            BlockBehaviour.Properties.of().friction(0.98F).sound(SoundType.AMETHYST).strength(2.0F, 2.0F)));
    public static final RegistryObject<WhiteLatexBlock> WHITE_LATEX_BLOCK = register("white_latex_block", () -> new WhiteLatexBlock(BlockBehaviour.Properties.copy(DARK_LATEX_BLOCK.get()).noOcclusion()
            .isViewBlocking(ChangedBlocks::never).isSuffocating(ChangedBlocks::never)));
    public static final RegistryObject<LatexWallSplotch> WHITE_LATEX_WALL_SPLOTCH = registerNoItem("white_latex_wall_splotch", () -> new LatexWallSplotch(ChangedLatexTypes.WHITE_LATEX, List.of(ChangedTransfurVariants.LATEX_HUMAN)));
    public static final RegistryObject<SaplingBlock> ORANGE_TREE_SAPLING = register("orange_tree_sapling", () -> new SaplingBlock(new AbstractTreeGrower() {
        @Override
        protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(@NotNull RandomSource random, boolean hasFlowers) {
            return ChangedFeatures.ORANGE_TREE;
        }
    }, BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
    public static final RegistryObject<FlowerPotBlock> POTTED_ORANGE_TREE_SAPLING = registerPottedPlant("potted_orange_tree_sapling", ORANGE_TREE_SAPLING);
    public static final RegistryObject<LeavesBlock> ORANGE_TREE_LEAVES = register("orange_tree_leaves", () -> new LeavesBlock(
            BlockBehaviour.Properties.of().strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(ChangedBlocks::ocelotOrParrot).isSuffocating(ChangedBlocks::never).isViewBlocking(ChangedBlocks::never)));

    public static final EnumMap<DyeColor, RegistryObject<Pillow>> PILLOWS = Util.make(new EnumMap<>(DyeColor.class), map -> {
        Arrays.stream(DyeColor.values()).forEach(color -> {
            map.put(color, register(color.getName() + "_pillow", () -> Pillow.forColor(color),
                    block -> new BlockEntityRenderedBlockItem(block, new Item.Properties())));
        });
    });

    // Structure specific block to help procedural generation
    public static final RegistryObject<GluBlock> GLU_BLOCK = registerNoItem("glu", GluBlock::new);

    private static Boolean ocelotOrParrot(BlockState p_50822_, BlockGetter p_50823_, BlockPos p_50824_, EntityType<?> p_50825_) {
        return p_50825_ == EntityType.OCELOT || p_50825_ == EntityType.PARROT;
    }

    private static RegistryObject<FlowerPotBlock> registerPottedPlant(String name, RegistryObject<? extends Block> plant) {
        return registerNoItem(name, () -> {
            var filledPot = new FlowerPotBlock(() -> (FlowerPotBlock)Blocks.FLOWER_POT, plant, BlockBehaviour.Properties.of().instabreak().noOcclusion());
            ((FlowerPotBlock)Blocks.FLOWER_POT).addPlant(plant.getId(), () -> filledPot);
            return filledPot;
        });
    }

    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
        return REGISTRY.register(name, block);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockConstructor) {
        return register(name, blockConstructor, block -> new BlockItem(block, new Item.Properties()));
    }

    private static <T extends Block, I extends Item> RegistryObject<T> register(String name, Supplier<T> blockConstructor,
                                                                                @Nullable Function<T, I> item) {
        RegistryObject<T> block = REGISTRY.register(name, blockConstructor);
        if (item != null)
            ChangedItems.REGISTRY.register(name, () -> item.apply(block.get()));
        return block;
    }

    public static ResourceLocation textureOf(RegistryObject<? extends Block> block) {
        return Changed.modResource("block/" + block.getId().getPath());
    }
}
