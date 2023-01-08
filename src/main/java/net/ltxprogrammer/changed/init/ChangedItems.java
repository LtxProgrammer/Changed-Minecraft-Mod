package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.item.*;
import net.minecraft.resources.ResourceLocation;
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
    public static final RegistryObject<Item> DARK_LATEX_CRYSTAL_FRAGMENT = register("dark_latex_crystal_fragment", AbstractLatexItem::new);
    public static final RegistryObject<Item> DARK_LATEX_GOO = register("dark_latex_goo", AbstractLatexGoo::new);
    public static final RegistryObject<Item> DARK_LATEX_MASK = register("dark_latex_mask", DarkLatexMask::new);
    public static final RegistryObject<Item> DARK_LATEX_BUCKET = register("dark_latex_bucket", AbstractLatexBucket.from(ChangedFluids.DARK_LATEX));
    public static final RegistryObject<Item> FLOPPY_DISK = register("floppy_disk", FloppyDisk::new);
    public static final RegistryObject<Item> LATEX_BASE = register("latex_base", () -> new Item(new Item.Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS)));
    public static final RegistryObject<Item> LATEX_DANCE_RECORD = register("latex_dance_record", LatexDanceRecord::new);
    public static final RegistryObject<Item> OWO_RECORD = register("owo_record", OwoRecord::new);
    public static final RegistryObject<Item> ORANGE = register("orange", () -> new Item((new Item.Properties()).tab(ChangedTabs.TAB_CHANGED_ITEMS).food(ChangedFoods.ORANGE)));
    public static final RegistryObject<Item> SYRINGE = register("syringe", () -> new Syringe(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> BLOOD_SYRINGE = register("blood_syringe", () -> new Syringe.BloodSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LATEX_BEIFENG_CRYSTAL_FRAGMENT = register("latex_beifeng_crystal_fragment", AbstractLatexItem::new);
    public static final RegistryObject<Item> LATEX_SYRINGE = register("latex_syringe", () -> new Syringe.LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> LATEX_TIPPED_ARROW = register("latex_tipped_arrow", LatexTippedArrowItem::new);
    public static final RegistryObject<Item> LATEX_WOLF_CRYSTAL_FRAGMENT = register("latex_wolf_crystal_fragment", AbstractLatexItem::new);
    public static final RegistryObject<Item> DARK_LATEX_DRAGON_CRYSTAL_FRAGMENT = register("dark_latex_dragon_crystal_fragment", AbstractLatexItem::new);
    public static final RegistryObject<Item> WHITE_LATEX_GOO = register("white_latex_goo", AbstractLatexGoo::new);
    public static final RegistryObject<Item> WHITE_LATEX_BUCKET = register("white_latex_bucket", AbstractLatexBucket.from(ChangedFluids.WHITE_LATEX));

    static RegistryObject<Item> register(String name, Supplier<Item> item) {
        return REGISTRY.register(name, item);
    }

    public static Item getByRegistry(ResourceLocation location) {
        for (RegistryObject<Item> registeredItem : REGISTRY.getEntries()) {
            if (registeredItem.get().getRegistryName() == location) {
                return registeredItem.get();
            }
        }

        return null;
    }

    public static Item getBlockItem(Block block) {
        return getByRegistry(block.getRegistryName());
    }
}
