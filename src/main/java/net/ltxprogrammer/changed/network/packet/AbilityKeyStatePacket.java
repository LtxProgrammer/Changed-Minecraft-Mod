package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.KeyReference;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AbilityKeyStatePacket implements ChangedPacket {
    final int id;
    final boolean keyDown;
    final KeyReference key;

    public AbilityKeyStatePacket(Player player, boolean keyDown, KeyReference key) {
        this.id = player.getId();
        this.keyDown = keyDown;
        this.key = key;
    }

    public AbilityKeyStatePacket(FriendlyByteBuf buffer) {
        this.id = buffer.readVarInt();
        this.keyDown = buffer.readBoolean();
        this.key = KeyReference.getNamedKey(buffer.readResourceLocation());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(id);
        buffer.writeBoolean(keyDown);
        buffer.writeResourceLocation(key.getId());
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(level.getEntity(this.id)), (player, variant) -> {
                    context.setPacketHandled(true);
                    if (variant.isTemporaryFromSuit())
                        return;

                    if (variant.abilityHandler.getFlipCount(key) < 6) { // Prevent DoS by limiting flip count / tick
                        variant.abilityHandler.queueKeyState(key, keyDown);
                    }
                });
            });
        }

        else {
            context.setPacketHandled(true);
            final var sender = context.getSender();
            if (sender.getId() != this.id)
                return CompletableFuture.failedFuture(new IllegalArgumentException("Incorrect ID for sending player"));

            return levelFuture.thenAccept(level -> {
                ProcessTransfur.ifPlayerTransfurred(sender, (variant) -> {
                    if (variant.isTemporaryFromSuit())
                        return;

                    if (variant.abilityHandler.getFlipCount(key) < 6) { // Prevent DoS by limiting flip count / tick
                        variant.abilityHandler.queueKeyState(key, keyDown);
                    }

                    Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> sender), this);
                });
            });
        }
    }
}
