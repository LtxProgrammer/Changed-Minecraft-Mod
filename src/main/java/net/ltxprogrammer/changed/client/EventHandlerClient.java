package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.client.gui.ContentWarningScreen;
import net.ltxprogrammer.changed.client.renderer.layers.DarkLatexMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.tfanimations.TransfurAnimator;
import net.ltxprogrammer.changed.effect.particle.EmoteParticle;
import net.ltxprogrammer.changed.effect.particle.GasParticle;
import net.ltxprogrammer.changed.effect.particle.LatexDripParticle;
import net.ltxprogrammer.changed.effect.particle.TscSweepParticle;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.latex.IClientLatexTypeExtensions;
import net.ltxprogrammer.changed.fluid.AbstractLatexFluid;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.network.packet.QueryTransfurPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverHitResult;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EventHandlerClient {
    private boolean shouldEntityBeRendered(LivingEntity entity) {
        if (entity instanceof LivingEntityDataExtension ext && ext.getGrabbedBy() != null) {
            var grabAbility = AbstractAbility.getAbilityInstance(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (grabAbility != null && !grabAbility.shouldRenderGrabbedEntity())
                return false;
        }

        var entityGrabAbility = AbstractAbility.getAbilityInstance(entity, ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (entityGrabAbility != null && !entityGrabAbility.shouldRenderLatex())
            return false;
        if (entity.isDeadOrDying() && entity.getLastDamageSource() != null && entity.getLastDamageSource().is(ChangedTags.DamageTypes.IS_TRANSFUR))
            return false;
        if (entity.getVehicle() instanceof SeatEntity seat && seat.shouldSeatedBeInvisible())
            return false;

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderEntityPre(RenderLivingEvent.Pre<?, ?> event) {
        if (!this.shouldEntityBeRendered(event.getEntity()))
            event.setCanceled(true);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getEntity();

        if (event.isCanceled())
            return;
        if (!this.shouldEntityBeRendered(event.getEntity())) {
            event.setCanceled(true);
            return;
        }

        if (player instanceof PlayerDataExtension ext && ext.isPlayerMover(PlayerMover.LATEX_SWIM.get())) {
            event.setCanceled(true);
            return;
        }

        if (!player.isRemoved() && !player.isSpectator() && !TransfurAnimator.shouldRenderHuman()) {
            if (RenderOverride.renderOverrides(player, null, event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getPartialTick()))
                event.setCanceled(true);
            else if (ProcessTransfur.isPlayerTransfurred(player)) {
                event.setCanceled(true);
                FormRenderHandler.renderForm(player, event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getPartialTick());
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if(!mc.player.isRemoved()) //we need to cache this as the hand may be rendered even in the death screen.
        {
            FormRenderHandler.lastPartialTick = event.getPartialTick();
        }
    }

    @SubscribeEvent
    public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ChangedParticles.DRIPPING_LATEX.get(), LatexDripParticle.Provider::new);
        event.registerSpriteSet(ChangedParticles.GAS.get(), GasParticle.Provider::new);
        event.registerSpriteSet(ChangedParticles.EMOTE.get(), EmoteParticle.Provider::new);
        event.registerSpriteSet(ChangedParticles.TSC_SWEEP_ATTACK.get(), TscSweepParticle.Provider::new);
    }

    @SubscribeEvent
    public static void onRegisterModelRenderTypes(RegisterNamedRenderTypesEvent event) {
        event.register("emissive", RenderType.cutout(), RenderType.eyes(TextureAtlas.LOCATION_BLOCKS));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderFog(ViewportEvent.RenderFog event) {
        if (!(event.getCamera().getBlockAtCamera().getFluidState().getType() instanceof AbstractLatexFluid abstractLatexFluid)) return;

        event.setNearPlaneDistance(0.25F);
        event.setFarPlaneDistance(1.0F);
        event.setCanceled(true);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onFogColors(ViewportEvent.ComputeFogColor event) {
        if (!(event.getCamera().getBlockAtCamera().getFluidState().getType() instanceof AbstractLatexFluid abstractLatexFluid)) return;

        var color = IClientLatexTypeExtensions.of(abstractLatexFluid.getLatexType()).getColor();
        event.setRed(color.red());
        event.setGreen(color.green());
        event.setBlue(color.blue());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRespawn(ClientPlayerNetworkEvent.Clone event) {
        Changed.PACKET_HANDLER.sendToServer(QueryTransfurPacket.Builder.of(event.getNewPlayer()));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onInputEvent(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isAttack() || event.isUseItem()) {
            LocalPlayer localPlayer = Minecraft.getInstance().player;

            ProcessTransfur.ifPlayerTransfurred(localPlayer, variant -> {
                variant.ifHasAbility(ChangedAbilities.GRAB_ENTITY_ABILITY.get(), ability -> {
                    if (ability.grabbedEntity != null && !ability.suited) {
                        event.setCanceled(true);
                        event.setSwingHand(false);
                    }
                });
            });

            GrabEntityAbility.getGrabberSafe(localPlayer).flatMap(entity -> entity.getAbilityInstanceSafe(ChangedAbilities.GRAB_ENTITY_ABILITY.get()))
                    .ifPresent(ability -> {
                        if (ability.grabbedHasControl) return;

                        event.setCanceled(true);
                        event.setSwingHand(false);
                    });
        }
    }

    /**
     * This function needs to be static
     * @param event
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRegisterReloadListenerEvent(RegisterClientReloadListenersEvent event) {
        ChangedClient.registerReloadListeners(event::registerReloadListener);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onSetScreen(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof TitleScreen) {
            if (GlUtil.getOpenGLVersion().contains("Mesa")) {
                Changed.LOGGER.warn("Mesa graphics driver detected, certain visual features will be disabled");
                Changed.config.client.renderDripParticlesWithNormal.set(false);
            }

            if (Changed.config.client.showContentWarning.get()) {
                // Comment this line out to disable the content warning screen
                event.setNewScreen(new ContentWarningScreen());
            }
        }
    }

    public static <T extends LivingEntity, M extends EntityModel<T>, R extends LivingEntityRenderer<T, M>> void addLatexParticles(EntityRenderersEvent.AddLayers event, EntityType<T> entityType) {
        R renderer = event.getRenderer(entityType);
        if (renderer != null)
            renderer.addLayer(new LatexParticlesLayer<>(renderer, renderer.getModel()));
        else
            Changed.LOGGER.warn("Renderer not present for {} in AddLayers event", entityType);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void addChangedLayers(EntityRenderersEvent.AddLayers event) {
        event.getSkins().stream().map(name -> Pair.of(name, event.getSkin(name))).forEach(pair -> {
            if (pair.getSecond() instanceof PlayerRenderer renderer) {
                renderer.addLayer(new DarkLatexMaskLayer<>(renderer, event.getEntityModels()));
                renderer.addLayer(new GasMaskLayer<>(renderer, event.getEntityModels()));
            }
        });
        addLatexParticles(event, EntityType.BEE);
        addLatexParticles(event, EntityType.RABBIT);
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeEventHandler {
        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void onNameFormat(RenderNameTagEvent event) {
            if (event.getEntity() instanceof ChangedEntity changedEntity && changedEntity.getUnderlyingPlayer() != null) {
                if (!Changed.config.server.showTFNametags.get()) {
                    event.setResult(Event.Result.DENY);
                    return;
                }

                var variant = ProcessTransfur.getPlayerTransfurVariant(changedEntity.getUnderlyingPlayer());
                if (variant != null && variant.isTransfurring()) {
                    event.setResult(Event.Result.DENY);
                    return;
                }

                event.setContent(PatreonBenefits.getPlayerName(changedEntity.getUnderlyingPlayer()));
            } else if (event.getEntity() instanceof Player player) {
                event.setContent(PatreonBenefits.getPlayerName(player, event.getContent()));
            }
        }

        @SubscribeEvent
        public static void onChangedVariant(ProcessTransfur.EntityVariantAssigned.ChangedVariant event) {
            if (event.livingEntity.level().isClientSide)
                return;

            if (event.oldVariant == event.newVariant || event.context == null)
                return;

            final int duration = event.livingEntity.level().getGameRules().getBoolean(ChangedGameRules.RULE_DO_TRANSFUR_ANIMATION) ?
                    (int)(event.context.cause().getDuration() * 20) : 40;
            event.livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 4, false, false));

            if (event.oldVariant != null || event.livingEntity.tickCount < 20)
                return; // Only do effect if player was human

            if (event.livingEntity instanceof Player player && player.isCreative())
                return; // Don't do effect if player is creative mode

            event.livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, 1, false, false));
            if (!event.newVariant.getEntityType().is(ChangedTags.EntityTypes.LATEX))
                return; // Only do blindness if variant is goo

            event.livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, duration, 1, false, false));
        }

        @SubscribeEvent
        public static void onRenderBlockHighlight(RenderHighlightEvent.Block event) {
            if (event.getTarget() instanceof LatexCoverHitResult)
                event.setCanceled(true);

            final var level = Minecraft.getInstance().level;
            final var getter = LatexCoverGetter.wrap(level);
            final var blockPos = event.getTarget().getBlockPos();

            LatexCoverState state = LatexCoverState.getAt(level, blockPos);
            if (!state.isAir() && level.getWorldBorder().isWithinBounds(blockPos)) {
                VertexConsumer bufferBuilder = event.getMultiBufferSource().getBuffer(RenderType.lines());
                Vec3 vec3 = event.getCamera().getPosition();
                double d0 = vec3.x();
                double d1 = vec3.y();
                double d2 = vec3.z();

                LevelRenderer.renderVoxelShape(event.getPoseStack(), bufferBuilder, state.getShape(getter, blockPos, CollisionContext.of(event.getCamera().getEntity())),
                        (double)blockPos.getX() - d0, (double)blockPos.getY() - d1, (double)blockPos.getZ() - d2, 0.0F, 0.0F, 0.0F, 0.4F, false);
            }
        }
    }
}
