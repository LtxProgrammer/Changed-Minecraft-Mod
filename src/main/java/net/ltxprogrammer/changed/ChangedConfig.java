package net.ltxprogrammer.changed;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChangedConfig {
    private interface AdditionalData {
        String getName();
        void load(CompoundTag tag);
        void save(CompoundTag tag);
    }

    private static final Logger LOGGER = LogManager.getLogger();
    static final Marker CONFIG = MarkerManager.getMarker("CONFIG");

    public static class Common {
        public final ForgeConfigSpec.ConfigValue<String> githubDomain;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Choose your domain. Use \"raw.fastgit.org\" if your ISP blocks github.");
            githubDomain = builder.define("githubDomain", "raw.githubusercontent.com");
        }
    }

    public static class Client implements AdditionalData {
        public final ForgeConfigSpec.ConfigValue<Boolean> useNewModels;
        public final ForgeConfigSpec.ConfigValue<Boolean> useGoopyInventory;
        public final ForgeConfigSpec.ConfigValue<Boolean> useGoopyHearts;
        public final ForgeConfigSpec.ConfigValue<Boolean> cacheGeneratedTextures;
        public final ForgeConfigSpec.ConfigValue<Boolean> memCacheBaseImages;
        public final ForgeConfigSpec.ConfigValue<Boolean> generateUniqueTexturesForAllBlocks;
        public final ForgeConfigSpec.ConfigValue<Boolean> fastAndCheapLatexBlocks;

        public final BasicPlayerInfo basicPlayerInfo = new BasicPlayerInfo();

        public Client(ForgeConfigSpec.Builder builder) {
            builder.comment("While some like the new models, you may not. Here's your chance to opt-out (Requires restart)");
            useNewModels = builder.define("useNewModels", true);
            builder.comment("Enable/disable the gooey inventory");
            useGoopyInventory = builder.define("useGooeyInventory", true);
            builder.comment("Enable/disable the gooey hearts");
            useGoopyHearts = builder.define("useGoopyHearts", true);
            builder.comment("Caching generated latex covering textures will decrease load time, but will disable recreating the cache if you change resource packs. This cache is stored on your disk. It's recommended to enable this while loading large modpacks");
            cacheGeneratedTextures = builder.define("cacheGeneratedTextures", true);
            builder.comment("While generating textures, the generator will store all used block textures in memory until all textures are generated. It's recommended to disable this while loading large modpacks");
            memCacheBaseImages = builder.define("memCacheBaseImages", true);
            builder.comment("Large modpacks will eat up all your memory if unique textures are generated for every block, this will apply a generic texture for all cube like models");
            generateUniqueTexturesForAllBlocks = builder.define("generateUniqueTexturesForAllBlocks", true);
            builder.comment("Got a lot of mods? Unique model generation will be limited to minecraft and changed");
            fastAndCheapLatexBlocks = builder.define("fastAndCheapLatexBlocks", false);
        }

        @Override
        public String getName() {
            return "client";
        }

        @Override
        public void load(CompoundTag tag) {
            basicPlayerInfo.load(tag.getCompound("basicPlayerInfo"));
        }

        @Override
        public void save(CompoundTag tag) {
            var bpi = new CompoundTag();
            basicPlayerInfo.save(bpi);
            tag.put("basicPlayerInfo", bpi);
        }
    }

    public static class Server {
        public final ForgeConfigSpec.ConfigValue<Double> transfurTolerance;

        public Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Replacing gamerule transfurTolerance, this config value acts like setting the max health of the player.");
            transfurTolerance = builder.define("transfurTolerance", 20.0);
        }
    }

    private final Pair<Common, ForgeConfigSpec> commonPair;
    private final Pair<Client, ForgeConfigSpec> clientPair;
    private final Pair<Server, ForgeConfigSpec> serverPair;
    private final List<AdditionalData> additionalDataList = new ArrayList<>();
    public final Common common;
    public final Client client;
    public final Server server;

    private static void earlyLoad(ModConfig.Type type, Pair<?, ForgeConfigSpec> specPair) {
        for (var config : ConfigTracker.INSTANCE.configSets().get(type)) {
            if (config.getSpec() == specPair.getRight()) {
                LOGGER.debug(CONFIG, "Early loading config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
                final CommentedFileConfig configData = config.getHandler().reader(FMLPaths.CONFIGDIR.get()).apply(config);
                specPair.getRight().acceptConfig(configData);
            }
        }
    }

    public void saveAdditionalData() {
        additionalDataList.forEach(data -> {
            var path = FMLPaths.CONFIGDIR.get().resolve(Changed.MODID).resolve(data.getName() + ".nbt");
            try {
                var tag = new CompoundTag();
                data.save(tag);
                path.getParent().toFile().mkdirs();
                NbtIo.writeCompressed(tag, path.toFile());
            } catch (IOException ex) {
                Changed.LOGGER.error("Failed to write data for \"{}\"", data.getName());
                ex.printStackTrace();
            }
        });
    }

    public void updateAdditionalData() {
        additionalDataList.forEach(data -> {
            var path = FMLPaths.CONFIGDIR.get().resolve(Changed.MODID).resolve(data.getName() + ".nbt");
            try {
                var tag = NbtIo.readCompressed(path.toFile());
                data.load(tag);
            } catch (IOException e) {
                Changed.LOGGER.warn("Data file missing or corrupted for \"{}\", initializing on defaults", data.getName());

                try {
                    var tag = new CompoundTag();
                    data.save(tag);
                    path.getParent().toFile().mkdirs();
                    NbtIo.writeCompressed(tag, path.toFile());
                } catch (IOException ex) {
                    Changed.LOGGER.error("Failed to write defaults for \"{}\"", data.getName());
                    ex.printStackTrace();
                }
            }
        });
    }

    public ChangedConfig(ModLoadingContext context) {
        commonPair = new ForgeConfigSpec.Builder()
                .configure(Common::new);
        clientPair = new ForgeConfigSpec.Builder()
                .configure(Client::new);
        serverPair = new ForgeConfigSpec.Builder()
                .configure(Server::new);

        additionalDataList.add(clientPair.getLeft());

        context.registerConfig(ModConfig.Type.COMMON, commonPair.getRight());
        context.registerConfig(ModConfig.Type.CLIENT, clientPair.getRight());
        context.registerConfig(ModConfig.Type.SERVER, serverPair.getRight());
        common = commonPair.getLeft();
        client = clientPair.getLeft();
        server = serverPair.getLeft();

        // Early load client config
        earlyLoad(ModConfig.Type.CLIENT, clientPair);

        updateAdditionalData();
    }
}
