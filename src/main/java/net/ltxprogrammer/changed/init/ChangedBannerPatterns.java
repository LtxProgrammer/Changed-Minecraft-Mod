package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ChangedBannerPatterns {
    public static final DeferredRegister<BannerPattern> REGISTRY = DeferredRegister.create(Registries.BANNER_PATTERN, Changed.MODID);

    private static RegistryObject<BannerPattern> register(String id, String hashName) {
        return REGISTRY.register(id, () -> new BannerPattern(hashName));
    }

    public static final RegistryObject<BannerPattern> DARK_LATEX_MASK = register("dark_latex_mask", "c:dlm");
    public static final RegistryObject<BannerPattern> GOO_BOTTOM = register("goo_bottom", "c:gb");
    public static final RegistryObject<BannerPattern> GOO_TOP = register("goo_top", "c:gt");
    public static final RegistryObject<BannerPattern> PAW_PRINT = register("paw_print", "c:pp");
    public static final RegistryObject<BannerPattern> DORSAL_FIN = register("dorsal_fin", "c:df");
    public static final RegistryObject<BannerPattern> TENTAPAWS = register("tentapaws", "c:tp");
    public static final RegistryObject<BannerPattern> TSC_LOGO = register("tsc_logo", "c:tsc");
    public static final RegistryObject<BannerPattern> CRYSTAL_BOTTOM = register("crystal_bottom", "c:bc");
    public static final RegistryObject<BannerPattern> CRYSTAL_TOP = register("crystal_top", "c:tc");
}
