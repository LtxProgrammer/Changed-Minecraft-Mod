package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.minecraft.resources.ResourceLocation;

public enum Gender {
    MALE,
    FEMALE;
    
    public static ResourceLocation switchGenderedForm(ResourceLocation form) {
        if (form.getPath().contains("/male")) {
            ResourceLocation newVariantId = new ResourceLocation(form.getNamespace(),
                    form.getPath().replace("/male", "/female"));
            if (LatexVariant.ALL_LATEX_FORMS.containsKey(newVariantId)) {
                return newVariantId;
            }
        }

        else if (form.getPath().contains("/female")) {
            ResourceLocation newVariantId = new ResourceLocation(form.getNamespace(),
                    form.getPath().replace("/female", "/male"));
            if (LatexVariant.ALL_LATEX_FORMS.containsKey(newVariantId)) {
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
