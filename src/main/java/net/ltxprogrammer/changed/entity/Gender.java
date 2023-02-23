package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.IExtensibleEnum;

public enum Gender implements IExtensibleEnum {
    MALE,
    FEMALE;

    public static Gender create(String name) {
        throw new IllegalStateException("Enum not extended");
    }
    
    public static ResourceLocation switchGenderedForm(ResourceLocation form) {
        if (form.getPath().contains("/male")) {
            ResourceLocation newVariantId = new ResourceLocation(form.getNamespace(),
                    form.getPath().replace("/male", "/female"));
            if (ChangedRegistry.LATEX_VARIANT.get().containsKey(newVariantId)) {
                return newVariantId;
            }
        }

        else if (form.getPath().contains("/female")) {
            ResourceLocation newVariantId = new ResourceLocation(form.getNamespace(),
                    form.getPath().replace("/female", "/male"));
            if (ChangedRegistry.LATEX_VARIANT.get().containsKey(newVariantId)) {
                return newVariantId;
            }
        }

        return form;
    }

    public static ResourceLocation getGenderedForm(ResourceLocation form, Gender gender) {
        return new ResourceLocation(form.toString() + "/" + gender.toString().toLowerCase());
    }

    public ResourceLocation convertToGendered(ResourceLocation formId) {
        return getGenderedForm(formId, this);
    }
}
