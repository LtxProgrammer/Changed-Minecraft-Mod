package net.ltxprogrammer.changed.fluid;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedFluids;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.List;

public abstract class WhiteLatexFluid extends AbstractLatexFluid {
    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
            ChangedFluids.WHITE_LATEX::get,
            ChangedFluids.WHITE_LATEX_FLOWING::get,
            FluidAttributes.builder(ChangedBlocks.textureOf(ChangedBlocks.WHITE_LATEX_BLOCK), ChangedBlocks.textureOf(ChangedBlocks.WHITE_LATEX_BLOCK)))
            .tickRate(50)
            .levelDecreasePerBlock(3)
            .explosionResistance(100f)
            .bucket(ChangedItems.WHITE_LATEX_BUCKET).block(() -> (LiquidBlock) ChangedBlocks.WHITE_LATEX_FLUID.get());

    public WhiteLatexFluid() { super(PROPERTIES, List.of(LatexVariant.WHITE_LATEX_WOLF)); }

    @Override
    public Vec3 getFlow(BlockGetter world, BlockPos pos, FluidState fluidstate) {
        return super.getFlow(world, pos, fluidstate).scale(-1);
    }


    public BlockState createLegacyBlock(FluidState p_76466_) {
        return ChangedBlocks.WHITE_LATEX_FLUID.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(p_76466_));
    }

    @Override
    public boolean canEntityStandOn(LivingEntity entity) {
        var variant = LatexVariant.getEntityVariant(entity);
        return variant != null && variant.getLatexType() == LatexType.WHITE_LATEX;
    }

    public static class Source extends WhiteLatexFluid {
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

    public static class Flowing extends WhiteLatexFluid {
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
