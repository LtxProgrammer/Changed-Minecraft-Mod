package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.ai.*;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.world.inventory.TamedEntityMenu;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDarkLatexEntity extends AbstractLatexWolf implements DarkLatexEntity {
    public AbstractDarkLatexEntity(EntityType<? extends AbstractLatexWolf> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected @Nullable Goal makeHurtByTargetGoal() {
        return new HurtByTargetGoal(this, AbstractDarkLatexEntity.class).setAlertOthers();
    }

    @Override
    protected void updateControlFlags() {
        super.updateControlFlags();
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, !this.isInteractingWith(this.getOwner()));
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

    public boolean canDoFavor(TamedEntityFavor favor) {
        return true;
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target instanceof AbstractDarkLatexEntity) {
            return false;
        }

        return super.wantsToAttack(target, owner);
    }

    protected void setTame(boolean tame) {
        super.setTame(tame);
        if (tame && this.inventory == null)
            this.inventory = this.createInventory();
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
}
