package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedFluids;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;

public class DarkLatexFluid extends LiquidBlock {
    public DarkLatexFluid() {
        super(() -> (FlowingFluid)ChangedFluids.DARK_LATEX.get(), BlockBehaviour.Properties.of(Material.WATER).strength(100f));
    }
}