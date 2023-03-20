package net.ltxprogrammer.changed.process;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.Emote;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.network.packet.EmotePacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class ProcessEmote {
    private static void rawEmote(LivingEntity entity, Emote emote) {
        entity.level.addParticle(ChangedParticles.emote(emote), entity.getX(), entity.getY() + entity.getDimensions(entity.getPose()).height + 0.65, entity.getZ(),
                0, 0, 0);
    }

    public static void playerEmote(Player player, Emote emote) {
        if (!player.level.isClientSide) {
            Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new EmotePacket(player.getUUID(), emote));
        } else {
            ProcessTransfur.ifPlayerLatex(player, variant -> {
                ProcessEmote.rawEmote(variant.getLatexEntity(), emote);
            }, () -> {
                ProcessEmote.rawEmote(player, emote);
            });
        }
    }
}
