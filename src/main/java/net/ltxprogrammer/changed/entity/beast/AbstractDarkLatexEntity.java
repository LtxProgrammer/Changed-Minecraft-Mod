package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.ability.*;
import net.ltxprogrammer.changed.ability.active.GrabEntityAbilityInstance;
import net.ltxprogrammer.changed.entity.ai.*;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.network.syncher.ChangedEntityDataSerializers;
import net.ltxprogrammer.changed.world.inventory.TamedDarkLatexMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public abstract class AbstractDarkLatexEntity extends AbstractLatexWolf implements DarkLatexEntity {
    public AbstractDarkLatexEntity(EntityType<? extends AbstractLatexWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected @Nullable Goal makeHurtByTargetGoal() {
        return new HurtByTargetGoal(this, AbstractDarkLatexEntity.class).setAlertOthers();
    }

    public GrabEntityAbilityInstance createGrabAbility() {
        return new GrabEntityAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get(), IAbstractChangedEntity.forEntity(this));
    }

    @Override
    protected void updateControlFlags() {
        super.updateControlFlags();
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, !this.isInteractingWith(this.getOwner()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("Inventory")) {
            TamedEntityTargetType.fromSerial(tag.getString("TargetType")).result().ifPresent(this::setTargetType);
            TamedEntityAttackType.fromSerial(tag.getString("AttackType")).result().ifPresent(this::setAttackType);
            TamedEntityAttackCondition.fromSerial(tag.getString("AttackCondition")).result().ifPresent(this::setAttackCondition);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (this.inventory != null) {
            tag.putString("TargetType", getTargetType().getSerializedName());
            tag.putString("AttackType", getAttackType().getSerializedName());
            tag.putString("AttackCondition", getAttackCondition().getSerializedName());
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
        // TODO: have npc DLs not target a player if that player has a tamed DL. Or a reputation system for the DLs.

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

                var items = new ListTag();
                darkLatexEntity.inventory.save(items);
                this.inventory.load(items);
                darkLatexEntity.inventory.clearContent();
            }
        }
    }

    public boolean canDoFavor(TamedEntityFavor favor) {
        return true;
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
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target instanceof AbstractDarkLatexEntity) {
            return false;
        }

        if (getAttackCondition() == TamedEntityAttackCondition.NEVER) {
            return false;
        }

        return super.wantsToAttack(target, owner);
    }

    protected void setTame(boolean tame) {
        super.setTame(tame);
        if (tame && this.inventory == null)
            this.inventory = this.createInventory();
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
                this.inventory.setItem(TamedEntityInventory.SLOT_OFFHAND, itemStack);
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

    @Override
    protected boolean isTameItem(ItemStack stack) {
        return stack.is(ChangedItems.WHITE_LATEX_GOO.get()) || stack.is(ChangedItems.ORANGE.get());
    }

    @Override
    protected boolean isHealItem(ItemStack stack) {
        return stack.is(ChangedItems.WHITE_LATEX_GOO.get()) || stack.is(ChangedItems.ORANGE.get());
    }

    @Override
    protected float getItemHealAmount(Player player, ItemStack itemStack) {
        return 2.0f;
    }

    @Override
    public void onDamagedBy(LivingEntity source) {
        super.onDamagedBy(source);
        if (source instanceof Player player && player.isCreative())
            return;
        if (getAttackCondition() == TamedEntityAttackCondition.NEVER)
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
}
