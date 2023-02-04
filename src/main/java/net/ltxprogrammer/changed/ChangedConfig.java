package net.ltxprogrammer.changed;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import net.minecraftforge.fml.ModLoadingContext;

import java.io.*;

public class ChangedConfig {
    private final String file;
    public String githubDomain;

    private static <T> T getOrDefault(CommentedConfig config, String name, T d) {
        T r = config.<T>get(name);
        return r == null ? d : r;
    }

    public ChangedConfig(ModLoadingContext context) {
        file = "config/" + context.getActiveContainer().getModId() + ".toml";
        reload();
    }

    public void reload() {
        var f = new File(file);
        if (!f.exists()) {
            var toml = new TomlWriter();
            var config = TomlFormat.newConfig();
            config.add("githubDomain", "raw.githubusercontent.com");
            config.setComment("githubDomain", "Choose your domain. Use \"raw.fastgit.org\" if your ISP blocks github.");
            try {
                var writer = new FileWriter(f);
                toml.write(config, writer);
                writer.close();
            } catch (Exception ex) {
                Changed.LOGGER.error("Failed to write default config.");
                ex.printStackTrace();
            }
        }
        try {
            var toml = new TomlParser();
            var reader = new FileReader(f);
            var config = toml.parse(reader);
            reader.close();
            githubDomain = getOrDefault(config, "githubDomain", "raw.githubusercontent.com");
        } catch (Exception ex) {
            Changed.LOGGER.error("Failed to read config.");
            ex.printStackTrace();
        }
    }
}
