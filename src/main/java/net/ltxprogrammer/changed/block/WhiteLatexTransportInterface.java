package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.ai.LatexAssimilationDecision;
import net.ltxprogrammer.changed.entity.latex.LatexSwimMover;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

public interface WhiteLatexTransportInterface {
    static boolean isEntityInWhiteLatex(LivingEntity entity) {
        if (entity instanceof PlayerDataExtension ext)
            return ext.getPlayerMover() != null && ext.getPlayerMover().is(PlayerMover.LATEX_SWIM.get());
        return false;
    }

    default boolean allowTransport(BlockState blockState) {
        return true;
    }

    static void entityEnterLatex(LivingEntity entity, BlockPos pos) {
        if (entity.level().isClientSide)
            return;

        if (TransfurVariant.getEntityVariant(entity) != null && !(TransfurVariant.getEntityVariant(entity).getEntityType().is(ChangedTags.EntityTypes.WHITE_LATEX_SWIMMING)))
            return;

        if (isEntityInWhiteLatex(entity) || entity.isDeadOrDying())
            return;

        if (entity instanceof Player player && player.isSpectator())
            return;

        if (AbstractAbility.getAbilityInstanceSafe(entity, ChangedAbilities.GRAB_ENTITY_ABILITY.get())
                .map(grabAbility -> !grabAbility.suited && grabAbility.grabbedEntity != null).orElse(false))
            return;

        ProcessTransfur.progressTransfur(entity, LatexAssimilationDecision.strong(LatexAssimilationDecision.Method.ABSORPTION,
                ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF.get(),
                TransfurContext.hazard(TransfurCause.WHITE_LATEX), 8.0f));

        if (entity instanceof PlayerDataExtension ext && (!entity.level().isClientSide || UniversalDist.isLocalPlayer(entity)))
            ext.setPlayerMoverType(PlayerMover.LATEX_SWIM.get());
        else {
            entity.refreshDimensions();
            entity.setInvulnerable(true);

            entity.playSound(ChangedSounds.ENTITY_ENTER_LATEX.get(), 1.0f, 1.0f);
        }

        final Vec3 center = new Vec3(0.5D, 0.5D, 0.5D);
        Vec3 surface = LatexCoverState.getAt(entity.level(), pos).findClosestSurface(center, null);
        Vec3 delta = surface.subtract(center);
        final Direction closestDirection = center.equals(surface) ? null : Direction.getNearest(delta.x, delta.y, delta.z);
        final Vec3 surfaceNormal = closestDirection == null ? null : new Vec3(closestDirection.getNormal().getX(), closestDirection.getNormal().getY(), closestDirection.getNormal().getZ())
                .multiply(-1, -1, -1);
        surface = closestDirection == null ? surface : switch (closestDirection) {
            case NORTH, SOUTH, EAST, WEST -> surface.add(surfaceNormal.multiply(LatexSwimMover.SIZE_RADIUS, LatexSwimMover.SIZE_RADIUS, LatexSwimMover.SIZE_RADIUS));
            case UP -> surface.add(surfaceNormal.multiply(LatexSwimMover.SIZE_HEIGHT, LatexSwimMover.SIZE_HEIGHT, LatexSwimMover.SIZE_HEIGHT));
            default -> surface;
        };

        entity.teleportTo(pos.getX() + surface.x, pos.getY() + surface.y, pos.getZ() + surface.z);
    }

    static Optional<BlockPos> isBoundingBoxInWhiteLatex(LivingEntity entity) {
        AABB testHitbox = entity.getBoundingBox().inflate(-0.15);
        return BlockPos.betweenClosedStream(testHitbox).filter(blockPos -> {
            final BlockState blockState = entity.level().getBlockState(blockPos);
            if (blockState.getBlock() instanceof WhiteLatexTransportInterface transportInterface)
                return transportInterface.allowTransport(blockState);

            return false;
        }).findFirst();
    }

    @Mod.EventBusSubscriber
    class EventSubscriber {
        protected LatexAssimilationDecision<?> makeAssimilationDecision(LivingEntity target) {
            return LatexAssimilationDecision.fromBlockOrItem(ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF.get(), TransfurContext.hazard(TransfurCause.WHITE_LATEX), 4.8f);
        }

        @SubscribeEvent
        static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END)
                return;

            if (isEntityInWhiteLatex(event.player))
                return;

            isBoundingBoxInWhiteLatex(event.player).ifPresent(latexPosition -> {
                ProcessTransfur.ifPlayerTransfurred(event.player, variant -> {
                    if (variant.getLatexType() == ChangedLatexTypes.WHITE_LATEX.get())
                        entityEnterLatex(event.player, latexPosition);
                    else if (ChangedLatexTypes.WHITE_LATEX.get().isHostileTo(variant.getLatexType()))
                        event.player.hurt(ChangedDamageSources.WHITE_LATEX.source(event.player.level().registryAccess()), 2.0f);
                }, () -> {
                    ProcessTransfur.progressTransfur(event.player, LatexAssimilationDecision.fromBlockOrItem(ChangedTransfurVariants.PURE_WHITE_LATEX_WOLF.get(), TransfurContext.hazard(TransfurCause.WHITE_LATEX), 4.8f, newEntity -> {
                        entityEnterLatex(newEntity.getEntity(), latexPosition);
                    }));
                });
            });
        }
    }
}
