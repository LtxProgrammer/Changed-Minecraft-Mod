package net.ltxprogrammer.changed;

import net.ltxprogrammer.changed.client.EventHandlerClient;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.network.packet.*;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod(Changed.MODID)
public class Changed {
    private static Changed instance;
    public static Changed getInstance() { return instance; }

    public static final String MODID = "changed";

    public static final Logger LOGGER = LogManager.getLogger(Changed.class);
    public static EventHandlerClient eventHandlerClient;

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(modResource("transfur_channel"), () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int messageID = 0;

    public Changed() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.register(eventHandlerClient = new EventHandlerClient()));

        try {
            PatreonBenefits.loadBenefits();
            PatreonBenefits.UPDATE_CHECKER.start();
        } catch (Exception ex) {
            Changed.LOGGER.error("Failed to load Patreon Benefits. Patrons will not receive benefits visible to this client.");
            ex.printStackTrace();
        }

        // Initialize packet types

        addNetworkMessage(CheckForUpdatesPacket.class, CheckForUpdatesPacket::new);
        addNetworkMessage(MountLatexPacket.class, MountLatexPacket::new);
        addNetworkMessage(SyncSwitchPacket.class, SyncSwitchPacket::new);
        addNetworkMessage(SyncTransfurPacket.class, SyncTransfurPacket::new);
        addNetworkMessage(SyncTransfurProgressPacket.class, SyncTransfurProgressPacket::new);

        instance = this;

        // Initialize (Note ModEventBus is a stack; first in - last out)

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //    vvv Last to process vvv
        ChangedRecipeSerializers.REGISTRY.register(modEventBus);
        ChangedFeatures.REGISTRY.register(modEventBus);
        ChangedBiomes.REGISTRY.register(modEventBus);
        ChangedBlockEntities.REGISTRY.register(modEventBus);
        ChangedFluids.REGISTRY.register(modEventBus);
        ChangedItems.REGISTRY.register(modEventBus);
        ChangedBlocks.REGISTRY.register(modEventBus);
        ChangedEntities.REGISTRY.register(modEventBus);
        //    ^^^ First to process ^^^
    }

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder,
                                             BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID++, messageType, encoder, decoder, messageConsumer);
    }

    public static <T extends ChangedPacket> void addNetworkMessage(Class<T> messageType, Function<FriendlyByteBuf, T> ctor) {
        addNetworkMessage(messageType, T::write, ctor, T::handle);
    }

    public static MinecraftServer currentServer = null;
    public static void chatLogAll(String message) {
        if (currentServer != null) {
            currentServer.getPlayerList().broadcastMessage(new TextComponent(message), ChatType.SYSTEM, Util.NIL_UUID);
        }
    }

    public static void chatLogLocal(String message) {
        Minecraft.getInstance().player.sendMessage(new TextComponent(message).withStyle(ChatFormatting.WHITE), Util.NIL_UUID);
    }

    public static void chatLogLocalError(String message) {
        Minecraft.getInstance().player.sendMessage(new TextComponent(message).withStyle(ChatFormatting.DARK_RED), Util.NIL_UUID);
    }

    public static ResourceLocation modResource(String path) {
        return new ResourceLocation(MODID, path);
    }
    public static String modResourceStr(String path) {
        return MODID + ":" + path;
    }
}
