package net.ltxprogrammer.changed.server;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.ability.tree.NodeEffect;
import net.ltxprogrammer.changed.ability.tree.events.StatCriteria;
import net.ltxprogrammer.changed.ability.tree.events.TimeBreathingFluid;
import net.ltxprogrammer.changed.ability.tree.events.TimeInFluid;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.network.packet.SyncActiveNodeEffectsPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ServerTransfurVariantInstance<T extends ChangedEntity> extends TransfurVariantInstance<T> {
    private final ServerPlayer host;

    protected final List<NodeEffect> lastSentNodeEffects = new ArrayList<>();

    protected static class FluidSubmersionVariables {
        final FluidType fluidType;
        int ticksTouching;
        int ticksSubmerged;
        int ticksBreathing;

        public FluidSubmersionVariables(FluidType fluidType) {
            this.fluidType = fluidType;
            this.ticksTouching = 0;
            this.ticksSubmerged = 0;
            this.ticksBreathing = 0;
        }

        public FluidSubmersionVariables resetTouching() {
            this.ticksTouching = 0;
            this.ticksSubmerged = 0;
            return this;
        }

        public void tickTouching(ServerTransfurVariantInstance<?> variantInstance, boolean submerged) {
            AbilityTreeInstance.offerPointEvent(variantInstance, ChangedAbilityPointEvents.TIME_IN_FLUID.get(),
                    new TimeInFluid.Criteria(this.ticksTouching, 1, fluidType, false));
            this.ticksTouching++;

            if (submerged) {
                AbilityTreeInstance.offerPointEvent(variantInstance, ChangedAbilityPointEvents.TIME_IN_FLUID.get(),
                        new TimeInFluid.Criteria(this.ticksSubmerged, 1, fluidType, true));
                this.ticksSubmerged++;
            }
            else
                this.ticksSubmerged = 0;
        }

        public FluidSubmersionVariables resetBreathing() {
            this.ticksBreathing = 0;
            return this;
        }

        public void tickBreathing(ServerTransfurVariantInstance<?> variantInstance) {
            AbilityTreeInstance.offerPointEvent(variantInstance, ChangedAbilityPointEvents.TIME_BREATHING_FLUID.get(),
                    new TimeBreathingFluid.Criteria(this.ticksBreathing, 1, fluidType));
            this.ticksBreathing++;

            if (fluidType == ForgeMod.WATER_TYPE.get()) {
                ChangedCriteriaTriggers.AQUATIC_BREATHE.trigger(variantInstance.host, this.ticksBreathing);
            }
        }

        public @Nullable CompoundTag save() {
            if (this.ticksTouching == 0 && this.ticksSubmerged == 0 && this.ticksBreathing == 0)
                return null;

            var tag = new CompoundTag();
            if (this.ticksTouching != 0)
                tag.putInt("touching", this.ticksTouching);
            if (this.ticksSubmerged != 0)
                tag.putInt("submerged", this.ticksSubmerged);
            if (this.ticksBreathing != 0)
                tag.putInt("breathing", this.ticksBreathing);
            return tag;
        }

        public void load(CompoundTag tag) {
            if (tag.contains("touching"))
                this.ticksTouching = tag.getInt("touching");
            if (tag.contains("submerged"))
                this.ticksSubmerged = tag.getInt("submerged");
            if (tag.contains("breathing"))
                this.ticksBreathing = tag.getInt("breathing");
        }
    }

    protected final Map<FluidType, FluidSubmersionVariables> ticksInFluids = new Object2ObjectArrayMap<>();

    public ServerTransfurVariantInstance(TransfurVariant<T> parent, ServerPlayer host) {
        super(parent, host);
        this.host = host;
    }

    @Override
    public CompoundTag saveForStorage() {
        var tag = super.saveForStorage();

        {
            var fluidTicksTag = new CompoundTag();
            ticksInFluids.forEach((fluidType, vars) -> {
                var varTag = vars.save();
                if (varTag == null)
                    return;

                fluidTicksTag.put(ForgeRegistries.FLUID_TYPES.get().getKey(fluidType).toString(), varTag);
            });
            tag.put("fluidTicks", fluidTicksTag);
        }

        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        this.ticksInFluids.clear();
        if (tag.contains("fluidTicks")) {
            var fluidTicksTag = tag.getCompound("fluidTicks");
            fluidTicksTag.getAllKeys().forEach(keyStr -> {
                var fluidKey = ResourceLocation.parse(keyStr);
                if (!ForgeRegistries.FLUID_TYPES.get().containsKey(fluidKey))
                    return;

                var fluidType = ForgeRegistries.FLUID_TYPES.get().getValue(fluidKey);
                this.ticksInFluids.computeIfAbsent(fluidType, FluidSubmersionVariables::new)
                        .load(fluidTicksTag.getCompound(keyStr));
            });
        }
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

        FluidType submergedFluid = host.getEyeInFluidType();
        ForgeRegistries.FLUID_TYPES.get().getValues().forEach(fluidType -> {
            boolean submerged = submergedFluid == fluidType;
            boolean breathing = submerged && event.canRefillAir();

            if (!breathing) {
                this.ticksInFluids.computeIfPresent(fluidType, (type, pair) -> pair.resetBreathing());
            } else {
                this.ticksInFluids.computeIfAbsent(fluidType, FluidSubmersionVariables::new).tickBreathing(this);
            }
        });
    }

    public final Map<Attribute, UUID> attributesByUUID = new HashMap<>();

    protected void tickAbilityTree() {
        var abilityTree = ((PlayerDataExtension)host).getAbilityTree();
        abilityTree.updateTrees(host);
        variantFeatures.clear();
        activeNodeEffects.clear();
        abilityTree.gatherNodeEffects(this, nodeEffect -> {
            activeNodeEffects.put(nodeEffect.getCodec(), nodeEffect);
        });

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

        List<NodeEffect> syncedEffects = activeNodeEffects.values().stream()
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
    public void tickAge() {
        int pre = ageAsVariant;
        super.tickAge();
        int delta = ageAsVariant - pre;

        AbilityTreeInstance.offerPointEvent(this, ChangedAbilityPointEvents.TIME_AS_VARIANT.get(), new StatCriteria(pre, delta));
    }

    @Override
    public void tick() {
        this.tickAbilityTree();

        super.tick();

        if (parent.getEntityType().is(ChangedTags.EntityTypes.LATEX))
            host.removeEffect(ChangedEffects.HYPERCOAGULATION.get());

        this.tickScare();

        if (shouldApplyAbilities()) {
            FluidType submergedFluid = host.getEyeInFluidType();
            ForgeRegistries.FLUID_TYPES.get().getValues().forEach(fluidType -> {
                boolean submerged = submergedFluid == fluidType;
                boolean touching = submerged || host.isInFluidType(fluidType);

                if (!submerged && !touching) {
                    this.ticksInFluids.computeIfPresent(fluidType, (type, pair) -> pair.resetTouching());
                } else {
                    this.ticksInFluids.computeIfAbsent(fluidType, FluidSubmersionVariables::new).tickTouching(this, submerged);
                }
            });
        }
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
