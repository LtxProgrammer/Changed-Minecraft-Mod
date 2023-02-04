package net.ltxprogrammer.changed.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModelLayerLocation;
import net.ltxprogrammer.changed.data.DeferredModelLayerLocation;
import net.ltxprogrammer.changed.data.DelayLoadedModel;
import net.ltxprogrammer.changed.data.OnlineResource;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
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

import static net.ltxprogrammer.changed.entity.variant.LatexVariant.PUBLIC_LATEX_FORMS;

public class PatreonBenefits {
    private static final int COMPATIBLE_VERSION = 2;

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

        public static AnimationData from(JsonElement object) {
            if (object == null || object.isJsonNull() || !object.isJsonObject()) return DEFAULT;
            else {
                JsonObject json = (JsonObject)object;
                return new AnimationData(
                        GsonHelper.getAsBoolean(json, "hastail", true),
                        GsonHelper.getAsBoolean(json, "swimtail", false),
                        GsonHelper.getAsBoolean(json, "haswings", false));
            }
        }
    }

    public record SpecialLatexForm(
            UUID uuid,
            List<ChangedParticles.Color3> weightedColors,
            ResourceLocation texture,
            Optional<ResourceLocation> emissive,
            DeferredModelLayerLocation modelLayerLocation,
            ArmorModelLayerLocation armorModelLayerLocation,
            DelayLoadedModel model,
            DelayLoadedModel armorModelInner,
            DelayLoadedModel armorModelOuter,
            AnimationData animationData,
            float shadowSize,
            float width,
            float height,
            LatexVariant<?> variant) {

        public boolean tailAidsInSwim() {
            return variant.getBreatheMode().canBreatheWater();
        }
    }

    private static String REPO_BASE = "https://raw.githubusercontent.com/LtxProgrammer/patreon-benefits/main/";
    private static String LINKS_DOCUMENT = REPO_BASE + "listing.json";
    private static String VERSION_DOCUMENT = REPO_BASE + "version.txt";
    private static String FORMS_DOCUMENT = REPO_BASE + "forms/index.json";
    private static String FORMS_BASE = REPO_BASE + "forms/";

    private static void updatePathStrings() {
        REPO_BASE = "https://" + Changed.config.githubDomain + "/LtxProgrammer/patreon-benefits/main/";
        LINKS_DOCUMENT = REPO_BASE + "listing.json";
        VERSION_DOCUMENT = REPO_BASE + "version.txt";
        FORMS_DOCUMENT = REPO_BASE + "forms/index.json";
        FORMS_BASE = REPO_BASE + "forms/";
    }

    private static final Map<UUID, Tier> CACHED_LEVELS = new HashMap<>();
    private static final Map<UUID, SpecialLatexForm> CACHED_SPECIAL_FORMS = new HashMap<>();
    public static final List<Resource> ONLINE_TEXTURES = new ArrayList<>();
    public static final Map<UUID, DeferredModelLayerLocation> LAYER_LOCATIONS = new HashMap<>();
    public static int currentVersion;

    private static class Internal {
        private static void lateRegisterModel(DeferredModelLayerLocation layerLocation, ArmorModelLayerLocation armorLocations, SpecialLatexForm form) {
            DynamicClient.lateRegisterLayerDefinition(layerLocation, () ->
                    form.model.createBodyLayer(
                            DelayLoadedModel.HUMANOID_PART_FIXER,
                            DelayLoadedModel.HUMANOID_GROUP_FIXER));
            DynamicClient.lateRegisterLayerDefinition(armorLocations.inner(), () ->
                    form.armorModelInner.createBodyLayer(
                            DelayLoadedModel.HUMANOID_PART_FIXER,
                            DelayLoadedModel.HUMANOID_GROUP_FIXER));
            DynamicClient.lateRegisterLayerDefinition(armorLocations.outer(), () ->
                    form.armorModelOuter.createBodyLayer(
                            DelayLoadedModel.HUMANOID_PART_FIXER,
                            DelayLoadedModel.HUMANOID_GROUP_FIXER));
        }
    }

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

            {
                HttpRequest request = HttpRequest.newBuilder(URI.create(FORMS_DOCUMENT)).GET().build();
                JsonElement json = JsonParser.parseString(client.send(request, HttpResponse.BodyHandlers.ofString()).body());
                JsonArray formLocations = json.getAsJsonObject().get("forms").getAsJsonArray();

                formLocations.forEach((element) -> {
                    try {
                        HttpClient iterClient = HttpClient.newHttpClient();
                        JsonObject object = element.getAsJsonObject();
                        if (GsonHelper.getAsInt(object, "version", 1) > COMPATIBLE_VERSION)
                            return;
                        String name = object.get("location").getAsString();
                        UUID uuid = UUID.fromString(name);
                        HttpRequest modelRequest = HttpRequest.newBuilder(URI.create(FORMS_BASE + name + "/model.json")).GET().build();
                        HttpRequest armorModelInnerRequest = HttpRequest.newBuilder(URI.create(FORMS_BASE + name + "/armor_inner.json")).GET().build();
                        HttpRequest armorModelOuterRequest = HttpRequest.newBuilder(URI.create(FORMS_BASE + name + "/armor_outer.json")).GET().build();

                        ResourceLocation textureLocation = Changed.modResource("special/texture_" + name + ".png");
                        var onlineResource = OnlineResource.of(
                                "special_" + name,
                                textureLocation,
                                URI.create(FORMS_BASE + name + "/texture.png")
                        );

                        ONLINE_TEXTURES.add(onlineResource);

                        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> DynamicClient.lateRegisterOnlineTexture(onlineResource));

                        ResourceLocation modelLocation = Changed.modResource("special/model_" + name);
                        DeferredModelLayerLocation layerLocation = new DeferredModelLayerLocation(modelLocation, "main");
                        ArmorModelLayerLocation armorLocations = new ArmorModelLayerLocation(
                                ArmorModelLayerLocation.createInnerArmorLocation(modelLocation),
                                ArmorModelLayerLocation.createOuterArmorLocation(modelLocation)
                        );
                        LAYER_LOCATIONS.put(uuid, layerLocation);
                        ArmorModelLayerLocation.ARMOR_LAYER_LOCATIONS.put(uuid, armorLocations);

                        List<ChangedParticles.Color3> colors = new ArrayList<>();
                        try {
                            object.get("particles").getAsJsonArray().forEach((particle) -> colors.add(ChangedParticles.Color3.getColor(particle.getAsString())));
                        } catch (Exception ignored) {}

                        SpecialLatexForm form = new SpecialLatexForm(
                                uuid,
                                colors,
                                textureLocation,
                                GsonHelper.getAsBoolean(object, "emissive", false) ?
                                        Optional.of(Changed.modResource("special/emissive_" + name + ".png")) : Optional.empty(),
                                layerLocation,
                                armorLocations,
                                DelayLoadedModel.parse(JsonParser.parseString(iterClient.send(modelRequest, HttpResponse.BodyHandlers.ofString()).body()).getAsJsonObject()),
                                DelayLoadedModel.parse(JsonParser.parseString(iterClient.send(armorModelInnerRequest, HttpResponse.BodyHandlers.ofString()).body()).getAsJsonObject()),
                                DelayLoadedModel.parse(JsonParser.parseString(iterClient.send(armorModelOuterRequest, HttpResponse.BodyHandlers.ofString()).body()).getAsJsonObject()),
                                AnimationData.from(object.get("animation")),
                                object.get("shadowsize").getAsFloat(),
                                object.get("width").getAsFloat(),
                                object.get("height").getAsFloat(),
                                LatexVariant.fromJson(Changed.modResource("special/form_" + uuid), object.get("variant").getAsJsonObject())
                        );

                        form.emissive.ifPresent(location -> {
                            var onlineResourceEmissive = OnlineResource.of(
                                    "special_" + name,
                                    form.emissive.get(),
                                    URI.create(FORMS_BASE + name + "/emissive.png")
                            );

                            ONLINE_TEXTURES.add(onlineResourceEmissive);
                            DynamicClient.lateRegisterOnlineTexture(onlineResourceEmissive);
                        });

                        CACHED_SPECIAL_FORMS.put(uuid, form);

                        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Internal.lateRegisterModel(layerLocation, armorLocations, form));

                        LatexVariant.registerSpecial(form.variant);
                    } catch (IOException | InterruptedException e) {
                        UniversalDist.displayClientMessage(
                                new TextComponent("Exception while loading patron data" + e.getLocalizedMessage()).withStyle(ChatFormatting.DARK_RED), false);
                        throw new ReportedException(new CrashReport("Exception while reloading patron data", e));
                    }
                });
            }

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
        request = HttpRequest.newBuilder(URI.create(FORMS_DOCUMENT)).GET().build();
        json = JsonParser.parseString(client.send(request, HttpResponse.BodyHandlers.ofString()).body());
        JsonArray formLocations = json.getAsJsonObject().get("forms").getAsJsonArray();

        formLocations.forEach((element) -> {
            try {
                HttpClient iterClient = HttpClient.newHttpClient();
                JsonObject object = element.getAsJsonObject();
                if (GsonHelper.getAsInt(object, "version", 1) > COMPATIBLE_VERSION)
                    return;
                String name = object.get("location").getAsString();
                UUID uuid = UUID.fromString(name);
                HttpRequest modelRequest = HttpRequest.newBuilder(URI.create(FORMS_BASE + name + "/model.json")).GET().build();
                HttpRequest armorModelInnerRequest = HttpRequest.newBuilder(URI.create(FORMS_BASE + name + "/armor_inner.json")).GET().build();
                HttpRequest armorModelOuterRequest = HttpRequest.newBuilder(URI.create(FORMS_BASE + name + "/armor_outer.json")).GET().build();

                ResourceLocation textureLocation = Changed.modResource("special/texture_" + name + ".png");
                ONLINE_TEXTURES.add(OnlineResource.of(
                        "special_" + name,
                        textureLocation,
                        URI.create(FORMS_BASE + name + "/texture.png")
                ));

                ResourceLocation modelLocation = Changed.modResource("special/model_" + name);
                DeferredModelLayerLocation layerLocation = new DeferredModelLayerLocation(modelLocation, "main");
                ArmorModelLayerLocation armorLocations = new ArmorModelLayerLocation(
                        ArmorModelLayerLocation.createInnerArmorLocation(modelLocation),
                        ArmorModelLayerLocation.createOuterArmorLocation(modelLocation)
                );
                LAYER_LOCATIONS.put(uuid, layerLocation);
                ArmorModelLayerLocation.ARMOR_LAYER_LOCATIONS.put(uuid, armorLocations);

                List<ChangedParticles.Color3> colors = new ArrayList<>();
                try {
                    object.get("particles").getAsJsonArray().forEach((particle) -> colors.add(ChangedParticles.Color3.getColor(particle.getAsString())));
                } catch (Exception ignored) {}

                SpecialLatexForm form = new SpecialLatexForm(
                        uuid,
                        colors,
                        textureLocation,
                        GsonHelper.getAsBoolean(object, "emissive", false) ?
                                Optional.of(Changed.modResource("special/emissive_" + name + ".png")) : Optional.empty(),
                        layerLocation,
                        armorLocations,
                        DelayLoadedModel.parse(JsonParser.parseString(iterClient.send(modelRequest, HttpResponse.BodyHandlers.ofString()).body()).getAsJsonObject()),
                        DelayLoadedModel.parse(JsonParser.parseString(iterClient.send(armorModelInnerRequest, HttpResponse.BodyHandlers.ofString()).body()).getAsJsonObject()),
                        DelayLoadedModel.parse(JsonParser.parseString(iterClient.send(armorModelOuterRequest, HttpResponse.BodyHandlers.ofString()).body()).getAsJsonObject()),
                        AnimationData.from(object.get("animation")),
                        object.get("shadowsize").getAsFloat(),
                        object.get("width").getAsFloat(),
                        object.get("height").getAsFloat(),
                        LatexVariant.fromJson(Changed.modResource("special/form_" + uuid), object.get("variant").getAsJsonObject())
                );

                form.emissive.ifPresent(location -> ONLINE_TEXTURES.add(OnlineResource.of(
                        "special_" + name,
                        location,
                        URI.create(FORMS_BASE + name + "/emissive.png")
                )));

                CACHED_SPECIAL_FORMS.put(uuid, form);

                LatexVariant.registerSpecial(form.variant);
            } catch (IOException | InterruptedException e) {
                UniversalDist.displayClientMessage(
                        new TextComponent("Exception while loading patron data" + e.getLocalizedMessage()).withStyle(ChatFormatting.DARK_RED), false);
                throw new ReportedException(new CrashReport("Exception while loading patron data", e));
            }
        });

        PUBLIC_LATEX_FORMS.put(LatexVariant.SPECIAL_LATEX, null);

        // Load version
        request = HttpRequest.newBuilder(URI.create(VERSION_DOCUMENT)).GET().build();
        currentVersion = Integer.parseInt(client.send(request, HttpResponse.BodyHandlers.ofString()).body().replace("\n", ""));
    }

    public static Tier getPlayerTier(Player player) {
        return CACHED_LEVELS.getOrDefault(player.getUUID(), Tier.NONE);
    }

    @Nullable
    public static SpecialLatexForm getPlayerSpecialForm(UUID player) {
        return CACHED_SPECIAL_FORMS.getOrDefault(player, null);
    }

    public static LatexVariant<?> getPlayerSpecialVariant(UUID player) {
        SpecialLatexForm form = getPlayerSpecialForm(player);
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
