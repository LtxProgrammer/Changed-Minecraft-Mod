package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.client.gui.*;
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
            MenuScreens.register(ChangedMenus.COMPUTER, ComputerScreen::new);
            MenuScreens.register(ChangedMenus.TAUR_SADDLE, TaurSaddleScreen::new);
            MenuScreens.register(ChangedMenus.INFUSER, InfuserScreen::new);
            MenuScreens.register(ChangedMenus.PURIFIER, PurifierScreen::new);
            MenuScreens.register(ChangedMenus.ABILITY_RADIAL, AbilityRadialScreen::new);
            MenuScreens.register(ChangedMenus.HAIRSTYLE_RADIAL, HairStyleRadialScreen::new);
            MenuScreens.register(ChangedMenus.SPECIAL_RADIAL, SpecialStateRadialScreen::new);
            MenuScreens.register(ChangedMenus.KEYPAD, KeypadScreen::new);
            MenuScreens.register(ChangedMenus.CLIPBOARD, ClipboardScreen::new);
            MenuScreens.register(ChangedMenus.NOTE, NoteScreen::new);
        });
    }
}
