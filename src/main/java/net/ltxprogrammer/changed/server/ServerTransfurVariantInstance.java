package net.ltxprogrammer.changed.server;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.ability.tree.NodeEffect;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.network.packet.SyncActiveNodeEffectsPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;

public class ServerTransfurVariantInstance<T extends ChangedEntity> extends TransfurVariantInstance<T> {
    private final ServerPlayer host;

    protected final List<NodeEffect> lastSentNodeEffects = new ArrayList<>();

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
                this.getParent().replaceEntity(host, transfurContext.source());
        }

        if (transfurProgression >= 1f && !isTemporaryFromSuit()) {
            transfurContext = transfurContext.withoutSource();
            if (willSurviveTransfur)
                ChangedCriteriaTriggers.TRANSFUR.trigger(host, this);
        }
    }

    @Override
    protected void tickFlying() {
        super.tickFlying();

        if (this.canCreativeFly() && shouldApplyAbilities()) {
            if (!host.isSpectator() && host.getAbilities().flying)
                ChangedCriteriaTriggers.FLYING.trigger(host, ticksFlying);
        }

        this.entity.setChangedEntityFlag(ChangedEntity.FLAG_IS_FLYING, host.getAbilities().flying &&
                host.getVehicle() == null);
    }

    @Override
    protected void tickBreathing(LivingBreatheEvent event) {
        super.tickBreathing(event);

        if (host.isAlive() && breatheMode.canBreatheWater() && shouldApplyAbilities() && host.isEyeInFluidType(ForgeMod.WATER_TYPE.get())) {
            ChangedCriteriaTriggers.AQUATIC_BREATHE.trigger(host, this.ticksBreathingUnderwater);
        }
    }

    public final Map<Attribute, UUID> attributesByUUID = new HashMap<>();

    protected void tickAbilityTree() {
        var abilityTree = ((PlayerDataExtension)host).getAbilityTree();
        abilityTree.updateTrees();
        activeNodeEffects.clear();
        abilityTree.gatherNodeEffects(this, activeNodeEffects::add);

        {
            Map<Attribute, Double> attributeAdders = getBaseAttributeValues(this.getHost().getAttributes());
            Map<Attribute, Double> baselineAttributes = Map.copyOf(attributeAdders);
            attributeAdders.replaceAll((a, v) -> 0.0);

            visitActiveNodeEffects(ChangedAbilityTreeCodecs.ATTRIBUTE_MODIFIER_EFFECT.get(), attributeModifier -> {
                if (!baselineAttributes.containsKey(attributeModifier.attribute))
                    return;

                switch (attributeModifier.method) {
                    case MULTIPLY_BASE -> {
                        attributeAdders.computeIfPresent(attributeModifier.attribute, (attr, current) -> {
                            return current + baselineAttributes.get(attr) * attributeModifier.factor;
                        });
                    }
                    case ADD -> {
                        attributeAdders.computeIfPresent(attributeModifier.attribute, (attr, current) -> {
                            return current + attributeModifier.factor;
                        });
                    }
                }
            });

            var attributes = host.getAttributes();
            attributeAdders.forEach((attribute, value) -> {
                var uuid = attributesByUUID.computeIfAbsent(attribute, ignored -> Mth.createInsecureUUID(RandomSource.createNewThreadLocalInstance()));
                var instance = attributes.getInstance(attribute);
                if (instance == null)
                    return;
                var existing = instance.getModifier(uuid);
                if (existing != null && existing.getAmount() == value)
                    return;

                if (existing == null && value == 0.0)
                    return;

                instance.removeModifier(uuid);
                if (value != 0.0)
                    instance.addTransientModifier(new AttributeModifier(uuid, "AbilityTree-Modifier", value, AttributeModifier.Operation.ADDITION));
            });
        }

        visitActiveNodeEffects(ChangedAbilityTreeCodecs.MOB_EFFECT_EFFECT.get(), mobEffectNode -> {
            host.addEffect(new MobEffectInstance(mobEffectNode.mobEffect));
        });

        List<NodeEffect> syncedEffects = activeNodeEffects.stream()
                .map(NodeEffect::getClientNodeEffect)
                .filter(Optional::isPresent)
                .map(Optional::get).toList();
        if (!lastSentNodeEffects.equals(syncedEffects)) {
            lastSentNodeEffects.clear();
            lastSentNodeEffects.addAll(syncedEffects);

            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(this::getHost),
                    new SyncActiveNodeEffectsPacket(host.getId(), lastSentNodeEffects));
        }
    }

    @Override
    public void tick() {
        this.tickAbilityTree();

        super.tick();

        if (parent.getEntityType().is(ChangedTags.EntityTypes.LATEX))
            host.removeEffect(ChangedEffects.HYPERCOAGULATION.get());

        this.tickScare();
    }

    public void tickScare() {
        if (this.parent.scares == null)
            return;

        final double distance = 8D;
        final double farRunSpeed = 1.0D;
        final double nearRunSpeed = 1.2D;

        if (host.isCreative() || host.isSpectator())
            return;

        List<PathfinderMob> entitiesScared = host.level().getEntitiesOfClass(
                PathfinderMob.class,
                host.getBoundingBox().inflate(distance, 6D, distance),
                target -> {
                    return this.parent.scares.test(this.entity, target) && target.hasLineOfSight(host);
                });

        for (var v : entitiesScared) {
            final double speedScale = (v instanceof AbstractVillager) ? 0.5D : 1.0D;

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

    @Override
    public void unhookAll(Player player) {
        super.unhookAll(player);
        attributesByUUID.forEach((attribute, uuid) -> {
            var instance = player.getAttributes().getInstance(attribute);
            if (instance == null)
                return;

            instance.removeModifier(uuid);
        });
    }
}
