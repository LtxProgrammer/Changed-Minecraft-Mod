package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.ability.active.GrabEntityAbility;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.world.inventory.AbilityRadialMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AbilitySelectMenuRequestPacket implements ChangedPacket {
    public static final AbilitySelectMenuRequestPacket INSTANCE = new AbilitySelectMenuRequestPacket();

    private AbilitySelectMenuRequestPacket() {}

    public static AbilitySelectMenuRequestPacket read(FriendlyByteBuf buffer) {
        return INSTANCE;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {}

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
            context.setPacketHandled(true);
            final var sender = context.getSender();

            return levelFuture.thenAccept(level -> {
                var grabber = GrabEntityAbility.getGrabber(sender);
                if (grabber != null) {
                    if (grabber.getAbilityInstanceSafe(ChangedAbilities.GRAB_ENTITY_ABILITY.get())
                            .map(ability -> ability.grabbedHasControl).orElse(false)) {
                        grabber.getEntity().interact(sender, InteractionHand.MAIN_HAND);
                        return;
                    }
                }

                var variant = ProcessTransfur.getPlayerTransfurVariant(sender);
                if (variant != null && !variant.isTemporaryFromSuit()) {
                    if (!sender.isUsingItem())
                        sender.openMenu(new SimpleMenuProvider((id, inventory, givenPlayer) ->
                                new AbilityRadialMenu(id, inventory, null), AbilityRadialMenu.CONTAINER_TITLE));
                }
            });
        }

        return CompletableFuture.failedFuture(makeIllegalSideException(context.getDirection().getReceptionSide(), LogicalSide.SERVER));
    }
}
