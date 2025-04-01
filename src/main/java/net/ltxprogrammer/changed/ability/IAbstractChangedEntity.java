package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Cacheable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IAbstractChangedEntity {
    @NotNull LivingEntity getEntity();
    @NotNull ChangedEntity getChangedEntity();

    @NotNull BlockPos getBlockPosition();
    @Nullable TransfurVariant<?> getSelfVariant();
    @Nullable TransfurVariant<?> getTransfurVariant();
    @Nullable TransfurVariantInstance<?> getTransfurVariantInstance();
    @NotNull Level getLevel();
    @NotNull UUID getUUID();
    @NotNull TransfurMode getTransfurMode();
    @Nullable <T extends AbstractAbilityInstance> T getAbilityInstance(AbstractAbility<T> ability);
    @Nullable AbstractContainerMenu getContainerMenu();
    @NotNull CompoundTag getPersistentData();
    @Nullable List<HairStyle> getValidHairStyles();
    @NotNull HairStyle getHairStyle();

    void replaceVariant(TransfurVariant<?> otherVariant);

    boolean isPlayer();
    boolean isStillLatex();
    boolean isDeadOrDying();
    boolean isCreative();
    boolean isCrouching();
    boolean isSleeping();
    boolean isInWaterOrBubble();
    boolean addItem(ItemStack item);
    float getFoodLevel();

    void setTransfurMode(TransfurMode mode);
    void displayClientMessage(Component message, boolean overlayMessage);
    void drop(ItemStack stack, boolean includeName);
    void openMenu(MenuProvider menuProvider);
    void closeContainer();
    void setHairStyle(HairStyle style);
    void setEyeStyle(EyeStyle style);
    void causeFoodExhaustion(float exhaustion);

    default boolean hasTransfurMode() {
        final TransfurMode mode = getTransfurMode();
        return mode != TransfurMode.NONE;
    }

    default boolean wantAbsorption() {
        boolean doesAbsorption;
        if (getEntity() instanceof TamableLatexEntity tamableLatex && tamableLatex.isTame())
            doesAbsorption = true;
        else if (getEntity() instanceof ChangedEntity changedEntity)
            doesAbsorption = changedEntity.getTransfurMode() == TransfurMode.ABSORPTION;
        else if (getTransfurVariantInstance() != null)
            doesAbsorption = getTransfurVariantInstance().transfurMode == TransfurMode.ABSORPTION;
        else if (getSelfVariant() != null)
            doesAbsorption = getSelfVariant().transfurMode() == TransfurMode.ABSORPTION;
        else if (getTransfurVariant() != null && getTransfurVariant().transfurMode() == TransfurMode.ABSORPTION)
            doesAbsorption = true;
        else if (getTransfurVariant() != null && getTransfurVariant().getEntityType() == ChangedEntities.SPECIAL_LATEX.get())
            doesAbsorption = true;
        else
            doesAbsorption = false;

        return doesAbsorption;
    }

    default @NotNull TransfurContext replicate() {
        return TransfurContext.latexHazard(this, TransfurCause.GRAB_REPLICATE);
    }
    default @NotNull TransfurContext absorb() {
        return TransfurContext.latexHazard(this, TransfurCause.GRAB_ABSORB);
    }

    default <T extends AbstractAbilityInstance> Optional<T> getAbilityInstanceSafe(AbstractAbility<T> ability) {
        return Optional.ofNullable(getAbilityInstance(ability));
    }

    default Optional<AccessorySlots> getAccessorySlots() {
        return AccessorySlots.getForEntity(this.getEntity());
    }

    static IAbstractChangedEntity forPlayer(Player player) {
        Cacheable<TransfurVariantInstance<?>> instance = Cacheable.of(() -> ProcessTransfur.getPlayerTransfurVariant(player));
        Cacheable<ChangedEntity> latex = Cacheable.of(() -> instance.get().getChangedEntity());

        return new IAbstractChangedEntity() {
            @Override
            public @NotNull LivingEntity getEntity() {
                return player;
            }

            @Override
            public @NotNull ChangedEntity getChangedEntity() {
                return latex.get();
            }

            @Override
            public @NotNull BlockPos getBlockPosition() {
                return player.blockPosition();
            }

            @Override
            public @NotNull TransfurVariant<?> getSelfVariant() {
                return instance.get().getParent();
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public TransfurVariant<?> getTransfurVariant() {
                return instance.get().getChangedEntity().getTransfurVariant();
            }

            @Override
            public @NotNull TransfurVariantInstance<?> getTransfurVariantInstance() {
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
                return instance.get().transfurMode;
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
            public void replaceVariant(TransfurVariant<?> otherVariant) {
                if (instance.get().getParent() == otherVariant)
                    return;

                var old = getTransfurVariantInstance();
                float beforeHealth = player.getHealth();
                ProcessTransfur.setPlayerTransfurVariant(player, otherVariant, old.transfurContext, 1.0f);
                player.setHealth(beforeHealth);
                instance.clear();
                latex.clear();
            }

            @Override
            public boolean isPlayer() {
                return true;
            }

            @Override
            public boolean isStillLatex() {
                return ProcessTransfur.getPlayerTransfurVariant(player) != null;
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
            public void displayClientMessage(Component message, boolean overlayMessage) {
                player.displayClientMessage(message, overlayMessage);
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
            public void setEyeStyle(EyeStyle style) {
                latex.get().setEyeStyle(style);
            }

            @Override
            public void causeFoodExhaustion(float exhaustion) {
                player.causeFoodExhaustion(exhaustion);
            }
        };
    }

    static IAbstractChangedEntity forEntity(ChangedEntity entity) {
        Cacheable<ChangedEntity> cached = Cacheable.of(() -> entity);

        return new IAbstractChangedEntity() {
            @Override
            public @NotNull LivingEntity getEntity() {
                return cached.get();
            }

            @Override
            public @NotNull ChangedEntity getChangedEntity() {
                return cached.get();
            }

            @Override
            public @NotNull BlockPos getBlockPosition() {
                return cached.get().blockPosition();
            }

            @Override
            public @Nullable TransfurVariant<?> getSelfVariant() {
                return cached.get().getSelfVariant();
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public TransfurVariant<?> getTransfurVariant() {
                return cached.get().getTransfurVariant();
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public TransfurVariantInstance<?> getTransfurVariantInstance() {
                return null;
            }

            @Override
            public @NotNull Level getLevel() {
                return cached.get().level;
            }

            @Override
            public @NotNull UUID getUUID() {
                return cached.get().getUUID();
            }

            @Override
            public @NotNull TransfurMode getTransfurMode() {
                return cached.get().getTransfurMode();
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public <T extends AbstractAbilityInstance> T getAbilityInstance(AbstractAbility<T> ability) {
                return cached.get().getAbilityInstance(ability);
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public AbstractContainerMenu getContainerMenu() {
                return null;
            }

            @Override
            public @NotNull CompoundTag getPersistentData() {
                return cached.get().getPersistentData();
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public List<HairStyle> getValidHairStyles() {
                return cached.get().getValidHairStyles();
            }

            @Override
            public @NotNull HairStyle getHairStyle() {
                return cached.get().getHairStyle();
            }

            @Override
            public void replaceVariant(TransfurVariant<?> otherVariant) { // Replaces entity without invalidating this abstraction
                if (otherVariant == getSelfVariant())
                    return; // No work

                ChangedEntity oldEntity = cached.get();
                ChangedEntity newEntity = otherVariant.getEntityType().create(getLevel());
                getLevel().addFreshEntity(newEntity);
                newEntity.teleportTo(oldEntity.getX(), oldEntity.getY(), oldEntity.getZ());
                newEntity.setYRot(oldEntity.getYRot());
                newEntity.setXRot(oldEntity.getXRot());
                oldEntity.discard();

                cached.forceValue(newEntity);
            }

            @Override
            public boolean isPlayer() {
                return false;
            }

            @Override
            public boolean isStillLatex() {
                return true;
            }

            @Override
            public boolean isDeadOrDying() {
                return cached.get().isDeadOrDying();
            }

            @Override
            public boolean isCreative() {
                return false;
            }

            @Override
            public boolean isCrouching() {
                return cached.get().isCrouching();
            }

            @Override
            public boolean isSleeping() {
                return cached.get().isSleeping();
            }

            @Override
            public boolean isInWaterOrBubble() {
                return cached.get().isInWaterOrBubble();
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
            public void displayClientMessage(Component message, boolean overlayMessage) {

            }

            @Override
            public void drop(ItemStack stack, boolean includeName) {
                if (stack.isEmpty()) {
                    return;
                } else {
                    if (cached.get().level.isClientSide) {
                        cached.get().swing(InteractionHand.MAIN_HAND);
                    }

                    double d0 = cached.get().getEyeY() - (double)0.3F;
                    ItemEntity itementity = new ItemEntity(cached.get().level, cached.get().getX(), d0, cached.get().getZ(), stack);
                    itementity.setPickUpDelay(40);
                    if (includeName) {
                        itementity.setThrower(cached.get().getUUID());
                    }

                    float f7 = 0.3F;
                    float f8 = Mth.sin(cached.get().getXRot() * ((float)Math.PI / 180F));
                    float f2 = Mth.cos(cached.get().getXRot() * ((float)Math.PI / 180F));
                    float f3 = Mth.sin(cached.get().getYRot() * ((float)Math.PI / 180F));
                    float f4 = Mth.cos(cached.get().getYRot() * ((float)Math.PI / 180F));
                    float f5 = cached.get().level.random.nextFloat() * ((float)Math.PI * 2F);
                    float f6 = 0.02F * cached.get().level.random.nextFloat();
                    itementity.setDeltaMovement((double)(-f3 * f2 * 0.3F) + Math.cos((double)f5) * (double)f6, (double)(-f8 * 0.3F + 0.1F + (cached.get().level.random.nextFloat() - cached.get().level.random.nextFloat()) * 0.1F), (double)(f4 * f2 * 0.3F) + Math.sin((double)f5) * (double)f6);
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
                cached.get().setHairStyle(style);
            }

            @Override
            public void setEyeStyle(EyeStyle style) {
                cached.get().setEyeStyle(style);
            }

            @Override
            public void causeFoodExhaustion(float exhaustion) {

            }
        };
    }

    static @Nullable IAbstractChangedEntity forEither(LivingEntity entity) {
        if (entity instanceof Player player && ProcessTransfur.isPlayerTransfurred(player))
            return forPlayer(player);
        else if (entity instanceof ChangedEntity changed)
            return forEntity(changed);
        return null;
    }

    static Optional<IAbstractChangedEntity> forEitherSafe(Entity entity) {
        if (entity instanceof Player player && ProcessTransfur.isPlayerTransfurred(player))
            return Optional.of(forPlayer(player));
        else if (entity instanceof ChangedEntity changed)
            return Optional.of(forEntity(changed));
        return Optional.empty();
    }
}
