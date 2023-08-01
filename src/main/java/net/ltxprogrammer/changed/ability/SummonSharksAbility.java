package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.util.CollectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SummonSharksAbility extends SimpleAbility {
    @Override
    public boolean canUse(Player player, LatexVariantInstance<?> variant) {
        return player.isInWaterOrBubble();
    }

    protected Stream<BlockPos> findWaterNearby(BlockGetter level, BlockPos near) {
        return BlockPos.betweenClosedStream(near.offset(-4, -2, -4), near.offset(4, 2, 4)).filter(
                pos -> level.getFluidState(pos).is(FluidTags.WATER)
        );
    }

    @Override
    public void startUsing(Player player, LatexVariantInstance<?> variant) {
        var level = player.level;
        if (level.isClientSide)
            return;

        ChangedSounds.broadcastSound(player, ChangedSounds.MONSTER2, 1.0f, 1.0f);

        var list = findWaterNearby(level, player.blockPosition()).toList();
        int attempts = Math.min(list.size(), 2);

        while (attempts > 0) {
            var blockPos = list.get(level.random.nextInt(list.size()));

            var shark = ChangedEntities.SHARK.get().create(level);
            level.addFreshEntity(shark);
            shark.setTarget(player.getLastHurtByMob());
            shark.moveTo(blockPos, 0.0f, 0.0f);

            attempts--;
        }
    }

    @Override
    public UseType getUseType(Player player, LatexVariantInstance<?> variant) {
        return UseType.CHARGE_TIME;
    }

    @Override
    public int getChargeTime(Player player, LatexVariantInstance<?> variant) {
        return 40;
    }

    @Override
    public int getCoolDown(Player player, LatexVariantInstance<?> variant) {
        return 2 * 60 * 20; // 2 Minutes
    }
}
