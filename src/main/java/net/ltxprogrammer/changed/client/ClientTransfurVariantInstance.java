package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.client.animations.AnimationCategory;
import net.ltxprogrammer.changed.client.animations.AnimationInstance;
import net.ltxprogrammer.changed.client.tfanimations.TransfurAnimations;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientTransfurVariantInstance<T extends ChangedEntity> extends TransfurVariantInstance<T> {
    private final AbstractClientPlayer host;

    public ClientTransfurVariantInstance(TransfurVariant<T> parent, AbstractClientPlayer host) {
        super(parent, host);
        this.host = host;
    }

    public void prepareForRender(float partialTicks) {
        AnimationInstance tfAnimation = ((ClientLivingEntityExtender)host).getAnimation(AnimationCategory.TRANSFUR);
        if (transfurProgression < 1f && tfAnimation == null) {
            tfAnimation = new AnimationInstance(TransfurAnimations.getAnimationFromCause(this.transfurContext.cause), host);
            if (this.transfurContext.source != null)
                tfAnimation.addEntity(this.transfurContext.source.getEntity());

            ((ClientLivingEntityExtender)host).addAnimation(AnimationCategory.TRANSFUR, tfAnimation);
        }

        if (tfAnimation != null)
            tfAnimation.setTime(this.getTransfurProgression(partialTicks) * this.transfurContext.cause.getDuration());
    }
}
