package net.ltxprogrammer.changed.entity;

import net.minecraft.resources.ResourceLocation;

public enum Gender {
    MALE,
    FEMALE;

    public static ResourceLocation getGenderedForm(ResourceLocation form, Gender gender) {
        return new ResourceLocation(form.toString() + "/" + gender.toString().toLowerCase());
    }

    public ResourceLocation convertToGendered(ResourceLocation formId) {
        return getGenderedForm(formId, this);
    }
}
