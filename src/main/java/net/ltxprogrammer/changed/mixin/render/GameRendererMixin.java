package net.ltxprogrammer.changed.mixin.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.tree.effects.PostChainNodeEffect;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.client.ChangedShaders;
import net.ltxprogrammer.changed.client.LocalTransfurVariantInstance;
import net.ltxprogrammer.changed.client.latexparticles.SetupContext;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedAbilityTreeCodecs;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.ModLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private Map<String, ShaderInstance> shaders;

    @Shadow @Final private Camera mainCamera;

    @Shadow public abstract LightTexture lightTexture();

    @Shadow public abstract boolean isPanoramicMode();

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "getNightVisionScale", at = @At("HEAD"), cancellable = true)
    private static void getNightVisionScale(LivingEntity livingEntity, float p_109110_, CallbackInfoReturnable<Float> callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(livingEntity), variant -> {
            if (variant.visionType.test(MobEffects.NIGHT_VISION)) {
                callback.setReturnValue(1.0f);
            }

            if (variant.getBreatheMode().canBreatheWater() && livingEntity.isEyeInFluidType(ForgeMod.WATER_TYPE.get())) {
                callback.setReturnValue(0.85f);
            }
        });
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void bobView(PoseStack pose, float partialTicks, CallbackInfo callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(Minecraft.getInstance().getCameraEntity()), variant -> {
            if (variant.getEntityShape().isLegless())
                callback.cancel();
        });
    }

    @Inject(method = "reloadShaders", at = @At("RETURN"))
    public void reloadChangedShaders(ResourceProvider resourceManager, CallbackInfo callback) {
        if (!ModLoader.isLoadingStateValid()) {
            Changed.LOGGER.error("Refusing to load shaders due to invalid loading state");
            return;
        }

        List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaderInstances = new ArrayList<>();
        try {
            ChangedShaders.reloadShaders(resourceManager, shaderInstances::add);
        } catch (IOException exception) {
            shaderInstances.forEach((instance) -> {
                instance.getFirst().close();
            });
            throw new RuntimeException("could not reload changed shaders", exception);
        }
        shaderInstances.forEach((pair) -> {
            ShaderInstance instance = pair.getFirst();
            this.shaders.put(instance.getName(), instance);
            pair.getSecond().accept(instance);
        });
    }

    @WrapOperation(
            method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getCameraEntity()Lnet/minecraft/world/entity/Entity;"),
            require = 0
    )
    public Entity overrideGrabbedEntity(Minecraft instance, Operation<Entity> original) {
        final var entity = original.call(instance);

        if (entity instanceof LivingEntityDataExtension ext && ext.getGrabbedBy() != null) {
            return AbstractAbility.getAbilityInstanceSafe(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get())
                    .map(ability -> ability.grabbedHasControl ? ability.grabbedEntity : null)
                    .orElseGet(ext::getGrabbedBy);
        }
        else if (entity instanceof LivingEntity livingEntity) {
            return AbstractAbility.getAbilityInstanceSafe(livingEntity, ChangedAbilities.GRAB_ENTITY_ABILITY.get())
                    .<Entity>map(ability -> ability.grabbedHasControl ? ability.grabbedEntity : null)
                    .orElse(entity);
        }

        return entity;
    }

    @Inject(method = "renderItemInHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LightTexture;turnOffLightLayer()V"))
    public void hookFirstPersonParticles(PoseStack pose, Camera camera, float partialTicks, CallbackInfo ci) {
        ChangedClient.particleSystem.getOrThrow().render(pose, this.lightTexture(), camera, partialTicks, null, SetupContext.FIRST_PERSON);
    }

    @WrapOperation(method = "checkEntityPostEffect", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;loadEntityShader(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/GameRenderer;)V", remap = false))
    public void changed$loadDistortionShader(@Nullable Entity entity, GameRenderer gameRenderer, Operation<Void> original) {
        var localPlayer = UniversalDist.getLocalPlayer();
        var variantInstance = ProcessTransfur.getPlayerTransfurVariant(localPlayer);
        if (!(variantInstance instanceof LocalTransfurVariantInstance<?> localVariant) || localVariant.getLastUsedPostChainNode() == null) {
            original.call(entity, gameRenderer);
            return;
        }

        gameRenderer.loadEffect(localVariant.getLastUsedPostChainNode().postChain);
    }
}
