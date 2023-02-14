package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.item.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, Changed.MODID);
    public static final RegistryObject<Item> AEROSOL_LATEX_BUCKET = register("aerosol_latex_bucket", AbstractLatexBucket.from(ChangedFluids.AEROSOL_LATEX));
    public static final RegistryObject<Benign.Pants> BENIGN_PANTS = register("benign_pants", Benign.Pants::new);
    public static final RegistryObject<PinkLatex.Pants> PINK_PANTS = register("pink_pants", PinkLatex.Pants::new);
    public static final RegistryObject<AbstractLatexItem> DARK_LATEX_CRYSTAL_FRAGMENT = register("dark_latex_crystal_fragment", AbstractLatexItem::new);
    public static final RegistryObject<AbstractLatexGoo> DARK_LATEX_GOO = register("dark_latex_goo", AbstractLatexGoo::new);
    public static final RegistryObject<DarkLatexMask> DARK_LATEX_MASK = register("dark_latex_mask", DarkLatexMask::new);
    public static final RegistryObject<Item> DARK_LATEX_BUCKET = register("dark_latex_bucket", AbstractLatexBucket.from(ChangedFluids.DARK_LATEX));
    public static final RegistryObject<CompactDisc> COMPACT_DISC = register("compact_disc", CompactDisc::new);
    public static final RegistryObject<Item> LATEX_BASE = register("latex_base", () -> new Item(new Item.Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS)));
    public static final RegistryObject<LatexDanceRecord> LATEX_DANCE_RECORD = register("latex_dance_record", LatexDanceRecord::new);
    public static final RegistryObject<OwoRecord> OWO_RECORD = register("owo_record", OwoRecord::new);
    public static final RegistryObject<Item> ORANGE = register("orange", () -> new Item((new Item.Properties()).tab(ChangedTabs.TAB_CHANGED_ITEMS).food(ChangedFoods.ORANGE)));
    public static final RegistryObject<Syringe> SYRINGE = register("syringe", () -> new Syringe(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<BloodSyringe> BLOOD_SYRINGE = register("blood_syringe", () -> new BloodSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<AbstractLatexItem> LATEX_BEIFENG_CRYSTAL_FRAGMENT = register("latex_beifeng_crystal_fragment", AbstractLatexItem::new);
    public static final RegistryObject<LatexInkballItem> LATEX_INKBALL = register("latex_inkball", LatexInkballItem::new);
    public static final RegistryObject<LatexSyringe> LATEX_SYRINGE = register("latex_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexTippedArrowItem> LATEX_TIPPED_ARROW = register("latex_tipped_arrow", LatexTippedArrowItem::new);
    public static final RegistryObject<AbstractLatexItem> LATEX_WOLF_CRYSTAL_FRAGMENT = register("latex_wolf_crystal_fragment", AbstractLatexItem::new);
    public static final RegistryObject<AbstractLatexItem> DARK_LATEX_DRAGON_CRYSTAL_FRAGMENT = register("dark_latex_dragon_crystal_fragment", AbstractLatexItem::new);
    public static final RegistryObject<AbstractLatexGoo> WHITE_LATEX_GOO = register("white_latex_goo", AbstractLatexGoo::new);
    public static final RegistryObject<Item> WHITE_LATEX_BUCKET = register("white_latex_bucket", AbstractLatexBucket.from(ChangedFluids.WHITE_LATEX));

    public static final RegistryObject<TscStaff> TSC_STAFF = register("tsc_staff", TscStaff::new);
    public static final RegistryObject<TscBaton> TSC_BATON = register("tsc_baton", TscBaton::new);

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
    public static final RegistryObject<AbdomenArmor> GOLD_UPPER_ABDOMEN_ARMOR = register("gold_upper_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.GOLD, EquipmentSlot.LEGS));
    public static final RegistryObject<AbdomenArmor> GOLD_LOWER_ABDOMEN_ARMOR = register("gold_lower_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.GOLD, EquipmentSlot.FEET));
    public static final RegistryObject<AbdomenArmor> DIAMOND_UPPER_ABDOMEN_ARMOR = register("diamond_upper_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.DIAMOND, EquipmentSlot.LEGS));
    public static final RegistryObject<AbdomenArmor> DIAMOND_LOWER_ABDOMEN_ARMOR = register("diamond_lower_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.DIAMOND, EquipmentSlot.FEET));
    public static final RegistryObject<AbdomenArmor> NETHERITE_UPPER_ABDOMEN_ARMOR = register("netherite_upper_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.NETHERITE, EquipmentSlot.LEGS));
    public static final RegistryObject<AbdomenArmor> NETHERITE_LOWER_ABDOMEN_ARMOR = register("netherite_lower_abdomen_armor",
            () -> new AbdomenArmor(ArmorMaterials.NETHERITE, EquipmentSlot.FEET));

    static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return REGISTRY.register(name, item);
    }

    public static Item getByRegistry(ResourceLocation location) {
        for (RegistryObject<Item> registeredItem : REGISTRY.getEntries()) {
            if (registeredItem.get().getRegistryName().equals(location)) {
                return registeredItem.get();
            }
        }

        return null;
    }

    public static Item getBlockItem(Block block) {
        return getByRegistry(block.getRegistryName());
    }
}
