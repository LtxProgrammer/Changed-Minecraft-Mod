package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientTransfurVariantInstance<T extends ChangedEntity> extends TransfurVariantInstance<T> {
    private final AbstractClientPlayer host;

    public ClientTransfurVariantInstance(TransfurVariant<T> parent, AbstractClientPlayer host) {
        super(parent, host);
        this.host = host;
    }
}
