package net.ltxprogrammer.changed.block;

import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.InputWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.Foods;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

public interface WhiteLatexTransportInterface extends NonLatexCoverableBlock {
    static boolean isEntityInWhiteLatex(LivingEntity entity) {
        if (entity instanceof PlayerDataExtension ext)
            return ext.getPlayerMover() != null && ext.getPlayerMover().is(PlayerMover.WHITE_LATEX_MOVER.get());
        return false;
    }

    default boolean allowTransport(BlockState blockState) {
        return true;
    }

    static void entityEnterLatex(LivingEntity entity, BlockPos pos) {
        if (TransfurVariant.getEntityVariant(entity) != null && !(TransfurVariant.getEntityVariant(entity).getEntityType().is(ChangedTags.EntityTypes.WHITE_LATEX_SWIMMING)))
            return;

        if (isEntityInWhiteLatex(entity) || entity.isDeadOrDying())
            return;

        if (entity instanceof Player player && player.isSpectator())
            return;

        ProcessTransfur.transfur(entity, entity.level, TransfurVariant.WHITE_LATEX_WOLF, false, TransfurContext.hazard(TransfurCause.WHITE_LATEX));

        if (entity instanceof PlayerDataExtension ext)
            ext.setPlayerMoverType(PlayerMover.WHITE_LATEX_MOVER.get());

        entity.refreshDimensions();
        entity.setInvulnerable(true);

        entity.playSound(ChangedSounds.POISON, 1.0f, 1.0f);
        entity.moveTo(pos, entity.getYRot(), entity.getXRot());
    }

    class LatexMover extends PlayerMover<LatexMover.MoverInstance> {
        @Override
        public MoverInstance createInstance() {
            return new MoverInstance(this);
        }

        public static class MoverInstance extends PlayerMoverInstance<LatexMover> {
            private Vec3 lastPos = null;
            private static final double ACCELERATION = 0.2;
            private static final double DECAY = 0.65;

            static boolean whiteLatex(BlockState blockState) {
                if (blockState.getBlock() instanceof WhiteLatexTransportInterface transportInterface)
                    return transportInterface.allowTransport(blockState);
                return (blockState.getProperties().contains(COVERED) && blockState.getValue(COVERED) == LatexType.WHITE_LATEX);
            }

            public MoverInstance(LatexMover parent) {
                super(parent);
            }

            public TransfurVariantInstance<?> getForm(Player player) {
                return ProcessTransfur.getPlayerTransfurVariant(player);
            }

            @Override
            public void aiStep(Player player, InputWrapper input, LogicalSide side) {
                if (lastPos == null)
                    lastPos = player.getPosition(1.0f);

                ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                    if (variant.getLatexType().isHostileTo(LatexType.WHITE_LATEX))
                        player.hurt(ChangedDamageSources.WHITE_LATEX, 2.0f);
                }, () -> {
                    ProcessTransfur.progressPlayerTransfur(player, 4.8f, TransfurVariant.WHITE_LATEX_WOLF, TransfurContext.hazard(TransfurCause.WHITE_LATEX));
                });

                player.setDeltaMovement(0, 0, 0);
                player.refreshDimensions();
                player.heal(0.0625F);
                if (player.tickCount % 50 == 0)
                    player.getFoodData().eat(Foods.DRIED_KELP.getNutrition(), Foods.DRIED_KELP.getSaturationModifier());
                player.resetFallDistance();

                Vec3 currentPos = player.getPosition(1.0f);
                Vec3 velocity = currentPos.subtract(lastPos).multiply(DECAY, DECAY, DECAY);

                Vec3 lookAngle = player.getLookAngle();
                Vec3 upAngle = player.getUpVector(1.0f);
                Vec3 leftAngle = upAngle.cross(lookAngle);

                Vec2 horizontal = input.getMoveVector();

                Vec3 controlDir = lookAngle.multiply(horizontal.y, horizontal.y, horizontal.y)
                        .add(leftAngle.multiply(horizontal.x, horizontal.x, horizontal.x)).multiply(ACCELERATION, ACCELERATION, ACCELERATION);

                player.move(MoverType.SELF, controlDir.add(velocity));
                lastPos = currentPos;
            }

            @Override
            public void serverAiStep(Player player, InputWrapper input, LogicalSide side) {
                var form = getForm(player);
                if (player instanceof ServerPlayer serverPlayer && form != null)
                    ChangedCriteriaTriggers.WHITE_LATEX_FUSE.trigger(serverPlayer, form.ticksWhiteLatex);
            }

            @Override
            public boolean shouldRemoveMover(Player player, InputWrapper input, LogicalSide side) {
                BlockState blockState = player.level.getBlockState(player.blockPosition());
                BlockState blockStateEye = player.level.getBlockState(player.eyeBlockPosition());

                if (!(whiteLatex(blockState) || whiteLatex(blockStateEye))) {
                    player.setInvulnerable(false);
                    player.playSound(ChangedSounds.POISON, 1.0f, 1.0f);
                    return true;
                }

                return player.isSpectator();
            }
        }
    }

    @Mod.EventBusSubscriber
    class EventSubscriber {
        static boolean whiteLatex(BlockState blockState) {
            if (blockState.getBlock() instanceof WhiteLatexTransportInterface transportInterface)
                return transportInterface.allowTransport(blockState);
            return (blockState.getProperties().contains(COVERED) && blockState.getValue(COVERED) == LatexType.WHITE_LATEX);
        }

        static boolean interactionCollide(LivingEntity entity, BlockPos pos, BlockState state) {
            VoxelShape collision = state.getCollisionShape(entity.level, pos, CollisionContext.of(entity));
            VoxelShape shapeMoved = collision.move(pos.getX(), pos.getY(), pos.getZ());
            return Shapes.joinIsNotEmpty(shapeMoved, Shapes.create(entity.getBoundingBox()), BooleanOp.AND);
        }

        @SubscribeEvent
        static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.END)
                return;

            /*if (event.player.getPersistentData().contains(TRANSPORT_TAG))
                entityEnterLatex(event.player, new BlockPos(event.player.getBlockX(), event.player.getBlockY(), event.player.getBlockZ()));*/

            BlockState blockState = event.player.level.getBlockState(event.player.blockPosition());
            BlockState blockStateEye = event.player.level.getBlockState(event.player.eyeBlockPosition());

            boolean colliding = interactionCollide(event.player, event.player.blockPosition(), blockState);
            boolean collidingEye = interactionCollide(event.player, event.player.eyeBlockPosition(), blockStateEye);

            if (!isEntityInWhiteLatex(event.player)) {
                if ((colliding && whiteLatex(blockState)) || (collidingEye && whiteLatex(blockStateEye))) {
                    ProcessTransfur.ifPlayerTransfurred(event.player, variant -> {
                        if (variant.getLatexType() == LatexType.WHITE_LATEX)
                            entityEnterLatex(event.player, new BlockPos(event.player.getBlockX(), event.player.getBlockY(), event.player.getBlockZ()));
                        else if (variant.getLatexType().isHostileTo(LatexType.WHITE_LATEX))
                            event.player.hurt(ChangedDamageSources.WHITE_LATEX, 2.0f);
                    }, () -> {
                        if (ProcessTransfur.progressPlayerTransfur(event.player, 4.8f, TransfurVariant.WHITE_LATEX_WOLF, TransfurContext.hazard(TransfurCause.WHITE_LATEX)))
                            entityEnterLatex(event.player, new BlockPos(event.player.getBlockX(), event.player.getBlockY(), event.player.getBlockZ()));
                    });
                }
            }

            else {
                /*var form = ProcessTransfur.getPlayerTransfurVariant(event.player);

                if (form != null) {
                    form.ticksWhiteLatex++;
                    form.getChangedEntity().overridePose = Pose.STANDING;
                } else
                    ProcessTransfur.transfur(event.player, event.player.level, TransfurVariant.WHITE_LATEX_WOLF, false, TransfurContext.hazard(TransfurCause.WHITE_LATEX));

                event.player.setInvisible(true);
                event.player.refreshDimensions();
                event.player.heal(0.0625F);
                if (event.player.tickCount % 50 == 0)
                    event.player.getFoodData().eat(Foods.DRIED_KELP.getNutrition(), Foods.DRIED_KELP.getSaturationModifier());
                event.player.resetFallDistance();

                multiplyMotion(event.player, 1.05F);
                if (!(whiteLatex(blockState) || whiteLatex(blockStateEye))) {
                    whiteLatexNoCollideMap.remove(event.player);
                    if (form != null) {
                        form.getChangedEntity().overridePose = null;
                        form.ticksWhiteLatex = 0;
                    }
                    event.player.getPersistentData().remove(TRANSPORT_TAG);
                    event.player.playSound(ChangedSounds.POISON, 1.0f, 1.0f);
                    event.player.setInvisible(false);
                    event.player.setInvulnerable(false);
                    event.player.refreshDimensions();
                }

                else if (event.player instanceof ServerPlayer serverPlayer && form != null)
                    ChangedCriteriaTriggers.WHITE_LATEX_FUSE.trigger(serverPlayer, form.ticksWhiteLatex);*/
            }
        }
    }
}
