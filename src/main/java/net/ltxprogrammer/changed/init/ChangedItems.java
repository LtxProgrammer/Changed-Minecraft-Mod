package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.item.*;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, Changed.MODID);
    public static final RegistryObject<Benign.Pants> BENIGN_PANTS = register("benign_pants", Benign.Pants::new);
    public static final RegistryObject<PinkLatex.Pants> PINK_PANTS = register("pink_pants", PinkLatex.Pants::new);
    public static final RegistryObject<AbstractLatexItem> DARK_LATEX_CRYSTAL_FRAGMENT = register("dark_latex_crystal_fragment", AbstractLatexItem::new);
    public static final RegistryObject<AbstractLatexGoo> DARK_LATEX_GOO = register("dark_latex_goo", () -> new AbstractLatexGoo(LatexType.DARK_LATEX));
    public static final RegistryObject<DarkLatexMask> DARK_LATEX_MASK = register("dark_latex_mask", DarkLatexMask::new);
    public static final RegistryObject<AbstractLatexBucket> DARK_LATEX_BUCKET = register("dark_latex_bucket", AbstractLatexBucket.from(ChangedFluids.DARK_LATEX));
    public static final RegistryObject<CompactDisc> COMPACT_DISC = register("compact_disc", CompactDisc::new);
    public static final RegistryObject<LabBook> LAB_BOOK = register("lab_book", LabBook::new);
    public static final RegistryObject<Item> LATEX_BASE = register("latex_base", () -> new Item(new Item.Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS)));
    public static final RegistryObject<Item> ORANGE = register("orange", () -> new ItemNameBlockItem(ChangedBlocks.DROPPED_ORANGE.get(), (new Item.Properties()).tab(ChangedTabs.TAB_CHANGED_ITEMS).food(ChangedFoods.ORANGE)));
    public static final RegistryObject<Syringe> SYRINGE = register("syringe", () -> new Syringe(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<BloodSyringe> BLOOD_SYRINGE = register("blood_syringe", () -> new BloodSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<AbstractLatexCrystalItem> LATEX_BEIFENG_CRYSTAL_FRAGMENT = register("latex_beifeng_crystal_fragment",
            () -> new AbstractLatexCrystalItem(LatexVariant.LATEX_BEIFENG));
    public static final RegistryObject<LatexInkballItem> LATEX_INKBALL = register("latex_inkball", LatexInkballItem::new);
    public static final RegistryObject<LatexSyringe> LATEX_SYRINGE = register("latex_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexTippedArrowItem> LATEX_TIPPED_ARROW = register("latex_tipped_arrow", LatexTippedArrowItem::new);
    public static final RegistryObject<AbstractLatexCrystalItem> LATEX_WOLF_CRYSTAL_FRAGMENT = register("latex_wolf_crystal_fragment",
            () -> new AbstractLatexCrystalItem(LatexVariant.LATEX_CRYSTAL_WOLF));
    public static final RegistryObject<AbstractLatexCrystalItem> DARK_LATEX_DRAGON_CRYSTAL_FRAGMENT = register("dark_latex_dragon_crystal_fragment",
            () -> new AbstractLatexCrystalItem(LatexVariant.DARK_LATEX_DRAGON));
    public static final RegistryObject<AbstractLatexGoo> WHITE_LATEX_GOO = register("white_latex_goo", () -> new AbstractLatexGoo(LatexType.WHITE_LATEX));
    public static final RegistryObject<AbstractLatexBucket> WHITE_LATEX_BUCKET = register("white_latex_bucket", AbstractLatexBucket.from(ChangedFluids.WHITE_LATEX));

    public static final RegistryObject<GameMasterBlockItem> GLU = register("glu", () -> new GameMasterBlockItem(ChangedBlocks.GLU_BLOCK.get(), (new Item.Properties()).rarity(Rarity.EPIC)));

    public static final RegistryObject<TscStaff> TSC_STAFF = register("tsc_staff", TscStaff::new);
    public static final RegistryObject<TscBaton> TSC_BATON = register("tsc_baton", TscBaton::new);
    public static final RegistryObject<TscShield> TSC_SHIELD = register("tsc_shield", TscShield::new);

    public static final RegistryObject<AbdomenArmor> LEATHER_UPPER_ABDOMEN_ARMOR = register("leather_upper_abdomen_armor",
            () -> new DyeableAbdomenArmor(ArmorMaterials.LEATHER, EquipmentSlot.LEGS));
    public static final RegistryObject<AbdomenArmor> LEATHER_LOWER_ABDOMEN_ARMOR = register("leather_lower_abdomen_armor",
            () -> new DyeableAbdomenArmor(ArmorMaterials.LEATHER, EquipmentSlot.FEET));
    public static final RegistryObject<AbdomenArmor> CHAINMAIL_UPPER_ABDOMEN_ARMOR = register("chainmail_upper_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.CHAIN, EquipmentSlot.LEGS));
    public static final RegistryObject<AbdomenArmor> CHAINMAIL_LOWER_ABDOMEN_ARMOR = register("chainmail_lower_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.CHAIN, EquipmentSlot.FEET));
    public static final RegistryObject<AbdomenArmor> IRON_UPPER_ABDOMEN_ARMOR = register("iron_upper_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.IRON, EquipmentSlot.LEGS));
    public static final RegistryObject<AbdomenArmor> IRON_LOWER_ABDOMEN_ARMOR = register("iron_lower_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.IRON, EquipmentSlot.FEET));
    public static final RegistryObject<AbdomenArmor> GOLDEN_UPPER_ABDOMEN_ARMOR = register("golden_upper_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.GOLD, EquipmentSlot.LEGS));
    public static final RegistryObject<AbdomenArmor> GOLDEN_LOWER_ABDOMEN_ARMOR = register("golden_lower_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.GOLD, EquipmentSlot.FEET));
    public static final RegistryObject<AbdomenArmor> DIAMOND_UPPER_ABDOMEN_ARMOR = register("diamond_upper_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.DIAMOND, EquipmentSlot.LEGS));
    public static final RegistryObject<AbdomenArmor> DIAMOND_LOWER_ABDOMEN_ARMOR = register("diamond_lower_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.DIAMOND, EquipmentSlot.FEET));
    public static final RegistryObject<AbdomenArmor> NETHERITE_UPPER_ABDOMEN_ARMOR = register("netherite_upper_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.NETHERITE, EquipmentSlot.LEGS, new Item.Properties().tab(ChangedTabs.TAB_CHANGED_COMBAT).fireResistant()));
    public static final RegistryObject<AbdomenArmor> NETHERITE_LOWER_ABDOMEN_ARMOR = register("netherite_lower_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.NETHERITE, EquipmentSlot.FEET, new Item.Properties().tab(ChangedTabs.TAB_CHANGED_COMBAT).fireResistant()));

    // Unsure whether to keep this record, it doesn't fit with the BGM from changed
    public static final RegistryObject<RecordItem> OWO_RECORD = register("owo_record", () -> new RecordItem(8, ChangedSounds.OWO, (new Item.Properties()).stacksTo(1).tab(ChangedTabs.TAB_CHANGED_MUSIC).rarity(Rarity.RARE)));
    public static final RegistryObject<LoopedRecordItem> BLACK_GOO_ZONE_RECORD = registerLoopedRecord("black_goo_zone_record", () -> ChangedSounds.MUSIC_BLACK_GOO_ZONE);
    public static final RegistryObject<LoopedRecordItem> CRYSTAL_ZONE_RECORD = registerLoopedRecord("crystal_zone_record", () -> ChangedSounds.MUSIC_CRYSTAL_ZONE);
    public static final RegistryObject<LoopedRecordItem> GAS_ROOM_RECORD = registerLoopedRecord("gas_room_record", () -> ChangedSounds.MUSIC_GAS_ROOM);
    public static final RegistryObject<LoopedRecordItem> LABORATORY_RECORD = registerLoopedRecord("laboratory_record", () -> ChangedSounds.MUSIC_LABORATORY);
    public static final RegistryObject<LoopedRecordItem> OUTSIDE_THE_TOWER_RECORD = registerLoopedRecord("outside_the_tower_record", () -> ChangedSounds.MUSIC_OUTSIDE_THE_TOWER);
    public static final RegistryObject<LoopedRecordItem> PURO_THE_BLACK_GOO_RECORD = registerLoopedRecord("puro_the_black_goo_record", () -> ChangedSounds.MUSIC_PURO_THE_BLACK_GOO);
    public static final RegistryObject<LoopedRecordItem> PUROS_HOME_RECORD = registerLoopedRecord("puros_home_record", () -> ChangedSounds.MUSIC_PUROS_HOME);
    public static final RegistryObject<LoopedRecordItem> THE_LIBRARY_RECORD = registerLoopedRecord("the_library_record", () -> ChangedSounds.MUSIC_THE_LIBRARY);
    public static final RegistryObject<LoopedRecordItem> THE_LION_CHASE_RECORD = registerLoopedRecord("the_lion_chase_record", () -> ChangedSounds.MUSIC_THE_LION_CHASE);
    public static final RegistryObject<LoopedRecordItem> THE_SCARLET_CRYSTAL_MINE_RECORD = registerLoopedRecord("the_scarlet_crystal_mine_record", () -> ChangedSounds.MUSIC_THE_SCARLET_CRYSTAL_MINE);
    public static final RegistryObject<LoopedRecordItem> THE_SHARK_RECORD = registerLoopedRecord("the_shark_record", () -> ChangedSounds.MUSIC_THE_SHARK);
    public static final RegistryObject<LoopedRecordItem> THE_SQUID_DOG_RECORD = registerLoopedRecord("the_squid_dog_record", () -> ChangedSounds.MUSIC_THE_SQUID_DOG);
    public static final RegistryObject<LoopedRecordItem> THE_WHITE_GOO_JUNGLE_RECORD = registerLoopedRecord("the_white_goo_jungle_record", () -> ChangedSounds.MUSIC_THE_WHITE_GOO_JUNGLE);
    public static final RegistryObject<LoopedRecordItem> THE_WHITE_TAIL_CHASE_PART_1 = registerLoopedRecord("the_white_tail_chase_part_1_record", () -> ChangedSounds.MUSIC_THE_WHITE_TAIL_CHASE_PART_1);
    public static final RegistryObject<LoopedRecordItem> THE_WHITE_TAIL_CHASE_PART_2 = registerLoopedRecord("the_white_tail_chase_part_2_record", () -> ChangedSounds.MUSIC_THE_WHITE_TAIL_CHASE_PART_2);
    public static final RegistryObject<LoopedRecordItem> VENT_PIPE_RECORD = registerLoopedRecord("vent_pipe_record", () -> ChangedSounds.MUSIC_VENT_PIPE);

    private static RegistryObject<RecordItem> registerRecord(String name, Supplier<SoundEvent> soundEventSupplier) {
        return register(name, () -> new RecordItem(8, soundEventSupplier, (new Item.Properties()).stacksTo(1).tab(ChangedTabs.TAB_CHANGED_MUSIC).rarity(Rarity.RARE)));
    }

    private static RegistryObject<LoopedRecordItem> registerLoopedRecord(String name, Supplier<SoundEvent> soundEventSupplier) {
        return register(name, () -> new LoopedRecordItem(8, soundEventSupplier, (new Item.Properties()).stacksTo(1).tab(ChangedTabs.TAB_CHANGED_MUSIC).rarity(Rarity.RARE)));
    }

    static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return REGISTRY.register(name, item);
    }

    public static BlockItem getBlockItem(Block block) {
        if (Registry.ITEM.get(block.getRegistryName()) instanceof BlockItem blockItem)
            return blockItem;
        return null;
    }
}
