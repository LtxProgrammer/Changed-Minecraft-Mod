package net.ltxprogrammer.changed.server;

import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedCriteriaTriggers;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ServerTransfurVariantInstance<T extends ChangedEntity> extends TransfurVariantInstance<T> {
    private final ServerPlayer host;

    public ServerTransfurVariantInstance(TransfurVariant<T> parent, ServerPlayer host) {
        super(parent, host);
        this.host = host;
    }

    @Override
    public boolean checkForTemporary() {
        final var grabber = GrabEntityAbility.getGrabber(this.host);

        if (super.checkForTemporary())
            return true;
        else if (isTemporaryFromSuit) {
            if (grabber == null || grabber.getEntity().isDeadOrDying() || grabber.getEntity().isRemoved()) { // Remove variant if grabber doesn't exist
                ProcessTransfur.removePlayerTransfurVariant(this.host);
                return true;
            }

            var ability = grabber.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (ability == null || ability.grabbedEntity != this.host) {
                ProcessTransfur.removePlayerTransfurVariant(this.host);
                return true;
            }
        }

        return false;
    }

    @Override
    protected void tickTransfurProgress() {
        super.tickTransfurProgress();

        if (transfurProgressionO < 1f && transfurProgression >= 1f) {
            if (!willSurviveTransfur)
                this.getParent().replaceEntity(host, transfurContext.source);
        }

        if (transfurProgression >= 1f && !isTemporaryFromSuit()) {
            transfurContext = transfurContext.withSource(null);
            if (willSurviveTransfur)
                ChangedCriteriaTriggers.TRANSFUR.trigger(host, getParent());
        }
    }

    @Override
    protected void tickFlying() {
        super.tickFlying();

        if (parent.canGlide && shouldApplyAbilities()) {
            if (!host.isSpectator() && host.getAbilities().flying)
                ChangedCriteriaTriggers.FLYING.trigger(host, ticksFlying);

            this.entity.setChangedEntityFlag(ChangedEntity.FLAG_IS_FLYING, host.getAbilities().flying);
        }
    }

    @Override
    protected void tickBreathing() {
        super.tickBreathing();

        if (host.isAlive() && parent.breatheMode.canBreatheWater() && shouldApplyAbilities() && host.isEyeInFluid(FluidTags.WATER)) {
            ChangedCriteriaTriggers.AQUATIC_BREATHE.trigger(host, this.ticksBreathingUnderwater);
        }
    }

    @Override
    public void tick() {
        super.tick();

        final double distance = 8D;
        final double farRunSpeed = 1.0D;
        final double nearRunSpeed = 1.2D;
        for (Class<? extends PathfinderMob> entityClass : parent.scares) {
            if (entityClass.isAssignableFrom(AbstractVillager.class) && (!parent.ctor.get().is(ChangedTags.EntityTypes.LATEX) || host.isCreative() || host.isSpectator()))
                continue;

            final double speedScale = entityClass.isAssignableFrom(AbstractVillager.class) ? 0.5D : 1.0D;

            List<? extends PathfinderMob> entitiesScared = host.level.getEntitiesOfClass(entityClass, host.getBoundingBox().inflate(distance, 6D, distance), entity -> entity.hasLineOfSight(host));

            for (var v : entitiesScared) {
                //if the creature has no path, or the target path is < distance, make the creature run.
                if (v.getNavigation().getPath() == null || host.distanceToSqr(v.getNavigation().getTargetPos().getX(), v.getNavigation().getTargetPos().getY(), v.getNavigation().getTargetPos().getZ()) < distance * distance) {
                    Vec3 vector3d = DefaultRandomPos.getPosAway(v, 16, 7, new Vec3(host.getX(), host.getY(), host.getZ()));

                    if (vector3d != null && host.distanceToSqr(vector3d) > host.distanceToSqr(v)) {
                        Path path = v.getNavigation().createPath(vector3d.x, vector3d.y, vector3d.z, 0);

                        if (path != null) {
                            double speed = v.distanceToSqr(host) < 49D ? nearRunSpeed : farRunSpeed;
                            v.getNavigation().moveTo(path, speed * speedScale);
                        }
                    }
                }
                else {
                    double speed = v.distanceToSqr(host) < 49D ? nearRunSpeed : farRunSpeed;
                    v.getNavigation().setSpeedModifier(speed * speedScale);
                }

                if (v.getTarget() == host)
                    v.setTarget(null);
            }
        }
    }
}
