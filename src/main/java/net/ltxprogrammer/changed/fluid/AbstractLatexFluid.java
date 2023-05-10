package net.ltxprogrammer.changed.fluid;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public abstract class AbstractLatexFluid extends ForgeFlowingFluid {
    private final List<LatexVariant<?>> form;
    protected AbstractLatexFluid(Properties properties, List<LatexVariant<?>> form) {
        super(properties);
        this.form = form;
    }

    public abstract boolean canEntityStandOn(LivingEntity entity);

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Player player && ProcessTransfur.isPlayerLatex(player))
            return;
        if (event.getEntityLiving() instanceof LatexEntity)
            return;

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

        if (event.getEntityLiving().isAlive() && !event.getEntityLiving().isDeadOrDying() && fluid != null) {
            event.getEntityLiving().makeStuckInBlock(state, new Vec3((double)0.8F, 0.75D, (double)0.8F));
            ProcessTransfur.progressTransfur(event.getEntityLiving(), 5.0f, fluid.form.get(level.random.nextInt(fluid.form.size())).getFormId());
        }
    }
}
