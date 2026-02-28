package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.world.inventory.*;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ChangedMenus {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Changed.MODID);

    public static final RegistryObject<MenuType<AbilityRadialMenu>> ABILITY_RADIAL = register("ability_radial", AbilityRadialMenu::new);
    public static final RegistryObject<MenuType<HairStyleRadialMenu>> HAIRSTYLE_RADIAL = register("hairstyle_radial", HairStyleRadialMenu::new);
    public static final RegistryObject<MenuType<SpecialStateRadialMenu>> SPECIAL_RADIAL = register("special_radial", SpecialStateRadialMenu::new);
    public static final RegistryObject<MenuType<ComputerMenu>> COMPUTER = register("computer", ComputerMenu::new);
    public static final RegistryObject<MenuType<InfuserMenu>> INFUSER = register("infuser", InfuserMenu::new);
    public static final RegistryObject<MenuType<PurifierMenu>> PURIFIER = register("purifier", PurifierMenu::new);
    public static final RegistryObject<MenuType<KeypadMenu>> KEYPAD = register("keypad", KeypadMenu::new);
    public static final RegistryObject<MenuType<ClipboardMenu>> CLIPBOARD = register("clipboard", ClipboardMenu::new);
    public static final RegistryObject<MenuType<NoteMenu>> NOTE = register("note", NoteMenu::new);
    public static final RegistryObject<MenuType<StasisChamberMenu>> STASIS_CHAMBER = register("stasis_chamber", StasisChamberMenu::new);
    public static final RegistryObject<MenuType<AccessoryAccessMenu>> ACCESSORY_ACCESS = register("accessory_access", AccessoryAccessMenu::new);
    public static final RegistryObject<MenuType<TamedDarkLatexMenu>> TAMED_DARK_LATEX = register("tamed_dark_latex", TamedDarkLatexMenu::new);
    public static final RegistryObject<MenuType<TamedDarkLatexInventoryMenu>> TAMED_DARK_LATEX_INVENTORY = register("tamed_dark_latex_inventory", TamedDarkLatexInventoryMenu::new);
    public static final RegistryObject<MenuType<AbilityTreeMenu>> ABILITY_TREE = register("ability_tree", AbilityTreeMenu::new);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, IContainerFactory<T> containerFactory) {
        return REGISTRY.register(name, () -> new MenuType<>(containerFactory, FeatureFlagSet.of()));
    }

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, IContainerFactory<T> containerFactory, FeatureFlagSet requiredFlags) {
        return REGISTRY.register(name, () -> new MenuType<>(containerFactory, requiredFlags));
    }
}
