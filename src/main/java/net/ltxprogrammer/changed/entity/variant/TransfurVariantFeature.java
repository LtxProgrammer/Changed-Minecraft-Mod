package net.ltxprogrammer.changed.entity.variant;

import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class TransfurVariantFeature {
    public TransfurVariantFeature(Combinator combinator) {
        this.combinator = combinator;
    }

    public static TransfurVariantFeature binary() {
        return new TransfurVariantFeature(Combinator.MAX);
    }

    public static TransfurVariantFeature maxPresent() {
        return new TransfurVariantFeature(Combinator.MAX);
    }

    public static TransfurVariantFeature sumPresent() {
        return new TransfurVariantFeature(Combinator.SUM);
    }

    public enum Combinator {
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
