package net.ltxprogrammer.changed.fluid;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Mod.EventBusSubscriber
public abstract class TransfurGas extends Gas {
    public final ImmutableList<Supplier<? extends TransfurVariant<?>>> variants;

    protected TransfurGas(Properties properties, Supplier<? extends TransfurVariant<?>> variant) {
        super(properties);
        this.variants = ImmutableList.of(variant);
    }

    protected TransfurGas(Properties properties, List<Supplier<? extends TransfurVariant<?>>> variants) {
        super(properties);
        this.variants = ImmutableList.copyOf(variants);
    }

    public static Optional<TransfurGas> validEntityInGas(LivingEntity entity) {
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(ChangedItems.GAS_MASK.get()))
            return Optional.empty();
        if (!entity.getType().is(ChangedTags.EntityTypes.HUMANOIDS))
            return Optional.empty();
        var variant = ProcessTransfur.getPlayerTransfurVariant(EntityUtil.playerOrNull(entity));
        if (variant != null)
            return Optional.empty();

        if (entity instanceof LivingEntityDataExtension ext)
            return ext.isEyeInGas(TransfurGas.class);
        return Optional.empty();
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        var entity = event.getEntityLiving();
        validEntityInGas(entity).ifPresent(transfurGas -> {
            int air = entity.getAirSupply();
            int i = EnchantmentHelper.getRespiration(entity);
            air = i > 0 && entity.getRandom().nextInt(i + 1) > 0 ? air : air - 3;

            if(air <= 0) {
                air = 0;
                ProcessTransfur.progressTransfur(entity, 8.0f, Util.getRandom(transfurGas.variants, entity.level.random).get(),
                        TransfurContext.hazard(TransfurCause.FACE_HAZARD));
            }

            entity.setAirSupply(air);
        });
    }
}
