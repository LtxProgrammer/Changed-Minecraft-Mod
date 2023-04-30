package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.entity.CardboardBoxBlockEntity;
import net.ltxprogrammer.changed.data.BiListener;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.network.packet.QueryTransfurPacket;
import net.ltxprogrammer.changed.network.packet.SyncTransfurProgressPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EventHandlerClient {
    @OnlyIn(Dist.CLIENT)
    public static final BiListener<UUID, ProcessTransfur.TransfurProgress> PROGRESS_LISTENER = SyncTransfurProgressPacket.SIGNAL.addListener((uuid, progress) -> {
        var player = Minecraft.getInstance().level.getPlayerByUUID(uuid);
        if (player == null)
            return;
        var oldProgress = ProcessTransfur.getPlayerTransfurProgress(player);
        if (Math.abs(oldProgress.ticks() - progress.ticks()) < 20 && oldProgress.type().equals(progress.type())) // Prevent sync shudder
            return;
        ProcessTransfur.setPlayerTransfurProgress(player, progress);
    });

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getPlayer();

        if (player.isDeadOrDying() && player.getLastDamageSource() instanceof ChangedDamageSources.TransfurDamageSource) {
            event.setCanceled(true);
            return;
        }

        if (player.vehicle != null && player.vehicle instanceof CardboardBoxBlockEntity.EntityContainer container) {
            if (player.isInvisible()) {
                event.setCanceled(true);
                return;
            }
        }

        if (!player.isRemoved() && !player.isSpectator()) {
            if (ProcessTransfur.isPlayerLatex(player)) {
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
    public void onRespawn(ClientPlayerNetworkEvent.RespawnEvent event) {
        Changed.PACKET_HANDLER.sendToServer(QueryTransfurPacket.Builder.of(event.getNewPlayer()));
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeEventHandler {
        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void onRenderNameplateEvent(RenderNameplateEvent event) {
            if (event.getEntity() instanceof Player player) // Can't believe this is all it takes
                event.setContent(PatreonBenefits.getPlayerName(player));
        }

        @SubscribeEvent
        public static void onChangedVariant(ProcessTransfur.EntityVariantAssigned.ChangedVariant event) {
            if (event.livingEntity instanceof Player player && player.isCreative())
                return; // Don't do effect if player is creative mode

            if (event.oldVariant != null || event.newVariant == null || event.livingEntity.tickCount < 20)
                return; // Only do effect if player was human

            event.livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 10 * 20, 1, false, false));
            if (event.newVariant.getEntityType().is(ChangedTags.EntityTypes.ORGANIC_LATEX))
                return; // Only do blindness if variant is goo

            event.livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 1, false, false));
        }
    }
}
