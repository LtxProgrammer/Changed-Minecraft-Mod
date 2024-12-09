package net.ltxprogrammer.changed.mixin.client;

import net.ltxprogrammer.changed.client.ClientLivingEntityExtender;
import net.ltxprogrammer.changed.entity.animation.AnimationCategory;
import net.ltxprogrammer.changed.client.animations.AnimationDefinition;
import net.ltxprogrammer.changed.client.animations.AnimationInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This mixin only loads on the client, so we can safely control animation instances here
 */
@Mixin(LivingEntity.class)
public class ClientLivingEntityMixin implements ClientLivingEntityExtender {
    @Unique
    private final Map<AnimationCategory, AnimationInstance> animations = new HashMap<>(0);

    @Override
    public Stream<AnimationInstance> getOrderedAnimations() {
        return animations.values().stream();
    }

    @Override
    public void addAnimation(AnimationCategory category, AnimationInstance animationInstance) {
        clearAnimation(category);
        animations.put(category, animationInstance);
    }

    @Override
    public @Nullable AnimationInstance getAnimation(AnimationCategory category) {
        return animations.get(category);
    }

    @Override
    public @Nullable AnimationInstance getAnimation(AnimationCategory category, Supplier<AnimationDefinition> definition) {
        final var instance = animations.get(category);
        if (instance == null)
            return null;
        if (instance.getDefinition() != definition.get())
            return null;
        return instance;
    }

    @Override
    public void clearAnimation(AnimationCategory category) {
        if (!animations.containsKey(category))
            return;

        animations.get(category).clear();
        animations.remove(category);
    }

    @Override
    public void clearAnimation(AnimationCategory category, Supplier<AnimationDefinition> definition) {
        if (!animations.containsKey(category))
            return;

        if (animations.get(category).getDefinition() != definition.get())
            return;

        animations.get(category).clear();
        animations.remove(category);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickAnimations(CallbackInfo ci) {
        animations.entrySet().stream().filter(entry -> entry.getValue().isDone()).collect(Collectors.toSet())
                .forEach(completed -> clearAnimation(completed.getKey()));
        animations.values().forEach(AnimationInstance::tickTime);
    }
}
