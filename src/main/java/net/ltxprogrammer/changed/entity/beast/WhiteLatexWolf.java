package net.ltxprogrammer.changed.entity.beast;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class WhiteLatexWolf extends WhiteLatexEntity {
    public WhiteLatexWolf(EntityType<? extends WhiteLatexWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
        return builder;
    }
}
