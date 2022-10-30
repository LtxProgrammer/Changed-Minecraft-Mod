package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.block.entity.CardboardBoxBlockEntity;
import net.ltxprogrammer.changed.data.Listener;
import net.ltxprogrammer.changed.network.packet.SyncTransfurProgressPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EventHandlerClient {
    @OnlyIn(Dist.CLIENT)
    public static final Listener<ProcessTransfur.TransfurProgress> PROGRESS_LISTENER = SyncTransfurProgressPacket.SIGNAL.addListener(progress -> {
        var player = Minecraft.getInstance().player;
        Objects.requireNonNull(player);
        ProcessTransfur.setPlayerTransfurProgress(player, progress);
    });

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Player player = event.getPlayer();

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
}
