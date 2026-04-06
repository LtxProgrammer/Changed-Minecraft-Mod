package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.IExtensibleEnum;

public enum Gender implements IExtensibleEnum, StringRepresentable {
    MALE("male"),
    FEMALE("female");

    private final String serialName;

    Gender(String serialName) {
        this.serialName = serialName;
    }

    @Override
    public String getSerializedName() {
        return serialName;
    }

    public static Gender create(String name, String serialName) {
        throw new IllegalStateException("Enum not extended");
    }

    /**
     * @deprecated use {@link net.ltxprogrammer.changed.init.ChangedTransfurVariants.Gendered#getOpposite(TransfurVariant)}
     */
    @Deprecated
    public static ResourceLocation switchGenderedForm(ResourceLocation form) {
        if (form.getPath().contains("/male")) {
            ResourceLocation newVariantId = ResourceLocation.fromNamespaceAndPath(form.getNamespace(),
                    form.getPath().replace("/male", "/female"));
            if (ChangedRegistry.TRANSFUR_VARIANT.get().containsKey(newVariantId)) {
                return newVariantId;
            }
        }

        else if (form.getPath().contains("/female")) {
            ResourceLocation newVariantId = ResourceLocation.fromNamespaceAndPath(form.getNamespace(),
                    form.getPath().replace("/female", "/male"));
            if (ChangedRegistry.TRANSFUR_VARIANT.get().containsKey(newVariantId)) {
                return newVariantId;
            }
        }

        return form;
    }

    public static ResourceLocation getGenderedForm(ResourceLocation form, Gender gender) {
        return ResourceLocation.fromNamespaceAndPath(form.getNamespace(), form.getPath() + "/" + gender.getSerializedName());
    }

    public ResourceLocation convertToGendered(ResourceLocation formId) {
        return getGenderedForm(formId, this);
    }
}
