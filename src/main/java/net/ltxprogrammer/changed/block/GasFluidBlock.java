package net.ltxprogrammer.changed.block;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;

import java.util.function.Supplier;

public class GasFluidBlock extends LiquidBlock {
    public GasFluidBlock(Supplier<? extends FlowingFluid> fluid) {
        super(fluid, Properties.of(Material.WATER).strength(100f));
    }
}