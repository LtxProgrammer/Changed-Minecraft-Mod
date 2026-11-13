package net.ltxprogrammer.changed.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModelLayerLocation;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModels;
import net.ltxprogrammer.changed.data.DeferredModelLayerLocation;
import net.ltxprogrammer.changed.data.DelayLoadedModel;
import net.ltxprogrammer.changed.data.OnlineResource;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PatreonBenefits {
    private static final int COMPATIBLE_VERSION = 3;

    public enum Tier {
        NONE(-1),
        LEVEL0(0),
        LEVEL1(1),
        LEVEL2(2),
        LEVEL3(3),
        LEVEL4(4);

        final int value;

        Tier(int value) {
            this.value = value;
        }

        public static Tier ofValue(int v) {
            for (Tier l : Tier.values())
                if (l.value == v)
                    return l;
            return null;
        }
    }

    public record AnimationData(
            boolean hasTail,
            boolean swimTail,
            boolean hasWings,
            boolean hasWingsV2) {
        public static final AnimationData DEFAULT = new AnimationData(true, false, false, false);

        public static AnimationData fromJSON(JsonElement object) {
            if (object == null || object.isJsonNull() || !object.isJsonObject()) return DEFAULT;
            else {
                JsonObject json = (JsonObject)object;
                return new AnimationData(
                        GsonHelper.getAsBoolean(json, "hasTail", GsonHelper.getAsBoolean(json, "hastail", true)),
                        GsonHelper.getAsBoolean(json, "swimTail", GsonHelper.getAsBoolean(json, "swimtail", false)),
                        GsonHelper.getAsBoolean(json, "hasWings", GsonHelper.getAsBoolean(json, "haswings", false)),
                        GsonHelper.getAsBoolean(json, "hasWingsV2", GsonHelper.getAsBoolean(json, "haswingsv2", false)));
            }
        }
    }

    public record EntityData(
            Color3 primaryColor,
            Color3 secondaryColor,
            List<Color3> dripColors,
            List<Color3> hairColors,
            List<HairStyle> hairStyles,
            EntityDimensions dimensions,
            boolean organic
    ) {
        public static EntityData fromJSON(UUID uuid, JsonObject object) {
            List<Color3> dripColors = new ArrayList<>();
            try {
                object.get("particles").getAsJsonArray().forEach(color -> dripColors.add(Color3.getColor(color.getAsString())));
            } catch (Exception ignored) {}

            List<Color3> hairColors = new ArrayList<>();
            try {
                object.get("hairColor").getAsJsonArray().forEach(color -> hairColors.add(Color3.getColor(color.getAsString())));
            } catch (Exception ignored) {}
            if (hairColors.isEmpty())
                hairColors.add(Color3.WHITE);

            List<HairStyle> styles = new ArrayList<>();
            if (object.has("hairStyles")) {
                if (object.get("hairStyles").isJsonArray()) object.get("hairColor").getAsJsonArray().forEach(style -> {
                    try {
                        styles.add(ChangedRegistry.HAIR_STYLE.get().getValue(ResourceLocation.tryParse(style.getAsString())));
                    } catch (Exception ex) {
                        LOGGER.warn("Bad hairStyle {}", style);
                    }
                });

                else {
                    try {
                        styles.addAll(Objects.requireNonNull(HairStyle.Collection.byName(object.get("hairStyles").getAsString())).getStyles());
                    } catch (Exception ex) {
                        LOGGER.warn("Bad type {}", object.get("hairStyles"));
                    }
                }
            }

            if (styles.isEmpty())
                styles.add(HairStyle.BALD.get());

            return new EntityData(
                    Color3.getColor(GsonHelper.getAsString(object, "primaryColor", "WHITE")),
                    Color3.getColor(GsonHelper.getAsString(object, "secondaryColor", "WHITE")),
                    dripColors,
                    hairColors,
                    styles,
                    EntityDimensions.scalable(object.get("width").getAsFloat(), object.get("height").getAsFloat()),
                    GsonHelper.getAsBoolean(object, "organic", false)
            );
        }
    }

    // Client only info
    public record ModelData(
            DeferredModelLayerLocation modelLayerLocation,
            Map<ArmorModel, DeferredModelLayerLocation> armorModelLayerLocation,

            ResourceLocation texture,
            Optional<ResourceLocation> emissive,
            AnimationData animationData,

            boolean oldModelRig,
            Cacheable<DelayLoadedModel> model,
            Cacheable<DelayLoadedModel> armorModelInner,
            Cacheable<DelayLoadedModel> armorModelOuter,

            float shadowSize,
            float hipOffset,
            float torsoWidth,
            float forwardOffset,
            float torsoLength,
            float armLength,
            float legLength
    ) {
        public static ModelData fromJSON(Function<String, JsonObject> jsonGetter, String fullId, JsonObject object) {
            ResourceLocation textureLocation = Changed.modResource(fullId + "/texture.png");
            ResourceLocation modelLocation = Changed.modResource(fullId + "/model");
            DeferredModelLayerLocation layerLocation = new DeferredModelLayerLocation(modelLocation, "main");
            Map<ArmorModel, DeferredModelLayerLocation> armorLocations = ArmorModels.streamModels()
                    .collect(Collectors.toMap(Function.identity(), layer -> ArmorModelLayerLocation.createArmorLocation(layer, modelLocation)));

            return new ModelData(
                    layerLocation,
                    armorLocations,
                    textureLocation,
                    GsonHelper.getAsBoolean(object, "emissive", false) ?
                            Optional.of(Changed.modResource(fullId + "/emissive.png")) : Optional.empty(),
                    AnimationData.fromJSON(object.get("animation")),
                    GsonHelper.getAsBoolean(object, "oldModelRig", true),
                    Cacheable.of(() -> DelayLoadedModel.parse(jsonGetter.apply(fullId + "/model.json"))),
                    Cacheable.of(() -> DelayLoadedModel.parse(jsonGetter.apply(fullId + "/armor_inner.json"))),
                    Cacheable.of(() -> DelayLoadedModel.parse(jsonGetter.apply(fullId + "/armor_outer.json"))),
                    GsonHelper.getAsFloat(object, "shadowsize", 0.5f),
                    GsonHelper.getAsFloat(object, "hipOffset", -2.0f),
                    GsonHelper.getAsFloat(object, "torsoWidth", 5.0f),
                    GsonHelper.getAsFloat(object, "forwardOffset", 0.0f),
                    GsonHelper.getAsFloat(object, "torsoLength", 12.0f),
                    GsonHelper.getAsFloat(object, "armLength", 12.0f),
                    GsonHelper.getAsFloat(object, "legLength", 12.0f)
            );
        }

        public void registerLayerDefinitions(BiConsumer<DeferredModelLayerLocation, Supplier<LayerDefinition>> registrar) {
            var partFixer = oldModelRig ? DelayLoadedModel.HUMANOID_PART_FIXER : DelayLoadedModel.PART_NO_FIX;
            var groupFixer = oldModelRig ? DelayLoadedModel.HUMANOID_GROUP_FIXER : DelayLoadedModel.GROUP_NO_FIX;

            registrar.accept(modelLayerLocation, () -> model.get().createBodyLayer(partFixer, groupFixer));
            registrar.accept(armorModelLayerLocation.get(ArmorModels.ARMOR_INNER), () -> armorModelInner.get().createBodyLayer(partFixer, groupFixer));
            registrar.accept(armorModelLayerLocation.get(ArmorModels.ARMOR_OUTER), () -> armorModelOuter.get().createBodyLayer(partFixer, groupFixer));

            registrar.accept(armorModelLayerLocation.get(ArmorModels.CLOTHING_INNER), () -> armorModelInner.get().createBodyLayer(partFixer, groupFixer, -0.25f));
            registrar.accept(armorModelLayerLocation.get(ArmorModels.CLOTHING_MIDDLE), () -> armorModelInner.get().createBodyLayer(partFixer, groupFixer, -0.2f));
            registrar.accept(armorModelLayerLocation.get(ArmorModels.CLOTHING_OUTER), () -> armorModelOuter.get().createBodyLayer(partFixer, groupFixer, -0.75f));
        }

        public void registerTextures(Consumer<ResourceLocation> registrar) {
            registrar.accept(texture);
            emissive.ifPresent(registrar);
        }
    }

    private static String REPO_BASE = "https://raw.githubusercontent.com/LtxProgrammer/patreon-benefits/main/";
    private static String LINKS_DOCUMENT = REPO_BASE + "listing.json";
    private static String VERSION_DOCUMENT = REPO_BASE + "version.txt";
    private static String FORMS_DOCUMENT = REPO_BASE + "forms/index.json";
    private static String FORMS_BASE = REPO_BASE + "forms/";

    private static void updatePathStrings() {
        REPO_BASE = "https://" + Changed.config.common.githubDomain.get() + "/LtxProgrammer/patreon-benefits/main/";
        LINKS_DOCUMENT = REPO_BASE + "listing.json";
        VERSION_DOCUMENT = REPO_BASE + "version.txt";
        FORMS_DOCUMENT = REPO_BASE + "forms/index.json";
        FORMS_BASE = REPO_BASE + "forms/";
    }

    private static final Map<UUID, Tier> CACHED_LEVELS = new HashMap<>();
    public static int currentVersion;

    private static final Logger LOGGER = LogManager.getLogger(Changed.class);

    public static void loadBenefits() throws IOException, InterruptedException {
        if (!Changed.config.common.downloadPatreonContent.get()) return;

        updatePathStrings();

        // Load levels
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(LINKS_DOCUMENT)).GET().build();

        try {
            JsonElement json = JsonParser.parseString(client.send(request, HttpResponse.BodyHandlers.ofString()).body());
            JsonArray links = json.getAsJsonObject().get("players").getAsJsonArray();

            links.forEach((element) -> {
                JsonObject object = element.getAsJsonObject();
                CACHED_LEVELS.put(UUID.fromString(object.get("uuid").getAsString()), Tier.ofValue(object.get("tier").getAsInt()));
            });
        } catch (Exception ex) {
            LOGGER.error("Encountered error while fetching patronage levels");
            throw ex;
        }

        // Load version
        try {
            request = HttpRequest.newBuilder(URI.create(VERSION_DOCUMENT)).GET().build();
            currentVersion = Integer.parseInt(client.send(request, HttpResponse.BodyHandlers.ofString()).body().replace("\n", ""));
        } catch (Exception ex) {
            LOGGER.error("Encountered error while fetching patron data version");
            throw ex;
        }
    }

    public static Tier getPlayerTier(Player player) {
        return CACHED_LEVELS.getOrDefault(player.getUUID(), Tier.NONE);
    }

    public static Component getPlayerName(Player player, Component username) {
        if (!Changed.config.common.displayPatronage.get())
            return username;

        Component copy = username.copy();

        switch (PatreonBenefits.getPlayerTier(player)) {
            case LEVEL0 -> {
                copy.getSiblings().add(Component.literal(" | "));
                copy.getSiblings().add(Component.translatable("changed.patreon.level0").withStyle(ChatFormatting.GRAY));
            }
            case LEVEL1 -> {
                copy.getSiblings().add(Component.literal(" | "));
                copy.getSiblings().add(Component.translatable("changed.patreon.level1").withStyle(ChatFormatting.GREEN));
            }
            case LEVEL2 -> {
                copy.getSiblings().add(Component.literal(" | "));
                copy.getSiblings().add(Component.translatable("changed.patreon.level2").withStyle(ChatFormatting.AQUA));
            }
            case LEVEL3 -> {
                copy.getSiblings().add(Component.literal(" | "));
                copy.getSiblings().add(Component.translatable("changed.patreon.level3").withStyle(ChatFormatting.LIGHT_PURPLE));
            }
            case LEVEL4 -> {
                copy.getSiblings().add(Component.literal(" | "));
                copy.getSiblings().add(Component.translatable("changed.patreon.level4").withStyle(ChatFormatting.GOLD));
            }
        }

        return copy;
    }

    public static Component getPlayerName(Player player) {
        return getPlayerName(player, player.getDisplayName());
    }
}
