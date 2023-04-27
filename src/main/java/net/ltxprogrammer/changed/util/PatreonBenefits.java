package net.ltxprogrammer.changed.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModelLayerLocation;
import net.ltxprogrammer.changed.data.DeferredModelLayerLocation;
import net.ltxprogrammer.changed.data.DelayLoadedModel;
import net.ltxprogrammer.changed.data.OnlineResource;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
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
            boolean hasWings) {
        public static final AnimationData DEFAULT = new AnimationData(true, false, false);

        public static AnimationData fromJSON(JsonElement object) {
            if (object == null || object.isJsonNull() || !object.isJsonObject()) return DEFAULT;
            else {
                JsonObject json = (JsonObject)object;
                return new AnimationData(
                        GsonHelper.getAsBoolean(json, "hasTail", GsonHelper.getAsBoolean(json, "hastail", true)),
                        GsonHelper.getAsBoolean(json, "swimTail", GsonHelper.getAsBoolean(json, "swimtail", false)),
                        GsonHelper.getAsBoolean(json, "hasWings", GsonHelper.getAsBoolean(json, "haswings", false)));
            }
        }
    }

    public record EntityData(
            ChangedParticles.Color3 primaryColor,
            ChangedParticles.Color3 secondaryColor,
            List<ChangedParticles.Color3> dripColors,
            List<ChangedParticles.Color3> hairColors,
            List<HairStyle> hairStyles,
            EntityDimensions dimensions,
            boolean organic
    ) {
        public static EntityData fromJSON(UUID uuid, JsonObject object) {
            List<ChangedParticles.Color3> dripColors = new ArrayList<>();
            try {
                object.get("particles").getAsJsonArray().forEach(color -> dripColors.add(ChangedParticles.Color3.getColor(color.getAsString())));
            } catch (Exception ignored) {}

            List<ChangedParticles.Color3> hairColors = new ArrayList<>();
            try {
                object.get("hairColor").getAsJsonArray().forEach(color -> hairColors.add(ChangedParticles.Color3.getColor(color.getAsString())));
            } catch (Exception ignored) {}
            if (hairColors.isEmpty())
                hairColors.add(ChangedParticles.Color3.WHITE);

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
                    ChangedParticles.Color3.getColor(GsonHelper.getAsString(object, "primaryColor", "WHITE")),
                    ChangedParticles.Color3.getColor(GsonHelper.getAsString(object, "secondaryColor", "WHITE")),
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
            ArmorModelLayerLocation armorModelLayerLocation,

            ResourceLocation texture,
            Optional<ResourceLocation> emissive,
            AnimationData animationData,

            boolean oldModelRig,
            DelayLoadedModel model,
            DelayLoadedModel armorModelInner,
            DelayLoadedModel armorModelOuter,

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
            ArmorModelLayerLocation armorLocations = new ArmorModelLayerLocation(
                    ArmorModelLayerLocation.createInnerArmorLocation(modelLocation),
                    ArmorModelLayerLocation.createOuterArmorLocation(modelLocation)
            );

            return new ModelData(
                    layerLocation,
                    armorLocations,
                    textureLocation,
                    GsonHelper.getAsBoolean(object, "emissive", false) ?
                            Optional.of(Changed.modResource(fullId + "/emissive.png")) : Optional.empty(),
                    AnimationData.fromJSON(object.get("animation")),
                    GsonHelper.getAsBoolean(object, "oldModelRig", true),
                    DelayLoadedModel.parse(jsonGetter.apply(fullId + "/model.json")),
                    DelayLoadedModel.parse(jsonGetter.apply(fullId + "/armor_inner.json")),
                    DelayLoadedModel.parse(jsonGetter.apply(fullId + "/armor_outer.json")),
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
            if (oldModelRig) {
                registrar.accept(modelLayerLocation, () -> model.createBodyLayer(
                        DelayLoadedModel.HUMANOID_PART_FIXER,
                        DelayLoadedModel.HUMANOID_GROUP_FIXER));
                registrar.accept(armorModelLayerLocation.inner(), () -> armorModelInner.createBodyLayer(
                        DelayLoadedModel.HUMANOID_PART_FIXER,
                        DelayLoadedModel.HUMANOID_GROUP_FIXER));
                registrar.accept(armorModelLayerLocation.outer(), () -> armorModelOuter.createBodyLayer(
                        DelayLoadedModel.HUMANOID_PART_FIXER,
                        DelayLoadedModel.HUMANOID_GROUP_FIXER));
            }

            else {
                registrar.accept(modelLayerLocation, () -> model.createBodyLayer(
                        DelayLoadedModel.PART_NO_FIX,
                        DelayLoadedModel.GROUP_NO_FIX));
                registrar.accept(armorModelLayerLocation.inner(), () -> armorModelInner.createBodyLayer(
                        DelayLoadedModel.PART_NO_FIX,
                        DelayLoadedModel.GROUP_NO_FIX));
                registrar.accept(armorModelLayerLocation.outer(), () -> armorModelOuter.createBodyLayer(
                        DelayLoadedModel.PART_NO_FIX,
                        DelayLoadedModel.GROUP_NO_FIX));
            }
        }

        public void registerTextures(Consumer<ResourceLocation> registrar) {
            registrar.accept(texture);
            emissive.ifPresent(registrar);
        }
    }

    public record SpecialForm(
            UUID playerUUID,

            String defaultState,
            Map<String, EntityData> entityData,
            Map<String, ModelData> modelData,
            LatexVariant<?> variant
    ) {
        public EntityData getDefaultEntity() {
            return entityData.get(defaultState);
        }

        public ModelData getDefaultModel() {
            return modelData.get(defaultState);
        }

        public void registerLayerDefinitions(BiConsumer<DeferredModelLayerLocation, Supplier<LayerDefinition>> registrar) {
            modelData.forEach((key, model) -> model.registerLayerDefinitions(registrar));
        }

        public void registerTextures(Consumer<ResourceLocation> registrar) {
            modelData.forEach((id, model) -> model.registerTextures(registrar));
        }

        public static SpecialForm fromJSON(Function<String, JsonObject> jsonGetter, JsonObject object) {
            Map<String, ModelData> models = new HashMap<>();
            Map<String, EntityData> entities = new HashMap<>();
            String dState = "default";
            UUID uuid = UUID.fromString(GsonHelper.getAsString(object, "location"));
            List<AbstractAbility<?>> injectedAbilities = new ArrayList<>();
            if (object.has("entities")) {
                GsonHelper.getAsJsonArray(object, "entities").forEach(element -> {
                    JsonObject entityObject = element.getAsJsonObject();
                    String id = GsonHelper.getAsString(entityObject, "id");
                    entities.put(id, EntityData.fromJSON(uuid, entityObject));
                });
                dState = GsonHelper.getAsString(object, "defaultState", dState);
            } else {
                entities.put(dState, EntityData.fromJSON(uuid, object));
            }
            if (entities.size() > 1)
                injectedAbilities.add(ChangedAbilities.SELECT_SPECIAL_STATE.get());
            for (var entry : entities.values())
                if (entry.hairStyles.size() > 1) {
                    injectedAbilities.add(ChangedAbilities.SELECT_HAIRSTYLE.get());
                    break;
                }

            final String lockedDefault = dState;
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if (object.has("models")) {
                    GsonHelper.getAsJsonArray(object, "models").forEach(element -> {
                        JsonObject modelObject = element.getAsJsonObject();
                        String id = GsonHelper.getAsString(modelObject, "id");
                        models.put(id, ModelData.fromJSON(jsonGetter, uuid + "/" + id, modelObject));
                    });
                } else {
                    models.put(lockedDefault, ModelData.fromJSON(jsonGetter, uuid.toString(), object));
                }
            });

            return new SpecialForm(
                    uuid,
                    dState,
                    entities,
                    models,
                    LatexVariant.fromJson(Changed.modResource("special/form_" + uuid), object.get("variant").getAsJsonObject(), injectedAbilities)
            );
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
    private static final Map<UUID, SpecialForm> CACHED_SPECIAL_FORMS = new HashMap<>();
    public static final List<Resource> ONLINE_TEXTURES = new ArrayList<>();
    public static final Map<UUID, DeferredModelLayerLocation> LAYER_LOCATIONS = new HashMap<>();
    public static int currentVersion;

    private static final Logger LOGGER = LogManager.getLogger(Changed.class);
    private static final Map<Dist, AtomicBoolean> UPDATE_FLAG = new HashMap<>();
    private static final AtomicBoolean UPDATE_PLAYER_JOIN_FLAG = new AtomicBoolean(false);
    public static final Thread UPDATE_CHECKER = new Thread(() -> {
        LOGGER.info("Update checker started");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(VERSION_DOCUMENT)).GET().build();

        while (true) {
            try {
                Thread.sleep(60000 * 3);
                while (!UPDATE_PLAYER_JOIN_FLAG.get()); // Loop until player joins
                UPDATE_PLAYER_JOIN_FLAG.set(false); // Consume
                LOGGER.info("Checking for updates");
                int version = Integer.parseInt(client.send(request, HttpResponse.BodyHandlers.ofString()).body().replace("\n", ""));

                if (version != currentVersion) {
                    LOGGER.info("Update found!");
                    UPDATE_FLAG.computeIfAbsent(Dist.CLIENT, dist -> new AtomicBoolean(true)).set(true);
                    UPDATE_FLAG.computeIfAbsent(Dist.DEDICATED_SERVER, dist -> new AtomicBoolean(true)).set(true);
                    currentVersion = version;
                }
            }

            catch (InterruptedException | IOException ex) {
                ex.printStackTrace();
            }
        }
    });

    @Mod.EventBusSubscriber
    private static class EventSub {
        @SubscribeEvent
        public static void onPlayerJoin(EntityJoinWorldEvent playerJoin) {
            if (playerJoin.getEntity() instanceof Player player) {
                LOGGER.info("Player joined, setting enabling update checker flag");
                UPDATE_PLAYER_JOIN_FLAG.set(true);
            }
        }
    }

    public static void loadSpecialForms(HttpClient client) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(FORMS_DOCUMENT)).GET().build();
        JsonElement json = JsonParser.parseString(client.send(request, HttpResponse.BodyHandlers.ofString()).body());
        JsonArray formLocations = json.getAsJsonObject().get("forms").getAsJsonArray();

        AtomicInteger count = new AtomicInteger(0);

        ChangedRegistry.LATEX_VARIANT.get().unfreeze();
        formLocations.forEach((element) -> {
            JsonObject object = element.getAsJsonObject();
            if (GsonHelper.getAsInt(object, "version", 1) > COMPATIBLE_VERSION)
                return;

            SpecialForm form = SpecialForm.fromJSON(
                    str -> {
                        try {
                            return JsonParser.parseString(client.send(HttpRequest.newBuilder(URI.create(FORMS_BASE + str)).GET().build(), HttpResponse.BodyHandlers.ofString()).body()).getAsJsonObject();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new JsonObject();
                        }
                    },
                    object
            );

            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                form.registerTextures(location ->
                        DynamicClient.lateRegisterOnlineTexture(OnlineResource.of(
                                location.getPath(),
                                location,
                                URI.create(FORMS_BASE + location.getPath())
                        )));

                form.registerLayerDefinitions(DynamicClient::lateRegisterLayerDefinition);
            });

            CACHED_SPECIAL_FORMS.put(form.playerUUID, form);
            LatexVariant.registerSpecial(form.variant);
            ChangedRegistry.LATEX_VARIANT.get().register(form.variant);
            count.getAndIncrement();
        });

        ChangedRegistry.LATEX_VARIANT.get().freeze();
        LOGGER.info("Updated {} patreon special forms", count.get());
    }

    public static boolean checkForUpdates() throws IOException, InterruptedException {
        if (UPDATE_FLAG.computeIfAbsent(FMLEnvironment.dist, dist -> new AtomicBoolean(false)).get()) {
            UPDATE_FLAG.get(FMLEnvironment.dist).set(false); // Consume update flag
            updatePathStrings();
            HttpClient client = HttpClient.newHttpClient();

            {
                HttpRequest request = HttpRequest.newBuilder(URI.create(LINKS_DOCUMENT)).GET().build();
                JsonElement json = JsonParser.parseString(client.send(request, HttpResponse.BodyHandlers.ofString()).body());
                JsonArray links = json.getAsJsonObject().get("players").getAsJsonArray();

                CACHED_LEVELS.clear();
                links.forEach((element) -> {
                    JsonObject object = element.getAsJsonObject();
                    CACHED_LEVELS.put(UUID.fromString(object.get("uuid").getAsString()), Tier.ofValue(object.get("tier").getAsInt()));
                });
            }

            loadSpecialForms(client);

            UniversalDist.displayClientMessage(new TextComponent("Updated Patreon Data."), false);
            return true;
        }

        return false;
    }

    public static void loadBenefits() throws IOException, InterruptedException {
        updatePathStrings();

        // Load levels
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(LINKS_DOCUMENT)).GET().build();
        JsonElement json = JsonParser.parseString(client.send(request, HttpResponse.BodyHandlers.ofString()).body());
        JsonArray links = json.getAsJsonObject().get("players").getAsJsonArray();

        links.forEach((element) -> {
            JsonObject object = element.getAsJsonObject();
            CACHED_LEVELS.put(UUID.fromString(object.get("uuid").getAsString()), Tier.ofValue(object.get("tier").getAsInt()));
        });

        // Load forms
        loadSpecialForms(client);

        // Load version
        request = HttpRequest.newBuilder(URI.create(VERSION_DOCUMENT)).GET().build();
        currentVersion = Integer.parseInt(client.send(request, HttpResponse.BodyHandlers.ofString()).body().replace("\n", ""));
    }

    public static Tier getPlayerTier(Player player) {
        return CACHED_LEVELS.getOrDefault(player.getUUID(), Tier.NONE);
    }

    @Nullable
    public static SpecialForm getPlayerSpecialForm(UUID player) {
        return CACHED_SPECIAL_FORMS.getOrDefault(player, null);
    }

    public static LatexVariant<?> getPlayerSpecialVariant(UUID player) {
        SpecialForm form = getPlayerSpecialForm(player);
        if (form == null)
            return null;

        return form.variant;
    }

    public static Component getPlayerName(Player player) {
        Component name = player.getDisplayName();

        switch (PatreonBenefits.getPlayerTier(player)) {
            case LEVEL0 -> {
                name.getSiblings().add(new TextComponent(" | "));
                name.getSiblings().add(new TranslatableComponent("changed.patreon.level0").withStyle(ChatFormatting.GRAY));
            }
            case LEVEL1 -> {
                name.getSiblings().add(new TextComponent(" | "));
                name.getSiblings().add(new TranslatableComponent("changed.patreon.level1").withStyle(ChatFormatting.GREEN));
            }
            case LEVEL2 -> {
                name.getSiblings().add(new TextComponent(" | "));
                name.getSiblings().add(new TranslatableComponent("changed.patreon.level2").withStyle(ChatFormatting.AQUA));
            }
            case LEVEL3 -> {
                name.getSiblings().add(new TextComponent(" | "));
                name.getSiblings().add(new TranslatableComponent("changed.patreon.level3").withStyle(ChatFormatting.LIGHT_PURPLE));
            }
            case LEVEL4 -> {
                name.getSiblings().add(new TextComponent(" | "));
                name.getSiblings().add(new TranslatableComponent("changed.patreon.level4").withStyle(ChatFormatting.GOLD));
            }
        }

        return name;
    }
}
