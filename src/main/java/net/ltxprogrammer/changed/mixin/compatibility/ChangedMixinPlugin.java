package net.ltxprogrammer.changed.mixin.compatibility;

import com.google.common.collect.ImmutableMap;
import net.ltxprogrammer.changed.Changed;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChangedMixinPlugin implements IMixinConfigPlugin {
    private static final Map<String, String> MOD_ID_MAP = new ImmutableMap.Builder<String, String>()
            .put("net.ltxprogrammer.changed.mixin.compatibility.HardcoreRevival", "hardcorerevival")
            .put("net.ltxprogrammer.changed.mixin.compatibility.Oculus", "oculus")
            .put("net.ltxprogrammer.changed.mixin.compatibility.Pehkui", "pehkui")
            .put("net.ltxprogrammer.changed.mixin.compatibility.PresenceFootsteps", "presencefootsteps")
            .put("net.ltxprogrammer.changed.mixin.compatibility.Rubidium", "rubidium")
            .put("net.ltxprogrammer.changed.mixin.compatibility.Vivecraft", "vivecraft").build();

    private static boolean isModPresent(String modId) {
        return FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    private static String getPackageName(String className) {
        return className.substring(0, className.lastIndexOf('.'));
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        var modId = MOD_ID_MAP.getOrDefault(getPackageName(mixinClassName), Changed.MODID);
        return isModPresent(modId);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
