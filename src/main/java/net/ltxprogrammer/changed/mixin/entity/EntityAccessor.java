package net.ltxprogrammer.changed.mixin.entity;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityAccessor {

    @Accessor("forgeFluidTypeOnEyes")
    FluidType getForgeFluidTypeOnEyes();

    @Accessor("forgeFluidTypeOnEyes")
    void setForgeFluidTypeOnEyes(FluidType fluidType);
}
