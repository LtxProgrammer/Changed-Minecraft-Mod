package net.ltxprogrammer.changed.entity.latex;

import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.ai.LatexAssimilationDecision;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.InputWrapper;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.Foods;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.fml.LogicalSide;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class LatexSwimMover extends PlayerMover<LatexSwimMover.MoverInstance> {
    public static final float SIZE_HEIGHT = 4.0f / 16.0f;
    public static final float SIZE_RADIUS = 2.0f / 16.0f;

    @Override
    public LatexSwimMover.MoverInstance createInstance() {
        return new LatexSwimMover.MoverInstance(this);
    }

    public static class MoverInstance extends PlayerMoverInstance<LatexSwimMover> {
        private Vec3 lastPos = null;
        private int ticksInLatex = 0;
        private Direction surfaceDirection = null;
        private static final double ACCELERATION = 0.2;
        private static final double DECAY = 0.65;
        private static final Vec3 UP = new Vec3(0.0, 1.0, 0.0);

        public MoverInstance(LatexSwimMover parent) {
            super(parent);
        }

        public TransfurVariantInstance<?> getForm(Player player) {
            return ProcessTransfur.getPlayerTransfurVariant(player);
        }

        public LatexType getExpectedLatexType(LivingEntity entity) {
            return ChangedLatexTypes.WHITE_LATEX.get();
        }

        @Override
        public void saveTo(CompoundTag tag) {
            super.saveTo(tag);
            tag.putInt("ticksInLatex", ticksInLatex);
        }

        @Override
        public void readFrom(CompoundTag tag) {
            super.readFrom(tag);
            ticksInLatex = tag.getInt("ticksInLatex");
        }

        public void updateSurfaceDirection(Player player, InputWrapper input, LogicalSide side) {
            final LatexType expectedLatexType = getExpectedLatexType(player);
            final Vec3 center = player.getBoundingBox().getCenter();
            final BlockPos blockPos = EntityUtil.getBlock(center);
            final BlockState blockState = player.level().getBlockState(blockPos);
            final LatexCoverState coverState = LatexCoverState.getAt(player.level(), blockPos);
            surfaceDirection = null;
            if (blockState.getBlock() instanceof WhiteLatexTransportInterface transportInterface && !transportInterface.allowTransport(blockState))
                return;
            if (!coverState.isAir() && coverState.getType() == expectedLatexType) {
                double pX = center.x - blockPos.getX();
                double pY = center.y - blockPos.getY();
                double pZ = center.z - blockPos.getZ();
                Vec3 localized = new Vec3(pX, pY, pZ);
                Vec3 lookAngle = player.getLookAngle();
                Vec3 surface = coverState.findClosestSurface(localized, Direction.getNearest(lookAngle.x, lookAngle.y, lookAngle.z).getAxis());
                if (localized.equals(surface))
                    return;
                Vec3 delta = surface.subtract(localized);
                surfaceDirection = Direction.getNearest(delta.x, delta.y, delta.z);
                player.move(MoverType.SELF, surface.subtract(localized));
            }
        }

        public void maybeAdhereToSurface(Player player, InputWrapper input, LogicalSide side) {
            AABB testHitbox = player.getBoundingBox().inflate(0.05);
            final LatexType expectedLatexType = getExpectedLatexType(player);
            final Set<Vec3> surfaces = BlockPos.betweenClosedStream(testHitbox).map(blockPos -> {
                final BlockState blockState = player.level().getBlockState(blockPos);
                final LatexCoverState coverState = LatexCoverState.getAt(player.level(), blockPos);
                if (blockState.getBlock() instanceof WhiteLatexTransportInterface transportInterface && !transportInterface.allowTransport(blockState))
                    return null;
                if (!coverState.isAir() && coverState.getType() == expectedLatexType) {
                    final Vec3 center = player.getBoundingBox().getCenter();
                    AABB newHitbox = player.getBoundingBox().inflate(-0.05);
                    var collision = coverState.getCollisionShape(LatexCoverGetter.wrap(player.level()), blockPos, CollisionContext.empty()).move((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
                    if (!Shapes.joinIsNotEmpty(collision, Shapes.create(newHitbox), BooleanOp.AND)) {
                        double pX = center.x - blockPos.getX();
                        double pY = center.y - blockPos.getY();
                        double pZ = center.z - blockPos.getZ();
                        Vec3 localized = new Vec3(pX, pY, pZ);
                        Vec3 surface = coverState.findClosestSurface(localized, surfaceDirection == null ? null : surfaceDirection.getAxis());
                        if (localized.equals(surface))
                            return null;
                        return surface.subtract(localized);
                    }
                }

                return null;
            }).filter(Objects::nonNull).collect(Collectors.toSet());

            if (surfaces.size() == 1) {
                player.move(MoverType.SELF, surfaces.iterator().next());
            }
        }

        protected LatexAssimilationDecision<?> makeAssimilationDecision(LivingEntity target) {
            return LatexAssimilationDecision.fromBlockOrItem(ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF.get(), TransfurContext.hazard(TransfurCause.WHITE_LATEX), 4.8f);
        }

        @Override
        public void aiStep(Player player, InputWrapper input, LogicalSide side) {
            if (lastPos == null)
                lastPos = player.getPosition(1.0f);

            ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                if (ChangedLatexTypes.WHITE_LATEX.get().isHostileTo(variant.getLatexType()))
                    player.hurt(ChangedDamageSources.WHITE_LATEX.source(player.level().registryAccess()), 2.0f);
            }, () -> {
                ProcessTransfur.progressTransfur(player, this.makeAssimilationDecision(player));
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

            this.updateSurfaceDirection(player, input, side);

            Vec3 lookAngle = player.getLookAngle();
            Vec3 upAngle = player.getUpVector(1.0f);
            Vec3 leftAngle = upAngle.cross(lookAngle);

            if (surfaceDirection != null) {
                double yAdjustedX = lookAngle.x + (lookAngle.y < 0f ? upAngle.x : -upAngle.x);
                double yAdjustedZ = lookAngle.z + (lookAngle.y < 0f ? upAngle.z : -upAngle.z);
                lookAngle = switch (surfaceDirection) {
                    case UP -> new Vec3(yAdjustedX, Mth.clamp(lookAngle.y, 0, 1), yAdjustedZ);
                    case DOWN -> new Vec3(yAdjustedX, Mth.clamp(lookAngle.y, -1, 0), yAdjustedZ);
                    case NORTH -> new Vec3(lookAngle.x, (-lookAngle.z * 0.5f) + lookAngle.y, lookAngle.z);
                    case SOUTH -> new Vec3(lookAngle.x, (lookAngle.z * 0.5f) + lookAngle.y, lookAngle.z);
                    case EAST -> new Vec3(lookAngle.x, (lookAngle.x * 0.5f) + lookAngle.y, lookAngle.z);
                    case WEST -> new Vec3(lookAngle.x, (-lookAngle.x * 0.5f) + lookAngle.y, lookAngle.z);
                    default -> lookAngle;
                };
                lookAngle = lookAngle.normalize();
            }

            Vec2 horizontal = input.getMoveVector();
            double vertical = (input.getJumping() ? 1.0 : 0.0) + (input.getShiftKeyDown() ? -1.0 : 0.0);

            double moveSpeed = (input.getSprintKeyDown() ? 1.5 : 0.85) * ACCELERATION;

            Vec3 controlDir = lookAngle.multiply(horizontal.y, horizontal.y, horizontal.y)
                    .add(UP.multiply(vertical, vertical, vertical))
                    .add(leftAngle.multiply(horizontal.x, horizontal.x, horizontal.x)).normalize().multiply(moveSpeed, moveSpeed, moveSpeed);

            player.move(MoverType.SELF, controlDir.add(velocity));
            if (!input.getJumping())
                this.maybeAdhereToSurface(player, input, side);
            lastPos = currentPos;

            ticksInLatex++;
        }

        @Override
        public void serverAiStep(Player player, InputWrapper input, LogicalSide side) {
            var form = getForm(player);
            if (player instanceof ServerPlayer serverPlayer && form != null)
                ChangedCriteriaTriggers.WHITE_LATEX_FUSE.trigger(serverPlayer, ticksInLatex);
        }

        public boolean isInsideSwimableMaterial(LivingEntity entity) {
            AABB testHitbox = entity.getBoundingBox().inflate(0.05);
            final LatexType expectedLatexType = getExpectedLatexType(entity);
            return BlockPos.betweenClosedStream(testHitbox).anyMatch(blockPos -> {
                final BlockState blockState = entity.level().getBlockState(blockPos);
                final LatexCoverState coverState = LatexCoverState.getAt(entity.level(), blockPos);
                if (blockState.getBlock() instanceof WhiteLatexTransportInterface transportInterface)
                    return transportInterface.allowTransport(blockState);
                if (!coverState.isAir() && coverState.getType() == expectedLatexType) {
                    var shape = coverState.getSwimShape(LatexCoverGetter.wrap(entity.level()), blockPos, CollisionContext.of(entity)).move((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
                    return Shapes.joinIsNotEmpty(shape, Shapes.create(testHitbox), BooleanOp.AND);
                }

                return false;
            });
        }

        @Override
        public boolean shouldRemoveMover(Player player, InputWrapper input, LogicalSide side) {
            return !isInsideSwimableMaterial(player) || player.isSpectator() ||
                    ProcessTransfur.getPlayerTransfurVariantSafe(player)
                            .map(variant -> variant.getLatexType() != ChangedLatexTypes.WHITE_LATEX.get()).orElse(false);
        }

        @Override
        public EntityDimensions getDimensions(LivingEntity entity, Pose pose, EntityDimensions currentDimensions) {
            return EntityDimensions.fixed(SIZE_RADIUS * 2f, SIZE_HEIGHT);
        }

        @Override
        public float getEyeHeight(LivingEntity entity, Pose pose, float eyeHeight) {
            if (surfaceDirection == Direction.UP)
                return -0.25f;
            else if (surfaceDirection == null || surfaceDirection == Direction.DOWN)
                return 0.5f;
            return SIZE_HEIGHT * 0.5f;
        }

        @Override
        public void onAdd(Player player) {
            super.onAdd(player);

            player.refreshDimensions();
            player.setInvulnerable(true);

            if (!UniversalDist.isClientRemotePlayer(player))
                player.playSound(ChangedSounds.ENTITY_ENTER_LATEX.get(), 1.0f, 1.0f);
        }

        @Override
        public void onRemove(Player player) {
            super.onRemove(player);

            if (player.isSpectator())
                return;

            player.setInvulnerable(false);

            if (!UniversalDist.isClientRemotePlayer(player))
                player.playSound(ChangedSounds.ENTITY_EXIT_LATEX.get(), 1.0f, 1.0f);
        }
    }
}