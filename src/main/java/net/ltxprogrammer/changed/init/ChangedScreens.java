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
            MenuScreens.register(ChangedMenus.EXTRA_HANDS, ExtraHandsScreen::new);
            MenuScreens.register(ChangedMenus.CENTAUR_SADDLE, CentaurSaddleScreen::new);
            MenuScreens.register(ChangedMenus.INFUSER, InfuserScreen::new);
            MenuScreens.register(ChangedMenus.PURIFIER, PurifierScreen::new);
        });
    }
}
