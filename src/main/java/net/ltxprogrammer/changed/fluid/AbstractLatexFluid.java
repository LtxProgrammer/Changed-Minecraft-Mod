package net.ltxprogrammer.changed.fluid;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public abstract class AbstractLatexFluid extends ForgeFlowingFluid {
    private final LatexVariant<?> form;

    protected AbstractLatexFluid(Properties properties, LatexVariant<?> form) {
        super(properties);
        this.form = form;
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof Player player && ProcessTransfur.isPlayerLatex(player))
            return;
        if (event.getEntityLiving() instanceof LatexEntity)
            return;

        Level level = event.getEntityLiving().level;
        AbstractLatexFluid fluid = null;
        if (level.getFluidState(event.getEntityLiving().eyeBlockPosition()).getType() instanceof AbstractLatexFluid fluidHead)
            fluid = fluidHead;

        if (event.getEntityLiving().isAlive() && !event.getEntityLiving().isDeadOrDying() && fluid != null) {
            ProcessTransfur.progressTransfur(event.getEntityLiving(), 2500, fluid.form.getFormId());
        }
    }
}
