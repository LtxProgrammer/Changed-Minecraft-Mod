package net.ltxprogrammer.changed.entity.animation;

public enum NoParameters implements AnimationParameters {
    INSTANCE;

    @Override
    public AnimationAssociation.Match apply(AnimationAssociation animationSetup) {
        return AnimationAssociation.Match.DEFAULT;
    }
}
