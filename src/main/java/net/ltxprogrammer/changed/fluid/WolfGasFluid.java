package net.ltxprogrammer.changed.fluid;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedFluids;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Changed.MODID)
public abstract class WolfGasFluid extends AbstractLatexFluid {
    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
            ChangedFluids.WOLF_GAS::get,
            ChangedFluids.WOLF_GAS_FLOWING::get,
            FluidAttributes.builder(ChangedBlocks.textureOf(ChangedBlocks.AEROSOL_LATEX), ChangedBlocks.textureOf(ChangedBlocks.AEROSOL_LATEX))
            .gaseous().density(-1024).viscosity(512)).explosionResistance(100f)
            .bucket(ChangedItems.WOLF_GAS_BUCKET).block(() -> (LiquidBlock) ChangedBlocks.AEROSOL_LATEX.get());

    private WolfGasFluid() {
        super(PROPERTIES, LatexVariant.AEROSOL_LATEX_WOLF);
    }

    public BlockState createLegacyBlock(FluidState p_76466_) {
        return ChangedBlocks.AEROSOL_LATEX.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(p_76466_));
    }

    public static class Source extends WolfGasFluid {
        public Source() {
            super();
        }

        public int getAmount(FluidState state) {
            return 8;
        }

        public boolean isSource(FluidState state) {
            return true;
        }
    }

    public static class Flowing extends WolfGasFluid {
        public Flowing() {
            super();
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
}
