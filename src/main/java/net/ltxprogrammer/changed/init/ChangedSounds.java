package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedSounds {
    public static Map<ResourceLocation, SoundEvent> REGISTRY = new HashMap<>();
    private static IForgeRegistry<SoundEvent> MINECRAFT_REGISTRY;

    public static final SoundEvent BLOW1 = register("blow1");
    public static final SoundEvent BOW2 = register("bow2");
    public static final SoundEvent CLOSE1 = register("close1");
    public static final SoundEvent BUZZER1 = register("buzzer1");
    public static final SoundEvent CHIME2 = register("chime2");
    public static final SoundEvent CLOSE3 = register("close3");
    public static final SoundEvent EQUIP1 = register("equip1");
    public static final SoundEvent EQUIP2 = register("equip2");
    public static final SoundEvent EQUIP3 = register("equip3");
    public static final SoundEvent EVASION = register("evasion");
    public static final SoundEvent ICE2 = register("ice2");
    public static final SoundEvent KEY = register("key");
    public static final SoundEvent MONSTER2 = register("monster2");
    public static final SoundEvent OPEN1 = register("open1");
    public static final SoundEvent OPEN2 = register("open2");
    public static final SoundEvent OPEN3 = register("open3");
    public static final SoundEvent PARALYZE1 = register("paralyze1");
    public static final SoundEvent PARALYZE3 = register("paralyze3");
    public static final SoundEvent POISON = register("poison");
    public static final SoundEvent SAVE = register("save");
    public static final SoundEvent SOUND3 = register("sound3");
    public static final SoundEvent SHOT1 = register("shot1");
    public static final SoundEvent SIREN = register("siren");
    public static final SoundEvent SWITCH1 = register("switch1");
    public static final SoundEvent SWITCH2 = register("switch2");
    public static final SoundEvent SWORD1 = register("sword1");

    public static final SoundEvent LATEX_DANCE = register("music_disc.latex_dance");
    public static final SoundEvent OWO = register("music_disc.owo");

    public static class Types {
        // Represents a sound type that has no sound
        public static final SoundType NONE = new SoundType(-100, 1, SoundEvents.METAL_BREAK, SoundEvents.METAL_STEP, SoundEvents.METAL_PLACE, SoundEvents.METAL_HIT, SoundEvents.METAL_FALL);
    }

    public static SoundEvent register(String name) {
        ResourceLocation location = Changed.modResource(name);
        SoundEvent soundEvent = new SoundEvent(location);
        REGISTRY.put(location, soundEvent);
        return soundEvent;
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        for (Map.Entry<ResourceLocation, SoundEvent> sound : REGISTRY.entrySet())
            event.getRegistry().register(sound.getValue().setRegistryName(sound.getKey()));
        MINECRAFT_REGISTRY = event.getRegistry();
    }

    public static void broadcastSound(MinecraftServer server, SoundEvent event, BlockPos blockPos, float volume, float pitch) {
        server.getPlayerList().broadcastAll(new ClientboundSoundPacket(event, SoundSource.BLOCKS, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, volume, pitch));
    }

    public static void broadcastSound(MinecraftServer server, SoundEvent event, SoundSource source, double x, double y, double z, float volume, float pitch) {
        server.getPlayerList().broadcastAll(new ClientboundSoundPacket(event, source, x, y, z, volume, pitch));
    }

    public static void broadcastSound(Entity entity, SoundEvent event, float volume, float pitch) {
        if (!entity.level.isClientSide)
            broadcastSound(entity.level.getServer(), event, SoundSource.NEUTRAL, entity.getX(), entity.getY(), entity.getZ(), volume, pitch);
    }

    public static void broadcastSound(MinecraftServer server, ResourceLocation name, SoundSource source, double x, double y, double z, float volume, float pitch) {
        var event = MINECRAFT_REGISTRY.getValue(name);
        if (event != null)
            server.getPlayerList().broadcastAll(new ClientboundSoundPacket(event, source, x, y, z, volume, pitch));
        else
            Changed.LOGGER.warn("Cannot play sound event " + name);
    }

    public static void broadcastSound(Entity entity, ResourceLocation event, float volume, float pitch) {
        if (!entity.level.isClientSide)
            broadcastSound(entity.level.getServer(), event, SoundSource.NEUTRAL, entity.getX(), entity.getY(), entity.getZ(), volume, pitch);
    }
}
