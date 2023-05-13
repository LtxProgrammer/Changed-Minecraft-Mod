package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.init.ChangedFluids;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;

public class DarkLatexFluidBlock extends AbstractLatexFluidBlock {
    public DarkLatexFluidBlock() {
        super(() -> (FlowingFluid)ChangedFluids.DARK_LATEX.get(), BlockBehaviour.Properties.of(Material.WATER).strength(100f));
    }
}