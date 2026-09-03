package net.ltxprogrammer.changed.entity.ai;

import net.ltxprogrammer.changed.block.entity.CardboardBoxTallBlockEntity;
import net.ltxprogrammer.changed.entity.SeatEntity;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.util.LevelUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Optional;

// Code from Foxyas
public class HideInABoxGoal extends Goal {
    private static final int SEARCH_RANGE = 10;

    protected final PathfinderMob holder;
    protected final float speedModifier;

    protected final IntProvider searchCooldownProvider;
    protected final IntProvider maxInBoxTicksProvider;

    protected BlockPos boxPos;
    protected BlockState boxBlockState = Blocks.AIR.defaultBlockState();

    protected int searchForBoxCooldown = 0;
    protected int noPathTimeout = 100;

    protected int inBoxTicks;
    protected int targetInBoxTicks;

    public HideInABoxGoal(PathfinderMob holder, float speedModifier, IntProvider searchCooldownProvider, IntProvider maxInBoxTicksProvider) {
        this.holder = holder;
        this.speedModifier = speedModifier;
        this.searchCooldownProvider = searchCooldownProvider;
        this.maxInBoxTicksProvider = maxInBoxTicksProvider;
        this.targetInBoxTicks = maxInBoxTicksProvider.getMaxValue();

        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (searchForBoxCooldown > 0) {
            searchForBoxCooldown--;
            return false;
        }

        if (holder.getTarget() != null) {
            return false;
        }

        if (boxPos == null) {
            tryFindBox();
        }

        return boxPos != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (isHiddenInBox()) {
            // Keep target suppressed while in box so external target setters don't break the goal early
            if (holder.getTarget() != null) {
                holder.setTarget(null);
            }
            return inBoxTicks < targetInBoxTicks;
        }

        // Outside the box: if target is assigned by external code, break goal immediately
        if (holder.getTarget() != null) {
            return false;
        }

        return boxPos != null;
    }

    @Override
    public void start() {
        if (boxPos == null) {
            tryFindBox();
        }
        if (boxPos != null) {
            holder.getNavigation().moveTo(boxPos.getX() + 0.5, boxPos.getY(), boxPos.getZ() + 0.5, speedModifier);
            this.inBoxTicks = 0;
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    protected boolean isHiddenInBox() {
        return holder.getVehicle() instanceof SeatEntity;
    }

    @Override
    public void tick() {
        Level level = holder.level();

        if (isHiddenInBox()) {
            inBoxTicks++;

            if (holder.getTarget() != null) {
                holder.setTarget(null);
            }

            boolean isSeatInvalid = !(holder.getVehicle() instanceof SeatEntity seatEntity) || seatEntity.isRemoved();
            if (isSeatInvalid) {
                inBoxTicks = targetInBoxTicks;
            }
            return;
        }

        if (boxPos != null) {
            PathNavigation navigation = holder.getNavigation();
            if (!isBlockInvalid(level, boxPos, level.getBlockState(boxPos))) {
                navigation.moveTo(boxPos.getX() + 0.5, boxPos.getY(), boxPos.getZ() + 0.5, speedModifier);
                Vec3 centerOfBox = Vec3.atCenterOf(boxPos);
                holder.getLookControl().setLookAt(
                        centerOfBox.x, centerOfBox.y, centerOfBox.z,
                        30.0F,
                        30.0F
                );
            } else {
                tryFindBox();
                if (boxPos == null) return;
            }

            boolean isTouchingBox = LevelUtil.isTouchingBlockInteraction(holder.level(), boxPos, boxBlockState, holder);
            if (holder.blockPosition().closerThan(boxPos, 1.5) || isTouchingBox) {
                if (level.getBlockEntity(boxPos) instanceof CardboardBoxTallBlockEntity box) {
                    if (box.hideEntity(holder)) {
                        this.inBoxTicks = 0;
                        this.targetInBoxTicks = maxInBoxTicksProvider.sample(holder.getRandom());
                        return;
                    }
                }
            }

            if (navigation.isStuck() || (navigation.getPath() != null && !navigation.getPath().canReach())) {
                noPathTimeout--;
                if (noPathTimeout <= 0) {
                    applySearchCooldown();
                    boxPos = null;
                } else if (noPathTimeout % 25 == 0) {
                    navigation.recomputePath();
                }
                return;
            }

            noPathTimeout = 100;
        }
    }

    private void stopHiddenBox() {
        boxPos = null;
        boxBlockState = Blocks.AIR.defaultBlockState();
        if (holder.getVehicle() instanceof SeatEntity) {
            holder.stopRiding();
        }
    }

    protected void tryFindBox() {
        Optional<BlockPos> nearestValidBox = getNearestValidBox();
        if (nearestValidBox.isPresent()) {
            boxPos = nearestValidBox.get();
            boxBlockState = holder.level().getBlockState(boxPos);
        } else {
            boxPos = null;
            boxBlockState = Blocks.AIR.defaultBlockState();
            applySearchCooldown();
        }
    }

    protected void applySearchCooldown() {
        this.searchForBoxCooldown = searchCooldownProvider.sample(holder.getRandom());
    }

    protected Optional<BlockPos> getNearestValidBox() {
        BlockPos center = holder.blockPosition();
        BlockPos closestBox = null;
        float closestDist = SEARCH_RANGE * SEARCH_RANGE + .01f;
        Level level = holder.level();

        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-SEARCH_RANGE, -SEARCH_RANGE, -SEARCH_RANGE), center.offset(SEARCH_RANGE, SEARCH_RANGE, SEARCH_RANGE))) {
            float dist = (float) pos.distSqr(center);
            if (dist >= closestDist || isBlockInvalid(level, pos, level.getBlockState(pos))) continue;
            closestDist = dist;
            closestBox = pos.immutable();
        }

        return Optional.ofNullable(closestBox);
    }

    protected boolean isBlockInvalid(Level level, BlockPos pos, BlockState state) {
        if (!state.is(ChangedBlocks.CARDBOARD_BOX_TALL.get()) || state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.UPPER) {
            return true;
        }

        if (!(level.getBlockEntity(pos) instanceof CardboardBoxTallBlockEntity box)) {
            return true;
        }

        // Allow block if it's empty OR if the holder itself is the entity inside
        return box.getSeatedEntity() != null && box.getSeatedEntity() != holder;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void stop() {
        holder.getNavigation().stop();
        inBoxTicks = 0;
        noPathTimeout = 100;

        stopHiddenBox();
        applySearchCooldown();
    }
}