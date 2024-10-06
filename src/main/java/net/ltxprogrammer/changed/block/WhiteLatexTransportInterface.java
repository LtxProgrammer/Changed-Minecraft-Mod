package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.InputWrapper;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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

        if (AbstractAbility.getAbilityInstanceSafe(entity, ChangedAbilities.GRAB_ENTITY_ABILITY.get())
                .map(grabAbility -> !grabAbility.suited && grabAbility.grabbedEntity != null).orElse(false))
            return;

        ProcessTransfur.transfur(entity, entity.level, ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF.get(), false, TransfurContext.hazard(TransfurCause.WHITE_LATEX));

        if (entity instanceof PlayerDataExtension ext && (!entity.level.isClientSide || UniversalDist.isLocalPlayer(entity)))
            ext.setPlayerMoverType(PlayerMover.WHITE_LATEX_MOVER.get());

        entity.refreshDimensions();
        entity.setInvulnerable(true);

        entity.playSound(ChangedSounds.POISON, 1.0f, 1.0f);
        if (!UniversalDist.isClientRemotePlayer(entity))
            entity.moveTo(pos, entity.getYRot(), entity.getXRot());
    }

    static boolean isBoundingBoxInWhiteLatex(LivingEntity entity) {
        return entity.level.getBlockStates(entity.getBoundingBox().inflate(-0.05)).anyMatch(blockState -> {
            if (blockState.getBlock() instanceof WhiteLatexTransportInterface transportInterface)
                return transportInterface.allowTransport(blockState);
            if (blockState.getProperties().contains(COVERED) && blockState.getValue(COVERED) == LatexType.WHITE_LATEX) {
                return blockState.isCollisionShapeFullBlock(entity.level, entity.blockPosition());
            }

            return false;
        });
    }

    class LatexMover extends PlayerMover<LatexMover.MoverInstance> {
        @Override
        public MoverInstance createInstance() {
            return new MoverInstance(this);
        }

        public static class MoverInstance extends PlayerMoverInstance<LatexMover> {
            private Vec3 lastPos = null;
            private int ticksWhiteLatex = 0;
            private static final double ACCELERATION = 0.2;
            private static final double DECAY = 0.65;
            private static final Vec3 UP = new Vec3(0.0, 1.0, 0.0);

            public MoverInstance(LatexMover parent) {
                super(parent);
            }

            public TransfurVariantInstance<?> getForm(Player player) {
                return ProcessTransfur.getPlayerTransfurVariant(player);
            }

            @Override
            public void saveTo(CompoundTag tag) {
                super.saveTo(tag);
                tag.putInt("ticksWhiteLatex", ticksWhiteLatex);
            }

            @Override
            public void readFrom(CompoundTag tag) {
                super.readFrom(tag);
                ticksWhiteLatex = tag.getInt("ticksWhiteLatex");
            }

            @Override
            public void aiStep(Player player, InputWrapper input, LogicalSide side) {
                if (lastPos == null)
                    lastPos = player.getPosition(1.0f);

                ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                    if (variant.getLatexType().isHostileTo(LatexType.WHITE_LATEX))
                        player.hurt(ChangedDamageSources.WHITE_LATEX, 2.0f);
                }, () -> {
                    ProcessTransfur.progressPlayerTransfur(player, 4.8f, ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF.get(), TransfurContext.hazard(TransfurCause.WHITE_LATEX));
                });

                player.setDeltaMovement(0, 0, 0);
                player.refreshDimensions();
                player.heal(0.0625F);
                if (player.tickCount % 50 == 0)
                    player.getFoodData().eat(Foods.DRIED_KELP.getNutrition(), Foods.DRIED_KELP.getSaturationModifier());
                player.resetFallDistance();

                Vec3 currentPos = player.getPosition(1.0f);
                Vec3 velocity = currentPos.subtract(lastPos).multiply(DECAY, DECAY, DECAY);
                if (velocity.distanceToSqr(Vec3.ZERO) < 0.00000625)
                    velocity = Vec3.ZERO;

                Vec3 lookAngle = player.getLookAngle();
                Vec3 upAngle = player.getUpVector(1.0f);
                Vec3 leftAngle = upAngle.cross(lookAngle);

                Vec2 horizontal = input.getMoveVector();
                double vertical = (input.getJumping() ? 1.0 : 0.0) + (input.getShiftKeyDown() ? -1.0 : 0.0);

                double moveSpeed = (input.getSprintKeyDown() ? 1.5 : 0.85) * ACCELERATION;

                Vec3 controlDir = lookAngle.multiply(horizontal.y, horizontal.y, horizontal.y)
                        .add(UP.multiply(vertical, vertical, vertical))
                        .add(leftAngle.multiply(horizontal.x, horizontal.x, horizontal.x)).normalize().multiply(moveSpeed, moveSpeed, moveSpeed);

                player.move(MoverType.SELF, controlDir.add(velocity));
                lastPos = currentPos;

                ticksWhiteLatex++;
            }

            @Override
            public void serverAiStep(Player player, InputWrapper input, LogicalSide side) {
                var form = getForm(player);
                if (player instanceof ServerPlayer serverPlayer && form != null)
                    ChangedCriteriaTriggers.WHITE_LATEX_FUSE.trigger(serverPlayer, ticksWhiteLatex);
            }

            @Override
            public boolean shouldRemoveMover(Player player, InputWrapper input, LogicalSide side) {
                return !isBoundingBoxInWhiteLatex(player) || player.isSpectator() ||
                        ProcessTransfur.getPlayerTransfurVariantSafe(player)
                                .map(variant -> variant.getLatexType() != LatexType.WHITE_LATEX).orElse(false);
            }

            @Override
            public void onRemove(Player player) {
                super.onRemove(player);

                if (player.isSpectator())
                    return;

                player.setInvulnerable(false);
                player.playSound(ChangedSounds.POISON, 1.0f, 1.0f);
            }
        }
    }

    @Mod.EventBusSubscriber
    class EventSubscriber {
        @SubscribeEvent
        static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.END)
                return;

            if (!isEntityInWhiteLatex(event.player) && isBoundingBoxInWhiteLatex(event.player)) {
                ProcessTransfur.ifPlayerTransfurred(event.player, variant -> {
                    if (variant.getLatexType() == LatexType.WHITE_LATEX)
                        entityEnterLatex(event.player, new BlockPos(event.player.getBlockX(), event.player.getBlockY(), event.player.getBlockZ()));
                    else if (variant.getLatexType().isHostileTo(LatexType.WHITE_LATEX))
                        event.player.hurt(ChangedDamageSources.WHITE_LATEX, 2.0f);
                }, () -> {
                    if (ProcessTransfur.progressPlayerTransfur(event.player, 4.8f, ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF.get(), TransfurContext.hazard(TransfurCause.WHITE_LATEX)))
                        entityEnterLatex(event.player, new BlockPos(event.player.getBlockX(), event.player.getBlockY(), event.player.getBlockZ()));
                });
            }
        }
    }
}
