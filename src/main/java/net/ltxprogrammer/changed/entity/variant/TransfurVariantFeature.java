package net.ltxprogrammer.changed.entity.variant;

import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public class TransfurVariantFeature {
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
