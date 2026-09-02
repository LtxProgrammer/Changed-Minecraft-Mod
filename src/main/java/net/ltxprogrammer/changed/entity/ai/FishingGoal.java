package net.ltxprogrammer.changed.entity.ai;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.AbstractDarkLatexEntity;
import net.ltxprogrammer.changed.util.LevelUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.stream.Stream;

public class FishingGoal extends MoveToBlockGoal {
    private static final int WATER_CHECK_SEARCH_HORIZONTAL = 8;
    private static final int WATER_CHECK_SEARCH_VERTICAL = 4;
    private static final int TIME_LIMIT_TO_PATHFIND = 5 * 20;

    public final ChangedEntity entity;
    public final Level level;
    private BlockPos targetWaterSurface = BlockPos.ZERO;

    public FishingGoal(ChangedEntity entity, double speedModifier, int searchRange, int verticalSearchRange) {
        super(entity, speedModifier, searchRange, verticalSearchRange);
        this.entity = entity;
        this.level = entity.level();
    }

    @Override
    protected int nextStartTick(PathfinderMob mob) {
        return 40;
    }

    public double acceptedDistance() {
        return 2.0D;
    }

    @Override
    public boolean canUse() {
        if (entity.getTarget() != null)
            return false;
        var inventory = entity.getInventory();
        if (inventory == null)
            return false;
        if (!entity.getMainHandItem().is(Tags.Items.TOOLS_FISHING_RODS))
            return false;

        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (entity.getTarget() != null)
            return false;
        if (!entity.getMainHandItem().is(Tags.Items.TOOLS_FISHING_RODS))
            return false;
        if (!this.isValidWaterSurface(level, targetWaterSurface))
            return false;

        return this.tryTicks <= TIME_LIMIT_TO_PATHFIND && super.canContinueToUse();
    }

    @Override
    protected boolean findNearestBlock() {
        if (!super.findNearestBlock())
            return false;

        BlockPos blockpos = this.blockPos;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(int y = 0; y <= WATER_CHECK_SEARCH_VERTICAL; y = y > 0 ? -y : 1 - y) {
            for(int r = 0; r < WATER_CHECK_SEARCH_HORIZONTAL; ++r) {
                for(int x = 0; x <= r; x = x > 0 ? -x : 1 - x) {
                    for(int z = x < r && x > -r ? r : 0; z <= r; z = z > 0 ? -z : 1 - z) {
                        blockpos$mutableblockpos.setWithOffset(blockpos, x, y - 1, z);
                        if (this.mob.isWithinRestriction(blockpos$mutableblockpos) && this.isValidWaterSurface(this.mob.level(), blockpos$mutableblockpos)) {
                            this.targetWaterSurface = blockpos$mutableblockpos;
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    protected boolean isValidWaterSurface(LevelReader level, BlockPos blockPos) {
        if (blockPos.getX() == this.blockPos.getX() && blockPos.getZ() == this.blockPos.getZ())
            return false; // Water cannot be directly below
        BlockState blockState = level.getBlockState(blockPos);
        if (!blockState.is(Blocks.WATER))
            return false;
        if (!level.isEmptyBlock(blockPos.above()))
            return false;
        if (!level.isEmptyBlock(blockPos.above().above()))
            return false;

        // Trace from surface to where the entity will fish from
        return Stream.concat(
                LevelUtil.getBlocksInLine(this.blockPos.above(), blockPos.above(), 0.25f),
                LevelUtil.getBlocksInLine(this.blockPos.above().above(), blockPos.above().above(), 0.25f)
        ).allMatch(tracePos -> {
            if (tracePos.getX() == this.blockPos.getX() && tracePos.getZ() == this.blockPos.getZ())
                return true; // Ignore perch block
            return level.isEmptyBlock(tracePos);
        });
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos blockPos) {
        if (!level.isEmptyBlock(blockPos.above()))
            return false;
        if (!level.isEmptyBlock(blockPos.above().above()))
            return false;
        if (!level.getBlockState(blockPos).isFaceSturdy(level, blockPos, Direction.UP))
            return false;

        return Direction.Plane.HORIZONTAL.stream().anyMatch(direction -> { // Is block on ledge
            BlockPos neighborPos = blockPos.relative(direction);
            BlockState neighbor = level.getBlockState(neighborPos);
            return neighbor.isAir() || neighbor.is(Blocks.WATER);
        });
    }

    protected int hookOutDuration = 0;
    protected int idleDuration = 0;

    @Override
    public void start() {
        super.start();
        hookOutDuration = 0;
        idleDuration = 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isReachedTarget()) {
            this.tryTicks = 0; // Causes the DL to stay until other it has a combat target, or has a different favor

            if (this.idleDuration <= 0 && hookOutDuration <= 0)
                this.startFishing();
            if (this.hookOutDuration >= 8 * 20)
                this.retrieveCast();

            if (this.idleDuration > 0)
                this.idleDuration--;
            if (this.hookOutDuration > 0)
                this.hookOutDuration++;
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (this.hookOutDuration > 0)
            this.cancelCast();
    }

    protected void startFishing() {
        entity.swing(InteractionHand.MAIN_HAND);
        level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        this.idleDuration = 0;
        this.hookOutDuration = 1;
    }

    protected void retrieveCast() {
        entity.swing(InteractionHand.MAIN_HAND);
        ItemStack itemstack = entity.getItemInHand(InteractionHand.MAIN_HAND);
        level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        this.idleDuration = 3 * 20;
        this.hookOutDuration = 0;

        int luck = EnchantmentHelper.getFishingLuckBonus(itemstack);
        LootParams lootparams = (new LootParams.Builder((ServerLevel)this.level))
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(targetWaterSurface))
                .withParameter(LootContextParams.TOOL, itemstack)
                .withParameter(LootContextParams.KILLER_ENTITY, entity)
                .withLuck(luck).create(LootContextParamSets.FISHING);
        LootTable loottable = this.level.getServer().getLootData().getLootTable(BuiltInLootTables.FISHING);
        loottable.getRandomItems(lootparams).forEach(caughtItem -> {
            entity.getInventory().placeItemBackInInventory(caughtItem);
        });
        itemstack.hurtAndBreak(1, entity, (handlerEntity) -> {
            handlerEntity.broadcastBreakEvent(InteractionHand.MAIN_HAND);
        });
    }

    protected void cancelCast() {
        entity.swing(InteractionHand.MAIN_HAND);
        level.playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
    }
}
