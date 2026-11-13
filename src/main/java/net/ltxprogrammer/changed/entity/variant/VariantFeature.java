package net.ltxprogrammer.changed.entity.variant;

import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class VariantFeature {
    public VariantFeature(Combinator combinator, boolean bad) {
        this.combinator = combinator;
        this.negative = bad;
    }

    /// Feature should either be on or off. It is considered an upside to have this feature.
    public static VariantFeature binary() {
        return new VariantFeature(Combinator.BINARY, false);
    }

    /// Feature should either be on or off. It is considered a downside to have this feature.
    public static VariantFeature binaryBad() {
        return new VariantFeature(Combinator.BINARY, true);
    }

    /// Feature should take the maximum value present in the trees
    public static VariantFeature maxPresent() {
        return new VariantFeature(Combinator.MAX, false);
    }

    public static VariantFeature maxPresentBad() {
        return new VariantFeature(Combinator.MAX, true);
    }

    /// Feature should take the sum of the values present in the trees
    public static VariantFeature sumPresent() {
        return new VariantFeature(Combinator.SUM, false);
    }
    public static VariantFeature sumPresentBad() {
        return new VariantFeature(Combinator.SUM, true);
    }

    public enum Combinator {
        BINARY,
        MAX,
        SUM
    }

    public final Combinator combinator;
    public final boolean negative;
    @Nullable
    private String descriptionIdGive;
    @Nullable
    private String descriptionIdTake;

    protected String getOrCreateDescriptionId(boolean giving) {
        if (this.descriptionIdGive == null) {
            var key = ChangedRegistry.TRANSFUR_VARIANT_FEATURES.getKey(this);
            if (this.combinator == Combinator.BINARY) {
                this.descriptionIdGive = Util.makeDescriptionId("transfur_variant_feature", key) + ".give";
                this.descriptionIdTake = Util.makeDescriptionId("transfur_variant_feature", key) + ".take";
            } else {
                this.descriptionIdGive = Util.makeDescriptionId("transfur_variant_feature", key);
                this.descriptionIdTake = this.descriptionIdGive;
            }
        }

        return giving ? this.descriptionIdGive : this.descriptionIdTake;
    }

    public String getDescriptionId(boolean giving) {
        return this.getOrCreateDescriptionId(giving);
    }

    public Component getDisplayName(boolean giving) {
        return Component.translatable(this.getDescriptionId(giving));
    }

    @Override
    public String toString() {
        return ChangedRegistry.TRANSFUR_VARIANT_FEATURES.getKey(this).toString();
    }
}
