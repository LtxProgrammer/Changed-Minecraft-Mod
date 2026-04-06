package net.ltxprogrammer.changed;

import net.ltxprogrammer.changed.ability.tree.AbilityTrees;
import net.ltxprogrammer.changed.client.*;
import net.ltxprogrammer.changed.client.latexparticles.LatexParticleType;
import net.ltxprogrammer.changed.data.BuiltinRepositorySource;
import net.ltxprogrammer.changed.entity.AccessoryEntities;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.PlayerMover;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.network.ChangedPackets;
import net.ltxprogrammer.changed.network.packet.ChangedPacket;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.ltxprogrammer.changed.world.ChangedDataFixer;
import net.ltxprogrammer.changed.world.features.structures.FacilityPieces;
import net.ltxprogrammer.changed.world.features.structures.facility.FacilityZoneEntities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventBusInvokeDispatcher;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod(Changed.MODID)
public class Changed {
    private static Changed instance;
    public static Changed getInstance() { return instance; }

    public static final String MODID = "changed";

    public static final Logger LOGGER = LogManager.getLogger(Changed.class);
    public static EventHandlerClient eventHandlerClient;
    public static ChangedConfig config;
    public static ChangedDataFixer dataFixer;
    //private static IEventBus modEventBus;

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(modResource("network"), () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static final ChangedPackets PACKETS = new ChangedPackets(PACKET_HANDLER);

    /**
     * This function is split out of the main function as a request by mod extension devs
     */
    private void registerLoadingEventListeners(IEventBus eventBus) {
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::customPacks);

        eventBus.addListener(ChangedAttributes::modifyEntityAttributes);
    }

    public Changed(FMLJavaModLoadingContext context) {
        //modEventBus = context.getModEventBus();
        config = new ChangedConfig(context);

        registerLoadingEventListeners(context.getModEventBus());

        addEventListener(this::dataListeners);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> registerClientEventListeners(context.getModEventBus()));

        PACKETS.registerPackets();

        instance = this;

        final IEventBus modEventBus = context.getModEventBus();

        HairStyle.REGISTRY.register(modEventBus);
        ChangedAbilities.REGISTRY.register(modEventBus);
        PlayerMover.REGISTRY.register(modEventBus);
        LatexParticleType.REGISTRY.register(modEventBus);
        ChangedLatexTypes.REGISTRY.register(modEventBus);

        ChangedAttributes.REGISTRY.register(modEventBus);
        ChangedEnchantments.REGISTRY.register(modEventBus);
        ChangedRecipeSerializers.REGISTRY.register(modEventBus);
        ChangedStructureTypes.REGISTRY.register(modEventBus);
        ChangedStructurePieceTypes.REGISTRY.register(modEventBus);
        ChangedLootItemFunctions.REGISTRY.register(modEventBus);
        ChangedRecipeTypes.REGISTRY.register(modEventBus);
        ChangedTabs.REGISTRY.register(modEventBus);
        ChangedSounds.REGISTRY.register(modEventBus);
        ChangedPaintings.REGISTRY.register(modEventBus);
        ChangedParticles.REGISTRY.register(modEventBus);
        ChangedFeatures.REGISTRY_FEATURE.register(modEventBus);
        ChangedFeatures.REGISTRY_PROCESSOR.register(modEventBus);
        ChangedMenus.REGISTRY.register(modEventBus);
        ChangedEffects.REGISTRY.register(modEventBus);
        ChangedBlockEntities.REGISTRY.register(modEventBus);
        ChangedFluids.REGISTRY_TYPES.register(modEventBus);
        ChangedFluids.REGISTRY_FLUIDS.register(modEventBus);
        ChangedItems.REGISTRY.register(modEventBus);
        ChangedBlockStateProviders.REGISTRY.register(modEventBus);
        ChangedBlocks.REGISTRY.register(modEventBus);
        ChangedTransfurVariants.REGISTRY.register(modEventBus);
        ChangedEntities.REGISTRY.register(modEventBus);
        ChangedAnimationEvents.REGISTRY.register(modEventBus);
        ChangedAccessorySlots.REGISTRY.register(modEventBus);
        ChangedWallSigns.REGISTRY.register(modEventBus);
        ChangedFacilityPieceTypes.REGISTRY.register(modEventBus);
        ChangedFacilityZones.REGISTRY.register(modEventBus);
        ChangedAbilityTreeCodecs.NODE_EFFECT_REGISTRY.register(modEventBus);
        ChangedAbilityTreeCodecs.EFFECT_CONDITION_REGISTRY.register(modEventBus);
        ChangedTransfurVariantFeatures.REGISTRY.register(modEventBus);

        // Our DFU references the above registries, so they need to be initialized before the DFU is created
        dataFixer = new ChangedDataFixer();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (!config.common.downloadPatreonContent.get()) {
                Changed.LOGGER.info("Patreon benefits is disabled on this client. Patrons will not receive benefits visible to this client.");
                return;
            }

            try {
                PatreonBenefits.loadBenefits();
            } catch (Exception ex) {
                Changed.LOGGER.error("Failed to load Patreon Benefits. Patrons will not receive benefits visible to this client.", ex);
            }
        });
        event.enqueueWork(() -> {
            ComposterBlock.COMPOSTABLES.put(ChangedBlocks.ORANGE_TREE_LEAVES.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(ChangedBlocks.ORANGE_TREE_SAPLING.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(ChangedItems.ORANGE.get(), 0.65F);
        });
    }

    private void registerClientEventListeners(IEventBus eventBus) {
        MinecraftForge.EVENT_BUS.register(eventHandlerClient = new EventHandlerClient());
        eventBus.addListener(RecipeCategories::registerCategories);
        eventBus.addListener(ChangedOverlays::registerOverlays);
        eventBus.addListener(ChangedClient::onBlockColorsInit);
        eventBus.addListener(ChangedClient::onItemColorsInit);
        eventBus.addListener(ChangedClient::onClientFinishSetup);
        eventBus.addListener(AbilityRenderer::onRegisterModels);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ChangedClient.registerEventListeners();
    }

    private void dataListeners(final AddReloadListenerEvent event) {
        event.addListener(ChangedFusions.INSTANCE);
        event.addListener(AccessoryEntities.INSTANCE);
        event.addListener(FacilityPieces.INSTANCE);
        event.addListener(FacilityZoneEntities.INSTANCE);
        event.addListener(AbilityTrees.INSTANCE);
        ChangedCompatibility.addDataListeners(event);
    }

    private void customPacks(final AddPackFindersEvent event) {
        try {
            switch (event.getPackType()) {
                case CLIENT_RESOURCES, SERVER_DATA ->
                        event.addRepositorySource(new BuiltinRepositorySource(event.getPackType(), MODID));
                default -> {}
            }
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    public static ResourceLocation modResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
    public static String modResourceStr(String path) {
        return MODID + ":" + path;
    }

    public static <T extends Event & IModBusEvent> void postModLoadingEvent(T event) {
        ModLoader.get().postEvent(event);
    }

    public static <T extends Event> void addEventListener(Consumer<T> listener) {
        MinecraftForge.EVENT_BUS.addListener(listener);
    }

    public static <T extends Event> boolean postModEvent(T event) {
        return MinecraftForge.EVENT_BUS.post(event);
    }

    public static <T extends Event> boolean postModEvent(T event, IEventBusInvokeDispatcher dispatcher) {
        return MinecraftForge.EVENT_BUS.post(event, dispatcher);
    }
}
