package net.ltxprogrammer.changed.mixin.compatibility;

import com.google.common.collect.Multimap;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ChangedMixinPlugin implements IMixinConfigPlugin {
    private static boolean isModPresent(String modId) {
        return FMLLoader.getLoadingModList().getModFileById(modId) != null;
    }

    private static boolean conditionSatisfied(String modIdCondition) {
        if (modIdCondition.startsWith("!"))
            return !isModPresent(modIdCondition.substring(1));
        else
            return isModPresent(modIdCondition);
    }

    // ParCool 4.0 removed the animation API (capability.Animation, PlayerModelRotator, PlayerModelTransformer)
    // that the Parcool mixins target. Those mixins must only apply when this class is still present.
    private static final String PARCOOL_LEGACY_ANIMATION_CLASS = "com/alrex/parcool/common/capability/Animation.class";

    private static boolean isParcoolLegacyAnimationPresent() {
        return ChangedMixinPlugin.class.getClassLoader().getResource(PARCOOL_LEGACY_ANIMATION_CLASS) != null;
    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // This map is generated at compile-time, and doesn't exist until then
        if (mixinClassName.startsWith("net.ltxprogrammer.changed.mixin.compatibility.Parcool.") && !isParcoolLegacyAnimationPresent())
            return false;

        final Multimap<String, String> dependencies = net.ltxprogrammer.changed.extension.MixinDependencies.MULTIMAP;
        return dependencies.get(mixinClassName).stream().allMatch(ChangedMixinPlugin::conditionSatisfied);
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
