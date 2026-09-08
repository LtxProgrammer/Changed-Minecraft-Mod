package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.client.player.RemotePlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;

@OnlyIn(Dist.CLIENT)
public class RemoteTransfurVariantInstance<T extends ChangedEntity> extends ClientTransfurVariantInstance<T> {
    private final RemotePlayer host;

    public RemoteTransfurVariantInstance(TransfurVariant<T> parent, RemotePlayer host) {
        super(parent, host);
        this.host = host;
    }

    @Override
    protected void tickBreathing(LivingBreatheEvent event) {
        super.tickBreathing(event);

        if (!event.canBreathe() && host.isCreative()) {
            event.setCanBreathe(true); // Fix that pesky visual bug
        }
    }
}
