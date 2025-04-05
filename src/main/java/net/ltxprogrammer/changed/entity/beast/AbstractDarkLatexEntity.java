package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TamableLatexEntity;
import net.ltxprogrammer.changed.entity.ai.LatexFollowOwnerGoal;
import net.ltxprogrammer.changed.entity.ai.LatexOwnerHurtByTargetGoal;
import net.ltxprogrammer.changed.entity.ai.LatexOwnerHurtTargetGoal;
import net.ltxprogrammer.changed.init.ChangedCriteriaTriggers;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.Util;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractDarkLatexEntity extends AbstractLatexWolf implements DarkLatexEntity, TamableLatexEntity {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(AbstractDarkLatexEntity.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(AbstractDarkLatexEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public AbstractDarkLatexEntity(EntityType<? extends AbstractLatexWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new LatexFollowOwnerGoal<>(this, 0.35D, 10.0F, 2.0F, false));
        this.targetSelector.addGoal(1, new LatexOwnerHurtByTargetGoal<>(this));
        this.targetSelector.addGoal(2, new LatexOwnerHurtTargetGoal<>(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        UUID uuid;
        if (tag.hasUUID("Owner")) {
            uuid = tag.getUUID("Owner");
        } else {
            String s = tag.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (tag.contains("FollowOwner"))
            this.setFollowOwner(tag.getBoolean("FollowOwner"));

        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
                this.setTame(true);
            } catch (Throwable throwable) {
                this.setTame(false);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (this.getOwnerUUID() != null) {
            tag.putUUID("Owner", this.getOwnerUUID());
        }

        tag.putBoolean("FollowOwner", this.isFollowingOwner());
    }

    public boolean isMaskless() {
        return false;
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.DARK_LATEX;
    }

    @Override
    protected boolean targetSelectorTest(LivingEntity livingEntity) {
        final var owner = this.getOwner();
        if (livingEntity == owner)
            return false;
        if (owner != null)
            return livingEntity == owner.getLastHurtByMob();
        
        if (!this.isMaskless()) {// Check if masked DL can see entity
            if (livingEntity.distanceToSqr(this) <= 1.0)
                return super.targetSelectorTest(livingEntity);
            if (getLevelBrightnessAt(livingEntity.blockPosition()) >= 5)
                return super.targetSelectorTest(livingEntity);

            var delta = livingEntity.getDeltaMovement();
            var xyMovement = delta.subtract(0, delta.y, 0);
            if (livingEntity.getPose() == Pose.CROUCHING || xyMovement.lengthSqr() < Mth.EPSILON)
                return false;
        }

        return super.targetSelectorTest(livingEntity);
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public boolean isPreventingPlayerRest(Player player) {
        if (isTame() && player.getUUID().equals(getOwnerUUID()))
            return false;
        return super.isPreventingPlayerRest(player);
    }

    protected void spawnTamingParticles(boolean success) {
        ParticleOptions particleoptions = ParticleTypes.HEART;
        if (!success) {
            particleoptions = ParticleTypes.SMOKE;
        }

        for(int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(particleoptions, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
        }
    }

    protected void spawnHeartParticles() {
        this.spawnTamingParticles(true);
    }

    public void handleEntityEvent(byte event) {
        if (event == 7) {
            this.spawnTamingParticles(true);
        } else if (event == 6) {
            this.spawnTamingParticles(false);
        } else if (event == 18) {
            this.spawnHeartParticles();
        } else {
            super.handleEntityEvent(event);
        }

    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        try {
            UUID uuid = this.getOwnerUUID();
            return uuid == null ? null : this.level.getPlayerByUUID(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(uuid));
    }

    public void tame(Player player) {
        this.setTame(true);
        this.setFollowOwner(true);
        this.setOwnerUUID(player.getUUID());
        if (player instanceof ServerPlayer serverPlayer) {
            ChangedCriteriaTriggers.TAME_LATEX.trigger(serverPlayer, this);
        }

    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (this.level.isClientSide) {
            boolean flag = this.isOwnedBy(player) || this.isTame();
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            if (this.isTame()) {
                if (this.isTame() && this.isTameItem(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    itemstack.shrink(1);
                    this.heal(2.0F);
                    this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
                    this.level.broadcastEntityEvent(this, (byte)7); // Spawn hearts
                    return InteractionResult.SUCCESS;
                } else {
                    InteractionResult interactionresult = super.mobInteract(player, hand);
                    if ((!interactionresult.consumesAction() || this.isBaby()) && this.isOwnedBy(player)) {
                        boolean shouldFollow = !this.isFollowingOwner();
                        this.setFollowOwner(shouldFollow);

                        player.displayClientMessage(new TranslatableComponent(shouldFollow ? "text.changed.tamed.follow" : "text.changed.tamed.wander", this.getDisplayName()), true);
                        this.jumping = false;
                        this.navigation.stop();
                        this.setTarget((LivingEntity) null);
                        return InteractionResult.SUCCESS;
                    }

                    return interactionresult;
                }
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFollowingOwner() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    @Override
    public void setFollowOwner(boolean value) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (value) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 1));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -2));
        }

    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target instanceof AbstractDarkLatexEntity) {
            return false;
        }

        return TamableLatexEntity.super.wantsToAttack(target, owner);
    }

    @Override
    public void checkDespawn() {
        if (isTame())
            return;
        super.checkDespawn();
    }

    public boolean isTame() {
        return (this.entityData.get(DATA_FLAGS_ID) & 4) != 0;
    }

    public void setTame(boolean tame) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (tame) {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 | 4));
        } else {
            this.entityData.set(DATA_FLAGS_ID, (byte)(b0 & -5));
        }

        this.reassessTameGoals();
    }

    protected void reassessTameGoals() {
    }

    public boolean isOwnedBy(LivingEntity entity) {
        return entity == this.getOwner();
    }

    public boolean canAttack(LivingEntity entity) {
        return !this.isOwnedBy(entity) && super.canAttack(entity);
    }

    public Team getTeam() {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (livingentity != null) {
                return livingentity.getTeam();
            }
        }

        return super.getTeam();
    }

    public boolean isAlliedTo(Entity entity) {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (entity == livingentity) {
                return true;
            }

            if (livingentity != null) {
                return livingentity.isAlliedTo(entity);
            }
        }

        return super.isAlliedTo(entity);
    }

    public void die(DamageSource source) {
        // FORGE: Super moved to top so that death message would be cancelled properly
        net.minecraft.network.chat.Component deathMessage = this.getCombatTracker().getDeathMessage();
        super.die(source);

        if (this.dead)
            if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayer) {
                this.getOwner().sendMessage(deathMessage, Util.NIL_UUID);
            }
    }

    protected boolean isTameItem(ItemStack stack) {
        return stack.is(ChangedItems.WHITE_LATEX_GOO.get()) || stack.is(ChangedItems.ORANGE.get());
    }

    @Override
    public void onDamagedBy(LivingEntity source) {
        super.onDamagedBy(source);
        if (source instanceof Player player && player.isCreative())
            return;

        double d0 = this.getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB aabb = AABB.unitCubeFromLowerCorner(this.position()).inflate(d0, 10.0D, d0);
        this.level.getEntitiesOfClass(AbstractDarkLatexEntity.class, aabb, EntitySelector.NO_SPECTATORS).forEach(nearby -> {
            if (nearby.getTarget() == null && !nearby.isAlliedTo(source))
                nearby.setTarget(source);
        });
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(25.0);
    }
}
