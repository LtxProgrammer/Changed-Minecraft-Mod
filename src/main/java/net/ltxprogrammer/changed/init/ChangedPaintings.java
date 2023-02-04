package net.ltxprogrammer.changed.init;

import net.minecraft.world.entity.decoration.Motive;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedPaintings {
    @SubscribeEvent
    public static void registerMotives(RegistryEvent.Register<Motive> event) {
        event.getRegistry().register(new Motive(16, 16).setRegistryName("dark_latex_mask"));
        event.getRegistry().register(new Motive(32, 32).setRegistryName("puro_place"));
        event.getRegistry().register(new Motive(64, 64).setRegistryName("sharks_gaze"));
        event.getRegistry().register(new Motive(32, 32).setRegistryName("dr_k"));
        event.getRegistry().register(new Motive(32, 32).setRegistryName("a_lazy_fox_on_the_paper"));
        event.getRegistry().register(new Motive(64, 32).setRegistryName("creation_of_lin"));
    }
}
