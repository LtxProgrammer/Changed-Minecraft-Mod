package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.effect.Hypercoagulation;
import net.ltxprogrammer.changed.effect.Shock;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedEffects {
    private static final List<MobEffect> REGISTRY = new ArrayList<MobEffect>();
    public static final MobEffect HYPERCOAGULATION = register(new Hypercoagulation());
    public static final MobEffect SHOCK = register(new Shock());

    private static MobEffect register(MobEffect effect) {
        REGISTRY.add(effect);
        return effect;
    }

    @SubscribeEvent
    public static void registerParticleTypes(RegistryEvent.Register<MobEffect> event) {
        event.getRegistry().registerAll(REGISTRY.toArray(new MobEffect[0]));
    }
}
