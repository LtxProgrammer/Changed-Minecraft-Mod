package net.ltxprogrammer.changed.VariantCheck;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class VariantConfig {
    public final ForgeConfigSpec.ConfigValue<Boolean> openOrigin;

    // Constructor that accepts ModLoadingContext
    public VariantConfig(ModLoadingContext context) {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Enabling this config will allow you to have both origin and latex variants. (default is false)");
        openOrigin = builder.define("openOrigin", false);

        // Register the config
        context.registerConfig(ModConfig.Type.COMMON, builder.build(), "variant-config.toml");
    }
}
