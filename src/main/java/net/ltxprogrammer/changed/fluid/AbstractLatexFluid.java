package net.ltxprogrammer.changed.fluid;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public abstract class AbstractLatexFluid extends ForgeFlowingFluid {
    private final List<LatexVariant<?>> form;
    private final LatexType latexType;
    protected AbstractLatexFluid(Properties properties, LatexType latexType, List<LatexVariant<?>> form) {
        super(properties);
        this.latexType = latexType;
        this.form = form;
    }

    public LatexType getLatexType() {
        return latexType;
    }

    public abstract boolean canEntityStandOn(LivingEntity entity);

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        Level level = event.getEntityLiving().level;
        AbstractLatexFluid fluid = null;
        BlockState state = Blocks.AIR.defaultBlockState();
        if (level.getFluidState(event.getEntityLiving().blockPosition()).getType() instanceof AbstractLatexFluid fluidFeet) {
            state = level.getBlockState(event.getEntityLiving().blockPosition());
            fluid = fluidFeet;
        }
        if (level.getFluidState(event.getEntityLiving().eyeBlockPosition()).getType() instanceof AbstractLatexFluid fluidHead) {
            state = level.getBlockState(event.getEntityLiving().blockPosition());
            fluid = fluidHead;
        }

        if (fluid != null) {
            if (LatexVariant.getEntityVariant(event.getEntityLiving()) != null) {
                var living = event.getEntityLiving();
                var delta = living.getDeltaMovement();
                living.resetFallDistance();
                living.setDeltaMovement(living.getDeltaMovement().multiply(1.0, delta.y > 0.0 ? 1.1 : 0.5, 1.0));
            } else
                event.getEntityLiving().makeStuckInBlock(state, new Vec3(0.75, 0.75, 0.75));
        }

        if (event.getEntityLiving() instanceof Player player && ProcessTransfur.isPlayerLatex(player))
            return;
        if (event.getEntityLiving() instanceof LatexEntity)
            return;

        if (event.getEntityLiving().isAlive() && !event.getEntityLiving().isDeadOrDying() && fluid != null) {
            ProcessTransfur.progressTransfur(event.getEntityLiving(), 5.0f, fluid.form.get(level.random.nextInt(fluid.form.size())).getFormId());
        }
    }

    public VoxelShape getCollisionShape(FluidState state, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        if (context.canStandOnFluid(Blocks.WATER.defaultBlockState().getFluidState(), state)) {
            if (level.getFluidState(blockPos.above()).is(ChangedTags.Fluids.LATEX)) {
                return Shapes.block();
            }

            int amount = state.getAmount();
            if (amount >= 8)
                return LiquidBlock.STABLE_SHAPE;
        }

        return Shapes.empty();
    }
}
