package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.ai.LatexAssimilationDecision;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.KeyStatesTracker;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class GrabEntityAbilityInstance extends AbstractAbilityInstance {
    @Nullable
    public LivingEntity grabbedEntity = null;
    public boolean grabbedHasControl = false;
    public boolean suited = false;
    public float grabStrength = 0.0f;
    public float grabStrengthO = 0.0f;
    public float suitTransition = 0.0f;
    public float suitTransitionO = 0.0f;

    public static final float SUIT_TRANSITION_MAX = 3.0f;

    public static final float GRAB_STRENGTH_DECAY = 0.1f / 20;
    public static final float GRAB_STRENGTH_DECAY_SUITED = 0.01f / 20;

    public static final float GRAB_STRENGTH_DECAY_PLAYER = 0.21f;
    public static final float GRAB_STRENGTH_DECAY_PLAYER_SUITED = 0.12f;
    public static final float GRAB_STRENGTH_DECAY_PENALTY = 0.2f;

    private static final int GRAB_ESCAPE_TRUST = 5;

    public boolean shouldRenderLatex() {
        return !shouldRenderGrabbedEntity();
    }

    public boolean shouldRenderGrabbedEntity() {
        return grabbedHasControl && grabbedEntity != null;
    }

    public boolean shouldAnimateArms() {
        return grabbedEntity != null && !suited;
    }

    public boolean canGrabbedEntityBeStolen() {
        return grabbedEntity != null && !suited;
    }

    public float getGrabStrength(float partialTicks) {
        return Mth.lerp(partialTicks, grabStrengthO, grabStrength);
    }

    public float getSuitTransitionProgress(float partialTicks) {
        return Mth.lerp(partialTicks, suitTransitionO, suitTransition) / SUIT_TRANSITION_MAX;
    }

    public LivingEntity getHoveredEntity(IAbstractChangedEntity entity) {
        if (!(entity.getEntity() instanceof Player player))
            return null;

        if (!UniversalDist.isLocalPlayer(player))
            return null;

        var hitResult = UniversalDist.getLocalHitResult();
        if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof Player targetPlayer && ProcessTransfur.isPlayerTransfurred(targetPlayer))
                return null;
            if (livingEntity.getType().is(ChangedTags.EntityTypes.HUMANOIDS) || livingEntity instanceof Player)
                return livingEntity;
        }
        return null;
    }

    public GrabEntityAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    @Override
    public AbstractAbility.UseType getUseType() {
        return AbstractAbility.UseType.HOLD;
    }

    @Override
    public void onSelected() {
        if (entity.getLevel().isClientSide)
            this.entity.displayClientMessage(Component.translatable("ability.changed.grab_entity.how_to_grab", KeyReference.ABILITY.getName(entity.getLevel())), true);
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canKeepUsing() {
        return true;
    }

    public void applyReleaseDebuff(float scale, @Nullable LivingEntity grabbed) {
        if (this.entity.getEntity().level().isClientSide)
            return;

        this.getController().forceCooldown(Mth.lerpInt(scale, 20 * 2 /* 2 Seconds */, 20 * 8 /* 8 Seconds */));
        this.ability.setDirty(this.entity);

        this.entity.getEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,
                Mth.lerpInt(scale, 0 /* 0 Seconds */, 20 * 5 /* 5 Seconds */), 1)); // Slowness II (0-5 seconds)
        this.entity.getEntity().addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,
                Mth.lerpInt(scale, 0 /* 0 Seconds */, 20 * 3 /* 3 Seconds */), 7)); // Mining Fatigue VIII (0-3 seconds)
        this.entity.getEntity().hurt(ChangedDamageSources.GRAB_ESCAPE.source(this.entity.getEntity().level().registryAccess(), grabbed),
                Mth.lerp(scale, 0.0F /* 0 Damage */, 6.0F /* 3 Hearts Damage */));
    }

    public void releaseEntity(boolean applyDebuffs) {
        var debuffStrength = 1.0F - this.grabStrength;

        this.grabbedHasControl = false;
        this.grabStrength = 0.0f;
        this.suitTransition = 0.0f;
        this.ticksGrabbed = 0;
        this.currentEscapeKey = null;
        this.escapeKeyRandom = null;
        this.lastEscapeKey = null;

        this.escapeKeys.reset(false);

        if (this.grabbedEntity == null) return;

        if (this.grabbedEntity instanceof LivingEntityDataExtension ext)
            ext.setGrabbedBy(null);

        if (this.entity.getEntity() instanceof Player player && player == UniversalDist.getLocalPlayer())
            Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.release(player, this.grabbedEntity));
        if (this.entity.getEntity() instanceof Player player) {
            this.grabbedEntity.setDeltaMovement(Vec3.ZERO);

            if (ProcessTransfur.isPlayerTransfurred(player)) {
                ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                    if (variant.isTemporaryFromSuit())
                        ProcessTransfur.removePlayerTransfurVariant(player);
                });
            }
        }

        if (!(this.grabbedEntity instanceof Player)) {
            debuffStrength *= 0.25F;
        }
        this.grabbedEntity.noPhysics = false;
        this.grabbedEntity.resetFallDistance();
        this.entity.getEntity().noPhysics = false;
        var lastGrabbed = this.grabbedEntity;
        this.grabbedEntity = null;
        this.suited = false;
        this.attackDown = false;
        this.useDown = false;

        if (applyDebuffs)
            this.applyReleaseDebuff(debuffStrength, lastGrabbed);
    }

    public void replaceEntityReference(LivingEntity newEntity) {
        if (newEntity == grabbedEntity)
            return;

        if (grabbedEntity instanceof LivingEntityDataExtension ext)
            ext.setGrabbedBy(null);
        if (newEntity instanceof LivingEntityDataExtension ext)
            ext.setGrabbedBy(entity.getEntity());

        this.grabbedEntity = newEntity;

        if (!entity.getLevel().isClientSide)
            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity::getEntity),
                    new GrabEntityPacket(entity.getEntity(), newEntity, GrabEntityPacket.GrabType.REPLACE));
    }

    public boolean suitEntity(LivingEntity entity) {
        var prevGrabber = GrabEntityAbility.getGrabberSafe(entity)
                .flatMap(current -> current.getAbilityInstanceSafe(ChangedAbilities.GRAB_ENTITY_ABILITY.get()))
                .filter(current -> current != this);

        if (prevGrabber.map(ability -> !ability.canGrabbedEntityBeStolen()).orElse(false))
            return false;

        getController().resetHoldTicks();

        ProcessTransfur.forceNearbyToRetarget(entity.level(), entity);

        if (this.grabbedEntity != entity)
            this.releaseEntity(true);

        this.grabbedEntity = entity;
        this.suited = true;
        this.grabStrength = 1.0f;

        if (entity instanceof Player player && !ProcessTransfur.isPlayerTransfurred(player)) {
            ProcessTransfur.setPlayerTransfurVariant(player, this.entity.getSelfVariant(), TransfurContext.latexHazard(this.entity, TransfurCause.GRAB_REPLICATE), 1.0f, true,
                    preApplyVariant -> {
                        this.entity.getChangedEntity().onSuitOther(
                                IAbstractChangedEntity.forPlayerWithVariant(player, preApplyVariant));
                    });
        }

        prevGrabber.ifPresent(this::stealGrabFrom);
        return true;
    }

    public boolean grabEntity(LivingEntity entity) {
        var prevGrabber = GrabEntityAbility.getGrabberSafe(entity)
                .flatMap(current -> current.getAbilityInstanceSafe(ChangedAbilities.GRAB_ENTITY_ABILITY.get()))
                .filter(current -> current != this);

        if (prevGrabber.map(ability -> !ability.canGrabbedEntityBeStolen()).orElse(false))
            return false;

        getController().resetHoldTicks();

        if (entity instanceof Player player && ProcessTransfur.isPlayerTransfurred(player)) {
            ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                if (variant.isTemporaryFromSuit())
                    ProcessTransfur.removePlayerTransfurVariant(player);
            });
        }

        if (this.grabbedEntity == entity) {
            this.suited = false;
            this.grabbedHasControl = false;
            return true;
        }

        this.releaseEntity(true);
        this.grabbedEntity = entity;
        this.grabStrength = 1.0f;

        prevGrabber.ifPresent(this::stealGrabFrom);
        return true;
    }

    protected void stealGrabFrom(GrabEntityAbilityInstance other) {
        if (other == this)
            return;

        // Copy escape state
        this.grabStrength = other.grabStrength;
        this.grabStrengthO = other.grabStrengthO;
        this.escapeKeys.copyFrom(other.escapeKeys);
        this.ticksUnpressed = other.ticksUnpressed;
        this.lastEscapeKey = other.lastEscapeKey;
        this.currentEscapeKey = other.currentEscapeKey;
        this.ticksGrabbed = other.ticksGrabbed;
        other.releaseEntity(false);
    }

    @Override
    public void onRemove() {
        releaseEntity(false);
    }

    void handleInstructions(Level level) {
        if (!level.isClientSide) return;

        if (instructionTicks == 180)
            this.entity.displayClientMessage(Component.translatable("ability.changed.grab_entity.how_to_release", KeyReference.ABILITY.getName(level)), true);
        else if (instructionTicks == 120)
            this.entity.displayClientMessage(Component.translatable("ability.changed.grab_entity.how_to_transfur", KeyReference.ATTACK.getName(level)), true);
        else if (instructionTicks == 60)
            this.entity.displayClientMessage(Component.translatable("ability.changed.grab_entity.how_to_suit", KeyReference.USE.getName(level)), true);
        if (instructionTicks > 0)
            instructionTicks--;

        if (instructionTicks == -180)
            this.entity.displayClientMessage(Component.translatable("ability.changed.grab_entity.how_to_release", KeyReference.ABILITY.getName(level)), true);
        else if (instructionTicks == -120)
            this.entity.displayClientMessage(Component.translatable("ability.changed.grab_entity.how_to_absorb", KeyReference.ATTACK.getName(level), KeyReference.USE.getName(level)), true);
        else if (instructionTicks == -60 && this.grabbedEntity instanceof Player) // Only show toggle when a player is grabbed
            this.entity.displayClientMessage(Component.translatable("ability.changed.grab_entity.how_to_toggle_control", KeyReference.ABILITY.getName(level)), true);
        if (instructionTicks < 0)
            instructionTicks++;
    }

    int instructionTicks = 0;
    public boolean attackDown = false;
    public boolean useDown = false;

    public final KeyStatesTracker<KeyReference> escapeKeys = new KeyStatesTracker<>(
            Set.of(KeyReference.MOVE_FORWARD, KeyReference.MOVE_BACKWARD, KeyReference.MOVE_LEFT, KeyReference.MOVE_RIGHT));
    public int ticksUnpressed = 0;
    public KeyReference lastEscapeKey = null;
    public KeyReference currentEscapeKey = null;
    public RandomSource escapeKeyRandom = null;
    public int ticksGrabbed = 0;
    private static final List<KeyReference> ORDERED_KEYS = List.of(KeyReference.MOVE_FORWARD, KeyReference.MOVE_BACKWARD, KeyReference.MOVE_LEFT, KeyReference.MOVE_RIGHT);

    protected KeyReference getNextEscapeKey() {
        return Util.getRandom(ORDERED_KEYS, escapeKeyRandom);
    }

    public void initializeEscape(long seed) {
        this.escapeKeyRandom = RandomSource.create(seed);
        this.currentEscapeKey = this.getNextEscapeKey();
        this.lastEscapeKey = null;
    }

    public void handleEscape() {
        ticksGrabbed++;

        float entityGrabStrengthDecay = this.suited ? GRAB_STRENGTH_DECAY_SUITED : GRAB_STRENGTH_DECAY;

        if (grabbedEntity != null) {
            AttributeInstance grabbedEntityAttribute = grabbedEntity.getAttribute(ChangedAttributes.GRAB_STRUGGLE_STRENGTH.get());
            if (grabbedEntityAttribute != null) {
                if (grabbedEntity instanceof Player) {
                    // Suited reduces grab strength decay by ~43% for players (100% - 43% = 57.14%)
                    entityGrabStrengthDecay = ((float) grabbedEntityAttribute.getValue()) * (this.suited ? 0.5714f : 1);
                } else {
                    // Suited reduces grab strength decay by 90% for entities (100% - 90% = 10%)
                    entityGrabStrengthDecay = ((float) grabbedEntityAttribute.getValue()) * (this.suited ? 0.1f : 1);
                }
            }
        }

        if (!(this.grabbedEntity instanceof Player player)) {
            this.grabStrength -= entityGrabStrengthDecay;
        }

        else {
            if (ticksGrabbed < 10) return;

            if (player == UniversalDist.getLocalPlayer()) { // Client-side code of the grabbed player
                boolean stateChanged = false;

                if (escapeKeys.queueKeyState(KeyReference.MOVE_FORWARD, KeyReference.MOVE_FORWARD.isDown(player.level())))
                    stateChanged = true;
                if (escapeKeys.queueKeyState(KeyReference.MOVE_BACKWARD, KeyReference.MOVE_BACKWARD.isDown(player.level())))
                    stateChanged = true;
                if (escapeKeys.queueKeyState(KeyReference.MOVE_LEFT, KeyReference.MOVE_LEFT.isDown(player.level())))
                    stateChanged = true;
                if (escapeKeys.queueKeyState(KeyReference.MOVE_RIGHT, KeyReference.MOVE_RIGHT.isDown(player.level())))
                    stateChanged = true;

                if (stateChanged) {
                    Changed.PACKET_HANDLER.sendToServer(new GrabEntityPacket.EscapeKeyState(player,
                            escapeKeys.isEffectivelyDown(KeyReference.MOVE_FORWARD),
                            escapeKeys.isEffectivelyDown(KeyReference.MOVE_BACKWARD),
                            escapeKeys.isEffectivelyDown(KeyReference.MOVE_LEFT),
                            escapeKeys.isEffectivelyDown(KeyReference.MOVE_RIGHT)));
                }
            }

            if (currentEscapeKey == null && !player.level().isClientSide) {
                long seed = this.entity.getEntity().getRandom().nextLong();
                this.initializeEscape(seed);
                Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new GrabEntityPacket.AnnounceEscapeSeed(player, seed));
            }

            final float resolvedDecay = entityGrabStrengthDecay;
            AtomicBoolean keysUnpressed = new AtomicBoolean(true);
            escapeKeys.handleStateUpdates((keyRef, isDown, wasDown, unique) -> {
                if (currentEscapeKey == null)
                    return;
                if (!(isDown && !wasDown))
                    return;

                keysUnpressed.set(false);

                // Key was just pressed
                if (keyRef == currentEscapeKey) {
                    // This is to reduce the strength of cheating (pressing the key too fast)
                    float trustStrength = Mth.clamp((float)ticksUnpressed / (float)GRAB_ESCAPE_TRUST, 0.0f, 1.0f);
                    float keyStrength = resolvedDecay * trustStrength;
                    if (!player.level().isClientSide) {
                        this.grabStrength -= keyStrength;
                        this.suitTransition = Mth.clamp(this.suitTransition - (keyStrength * 0.5f), 0.0f, SUIT_TRANSITION_MAX);
                        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(this.entity::getEntity),
                                new GrabEntityPacket.SyncGrabStrength(this.entity.getEntity(),
                                        this.grabStrength,
                                        this.grabStrengthO,
                                        this.suitTransition,
                                        this.suitTransitionO));
                    }
                } else {
                    if (!player.level().isClientSide) {
                        this.grabStrength = Mth.clamp(this.grabStrength + GRAB_STRENGTH_DECAY_PENALTY, 0.0f, 1.0f);
                        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(this.entity::getEntity),
                                new GrabEntityPacket.SyncGrabStrength(this.entity.getEntity(),
                                        this.grabStrength,
                                        this.grabStrengthO,
                                        this.suitTransition,
                                        this.suitTransitionO));
                    }
                }

                lastEscapeKey = currentEscapeKey;
                currentEscapeKey = this.getNextEscapeKey();
                ticksUnpressed = 0;
            });

            if (keysUnpressed.getAcquire())
                ticksUnpressed++;
        }
    }

    public boolean isGrabbedInvalid() {
        if (this.grabbedEntity == null)
            return false;

        if (this.entity.getEntity() instanceof Player player && (player.isDeadOrDying() || player.isRemoved() || player.isSpectator())) {
            return true;
        }

        if (this.grabbedEntity instanceof Player player && player.isSpectator()) {
            return true;
        }

        if (this.grabbedEntity instanceof Player player && ProcessTransfur.isPlayerTransfurred(player)) {
            var variant = ProcessTransfur.getPlayerTransfurVariant(player);
            if (!variant.isTemporaryFromSuit()) {
                return true;
            }
        }

        if (this.grabbedEntity.isDeadOrDying() || this.grabbedEntity.isRemoved()) {
            return true;
        }

        return false;
    }

    protected @Nullable LatexAssimilationDecision<?> makeAssimilationDecision() {
        return entity.makeLatexAssimilationDecision(
                suited ? TransfurCause.GRAB_ABSORB :TransfurCause.GRAB_REPLICATE, grabbedEntity
        );
    }

    public void tickIdle() { // Called every tick of LatexVariantInstance, for variants that have this ability
        this.grabStrengthO = this.grabStrength;
        this.suitTransitionO = this.suitTransition;

        if (this.grabbedEntity != null) {
            if (this.grabbedEntity instanceof Mob mob) {
                mob.goalSelector.getRunningGoals().forEach(WrappedGoal::stop);
                mob.targetSelector.getRunningGoals().forEach(WrappedGoal::stop);
                mob.setTarget(null);
            }

            Level level = entity.getLevel();

            handleInstructions(level);

            if (this.grabbedEntity instanceof LivingEntityDataExtension ext)
                ext.setGrabbedBy(this.entity.getEntity());

            if (!grabbedHasControl) {
                handleEscape();
                this.grabbedEntity.noPhysics = true;
                this.entity.getEntity().noPhysics = false;
                TransfurVariantInstance.syncEntityPosRotWithEntity(this.grabbedEntity, this.entity.getEntity());
            } else {
                this.grabbedEntity.noPhysics = false;
                this.entity.getEntity().noPhysics = true;
                TransfurVariantInstance.syncEntityPosRotWithEntity(this.entity.getEntity(), this.grabbedEntity);
            }

            if (suited) {
                this.suitTransition = SUIT_TRANSITION_MAX;
            }

            if (suited && !(this.grabbedEntity instanceof Player)) {
                this.grabbedEntity.setAirSupply(this.entity.getEntity().getAirSupply());
                this.grabbedEntity.resetFallDistance();
            }

            if (this.isGrabbedInvalid()) {
                this.releaseEntity(false);
                return;
            } else if (this.grabStrength <= 0.0f) {
                this.releaseEntity(true);
                return;
            }

            if (UniversalDist.getLocalPlayer() == this.entity.getEntity()) {
                boolean attackKeyDown = KeyReference.ATTACK.isDown(level);
                boolean useKeyDown = KeyReference.USE.isDown(level);
                if (attackKeyDown != attackDown || useKeyDown != useDown)
                    Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.keyState(UniversalDist.getLocalPlayer(), attackKeyDown, useKeyDown));
                attackDown = attackKeyDown;
                useDown = useKeyDown;
            }

            var assimilationDecision = this.makeAssimilationDecision();
            if (assimilationDecision != null && attackDown && useDown && suited) {
                if (ProcessTransfur.progressTransfur(this.grabbedEntity, assimilationDecision.withTransfurProgress(assimilationDecision.transfurProgress() * 1.5f)))
                    this.releaseEntity(false);
            }

            if (assimilationDecision != null && attackDown && !suited) {
                if (ProcessTransfur.progressTransfur(this.grabbedEntity, assimilationDecision))
                    this.releaseEntity(false);
            }

            else if (useDown) {
                this.suitTransition += 0.075f;
            }

            if (this.suitTransition > 0.0f && !suited) {
                this.suitTransition = Math.max(0.0f, this.suitTransition - 0.025f);

                if (this.suitTransition > SUIT_TRANSITION_MAX) { // 3 seconds
                    this.suited = true;
                    this.suitTransition = SUIT_TRANSITION_MAX;

                    if (this.entity.getEntity() instanceof Player player && player.level().isClientSide) {
                        Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.suitGrab(player, this.grabbedEntity));
                        this.instructionTicks = -180;
                        this.grabStrength = 1.0f;
                    }
                }
            }
        }
    }

    @Override
    public void startUsing() {

    }

    @Override
    public void tick() {
        if (this.grabbedEntity != null) {
            if (grabbedEntity instanceof Player && !Changed.config.server.isGrabEnabled.get()) {
                this.releaseEntity(false);
                return;
            }

            if (this.getController().getHoldTicks() >= 40) {
                if (suited) {
                    this.grabEntity(this.grabbedEntity);
                    if (this.entity.getLevel().isClientSide)
                        Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.initialGrab((Player)entity.getEntity(), this.grabbedEntity));
                    this.suitTransition = 0.0f;
                } else
                    this.releaseEntity(true);

                this.getController().resetHoldTicks();
            }
            else if (suited) {
                this.suitTransition = (1.0f - (this.getController().getHoldTicks() / 40.0f)) * SUIT_TRANSITION_MAX;
            }
            return;
        }

        var grabbedEntity = this.getHoveredEntity(entity);
        if (grabbedEntity != null && entity.getLevel().isClientSide && entity.getEntity() instanceof PlayerDataExtension ext) {
            if (!this.entity.getEntity().getBoundingBox().inflate(0.5, 0.0, 0.5).intersects(grabbedEntity.getBoundingBox()))
                return;
            if (grabbedEntity instanceof Player && !Changed.config.server.isGrabEnabled.get())
                return;

            if (!this.grabEntity(grabbedEntity))
                return;
            Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.initialGrab((Player)entity.getEntity(), grabbedEntity));
            this.instructionTicks = 180;
        }
    }

    @Override
    public void stopUsing() {
        if (this.grabbedEntity != null && suited && this.getController().getHoldTicks() < 40) {
            if (this.grabbedEntity instanceof Player) {
                this.grabbedHasControl = !this.grabbedHasControl;
                this.grabbedEntity.noPhysics = !this.grabbedHasControl;
                this.entity.getEntity().noPhysics = this.grabbedHasControl;

                if (this.grabbedHasControl) {
                    this.grabbedEntity.setDeltaMovement(Vec3.ZERO);
                } else {
                    this.entity.getEntity().setDeltaMovement(Vec3.ZERO);
                }
            }
        }
    }

    @Override
    public void saveData(CompoundTag tag) {
        super.saveData(tag);
        if (grabbedEntity != null)
            tag.putUUID("GrabbedEntity", grabbedEntity.getUUID());
        tag.putBoolean("GrabbedHasControl", grabbedHasControl);
        tag.putBoolean("Suited", suited);
        tag.putFloat("GrabStrength", grabStrength);
        tag.putFloat("SuitTransition", suitTransition);
        tag.putInt("TicksGrabbed", ticksGrabbed);
    }

    @Override
    public void readData(CompoundTag tag) {
        super.readData(tag);
        if (tag.contains("GrabbedEntity")) {
            final UUID entityUUID = tag.getUUID("GrabbedEntity");
            this.entity.getLevel().getEntities(this.entity.getEntity(), this.entity.getEntity().getBoundingBox().inflate(1.0)).forEach(foundEntity -> {
                if (foundEntity instanceof LivingEntity livingEntity && livingEntity.getUUID().equals(entityUUID)) {
                    this.grabbedEntity = livingEntity;
                }
            });
        }
        if (this.grabbedEntity != null) {
            this.grabbedHasControl = tag.getBoolean("GrabbedHasControl");
            this.suited = tag.getBoolean("Suited");
            this.grabStrength = tag.getFloat("GrabStrength");
            this.suitTransition = tag.getFloat("SuitTransition");
            this.ticksGrabbed = tag.getInt("TicksGrabbed");
        }
    }
}
