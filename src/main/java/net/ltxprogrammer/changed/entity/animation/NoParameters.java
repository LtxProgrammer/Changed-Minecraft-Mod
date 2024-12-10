package net.ltxprogrammer.changed.entity.animation;

public enum NoParameters implements AnimationParameters {
    INSTANCE;

    @Override
    public AnimationAssociation.Match matchesAssociation(AnimationAssociation animationSetup) {
        return AnimationAssociation.Match.DEFAULT;
    }
}
