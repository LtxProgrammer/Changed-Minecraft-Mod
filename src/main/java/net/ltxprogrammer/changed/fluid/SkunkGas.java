package net.ltxprogrammer.changed.fluid;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedFluids;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidAttributes;

import java.util.List;

public abstract class SkunkGas extends TransfurGas {
    public static final Properties PROPERTIES = new Properties(
            ChangedFluids.SKUNK_GAS,
            ChangedFluids.SKUNK_GAS_FLOWING,
            FluidAttributes.builder(Changed.modResource("blocks/skunk_gas"), Changed.modResource("blocks/skunk_gas"))
                    .viscosity(200).color(0x7FFFFFFF))
            .explosionResistance(100f)
            .tickRate(4).levelDecreasePerBlock(1)
            .block(ChangedBlocks.SKUNK_GAS);

    protected SkunkGas() {
        super(PROPERTIES, ChangedTransfurVariants.GAS_SKUNK);
    }

    @Override
    public Color3 getColor() {
        return Color3.fromInt(0xb3e53a);
    }

    @Override
    public BlockState createLegacyBlock(FluidState fluidState) {
        return ChangedBlocks.SKUNK_GAS.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(fluidState));
    }

    public static class Flowing extends SkunkGas {
        public Flowing() {
            registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7));
        }

        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends SkunkGas {
        public Source() {}

        public int getAmount(FluidState state) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
