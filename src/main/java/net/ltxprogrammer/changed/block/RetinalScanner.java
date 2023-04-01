package net.ltxprogrammer.changed.block;

//import net.ltxprogrammer.changed.block.entity.RetinalScannerBlockEntity;

import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Optional;

@Mod.EventBusSubscriber
public class RetinalScanner extends AbstractCustomShapeBlock {
    public static final VoxelShape SHAPE_WHOLE = Block.box(2.0D, 3.0D, 11.0D, 14.0D, 13.0D, 16.0D);

    public RetinalScanner(Properties properties) {
        super(properties);
    }

    public PushReaction getPistonPushReaction(BlockState p_52814_) {
        return PushReaction.DESTROY;
    }

    public void setPlacedBy(Level p_52749_, BlockPos p_52750_, BlockState p_52751_, LivingEntity p_52752_, ItemStack p_52753_) {
        super.setPlacedBy(p_52749_, p_52750_, p_52751_, p_52752_, p_52753_);
    }

    public boolean canSurvive(BlockState p_52783_, LevelReader p_52784_, BlockPos p_52785_) {
        return p_52784_.getBlockState(p_52785_.relative(p_52783_.getValue(FACING).getOpposite())).isFaceSturdy(p_52784_,
                p_52785_.relative(p_52783_.getValue(FACING).getOpposite()),
                p_52783_.getValue(FACING));
    }

    public RenderShape getRenderShape(BlockState p_54559_) {
        return super.getRenderShape(p_54559_);
    }

    public VoxelShape getOcclusionShape(BlockState p_54584_, BlockGetter p_54585_, BlockPos p_54586_) {
        return getInteractionShape(p_54584_, p_54585_, p_54586_);
    }

    public VoxelShape getCollisionShape(BlockState p_54577_, BlockGetter p_54578_, BlockPos p_54579_, CollisionContext p_54580_) {
        return getInteractionShape(p_54577_, p_54578_, p_54579_);
    }

    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return calculateShapes(p_60547_.getValue(FACING), SHAPE_WHOLE);
    }

    public VoxelShape getShape(BlockState p_54561_, BlockGetter p_54562_, BlockPos p_54563_, CollisionContext p_54564_) {
        return getInteractionShape(p_54561_, p_54562_, p_54563_);
    }

    public BlockState updateShape(BlockState p_52796_, Direction p_52797_, BlockState p_52798_, LevelAccessor p_52799_, BlockPos p_52800_, BlockPos p_52801_) {
        return super.updateShape(p_52796_, p_52797_, p_52798_, p_52799_, p_52800_, p_52801_);
    }

    public static boolean canSetSpawn(Level level) {
        return true;
    }

    public boolean isPossibleToRespawnInThis() {
        return true;
    }

    public Optional<Vec3> getRespawnPosition(BlockState state, EntityType<?> type, LevelReader levelReader, BlockPos pos, float orientation, @Nullable LivingEntity entity) {
        pos = pos.relative(state.getValue(FACING)).below();
        return Optional.of(new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }

    public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        } else {
            if (!canSetSpawn(level)) {
                level.removeBlock(blockPos, false);
                level.explode((Entity)null, DamageSource.badRespawnPointExplosion(), (ExplosionDamageCalculator)null, (double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, 5.0F, true, Explosion.BlockInteraction.DESTROY);
                return InteractionResult.SUCCESS;
            } else if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.setRespawnPosition(level.dimension(), blockPos, player.getYRot(), false, true);
                ChangedSounds.broadcastSound(level.getServer(), ChangedSounds.SAVE, blockPos, 1, 1);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.CONSUME;
        }
    }
}
