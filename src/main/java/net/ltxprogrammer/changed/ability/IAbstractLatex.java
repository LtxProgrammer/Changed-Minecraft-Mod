package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Cacheable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface IAbstractLatex {
    @NotNull LivingEntity getEntity();
    @NotNull LatexEntity getLatexEntity();

    @NotNull BlockPos getBlockPosition();
    @Nullable LatexVariantInstance<?> getLatexVariantInstance();
    @NotNull Level getLevel();
    @NotNull UUID getUUID();
    @NotNull TransfurMode getTransfurMode();
    @Nullable <T extends AbstractAbilityInstance> T getAbilityInstance(AbstractAbility<T> ability);
    @Nullable AbstractContainerMenu getContainerMenu();
    @NotNull CompoundTag getPersistentData();
    @Nullable List<HairStyle> getValidHairStyles();
    @NotNull HairStyle getHairStyle();

    boolean isStillLatex();
    boolean isDeadOrDying();
    boolean isCreative();
    boolean isCrouching();
    boolean isSleeping();
    boolean isInWaterOrBubble();
    boolean addItem(ItemStack item);
    float getFoodLevel();

    void setTransfurMode(TransfurMode mode);
    void displayClientMessage(Component message, boolean animateMessage);
    void drop(ItemStack stack, boolean includeName);
    void openMenu(MenuProvider menuProvider);
    void closeContainer();
    void setHairStyle(HairStyle style);
    void causeFoodExhaustion(float exhaustion);

    static IAbstractLatex forPlayer(Player player) {
        Cacheable<LatexVariantInstance<?>> instance = Cacheable.of(() -> ProcessTransfur.getPlayerLatexVariant(player));
        Cacheable<LatexEntity> latex = Cacheable.of(() -> instance.get().getLatexEntity());

        return new IAbstractLatex() {
            @Override
            public @NotNull LivingEntity getEntity() {
                return player;
            }

            @Override
            public @NotNull LatexEntity getLatexEntity() {
                return latex.get();
            }

            @Override
            public @NotNull BlockPos getBlockPosition() {
                return player.blockPosition();
            }

            @Override
            public @NotNull LatexVariantInstance<?> getLatexVariantInstance() {
                return instance.get();
            }

            @Override
            public @NotNull Level getLevel() {
                return player.level;
            }

            @Override
            public @NotNull UUID getUUID() {
                return player.getUUID();
            }

            @Override
            public @NotNull TransfurMode getTransfurMode() {
                return ProcessTransfur.getPlayerLatexVariant(player).transfurMode;
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public <T extends AbstractAbilityInstance> T getAbilityInstance(AbstractAbility<T> ability) {
                return instance.get().getAbilityInstance(ability);
            }

            @Override
            public @NotNull AbstractContainerMenu getContainerMenu() {
                return player.containerMenu;
            }

            @Override
            public @NotNull CompoundTag getPersistentData() {
                return player.getPersistentData();
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public List<HairStyle> getValidHairStyles() {
                return latex.get().getValidHairStyles();
            }

            @Override
            public @NotNull HairStyle getHairStyle() {
                return latex.get().getHairStyle();
            }

            @Override
            public boolean isStillLatex() {
                return ProcessTransfur.getPlayerLatexVariant(player) != null;
            }

            @Override
            public boolean isDeadOrDying() {
                return player.isDeadOrDying();
            }

            @Override
            public boolean isCreative() {
                return player.isCreative();
            }

            @Override
            public boolean isCrouching() {
                return player.isCrouching();
            }

            @Override
            public boolean isSleeping() {
                return player.isSleeping();
            }

            @Override
            public boolean isInWaterOrBubble() {
                return player.isInWaterOrBubble();
            }

            @Override
            public boolean addItem(ItemStack item) {
                return player.addItem(item);
            }

            @Override
            public float getFoodLevel() {
                return player.getFoodData().getFoodLevel();
            }

            @Override
            public void setTransfurMode(TransfurMode mode) {
                instance.get().transfurMode = mode;
            }

            @Override
            public void displayClientMessage(Component message, boolean animateMessage) {
                player.displayClientMessage(message, animateMessage);
            }

            @Override
            public void drop(ItemStack stack, boolean includeName) {
                player.drop(stack, includeName);
            }

            @Override
            public void openMenu(MenuProvider menuProvider) {
                player.openMenu(menuProvider);
            }

            @Override
            public void closeContainer() {
                player.closeContainer();
            }

            @Override
            public void setHairStyle(HairStyle style) {
                latex.get().setHairStyle(style);
            }

            @Override
            public void causeFoodExhaustion(float exhaustion) {
                player.causeFoodExhaustion(exhaustion);
            }
        };
    }

    static IAbstractLatex forLatex(LatexEntity latex) {
        return new IAbstractLatex() {
            @Override
            public @NotNull LivingEntity getEntity() {
                return latex;
            }

            @Override
            public @NotNull LatexEntity getLatexEntity() {
                return latex;
            }

            @Override
            public @NotNull BlockPos getBlockPosition() {
                return latex.blockPosition();
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public LatexVariantInstance<?> getLatexVariantInstance() {
                return null;
            }

            @Override
            public @NotNull Level getLevel() {
                return latex.level;
            }

            @Override
            public @NotNull UUID getUUID() {
                return latex.getUUID();
            }

            @Override
            public @NotNull TransfurMode getTransfurMode() {
                return latex.getTransfurMode();
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public <T extends AbstractAbilityInstance> T getAbilityInstance(AbstractAbility<T> ability) {
                return latex.getAbilityInstance(ability);
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public AbstractContainerMenu getContainerMenu() {
                return null;
            }

            @Override
            public @NotNull CompoundTag getPersistentData() {
                return latex.getPersistentData();
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public List<HairStyle> getValidHairStyles() {
                return latex.getValidHairStyles();
            }

            @Override
            public @NotNull HairStyle getHairStyle() {
                return latex.getHairStyle();
            }

            @Override
            public boolean isStillLatex() {
                return true;
            }

            @Override
            public boolean isDeadOrDying() {
                return latex.isDeadOrDying();
            }

            @Override
            public boolean isCreative() {
                return false;
            }

            @Override
            public boolean isCrouching() {
                return latex.isCrouching();
            }

            @Override
            public boolean isSleeping() {
                return latex.isSleeping();
            }

            @Override
            public boolean isInWaterOrBubble() {
                return latex.isInWaterOrBubble();
            }

            @Override
            public boolean addItem(ItemStack item) {
                return false;
            }

            @Override
            public float getFoodLevel() {
                return 20;
            }

            @Override
            public void setTransfurMode(TransfurMode mode) {
                
            }

            @Override
            public void displayClientMessage(Component message, boolean animateMessage) {

            }

            @Override
            public void drop(ItemStack stack, boolean includeName) {
                if (stack.isEmpty()) {
                    return;
                } else {
                    if (latex.level.isClientSide) {
                        latex.swing(InteractionHand.MAIN_HAND);
                    }

                    double d0 = latex.getEyeY() - (double)0.3F;
                    ItemEntity itementity = new ItemEntity(latex.level, latex.getX(), d0, latex.getZ(), stack);
                    itementity.setPickUpDelay(40);
                    if (includeName) {
                        itementity.setThrower(latex.getUUID());
                    }

                    float f7 = 0.3F;
                    float f8 = Mth.sin(latex.getXRot() * ((float)Math.PI / 180F));
                    float f2 = Mth.cos(latex.getXRot() * ((float)Math.PI / 180F));
                    float f3 = Mth.sin(latex.getYRot() * ((float)Math.PI / 180F));
                    float f4 = Mth.cos(latex.getYRot() * ((float)Math.PI / 180F));
                    float f5 = latex.level.random.nextFloat() * ((float)Math.PI * 2F);
                    float f6 = 0.02F * latex.level.random.nextFloat();
                    itementity.setDeltaMovement((double)(-f3 * f2 * 0.3F) + Math.cos((double)f5) * (double)f6, (double)(-f8 * 0.3F + 0.1F + (latex.level.random.nextFloat() - latex.level.random.nextFloat()) * 0.1F), (double)(f4 * f2 * 0.3F) + Math.sin((double)f5) * (double)f6);
                }
            }

            @Override
            public void openMenu(MenuProvider menuProvider) {

            }

            @Override
            public void closeContainer() {

            }

            @Override
            public void setHairStyle(HairStyle style) {
                latex.setHairStyle(style);
            }

            @Override
            public void causeFoodExhaustion(float exhaustion) {

            }
        };
    }
}
