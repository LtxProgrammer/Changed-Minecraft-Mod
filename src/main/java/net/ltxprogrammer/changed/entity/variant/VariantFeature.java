package net.ltxprogrammer.changed.entity.variant;

import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class VariantFeature {
    public VariantFeature(Combinator combinator) {
        this.combinator = combinator;
    }

    /// Feature should either be on or off
    public static VariantFeature binary() {
        return new VariantFeature(Combinator.BINARY);
    }

    /// Feature should take the maximum value present in the trees
    public static VariantFeature maxPresent() {
        return new VariantFeature(Combinator.MAX);
    }

    /// Feature should take the sum of the values present in the trees
    public static VariantFeature sumPresent() {
        return new VariantFeature(Combinator.SUM);
    }

    public enum Combinator {
        BINARY,
        MAX,
        SUM
    }

    public final Combinator combinator;
    @Nullable
    private String descriptionId;

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("transfur_variant_feature", ChangedRegistry.TRANSFUR_VARIANT_FEATURES.getKey(this));
        }

        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public Component getDisplayName() {
        return Component.translatable(this.getDescriptionId());
    }
}
