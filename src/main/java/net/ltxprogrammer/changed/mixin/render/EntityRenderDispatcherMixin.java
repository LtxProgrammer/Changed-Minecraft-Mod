package net.ltxprogrammer.changed.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.PlayerLayerWrapper;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedEntityRenderers;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow private static void renderShadow(PoseStack p_114458_, MultiBufferSource p_114459_, Entity p_114460_, float p_114461_, float p_114462_, LevelReader p_114463_, float p_114464_) {}

    @Shadow public abstract <T extends Entity> EntityRenderer<? super T> getRenderer(T p_114383_);

    @Shadow(remap = false) public abstract Map<String, EntityRenderer<? extends Player>> getSkinMap();

    @Shadow public Map<EntityType<?>, EntityRenderer<?>> renderers;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    public <E extends Entity> void adjustRiderOffset(E entity, double camX, double camY, double camZ, float p_114389_, float p_114390_, PoseStack poseStack, MultiBufferSource buffers, int p_114393_, CallbackInfo callback) {
        if (!(Minecraft.getInstance().cameraEntity instanceof LivingEntity livingEntity) ||
                IAbstractChangedEntity.forEither(livingEntity) == null)
            return;

        if (entity.getVehicle() == Minecraft.getInstance().cameraEntity && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            double yRot = Math.toRadians(Mth.rotLerp(Minecraft.getInstance().getDeltaFrameTime(), livingEntity.yBodyRotO, livingEntity.yBodyRot));
            poseStack.translate(Math.sin(yRot) * 0.5f, 0.0f, -Math.cos(yRot) * 0.5f);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;FFLnet/minecraft/world/level/LevelReader;F)V"))
    public <E extends Entity> void maybeRenderShadow(PoseStack stack, MultiBufferSource blockpos, E entity, float shadowStrength, float partialTicks, LevelReader level, float radius) {
        if (entity instanceof LivingEntityDataExtension ext && ext.getGrabbedBy() != null) {
            var grabAbility = AbstractAbility.getAbilityInstance(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (grabAbility != null && !grabAbility.grabbedHasControl) {
                return;
            }
        } else if (entity instanceof LivingEntity living) {
            var grabAbility = AbstractAbility.getAbilityInstance(living, ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (grabAbility != null && grabAbility.grabbedHasControl) {
                return;
            }
        }

        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(entity), variant -> {
            float morph = variant.getMorphProgression(partialTicks);
            float morphRadius = Mth.lerp(morph, radius, this.getRenderer(variant.getChangedEntity()).shadowRadius);

            renderShadow(stack, blockpos, entity, shadowStrength, partialTicks, level, morphRadius);
        },() -> {
            renderShadow(stack, blockpos, entity, shadowStrength, partialTicks, level, radius);
        });
    }

    @Inject(method = "getRenderer", at = @At("HEAD"), cancellable = true)
    public <E extends Entity> void getRendererOrOverridden(E entity, CallbackInfoReturnable<EntityRenderer<? super E>> callback) {
        ChangedEntityRenderers.getComplexRenderer(entity).ifPresent(callback::setReturnValue);
    }

    @Inject(method = "onResourceManagerReload", at = @At("RETURN"))
    public void copyPlayerLayers(ResourceManager resources, CallbackInfo ci) {
        this.getSkinMap().forEach((name, renderer) -> {
            if (name.equals("default") && renderer instanceof LivingEntityRenderer<?,?> playerRenderer) {
                playerRenderer.layers.stream().filter(PlayerLayerWrapper::isWrappable).forEach(layer -> {
                    ChangedEntityRenderers.getCopyPlayerLayers().forEach(type -> {
                        if (this.renderers.get(type) instanceof AdvancedHumanoidRenderer<?,?,?> advanced)
                            advanced.wrapLayer(layer);
                    });
                });
            }

            if (renderer instanceof LivingEntityRenderer<?,?> playerRenderer) {
                playerRenderer.layers.stream().filter(PlayerLayerWrapper::isWrappable).forEach(layer -> {
                    ChangedEntityRenderers.getComplexRenderers(name).forEach(type -> {
                        if (type instanceof AdvancedHumanoidRenderer<?,?,?> advanced)
                            advanced.wrapLayer(layer);
                    });
                });
            }
        });
    }
}
