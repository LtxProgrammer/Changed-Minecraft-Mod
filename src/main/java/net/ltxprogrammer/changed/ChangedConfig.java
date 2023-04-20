package net.ltxprogrammer.changed;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class ChangedConfig {
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

    public final Common common;
    public final Client client;

    public ChangedConfig(ModLoadingContext context) {
        {
            Pair<Common, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
                    .configure(Common::new);
            context.registerConfig(ModConfig.Type.COMMON, pair.getRight());
            common = pair.getLeft();
        }

        {
            Pair<Client, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
                    .configure(Client::new);
            context.registerConfig(ModConfig.Type.CLIENT, pair.getRight());
            client = pair.getLeft();
        }
    }
}
