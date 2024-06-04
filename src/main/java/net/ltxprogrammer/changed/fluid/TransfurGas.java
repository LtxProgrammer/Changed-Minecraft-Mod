package net.ltxprogrammer.changed.fluid;

import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Supplier;

@Mod.EventBusSubscriber
public abstract class TransfurGas extends Gas {
    private final Supplier<? extends TransfurVariant<?>> variant;

    protected TransfurGas(Properties properties, Supplier<? extends TransfurVariant<?>> variant) {
        super(properties);
        this.variant = variant;
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        var entity = event.getEntityLiving();
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(ChangedItems.GAS_MASK.get()))
            return;

        // Code from Entity.updateFluidOnEyes()
        double yCheck = entity.getEyeY() - (double)0.11111111F;
        Entity vehicle = entity.getVehicle();
        if (vehicle instanceof Boat boat) {
            if (!boat.isUnderWater() && boat.getBoundingBox().maxY >= yCheck && boat.getBoundingBox().minY <= yCheck) {
                return;
            }
        }

        BlockPos blockpos = new BlockPos(entity.getX(), yCheck, entity.getZ());
        FluidState fluidstate = entity.level.getFluidState(blockpos);
        double yFluid = (double)((float)blockpos.getY() + fluidstate.getHeight(entity.level, blockpos));
        if (yFluid > yCheck && fluidstate.getType() instanceof TransfurGas transfurGas) {
            int air = entity.getAirSupply();
            int i = EnchantmentHelper.getRespiration(entity);
            air = i > 0 && entity.getRandom().nextInt(i + 1) > 0 ? air : air - 6;

            if(air <= 0) {
                air = 0;
                ProcessTransfur.progressTransfur(entity, 8.0f, transfurGas.variant.get(),
                        TransfurContext.hazard(TransfurCause.FACE_HAZARD));
            }

            entity.setAirSupply(air);
        }
    }
}
