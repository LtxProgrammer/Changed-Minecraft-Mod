package net.ltxprogrammer.changed.entity.robot;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public interface WearableExoskeleton {
    Multimap<Attribute, AttributeModifier> getAttributeModifiers();

    default float getJumpStrengthMultiplier() {
        return 1f;
    }

    default float getFallDamageMultiplier() {
        return 1f;
    }
}
