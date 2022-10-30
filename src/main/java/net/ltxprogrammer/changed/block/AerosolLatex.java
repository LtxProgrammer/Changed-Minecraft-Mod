package net.ltxprogrammer.changed.block;


import net.ltxprogrammer.changed.init.ChangedFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;

public class AerosolLatex extends LiquidBlock {
    public AerosolLatex() {
        super(() -> (FlowingFluid)ChangedFluids.AEROSOL_LATEX.get(), BlockBehaviour.Properties.of(Material.WATER).strength(100f));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }
}