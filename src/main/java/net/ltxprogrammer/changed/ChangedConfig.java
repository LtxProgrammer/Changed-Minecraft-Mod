package net.ltxprogrammer.changed;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
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

public class ChangedConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    static final Marker CONFIG = MarkerManager.getMarker("CONFIG");

    public static class Common {
        public final ForgeConfigSpec.ConfigValue<String> githubDomain;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Choose your domain. Use \"raw.fastgit.org\" if your ISP blocks github.");
            githubDomain = builder.define("githubDomain", "raw.githubusercontent.com");
        }
    }

    public static class Client {
        public final ForgeConfigSpec.ConfigValue<Boolean> useNewModels;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.comment("While some like the new models, you may not. Here's your chance to opt-out (Requires restart)");
            useNewModels = builder.define("useNewModels", true);
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

    public ChangedConfig(ModLoadingContext context) {
        commonPair = new ForgeConfigSpec.Builder()
                .configure(Common::new);
        clientPair = new ForgeConfigSpec.Builder()
                .configure(Client::new);
        serverPair = new ForgeConfigSpec.Builder()
                .configure(Server::new);

        context.registerConfig(ModConfig.Type.COMMON, commonPair.getRight());
        context.registerConfig(ModConfig.Type.CLIENT, clientPair.getRight());
        context.registerConfig(ModConfig.Type.SERVER, serverPair.getRight());
        common = commonPair.getLeft();
        client = clientPair.getLeft();
        server = serverPair.getLeft();

        // Early load client config
        earlyLoad(ModConfig.Type.CLIENT, clientPair);
    }
}
