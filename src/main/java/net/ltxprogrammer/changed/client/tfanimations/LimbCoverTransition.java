package net.ltxprogrammer.changed.client.tfanimations;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public enum LimbCoverTransition {
    /**
     * This transition indicates the part should instantly be covered
     */
    INSTANT(Changed.modResource("instant")),
    /**
     * This transition indicates the part should fade to the latex cover
     */
    FADE(Changed.modResource("fade")),
    /**
     * This transition indicates the part should cover, with the latex crawling from the torso to the end of the limb
     */
    COVER_START(Changed.modResource("cover_start"), true, false, true, true),
    /**
     * This transition indicates the part should cover, with the latex crawling from the end of the limb to the torso
     */
    COVER_END(Changed.modResource("cover_end"), true, false, true, true),
    /**
     * This transition indicates the part should cover, with the latex crawling from any point on the limb (designer's choice), spreading out
     */
    COVER_ATTACK(Changed.modResource("cover_attack"), true, true, true, true),
    /**
     * This transition is for the torso, latex spreads from the head, down to the other limbs
     */
    COVER_FROM_HEAD(Changed.modResource("cover_from_head"), false, true, false, false),
    /**
     * This transition is for the torso, latex spreads from the left arm, down to the other limbs
     */
    COVER_FROM_LEFT_ARM(Changed.modResource("cover_from_left_arm"), false, true, false, false),
    /**
     * This transition is for the torso, latex spreads from the right arm, down to the other limbs
     */
    COVER_FROM_RIGHT_ARM(Changed.modResource("cover_from_right_arm"), false, true, false, false),
    /**
     * This transition is for the torso, latex spreads from the left leg, up to the other limbs
     */
    COVER_FROM_LEFT_LEG(Changed.modResource("cover_from_left_leg"), false, true, false, false),
    /**
     * This transition is for the torso, latex spreads from the right leg, up to the other limbs
     */
    COVER_FROM_RIGHT_LEG(Changed.modResource("cover_from_right_leg"), false, true, false, false),
    /**
     * This transition is for the torso, latex spreads from the arms, down to the other limbs
     */
    COVER_FROM_ARMS(Changed.modResource("cover_from_arms"), false, true, false, false),
    /**
     * This transition is for the torso, latex spreads from the legs, down to the other limbs
     */
    COVER_FROM_LEGS(Changed.modResource("cover_from_legs"), false, true, false, false);

    public static final Codec<LimbCoverTransition> CODEC = ResourceLocation.CODEC.xmap(LimbCoverTransition::getFromTransition, LimbCoverTransition::getTransition);
    public static final ResourceLocation FULL_COVER = Changed.modResource("textures/models/latex_cover/full.png");
    private static final int FRAME_COUNT = 8;

    private final ResourceLocation transition;
    private final boolean validForHead;
    private final boolean validForTorso;
    private final boolean validForArm;
    private final boolean validForLeg;

    LimbCoverTransition(ResourceLocation transition) {
        this(transition, true, true, true, true);
    }

    LimbCoverTransition(ResourceLocation transition, boolean validForHead, boolean validForTorso, boolean validForArm, boolean validForLeg) {
        this.transition = transition;
        this.validForArm = validForArm;
        this.validForTorso = validForTorso;
        this.validForHead = validForHead;
        this.validForLeg = validForLeg;
    }

    public ResourceLocation getTransition() {
        return transition;
    }

    public static LimbCoverTransition getFromTransition(ResourceLocation transition) {
        for (var t : values()) {
            if (t.transition.equals(transition))
                return t;
        }

        return null;
    }

    public boolean isValidForHead() {
        return validForHead;
    }

    public boolean isValidForTorso() {
        return validForTorso;
    }

    public boolean isValidForArm() {
        return validForArm;
    }

    public boolean isValidForLeg() {
        return validForLeg;
    }

    public ResourceLocation getTextureForProgress(float progress) {
        if (progress >= 1f)
            return FULL_COVER;
        if (this == INSTANT || this == FADE)
            return FULL_COVER;

        int index = Mth.floor(progress * FRAME_COUNT);
        return new ResourceLocation(transition.getNamespace(), "textures/models/latex_cover/" + transition.getPath() + "/" + index + ".png");
    }

    public float getAlphaForProgress(float progress) {
        if (this == FADE)
            return progress;
        else
            return 1f;
    }
}
