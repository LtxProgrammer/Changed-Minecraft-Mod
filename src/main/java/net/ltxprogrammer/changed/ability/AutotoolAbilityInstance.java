package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AutotoolAbilityInstance extends AbstractAbilityInstance {
    public static final int ITEM_TRANSITION_TICKS = 10;

    private boolean isActive = false;

    private ItemStack renderingActiveItem = ItemStack.EMPTY;
    /// Zero ticks = fully raised, ITEM_TRANSITION_TICKS = fully lowered
    private int renderingRaiseTicks = ITEM_TRANSITION_TICKS;
    private int renderingRaiseTicksO = ITEM_TRANSITION_TICKS;
    private InteractionHand renderingSide = InteractionHand.MAIN_HAND;
    private boolean shouldSwing = false;

    public AutotoolAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    public boolean isActive() {
        return isActive;
    }

    public ItemStack getRenderingActiveItem() {
        return renderingActiveItem;
    }

    /// Zero ticks = fully raised, ITEM_TRANSITION_TICKS = fully lowered
    public float getRenderingRaiseTicks(float partialTicks) {
        return Mth.lerp(partialTicks, renderingRaiseTicksO, renderingRaiseTicks);
    }

    public InteractionHand getRenderingSide() {
        return renderingSide;
    }

    public boolean shouldSwing() {
        return shouldSwing;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canKeepUsing() {
        return true;
    }

    @Override
    public void startUsing() {
        this.isActive = !this.isActive;
        entity.displayClientMessage(isActive ? AutotoolAbility.ENABLE : AutotoolAbility.DISABLE, true);
    }

    @Override
    public void tick() {

    }

    @Override
    public void tickIdle() {
        super.tickIdle();

        ItemStack targetItem;
        if (isActive() && this.entity.getEntity() == UniversalDist.getCameraEntity() && UniversalDist.getLocalHitResult() instanceof BlockHitResult blockHitResult) {
            BlockPos targetBlockPos = blockHitResult.getBlockPos();
            BlockState targetBlockState = entity.getLevel().getBlockState(targetBlockPos);

            targetItem = AutotoolAbility.getFirstCorrectItem(this.entity, targetBlockState);
            if (targetItem == null || targetItem == this.entity.getEntity().getMainHandItem())
                targetItem = ItemStack.EMPTY;
        } else {
            targetItem = ItemStack.EMPTY;
        }

        this.renderingRaiseTicksO = this.renderingRaiseTicks;
        if (this.renderingActiveItem != targetItem) {
            this.shouldSwing = false;
            if (this.renderingRaiseTicks < ITEM_TRANSITION_TICKS)
                this.renderingRaiseTicks++; // Lower old item
            else {
                this.renderingActiveItem = targetItem;

                var switchHands = entity.getAbilityInstance(ChangedAbilities.SWITCH_HANDS.get());
                if (switchHands != null) {
                    this.renderingSide = switchHands.getOffHandItems().anyMatch(stack -> stack == renderingActiveItem)
                            ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                } else {
                    this.renderingSide = entity.getEntity().getOffhandItem() == renderingActiveItem
                            ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                }
            }
        } else {
            if (!targetItem.isEmpty()) {
                this.renderingRaiseTicks = Math.max(this.renderingRaiseTicks - 1, 0); // Raise target item
                this.shouldSwing = true;
            } else {
                this.renderingRaiseTicks = ITEM_TRANSITION_TICKS; // Stay lowered if empty
                this.shouldSwing = false;
            }
        }
    }

    @Override
    public void stopUsing() {

    }
}
