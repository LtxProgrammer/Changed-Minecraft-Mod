package net.ltxprogrammer.changed.fluid;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public abstract class WhiteLatexFluid extends AbstractLatexFluid {
    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
            ChangedFluids.WHITE_LATEX_FLUID, ChangedFluids.WHITE_LATEX, ChangedFluids.WHITE_LATEX_FLOWING)
            .tickRate(50)
            .levelDecreasePerBlock(3)
            .explosionResistance(100f)
            .bucket(ChangedItems.WHITE_LATEX_BUCKET)
            .block(ChangedBlocks.WHITE_LATEX_FLUID);

    public static FluidType createFluidType() {
        return new FluidType(AbstractLatexFluid.createProperties().descriptionId("white_latex")) {
            @Override
            public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                consumer.accept(new IClientFluidTypeExtensions() {
                    private static final ResourceLocation WHITE_LATEX_STILL = Changed.modResource("block/white_latex_block_top");
                    private static final ResourceLocation WHITE_LATEX_FLOW = Changed.modResource("block/white_latex_fluid_flow");
                    private static final ResourceLocation WHITE_LATEX_OVERLAY = Changed.modResource("block/white_latex_fluid_overlay");
                    private static final ResourceLocation WHITE_LATEX_RENDER_OVERLAY = Changed.modResource("textures/block/white_latex_fluid_flow.png");

                    public ResourceLocation getStillTexture() {
                        return WHITE_LATEX_STILL;
                    }

                    public ResourceLocation getFlowingTexture() {
                        return WHITE_LATEX_FLOW;
                    }

                    @Override
                    public @Nullable ResourceLocation getOverlayTexture() {
                        return WHITE_LATEX_OVERLAY;
                    }

                    @Override
                    public @Nullable ResourceLocation getRenderOverlayTexture(Minecraft mc) {
                        return WHITE_LATEX_RENDER_OVERLAY;
                    }
                });
            }

            @Override
            public boolean canDrownIn(LivingEntity entity) {
                return !ChangedLatexTypes.DARK_LATEX.get().isFriendlyTo(LatexType.getEntityLatexType(entity));
            }
        };
    }

    public WhiteLatexFluid() {
        super(PROPERTIES, ChangedLatexTypes.WHITE_LATEX, List.of(ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF));
    }

    public BlockState createLegacyBlock(FluidState p_76466_) {
        return ChangedBlocks.WHITE_LATEX_FLUID.get().defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(p_76466_));
    }

    @Override
    public boolean canEntityStandOn(LivingEntity entity) {
        return this.getLatexType().isFriendlyTo(LatexType.getEntityLatexType(entity));
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
