package net.ltxprogrammer.changed.entity.beast;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.*;
import net.ltxprogrammer.changed.entity.TamableLatexEntity;
import net.ltxprogrammer.changed.entity.ai.*;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.ltxprogrammer.changed.network.syncher.ChangedEntityDataSerializers;
import net.ltxprogrammer.changed.world.inventory.TamedDarkLatexInventoryMenu;
import net.ltxprogrammer.changed.world.inventory.TamedDarkLatexMenu;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class AbstractDarkLatexEntity extends AbstractLatexWolf implements DarkLatexEntity, TamableLatexEntity {
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(AbstractDarkLatexEntity.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(AbstractDarkLatexEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<DarkLatexTargetType> DATA_TARGET_TYPE_ID = SynchedEntityData.defineId(AbstractDarkLatexEntity.class, ChangedEntityDataSerializers.DARK_LATEX_TARGET_TYPE);
    protected static final EntityDataAccessor<DarkLatexAttackType> DATA_ATTACK_TYPE_ID = SynchedEntityData.defineId(AbstractDarkLatexEntity.class, ChangedEntityDataSerializers.DARK_LATEX_ATTACK_TYPE);
    protected static final EntityDataAccessor<DarkLatexAttackCondition> DATA_ATTACK_CONDITION_ID = SynchedEntityData.defineId(AbstractDarkLatexEntity.class, ChangedEntityDataSerializers.DARK_LATEX_ATTACK_CONDITION);
    protected static final EntityDataAccessor<DarkLatexFavor> DATA_FAVOR_ID = SynchedEntityData.defineId(AbstractDarkLatexEntity.class, ChangedEntityDataSerializers.DARK_LATEX_FAVOR);
    protected @Nullable DarkLatexInventory inventory; // Inventory doesn't exist until DL is tamed
    protected @Nullable GrabEntityAbilityInstance grabEntityAbilityInstance; // Grab doesn't exist until DL is tamed

    public static final int OWNER_HOSTILE_DURATION_TICKS = 600;

    public AbstractDarkLatexEntity(EntityType<? extends AbstractLatexWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.inventory = null;
        this.grabEntityAbilityInstance = null;
    }

    public GrabEntityAbilityInstance createGrabAbility() {
        return new GrabEntityAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get(), IAbstractChangedEntity.forEntity(this));
    }

    public DarkLatexInventory createInventory() {
        return new DarkLatexInventory(this);
    }

    public @Nullable DarkLatexInventory getInventory() {
        return inventory;
    }

    public @Nullable GrabEntityAbilityInstance getGrabAbility() {
        return grabEntityAbilityInstance;
    }

    @Override
    public <A extends AbstractAbilityInstance> A getAbilityInstance(AbstractAbility<A> ability) {
        if (grabEntityAbilityInstance != null && ability == grabEntityAbilityInstance.ability)
            return (A) grabEntityAbilityInstance;
        return super.getAbilityInstance(ability);
    }

    @Override
    public ItemStack getItemInHand(InteractionHand hand) {
        if (inventory == null)
            return super.getItemInHand(hand);
        if (hand == InteractionHand.OFF_HAND)
            return inventory.getItem(DarkLatexInventory.SLOT_OFFHAND);
        else {
            return inventory.getSelected();
        }
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        if (inventory == null)
            return super.getItemBySlot(slot);
        switch (slot.getType()) {
            case HAND:
                return switch (slot) {
                    case MAINHAND -> inventory.getSelected();
                    case OFFHAND -> inventory.getItem(DarkLatexInventory.SLOT_OFFHAND);
                    default -> ItemStack.EMPTY;
                };
            default:
                return super.getItemBySlot(slot);
        }
    }

    @Override
    protected void updateControlFlags() {
        super.updateControlFlags();
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, !this.isInteractingWith(this.getOwner()));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (inventory != null && !this.isInteractingWith(this.getOwner())) {
            if (!level().isClientSide) {
                List<Pair<EquipmentSlot, ItemStack>> list = null;
                var mainHandItem = this.getItemInHand(InteractionHand.MAIN_HAND);
                var offHandItem = this.getItemInHand(InteractionHand.OFF_HAND);

                if (mainHandItem != super.getItemBySlot(EquipmentSlot.MAINHAND)) {
                    super.setItemSlot(EquipmentSlot.MAINHAND, mainHandItem);
                    if (list == null)
                        list = Lists.newArrayListWithCapacity(2);
                    list.add(Pair.of(EquipmentSlot.MAINHAND, mainHandItem));
                }
                if (offHandItem != super.getItemBySlot(EquipmentSlot.OFFHAND)) {
                    super.setItemSlot(EquipmentSlot.OFFHAND, offHandItem);
                    if (list == null)
                        list = Lists.newArrayListWithCapacity(2);
                    list.add(Pair.of(EquipmentSlot.OFFHAND, offHandItem));
                }

                if (list != null && !list.isEmpty())
                    ((ServerLevel)this.level()).getChunkSource().broadcast(this, new ClientboundSetEquipmentPacket(this.getId(), list));
            }
        }
    }

    @Override
    public SlotAccess getSlot(int slotIndex) {
        if (this.inventory == null)
            return super.getSlot(slotIndex);
        else {
            if (slotIndex >= 0 && slotIndex < this.inventory.items.size()) {
                return SlotAccess.forContainer(this.inventory, slotIndex);
            } else {
                return super.getSlot(slotIndex);
            }
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new DarkLatexSuitOwnerGoal(this, 0.28, true));
        this.goalSelector.addGoal(2, new DarkLatexFishingGoal(this, 0.3, 24, 3));
        this.goalSelector.addGoal(2, new DarkLatexCaveHarvestGoal(this, 0.3, 24, 3));
        this.goalSelector.addGoal(3, new DarkLatexCaveTorchingGoal(this, 0.3, 16, 3));
        this.goalSelector.addGoal(6, new LatexFollowOwnerGoal<>(this, 0.35D, 10.0F, 2.0F, false));
        this.targetSelector.addGoal(1, new LatexOwnerHurtByTargetGoal<>(this));
        this.targetSelector.addGoal(2, new LatexOwnerHurtTargetGoal<>(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
        this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
        this.entityData.define(DATA_TARGET_TYPE_ID, DarkLatexTargetType.TRANSFURABLE_ENTITIES);
        this.entityData.define(DATA_ATTACK_TYPE_ID, DarkLatexAttackType.TRY_TRANSFUR);
        this.entityData.define(DATA_ATTACK_CONDITION_ID, DarkLatexAttackCondition.ALWAYS);
        this.entityData.define(DATA_FAVOR_ID, DarkLatexFavor.NONE);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (DATA_OWNERUUID_ID.equals(accessor)) {
            if (this.inventory == null)
                this.inventory = createInventory();
            if (this.grabEntityAbilityInstance == null)
                this.grabEntityAbilityInstance = createGrabAbility();
        }
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

        if (tag.contains("Inventory")) {
            ListTag listtag = tag.getList("Inventory", 10);
            this.inventory = this.createInventory();
            this.inventory.load(listtag);
            this.inventory.selected = tag.getInt("SelectedItemSlot");
            this.grabEntityAbilityInstance = createGrabAbility();

            DarkLatexTargetType.fromSerial(tag.getString("TargetType")).result().ifPresent(this::setTargetType);
            DarkLatexAttackType.fromSerial(tag.getString("AttackType")).result().ifPresent(this::setAttackType);
            DarkLatexAttackCondition.fromSerial(tag.getString("AttackCondition")).result().ifPresent(this::setAttackCondition);
            DarkLatexFavor.fromSerial(tag.getString("FavorToOwner")).result().ifPresent(this::setFavor);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (this.getOwnerUUID() != null) {
            tag.putUUID("Owner", this.getOwnerUUID());
        }

        tag.putBoolean("FollowOwner", this.isFollowingOwner());
        if (this.inventory != null) {
            tag.put("Inventory", this.inventory.save(new ListTag()));
            tag.putInt("SelectedItemSlot", this.inventory.selected);

            tag.putString("TargetType", getTargetType().getSerializedName());
            tag.putString("AttackType", getAttackType().getSerializedName());
            tag.putString("AttackCondition", getAttackCondition().getSerializedName());
            tag.putString("FavorToOwner", getCurrentFavor().getSerializedName());
        }
    }

    public boolean isMaskless() {
        return false;
    }

    @Override
    public LatexType getLatexType() {
        return ChangedLatexTypes.DARK_LATEX.get();
    }

    @Override
    protected boolean targetSelectorTest(LivingEntity livingEntity) {
        final var owner = this.getOwner();
        if (livingEntity == owner)
            return false;
        if (owner != null && livingEntity instanceof TamableLatexEntity tamableLatexEntity && tamableLatexEntity.getOwner() == owner)
            return false;
        // TODO: have npc DLs not target a player if that player has a tamed DL. Or a reputation system for the DLs.

        Predicate<LivingEntity> superPredicate = super::targetSelectorTest;
        if (owner != null) {
            superPredicate = switch (getAttackCondition()) {
                case NEVER -> target -> false;
                case ALWAYS -> getTargetType().forEntity(this);
                case OWNER_IS_HOSTILE -> getTargetType().forEntity(this)
                        .and(target -> owner.tickCount - owner.getLastHurtMobTimestamp() < OWNER_HOSTILE_DURATION_TICKS);
            };
        }
        
        if (!this.isMaskless()) {// Check if masked DL can see entity
            if (livingEntity.distanceToSqr(this) <= 1.0)
                return superPredicate.test(livingEntity);
            if (getLevelBrightnessAt(livingEntity.blockPosition()) >= 5)
                return superPredicate.test(livingEntity);

            var delta = livingEntity.getDeltaMovement();
            var xyMovement = delta.subtract(0, delta.y, 0);
            if (livingEntity.getPose() == Pose.CROUCHING || xyMovement.lengthSqr() < Mth.EPSILON)
                return false;
        }

        return superPredicate.test(livingEntity);
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public DarkLatexTargetType getTargetType() {
        return this.entityData.get(DATA_TARGET_TYPE_ID);
    }

    public DarkLatexAttackType getAttackType() {
        return this.entityData.get(DATA_ATTACK_TYPE_ID);
    }

    public DarkLatexAttackCondition getAttackCondition() {
        return this.entityData.get(DATA_ATTACK_CONDITION_ID);
    }

    public DarkLatexFavor getCurrentFavor() {
        DarkLatexFavor favor = this.entityData.get(DATA_FAVOR_ID);
        if (!canDoFavor(favor))
            favor = DarkLatexFavor.NONE;
        return favor;
    }

    public void setTargetType(DarkLatexTargetType value) {
        this.entityData.set(DATA_TARGET_TYPE_ID, value);
    }

    public void setAttackType(DarkLatexAttackType value) {
        this.entityData.set(DATA_ATTACK_TYPE_ID, value);
    }

    public void setAttackCondition(DarkLatexAttackCondition value) {
        this.entityData.set(DATA_ATTACK_CONDITION_ID, value);
    }

    public void setFavor(DarkLatexFavor value) {
        if (!this.canDoFavor(value))
            value = DarkLatexFavor.NONE;
        this.entityData.set(DATA_FAVOR_ID, value);

        var owner = this.getOwner();
        if (value != DarkLatexFavor.SUIT_OWNER) {
            if (owner != null && grabEntityAbilityInstance != null && grabEntityAbilityInstance.grabbedEntity == owner) {
                grabEntityAbilityInstance.releaseEntity(false);
                Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
                        new GrabEntityPacket(this, owner, GrabEntityPacket.GrabType.RELEASE));
                ChangedSounds.broadcastSound(this, ChangedSounds.LATEX_UNSUIT_ENTITY, 1.0f, 1.0f);
            }
            if (this.getTarget() == owner)
                this.setTarget(null);
        }
    }

    @Override
    public void copyTraitsFrom(IAbstractChangedEntity entity) {
        super.copyTraitsFrom(entity);

        if (entity.getChangedEntity() instanceof AbstractDarkLatexEntity darkLatexEntity) {
            this.setTame(darkLatexEntity.isTame());
            this.setOwnerUUID(darkLatexEntity.getOwnerUUID());
            this.setFollowOwner(darkLatexEntity.isFollowingOwner());
            this.setCustomName(darkLatexEntity.getCustomName());
            this.setUnderlyingPlayer(darkLatexEntity.getUnderlyingPlayer());
            if (darkLatexEntity.inventory != null) {
                this.inventory = this.createInventory();
                this.grabEntityAbilityInstance = this.createGrabAbility();

                var items = new ListTag();
                darkLatexEntity.inventory.save(items);
                this.inventory.load(items);
                darkLatexEntity.inventory.clearContent();
            }
        }
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
            this.level().addParticle(particleoptions, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
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
            return uuid == null ? null : this.level().getPlayerByUUID(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(uuid));
    }

    // Prevents the DL from switching items while the player is interacting with them
    public boolean isInteractingWith(LivingEntity entity) {
        if (entity instanceof Player player) {
            if (player.containerMenu instanceof TamedDarkLatexMenu menu)
                return menu.tamedDarkLatex == this;
            if (player.containerMenu instanceof TamedDarkLatexInventoryMenu menu)
                return menu.tamedDarkLatex == this;
        }

        return false;
    }

    public boolean canDoFavor(DarkLatexFavor favor) {
        return true;
    }

    public void tame(Player player) {
        this.setTame(true);
        this.setFollowOwner(true);
        this.setOwnerUUID(player.getUUID());
        if (player instanceof ServerPlayer serverPlayer) {
            ChangedCriteriaTriggers.TAME_LATEX.trigger(serverPlayer, this);
        }

    }

    protected InteractionResult tamedInteract(Player player, InteractionHand hand) {
        if (player instanceof ServerPlayer serverPlayer)
            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider(
                    (id, inv, viewer) -> new TamedDarkLatexMenu(id, inv, this),
                    this.getDisplayName()
            ), extraData -> {
                extraData.writeInt(this.getId());
            });
        return InteractionResult.sidedSuccess(player.level().isClientSide);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (this.level().isClientSide) {
            boolean flag = this.isOwnedBy(player) || this.isTame();
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            if (this.isTame()) {
                if (this.isTame() && this.isTameItem(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    itemstack.shrink(1);
                    this.heal(2.0F);
                    this.level().broadcastEntityEvent(this, (byte)7); // Spawn hearts
                    return InteractionResult.SUCCESS;
                } else {
                    InteractionResult interactionresult = super.mobInteract(player, hand);
                    if ((!interactionresult.consumesAction() || this.isBaby()) && this.isOwnedBy(player)) {
                        return this.tamedInteract(player, hand);
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

        if (getAttackCondition() == DarkLatexAttackCondition.NEVER) {
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
        if (tame && this.inventory == null)
            this.inventory = this.createInventory();
        if (tame && this.grabEntityAbilityInstance == null)
            this.grabEntityAbilityInstance = createGrabAbility();
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
            if (!this.level().isClientSide && this.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayer) {
                this.getOwner().sendSystemMessage(deathMessage);
            }
    }

    protected void dropEquipment() {
        super.dropEquipment();
        if (this.inventory != null)
            this.inventory.dropAll();
    }

    @Override
    public boolean canPickUpLoot() {
        return inventory != null;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {
        if (inventory == null || equipmentSlot.isArmor())
            super.setItemSlot(equipmentSlot, itemStack);
        else {
            if (equipmentSlot == EquipmentSlot.MAINHAND)
                this.inventory.setItem(this.inventory.selected, itemStack);
            else
                this.inventory.setItem(DarkLatexInventory.SLOT_OFFHAND, itemStack);
        }
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity) {
        if (inventory == null)
            super.pickUpItem(itemEntity);
        else {
            ItemStack itemStack = itemEntity.getItem();
            ItemStack copy = itemStack.copy();

            EquipmentSlot equipmentSlot = itemStack.getEquipmentSlot();
            if (equipmentSlot != null && equipmentSlot.isArmor()) {
                ItemStack currentArmor = this.getItemBySlot(equipmentSlot);
                if (this.canReplaceCurrentItem(itemStack, currentArmor)) {
                    this.setItemSlot(equipmentSlot, itemStack.split(1));
                    this.inventory.placeItemBackInInventory(currentArmor);

                    int delta = copy.getCount() - itemStack.getCount();
                    copy.setCount(delta);
                    this.take(itemEntity, delta);
                    if (itemStack.isEmpty()) {
                        itemEntity.discard();
                        itemStack.setCount(delta);
                    }

                    this.onItemPickup(itemEntity);
                    return;
                }
            }

            if (this.inventory.add(itemStack)) {
                int tookAmount = copy.getCount() - itemStack.getCount();
                copy.setCount(tookAmount);
                this.take(itemEntity, tookAmount);
                if (itemStack.isEmpty()) {
                    itemEntity.discard();
                    itemStack.setCount(tookAmount);
                }

                this.onItemPickup(itemEntity);
            }
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
        if (getAttackCondition() == DarkLatexAttackCondition.NEVER)
            return;

        double d0 = this.getAttributeValue(Attributes.FOLLOW_RANGE);
        AABB aabb = AABB.unitCubeFromLowerCorner(this.position()).inflate(d0, 10.0D, d0);
        this.level().getEntitiesOfClass(AbstractDarkLatexEntity.class, aabb, EntitySelector.NO_SPECTATORS).forEach(nearby -> {
            if (nearby.getTarget() == null && !nearby.isAlliedTo(source))
                nearby.setTarget(source);
        });
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(25.0);
    }

    @Override
    public boolean tryTransfurTarget(Entity entity) {
        if (entity instanceof LivingEntity livingEntity && this.getUnderlyingPlayer() == null) {
            if (!getAttackType().test(this, livingEntity))
                return false; // Cancel attempt to transfur
        }

        return super.tryTransfurTarget(entity);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            updateHeldItemChoice();
            updateOffhandItemChoice();
        }

        if (grabEntityAbilityInstance != null) {
            grabEntityAbilityInstance.tickIdle();
            if (grabEntityAbilityInstance.grabbedEntity == this.getOwner() && grabEntityAbilityInstance.grabbedEntity != null) {
                grabEntityAbilityInstance.grabbedHasControl = true;
                grabEntityAbilityInstance.suited = true;
            }
        }
    }

    protected int findSlotForTransfur() {
        return this.inventory == null ? -1 : this.inventory.getFreeSlot();
    }

    protected int findSlotForCombat() {
        // Maybe add bow AI?
        if (this.inventory == null)
            return -1;

        double bestScore = 0;
        int bestSlot = this.inventory.selected;

        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            var itemStack = this.inventory.getItem(i);
            if (itemStack.isEmpty())
                continue;
            double score = 0;
            var modifiers = this.inventory.getItem(i).getAttributeModifiers(EquipmentSlot.MAINHAND);
            if (modifiers.containsKey(Attributes.ATTACK_DAMAGE))
                score += modifiers.get(Attributes.ATTACK_DAMAGE).stream().mapToDouble(AttributeModifier::getAmount).sum();
            if (modifiers.containsKey(Attributes.ATTACK_SPEED))
                score += modifiers.get(Attributes.ATTACK_SPEED).stream().mapToDouble(AttributeModifier::getAmount).sum();

            if (score > bestScore) {
                bestScore = score;
                bestSlot = i;
            }
        }

        return bestSlot;
    }

    protected int findSlotForFishingRod() {
        if (inventory == null)
            return -1;

        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            var slot = inventory.getItem(i);
            if (slot.isEmpty())
                continue;

            if (slot.is(Tags.Items.TOOLS_FISHING_RODS))
                return i;
        }

        return inventory.selected;
    }

    protected int findSlotForPickaxe() {
        if (inventory == null)
            return -1;

        Tier bestTier = null;
        int bestSlot = this.inventory.selected;

        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            var slot = inventory.getItem(i);
            if (slot.isEmpty())
                continue;

            if (slot.getItem() instanceof PickaxeItem pickaxeItem) {
                if (bestTier == null || TierSortingRegistry.getTiersLowerThan(pickaxeItem.getTier()).contains(bestTier)) {
                    bestTier = pickaxeItem.getTier();
                    bestSlot = i;
                }
            }
        }

        return bestSlot;
    }

    protected int findSlotForNonCombat() {
        // TODO find a book, food, fishing rod, etc.
        if (inventory == null)
            return -1;

        if (getCurrentFavor() == DarkLatexFavor.FISHING)
            return this.findSlotForFishingRod();

        if (getCurrentFavor() == DarkLatexFavor.CAVING)
            return this.findSlotForPickaxe();

        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            var slot = inventory.getItem(i);
            if (slot.isEmpty()) {
                if (getCurrentFavor() == DarkLatexFavor.SUIT_OWNER)
                    return i;
            }
        }

        return inventory.selected;
    }

    public void updateHeldItemChoice() {
        if (this.inventory == null || this.isInteractingWith(this.getOwner()))
            return;

        LivingEntity target = this.getTarget();
        boolean inCombat = target != null && target != this.getOwner();
        boolean wantTransfur = inCombat && getAttackType().test(this, target); // Find empty slot, or else a strong weapon

        if (inCombat) {
            if (wantTransfur) {
                this.inventory.selected = this.findSlotForTransfur();
            }

            if (!wantTransfur || this.inventory.selected == -1) { // No Free slot,
                this.inventory.selected = this.findSlotForCombat();
            }
        } else {
            if (getAttackCondition() == DarkLatexAttackCondition.ALWAYS && getCurrentFavor() == DarkLatexFavor.NONE) {
                this.inventory.selected = this.findSlotForCombat();
            }

            if (getAttackCondition() != DarkLatexAttackCondition.ALWAYS || this.inventory.selected == -1 || getCurrentFavor() != DarkLatexFavor.NONE) {
                this.inventory.selected = this.findSlotForNonCombat();
            }
        }

        if (this.inventory.selected == -1) {
            this.inventory.selected = this.findSlotForNonCombat();
        }

        if (this.inventory.selected == -1) {
            this.inventory.selected = 0; // Fail :(
        }
    }

    protected void swapSlotWithOffhand(int swapWith) {
        if (this.inventory == null)
            return;

        var slot = inventory.getItem(swapWith);
        this.inventory.setItem(swapWith, this.inventory.getItem(DarkLatexInventory.SLOT_OFFHAND));
        this.inventory.setItem(DarkLatexInventory.SLOT_OFFHAND, slot);
    }

    public void updateOffhandItemChoice() {
        if (this.inventory == null || this.isInteractingWith(this.getOwner()))
            return;

        for (int slotIndex = 0; slotIndex < this.inventory.getContainerSize(); ++slotIndex) {
            if (slotIndex == DarkLatexInventory.SLOT_OFFHAND)
                continue;
            var slot = inventory.getItem(slotIndex);
            if (slot.isEmpty())
                continue;

            if (slot.is(Items.TORCH) && getCurrentFavor() == DarkLatexFavor.CAVING) {
                swapSlotWithOffhand(slotIndex);
                return;
            }

            if (slot.is(Tags.Items.TOOLS_SHIELDS) && getCurrentFavor() != DarkLatexFavor.CAVING) {
                swapSlotWithOffhand(slotIndex);
                return;
            }
        }
    }
}
