package net.ltxprogrammer.changed.entity.ai;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.util.LevelUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.Arrays;

public class CaveHarvestGoal extends MoveToBlockGoal {
    private static final int TIME_LIMIT_TO_PATHFIND = 5 * 20;
    private static final int ORE_CHECK_SEARCH_HORIZONTAL = 6;
    private static final int ORE_CHECK_SEARCH_VERTICAL = 4;

    public final ChangedEntity entity;
    public final Level level;
    private TamedEntityInventory inventory = null;
    private BlockPos targetOrePosition = BlockPos.ZERO;

    public CaveHarvestGoal(ChangedEntity entity, double speedModifier, int searchRange, int verticalSearchRange) {
        super(entity, speedModifier, searchRange, verticalSearchRange);
        this.entity = entity;
        this.level = entity.level();
    }

    @Override
    protected int nextStartTick(PathfinderMob mob) {
        return 30;
    }

    public double acceptedDistance() {
        return 2.0D;
    }

    public boolean wantToMine(BlockPos orePos) {
        ItemStack pickaxe = entity.getMainHandItem();
        BlockState oreBlock = level.getBlockState(orePos);
        if (!oreBlock.is(Tags.Blocks.ORES))
            return false;

        // Don't mine blocks that will yield nothing
        return pickaxe.isCorrectToolForDrops(oreBlock);
    }

    @Override
    public boolean canUse() {
        if (entity.getTarget() != null)
            return false;
        var inventory = entity.getInventory();
        if (inventory == null)
            return false;
        this.inventory = inventory;
        if (!entity.getMainHandItem().is(ItemTags.PICKAXES))
            return false;

        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (entity.getTarget() != null)
            return false;
        if (!entity.getMainHandItem().is(ItemTags.PICKAXES))
            return false;
        if (!wantToMine(targetOrePosition))
            return false;

        return this.tryTicks <= TIME_LIMIT_TO_PATHFIND && super.canContinueToUse();
    }

    @Override
    protected boolean findNearestBlock() {
        if (!super.findNearestBlock())
            return false;

        BlockPos blockpos = this.blockPos;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(int y = 0; y <= ORE_CHECK_SEARCH_VERTICAL; y = y > 0 ? -y : 1 - y) {
            for(int r = 0; r < ORE_CHECK_SEARCH_HORIZONTAL; ++r) {
                for(int x = 0; x <= r; x = x > 0 ? -x : 1 - x) {
                    for(int z = x < r && x > -r ? r : 0; z <= r; z = z > 0 ? -z : 1 - z) {
                        blockpos$mutableblockpos.setWithOffset(blockpos, x, y - 1, z);
                        if (this.mob.isWithinRestriction(blockpos$mutableblockpos) && this.isValidOre(this.mob.level(), blockpos$mutableblockpos)) {
                            this.targetOrePosition = blockpos$mutableblockpos;
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    protected boolean isValidOre(LevelReader level, BlockPos orePos) {
        if (!wantToMine(orePos))
            return false;
        if (orePos.equals(this.blockPos))
            return false; // Don't mine directly down

        // Find an exposed surface the entity can mine
        return Arrays.stream(Direction.values()).filter(surface -> {
            return level.isEmptyBlock(orePos.relative(surface));
        }).anyMatch(surface -> {
            return LevelUtil.getBlocksInLine(this.blockPos.above().above(), orePos.relative(surface), 0.125f).allMatch(tracePos -> {
                if (tracePos.equals(orePos))
                    return true;
                return level.isEmptyBlock(tracePos);
            });
        });
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos blockPos) {
        if (!level.isEmptyBlock(blockPos.above()))
            return false;
        if (!level.isEmptyBlock(blockPos.above().above()))
            return false;

        return level.getBlockState(blockPos).isFaceSturdy(level, blockPos, Direction.UP);
    }

    protected boolean isDestroying = false;
    protected float destroyProgress = 0;
    protected float destroyTicks = 0;

    @Override
    public void start() {
        super.start();
        isDestroying = false;
        destroyProgress = 0;
        destroyTicks = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.cancelMining();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isReachedTarget()) {
            ItemStack pickaxe = entity.getMainHandItem();

            if (!isDestroying)
                this.startMining(pickaxe);
            else if (destroyProgress >= 1f)
                this.finishMining(pickaxe);
            else
                this.tickMining(pickaxe);
        }
    }

    public int getDestroyStage() {
        return this.destroyProgress > 0.0F ? (int)(this.destroyProgress * 10.0F) : -1;
    }

    public float getDigSpeed(BlockState blockState, @Nullable BlockPos pos) {
        float f = inventory.getDestroySpeed(blockState);
        if (f > 1.0F) {
            int i = EnchantmentHelper.getBlockEfficiency(entity);
            ItemStack itemstack = entity.getMainHandItem();
            if (i > 0 && !itemstack.isEmpty()) {
                f += (float)(i * i + 1);
            }
        }

        if (MobEffectUtil.hasDigSpeed(entity)) {
            f *= 1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(entity) + 1) * 0.2F;
        }

        if (entity.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            float f1;
            switch (entity.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
                case 0:
                    f1 = 0.3F;
                    break;
                case 1:
                    f1 = 0.09F;
                    break;
                case 2:
                    f1 = 0.0027F;
                    break;
                case 3:
                default:
                    f1 = 8.1E-4F;
            }

            f *= f1;
        }

        if (entity.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) && !EnchantmentHelper.hasAquaAffinity(entity)) {
            f /= 5.0F;
        }

        if (!entity.onGround()) {
            f /= 5.0F;
        }

        return f;
    }

    public float getDestroyProgress(BlockState blockState, BlockGetter p_60468_, BlockPos p_60469_) {
        float f = blockState.getDestroySpeed(p_60468_, p_60469_);
        if (f == -1.0F) {
            return 0.0F;
        } else {
            //int i = net.minecraftforge.common.ForgeHooks.isCorrectToolForDrops(blockState, player) ? 30 : 100;
            return this.getDigSpeed(blockState, p_60469_) / f / 30.0f;
        }
    }

    public void startMining(ItemStack pickaxe) {
        isDestroying = true;
        destroyProgress = 0;
        destroyTicks = 0;
    }

    public void tickMining(ItemStack pickaxe) {
        isDestroying = true;
        BlockState blockState = level.getBlockState(targetOrePosition);
        this.destroyProgress += this.getDestroyProgress(blockState, this.level, targetOrePosition);
        if (this.destroyTicks % 4.0F == 0.0F) {
            SoundType soundtype = blockState.getSoundType(this.level, targetOrePosition, this.entity);
            this.level.playSound(null, targetOrePosition, soundtype.getHitSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 8.0F, soundtype.getPitch() * 0.5F);
        }
        entity.swing(InteractionHand.MAIN_HAND);
        ++this.destroyTicks;
        level.destroyBlockProgress(entity.getId(), targetOrePosition, getDestroyStage());
    }

    public void finishMining(ItemStack pickaxe) {
        isDestroying = false;
        this.destroyProgress = 0;
        this.destroyTicks = 0;

        BlockState blockState = level.getBlockState(targetOrePosition);
        Block.getDrops(blockState, (ServerLevel) level, targetOrePosition, level.getBlockEntity(targetOrePosition), entity, pickaxe).forEach(itemStack -> {
            inventory.add(itemStack);
            Block.popResource(level, targetOrePosition, itemStack);
        });
        blockState.spawnAfterBreak((ServerLevel) level, targetOrePosition, pickaxe, false);
        level.setBlockAndUpdate(targetOrePosition, blockState.getFluidState().createLegacyBlock());
        level.levelEvent(2001, targetOrePosition, Block.getId(blockState));
        level.destroyBlockProgress(entity.getId(), targetOrePosition, -1);

        if (!blockState.isAir())
            pickaxe.hurtAndBreak(1, entity, (handlerEntity) -> {
                handlerEntity.broadcastBreakEvent(InteractionHand.MAIN_HAND);
            });

        this.nextStartTick = 10;
    }

    public void cancelMining() {
        isDestroying = false;
        this.destroyProgress = 0;
        this.destroyTicks = 0;
        level.destroyBlockProgress(entity.getId(), targetOrePosition, -1);
    }
}
