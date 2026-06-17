package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.block.InkCloud;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.ai.LatexAssimilationDecision;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;

public class InkCloudBlockEntity extends BlockEntity {
    public static final double CLOUD_RADIUS = 4.0;

    @Nullable
    private UUID ownerUUID;
    @Nullable
    private Entity cachedOwner;
    private BlockPos origin = BlockPos.ZERO;
    private int dissipateTicks = 4 * 20;

    public InkCloudBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ChangedBlockEntities.INK_CLOUD.get(), blockPos, blockState);
    }

    public void setOrigin(BlockPos origin) {
        this.origin = origin;
    }

    public BlockPos getOrigin() {
        return origin;
    }

    public void setOwner(@Nullable Entity owner) {
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
            this.cachedOwner = owner;
        }

    }

    @Nullable
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level instanceof ServerLevel) {
            this.cachedOwner = ((ServerLevel)this.level).getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }

        tag.putInt("DissipateTicks", this.dissipateTicks);
        TagUtil.putBlockPos(tag, "Origin", this.origin);
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
            this.cachedOwner = null;
        }

        this.dissipateTicks = tag.getInt("DissipateTicks");
        this.origin = TagUtil.getBlockPos(tag, "Origin");
    }

    protected LatexAssimilationDecision<?> makeAssimilationDecision(RandomSource random, int age) {
        IAbstractChangedEntity sourceEntity;
        if (cachedOwner instanceof LivingEntity livingOwner)
            sourceEntity = IAbstractChangedEntity.forEither(livingOwner);
        else
            sourceEntity = null;

        return LatexAssimilationDecision.fromBlockOrItem(
                ChangedTransfurVariants.Gendered.LATEX_SQUID_DOGS.getRandomVariant(random),
                TransfurContext.latexHazard(sourceEntity, TransfurCause.SQUID_DOG_INKBALL), 2.0f * (5 - age));
    }

    public static boolean canExistIn(BlockState blockState) {
        return blockState.is(Blocks.WATER) && blockState.getFluidState().getAmount() >= 8 && blockState.getFluidState().isSource();
    }

    protected void tickSpread(Level level) {
        BlockPos.MutableBlockPos blockPos = this.getBlockPos().mutable();
        Arrays.stream(Direction.values()).forEach(direction -> {
            blockPos.setWithOffset(this.getBlockPos(), direction);
            if (blockPos.distSqr(this.getOrigin()) > CLOUD_RADIUS * CLOUD_RADIUS)
                return;

            if (canExistIn(level.getBlockState(blockPos))) {
                level.setBlockAndUpdate(blockPos, this.getBlockState());
                level.getBlockEntity(blockPos, ChangedBlockEntities.INK_CLOUD.get()).ifPresent(blockEntity -> {
                    blockEntity.setOwner(this.getOwner());
                    blockEntity.setOrigin(this.getOrigin());
                });
            }
        });
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, InkCloudBlockEntity blockEntity) {
        int age = state.getValue(InkCloud.AGE);
        blockEntity.dissipateTicks--;
        if (blockEntity.dissipateTicks <= 0) {
            if (age < 5) {
                level.setBlockAndUpdate(pos, state.setValue(InkCloud.AGE, age + 1));
                blockEntity.dissipateTicks = level.random.nextInt(4 * 20, 5 * 20);
            }
            else {
                level.setBlockAndUpdate(pos, state.getFluidState().createLegacyBlock());
                return;
            }
        }

        if (age < 2) {
            blockEntity.tickSpread(level);
        }

        var blockBox = new AABB(pos);

        level.getEntitiesOfClass(LivingEntity.class, blockBox).forEach(target -> {
            if (target == blockEntity.cachedOwner)
                return;

            ProcessTransfur.progressTransfur(target, blockEntity.makeAssimilationDecision(level.random, age));
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100));

            if (blockBox.contains(target.getEyePosition())) {
                target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100));
                target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20));
            }
        });
    }
}
