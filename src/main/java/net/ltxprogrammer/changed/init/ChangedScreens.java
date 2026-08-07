package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.client.gui.*;
import net.ltxprogrammer.changed.client.gui.computer.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedScreens {
    @SubscribeEvent
    public static void clientLoad(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ChangedMenus.COMPUTER.get(), ComputerScreen::new);
            MenuScreens.register(ChangedMenus.INFUSER.get(), InfuserScreen::new);
            MenuScreens.register(ChangedMenus.PURIFIER.get(), PurifierScreen::new);
            MenuScreens.register(ChangedMenus.ABILITY_RADIAL.get(), AbilityRadialScreen::new);
            MenuScreens.register(ChangedMenus.KEYPAD.get(), KeypadScreen::new);
            MenuScreens.register(ChangedMenus.CLIPBOARD.get(), ClipboardScreen::new);
            MenuScreens.register(ChangedMenus.NOTE.get(), NoteScreen::new);
            MenuScreens.register(ChangedMenus.STASIS_CHAMBER.get(), StasisChamberScreen::new);
            MenuScreens.register(ChangedMenus.ACCESSORY_ACCESS.get(), AccessoryAccessScreen::new);
            MenuScreens.register(ChangedMenus.TAMED_DARK_LATEX.get(), TamedDarkLatexScreen::new);
            MenuScreens.register(ChangedMenus.TAMED_DARK_LATEX_INVENTORY.get(), TamedDarkLatexInventoryScreen::new);
            MenuScreens.register(ChangedMenus.ABILITY_TREE.get(), AbilityTreeScreen::new);
        });
        event.enqueueWork(() -> {
            ApplicationScreens.register(ChangedApplications.DESKTOP.get(), DesktopScreen::new);
            ApplicationScreens.register(ChangedApplications.FILE_EXPLORER.get(), FileExplorerScreen::new);
            ApplicationScreens.register(ChangedApplications.DOOR_CONTROLLER.get(), DoorControllerScreen::new);
            ApplicationScreens.register(ChangedApplications.TEXT_EDITOR.get(), TextEditorScreen::new);
        });
    }
}
