package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.block.entity.InkCloudBlockEntity;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.Collection;
import java.util.Collections;

public class InkSmokeScreenAbility extends SimpleAbility {
    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        var center = BlockPos.containing(entity.getEntity().getBoundingBox().getCenter());
        return InkCloudBlockEntity.canExistIn(entity.getLevel().getBlockState(center));
    }

    @Override
    public boolean canKeepUsing(IAbstractChangedEntity entity) { return false; }

    protected void playInkSound(IAbstractChangedEntity entity) {
        var random = entity.getEntity().getRandom();
        entity.getEntity().playSound(ChangedSounds.INK_SMOKE_SCREEN.get(), 1.0f, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F);
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        if (!entity.getLevel().isClientSide) {
            var center = BlockPos.containing(entity.getEntity().getBoundingBox().getCenter());
            entity.getLevel().setBlockAndUpdate(center, ChangedBlocks.INK_CLOUD.get().defaultBlockState());
            entity.getLevel().getBlockEntity(center, ChangedBlockEntities.INK_CLOUD.get()).ifPresent(blockEntity -> {
                blockEntity.setOwner(entity.getEntity());
                blockEntity.setOrigin(center);
            });
        }

        this.playInkSound(entity);

        if (!entity.isCreative()) {
            entity.causeFoodExhaustion(5.0f);
            entity.getEntity().addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, 0));
        }
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.CHARGE_TIME;
    }

    @Override
    public int getChargeTime(IAbstractChangedEntity entity) {
        return 60;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 20 * 120;
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.ink_smoke_screen.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }
}
