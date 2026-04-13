package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.LevelUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class AlertingPuddle extends ChangedBlock {
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
    public static final VoxelShape SHAPE_WHOLE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
    protected final TargetingConditions targetingConditions = TargetingConditions.forCombat().ignoreLineOfSight();
    protected final Predicate<? super LivingEntity> toTarget;
    protected final Predicate<? super LivingEntity> toAlert;

    public AlertingPuddle(Supplier<? extends LatexType> latexType) {
        this(entity -> latexType.get().isHostileTo(LatexType.getEntityLatexType(entity)) ||
                        EntityUtil.maybeGetOverlaying(entity).getType().is(ChangedTags.EntityTypes.HUMANOIDS),
                entity -> latexType.get() == LatexType.getEntityLatexType(entity));
    }

    public AlertingPuddle(TagKey<EntityType<?>> tag) {
        this(entity -> EntityUtil.maybeGetOverlaying(entity).getType().is(ChangedTags.EntityTypes.HUMANOIDS),
                entity -> EntityUtil.maybeGetOverlaying(entity).getType().is(tag));
    }

    public AlertingPuddle(Predicate<? super LivingEntity> toTarget, Predicate<? super LivingEntity> toAlert) {
        super(BlockBehaviour.Properties.of().sound(SoundType.SLIME_BLOCK).strength(0.1F));
        this.registerDefaultState(this.stateDefinition.any().setValue(OCCUPIED, false));
        this.toTarget = toTarget;
        this.toAlert = toAlert;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OCCUPIED);
    }

    public boolean canSurvive(BlockState p_49325_, LevelReader level, BlockPos blockPos) {
        BlockPos below = blockPos.below();
        return canSupportRigidBlock(level, below);
    }

    public PushReaction getPistonPushReaction(BlockState p_52814_) {
        return PushReaction.DESTROY;
    }

    @Override
    public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
        super.entityInside(blockState, level, blockPos, entity);
        if (blockState.getValue(OCCUPIED))
            return;

        if (!level.isClientSide && entity instanceof LivingEntity livingEntity && LevelUtil.isTouchingBlockCollision(level, blockPos, blockState, livingEntity)) {
            if (this.toTarget.test(livingEntity)) {
                if (livingEntity instanceof Player player) {
                    player.playNotifySound(ChangedSounds.PUDDLE_ALERT.get(), SoundSource.HOSTILE, 1.0f, 1.0f);
                }

                AABB alertZone = new AABB(blockPos).inflate(12.0);
                level.getEntities(EntityTypeTest.forClass(LivingEntity.class), alertZone, this.toAlert).forEach(alertEntity -> {
                    if (!this.targetingConditions.test(alertEntity, livingEntity))
                        return;

                    if (alertEntity instanceof Player player) {
                        ChangedSounds.sendLocalSound(player, blockPos, ChangedSounds.PUDDLE_ALERT, 1.0f, 1.0f);
                    } else if (alertEntity instanceof Mob mob) {
                        if (mob.getTarget() == null)
                            mob.setTarget(livingEntity);
                    }
                });

                level.setBlockAndUpdate(blockPos, blockState.setValue(OCCUPIED, true));
                level.scheduleTick(blockPos, this, 20);
            }
        }
    }

    public boolean shouldGlowLocally(LivingEntity target, Entity viewer) {
        if (viewer.distanceToSqr(target) > 144)
            return false;

        if (viewer instanceof LivingEntity livingViewer)
            return this.toTarget.test(target) && this.toAlert.test(livingViewer);
        return false;
    }

    @Override
    public void tick(BlockState blockState, ServerLevel level, BlockPos blockPos, RandomSource random) {
        super.tick(blockState, level, blockPos, random);
        if (blockState.getValue(OCCUPIED)) {
            if (level.getEntities(EntityTypeTest.forClass(LivingEntity.class), new AABB(blockPos), this.toTarget).isEmpty()) {
                level.setBlockAndUpdate(blockPos, blockState.setValue(OCCUPIED, false));
            } else {
                level.scheduleTick(blockPos, this, 20);
            }
        }
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block source, BlockPos sourcePos, boolean simulate) {
        super.neighborChanged(blockState, level, blockPos, source, sourcePos, simulate);
        if (!blockState.canSurvive(level, blockPos)) {
            BlockEntity blockentity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
            dropResources(blockState, level, blockPos, blockentity);
            level.removeBlock(blockPos, false);
        }
    }

    @Override
    public @Nullable BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return BlockPathTypes.WALKABLE;
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE_WHOLE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return SHAPE_WHOLE;
    }
}
