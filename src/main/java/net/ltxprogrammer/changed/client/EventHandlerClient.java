package net.ltxprogrammer.changed.client;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.client.gui.ContentWarningScreen;
import net.ltxprogrammer.changed.client.renderer.layers.DarkLatexMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.tfanimations.TransfurAnimator;
import net.ltxprogrammer.changed.data.BiListener;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.fluid.AbstractLatexFluid;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.network.packet.QueryTransfurPacket;
import net.ltxprogrammer.changed.network.packet.SyncTransfurProgressPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EventHandlerClient {
    @OnlyIn(Dist.CLIENT)
    public static final BiListener<UUID, Float> PROGRESS_LISTENER = SyncTransfurProgressPacket.SIGNAL.addListener((uuid, progress) -> {
        var player = Minecraft.getInstance().level.getPlayerByUUID(uuid);
        if (player == null)
            return;
        var oldProgress = ProcessTransfur.getPlayerTransfurProgress(player);
        if (Math.abs(oldProgress - progress) < 0.02f) // Prevent sync shudder
            return;
        ProcessTransfur.setPlayerTransfurProgress(player, progress);
    });

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderEntityPre(RenderLivingEvent.Pre<?, ?> event) {
        if (event.getEntity() instanceof LivingEntityDataExtension ext && ext.getGrabbedBy() != null) {
            var grabAbility = AbstractAbility.getAbilityInstance(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (grabAbility != null && !grabAbility.shouldRenderGrabbedEntity()) {
                event.setCanceled(true);
            } else if (grabAbility != null && grabAbility.shouldRenderGrabbedEntity()) {
                // TODO deprecate
            }
            return;
        }

        var entityGrabAbility = AbstractAbility.getAbilityInstance(event.getEntity(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (entityGrabAbility != null && !entityGrabAbility.shouldRenderLatex())
            event.setCanceled(true);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getPlayer();

        if (event.isCanceled())
            return;

        if (player.isDeadOrDying() && player.getLastDamageSource() instanceof ChangedDamageSources.TransfurDamageSource) {
            event.setCanceled(true);
            return;
        }

        if (player.vehicle != null && player.vehicle instanceof SeatEntity seat) {
            if (seat.shouldSeatedBeInvisible()) {
                event.setCanceled(true);
                return;
            }
        }

        if (player instanceof PlayerDataExtension ext && ext.isPlayerMover(PlayerMover.WHITE_LATEX_MOVER.get())) {
            event.setCanceled(true);
            return;
        }

        if (event.getEntity() instanceof LivingEntityDataExtension ext && ext.getGrabbedBy() != null) {
            var grabAbility = AbstractAbility.getAbilityInstance(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (grabAbility != null && grabAbility.suited && !grabAbility.grabbedHasControl) {
                event.setCanceled(true);
                return;
            }
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
            FormRenderHandler.lastPartialTick = event.getPartialTicks();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        if (!(event.getCamera().getBlockAtCamera().getFluidState().getType() instanceof AbstractLatexFluid abstractLatexFluid)) return;

        event.setNearPlaneDistance(0.25F);
        event.setFarPlaneDistance(1.0F);
        event.setCanceled(true);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onFogColors(EntityViewRenderEvent.FogColors event) {
        if (!(event.getCamera().getBlockAtCamera().getFluidState().getType() instanceof AbstractLatexFluid abstractLatexFluid)) return;

        var color = abstractLatexFluid.getLatexType().color;
        event.setRed(color.red());
        event.setGreen(color.green());
        event.setBlue(color.blue());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRespawn(ClientPlayerNetworkEvent.RespawnEvent event) {
        Changed.PACKET_HANDLER.sendToServer(QueryTransfurPacket.Builder.of(event.getNewPlayer()));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onInputEvent(InputEvent.ClickInputEvent event) {
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
    public void onSetScreen(ScreenOpenEvent event) {
        if (event.getScreen() instanceof TitleScreen && Changed.config.client.showContentWarning.get()) {
            // Comment this line out to disable the content warning screen
            event.setScreen(new ContentWarningScreen());
        }
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
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeEventHandler {
        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void onNameFormat(RenderNameplateEvent event) {
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
            if (event.livingEntity.level.isClientSide)
                return;

            if (event.oldVariant == event.newVariant || event.cause == null)
                return;

            final int duration = event.livingEntity.level.getGameRules().getBoolean(ChangedGameRules.RULE_DO_TRANSFUR_ANIMATION) ?
                    (int)(event.cause.getDuration() * 20) : 40;
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
    }
}
