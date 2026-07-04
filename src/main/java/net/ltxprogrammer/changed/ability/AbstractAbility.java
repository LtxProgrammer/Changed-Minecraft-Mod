package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.network.packet.SyncVariantAbilityPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BiFunction;

public abstract class AbstractAbility<Instance extends AbstractAbilityInstance> {
    public static class Controller {
        private final AbstractAbilityInstance abilityInstance;
        private boolean startedUsing = false;
        private int chargeTicks = 0;
        private int holdTicks = 0;
        private int coolDownTicksRemaining = 0;
        private boolean keyStateO = false;

        public Controller(AbstractAbilityInstance abilityInstance) {
            this.abilityInstance = abilityInstance;
        }

        public int getHoldTicks() {
            return holdTicks;
        }

        public void resetHoldTicks() {
            this.holdTicks = 0;
        }

        public void saveData(CompoundTag tag) {
            tag.putInt("Charge", chargeTicks);
            tag.putInt("Hold", holdTicks);
            tag.putInt("CoolDown", coolDownTicksRemaining);
        }

        public void readData(CompoundTag tag) {
            chargeTicks = tag.getInt("Charge");
            holdTicks = tag.getInt("Hold");
            coolDownTicksRemaining = tag.getInt("CoolDown");
        }

        public void activateAbility() {
            if (abilityInstance.canUse()) {
                abilityInstance.startUsing();
                startedUsing = true;
            }

            else
                startedUsing = false;
        }

        public boolean canKeepUsing() {
            return abilityInstance.canKeepUsing();
        }

        public void tickAbility(boolean uniqueTick) {
            if (startedUsing) {
                holdTicks += uniqueTick ? 1 : 0;
                abilityInstance.tick();
            }
        }

        public void tickCharge() {
            abilityInstance.ability.tickCharge(abilityInstance.entity,
                    (float)chargeTicks);
        }

        public void deactivateAbility() {
            holdTicks = 0;
            if (startedUsing) {
                abilityInstance.stopUsing();
                startedUsing = false;
            }
        }

        public void applyCoolDown() {
            if (abilityInstance.ability.shouldApplyCoolDown(abilityInstance.entity))
                coolDownTicksRemaining = abilityInstance.ability.getCoolDown(abilityInstance.entity);
        }

        public void tickCoolDown() {
            if (coolDownTicksRemaining > 0)
                coolDownTicksRemaining--;
        }

        public void forceCooldown(int ticks) {
            coolDownTicksRemaining = ticks;
        }

        public boolean exchangeKeyState(boolean keyState) {
            boolean oState = keyStateO;
            keyStateO = keyState;
            return oState;
        }

        public boolean chargeAbility(boolean uniqueTick) {
            chargeTicks += uniqueTick ? 1 : 0;
            return chargeTicks >= abilityInstance.ability.getChargeTime(abilityInstance.entity);
        }

        public void resetCharge() {
            chargeTicks = 0;
        }

        public float chargePercent() {
            return (float)chargeTicks / (float)abilityInstance.ability.getChargeTime(abilityInstance.entity);
        }

        public float coolDownPercent() {
            var ttl = abilityInstance.ability.getCoolDown(abilityInstance.entity);
            return ttl > 0 ? 1.0f - ((float)coolDownTicksRemaining / (float)ttl) : 1.0f;
        }

        public float getProgressActive() {
            return abilityInstance.getUseType().activePercent(keyStateO, this);
        }

        public boolean isCoolingDown() {
            return coolDownTicksRemaining > 0;
        }
    }

    public interface UseActivate {
        void check(boolean currentKeyState, boolean oldKeyState, boolean uniqueTick, Controller controller);
    }

    public interface UseProgressActive {
        float activePercent(boolean currentKeyState, Controller controller);
    }

    public enum UseType implements UseActivate, UseProgressActive {
        /**
         * Indicates the ability should activate upon keypress
         */
        INSTANT((keyState, oldState, uniqueTick, controller) -> {
            if (!oldState && keyState) {
                controller.applyCoolDown();
                controller.activateAbility();
                controller.deactivateAbility();
            }
        }, (keyState, controller) -> keyState ? 1.0F : 0.0F),
        /**
         * Indicates the ability needs to charge while key is pressed for some time, then activates
         */
        CHARGE_TIME((keyState, oldState, uniqueTick, controller) -> {
            if (keyState) {
                if (!controller.chargeAbility(uniqueTick))
                    return;

                controller.applyCoolDown();
                controller.activateAbility();
                controller.deactivateAbility();

                controller.resetCharge();
            }

            else {
                controller.resetCharge();
            }
        }, (keyState, controller) -> controller.chargePercent()),
        /**
         * Indicates the ability activates when the key is released
         */
        CHARGE_RELEASE((keyState, oldState, uniqueTick, controller) -> {
            if (keyState) {
                controller.tickCharge();
            }

            if (!keyState && oldState) {
                controller.applyCoolDown();
                controller.activateAbility();
                controller.deactivateAbility();
            }
        }, (keyState, controller) -> keyState ? 1.0F : 0.0F),
        /**
         * Indicates the ability activates when the key is released only after fully charging
         */
        CHARGE_RELEASE_MINIMUM((keyState, oldState, uniqueTick, controller) -> {
            if (keyState && !controller.chargeAbility(uniqueTick))
                controller.tickCharge();

            if (!keyState && oldState && controller.chargePercent() >= 1.0f) {
                controller.applyCoolDown();
                controller.activateAbility();
                controller.deactivateAbility();
            }
            if (!keyState)
                controller.resetCharge();
        }, (keyState, controller) -> Math.min(controller.chargePercent(), 1.0f)),
        /**
         * Indicates the ability activates upon keypress, and continues to fire per tick while key is down
         */
        HOLD((keyState, oldState, uniqueTick, controller) -> {
            if (keyState && !oldState)
                controller.activateAbility();
            else if (keyState && controller.canKeepUsing())
                controller.tickAbility(uniqueTick);
            else if (oldState) {
                controller.deactivateAbility();
                controller.applyCoolDown();
            }
        }, (keyState, controller) -> keyState ? 1.0F : 0.0F),
        /**
         * Indicates the ability should activate upon selecting in the ability menu, and does not overwrite selected ability
         */
        MENU(INSTANT, INSTANT);

        private final UseActivate activate;
        private final UseProgressActive progressActive;

        UseType(UseActivate activate, UseProgressActive progressActive) {
            this.activate = activate;
            this.progressActive = progressActive;
        }

        @Override
        public void check(boolean keyState, boolean oldKeyState, boolean uniqueTick, Controller controller) {
            activate.check(keyState, oldKeyState, uniqueTick, controller);
        }

        @Override
        public float activePercent(boolean currentKeyState, Controller controller) {
            return progressActive.activePercent(currentKeyState, controller);
        }
    }

    private final BiFunction<AbstractAbility<Instance>, IAbstractChangedEntity, Instance> ctor;

    public AbstractAbility(BiFunction<AbstractAbility<Instance>, IAbstractChangedEntity, Instance> ctor) {
        this.ctor = ctor;
    }

    protected final int getAbilityLevel(IAbstractChangedEntity entity) {
        return entity.getAbilityInstanceSafe(this).map(AbstractAbilityInstance::getAbilityLevel).orElse(0);
    }

    public Instance makeInstance(IAbstractChangedEntity entity) {
        return ctor.apply(this, entity);
    }

    @Nullable
    public Component getSelectedDisplayText(IAbstractChangedEntity entity) {
        return null;
    }

    public Component getAbilityName(IAbstractChangedEntity entity) {
        return Component.translatable("ability." + ChangedRegistry.ABILITY.getKey(this).toString()
                .replace(':', '.')
                .replace('/', '.'));
    }

    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return Collections.emptyList();
    }

    public UseType getUseType(IAbstractChangedEntity entity) { return UseType.INSTANT; }
    public int getChargeTime(IAbstractChangedEntity entity) { return 0; }
    public int getCoolDown(IAbstractChangedEntity entity) { return 0; }
    public boolean shouldApplyCoolDown(IAbstractChangedEntity entity) { return true; }

    public boolean canUse(IAbstractChangedEntity entity) { return false; }
    public boolean canKeepUsing(IAbstractChangedEntity entity) { return canUse(entity); }

    public void startUsing(IAbstractChangedEntity entity) {}
    public void tick(IAbstractChangedEntity entity) {}
    public void tickIdle(IAbstractChangedEntity entity) {}
    public void stopUsing(IAbstractChangedEntity entity) {}

    public void tickCharge(IAbstractChangedEntity entity, float ticks) {}

    // Called when the entity loses the variant (death or untransfur)
    public void onRemove(IAbstractChangedEntity entity) {}

    // A unique tag for the ability is provided when saving/reading data. If no data is saved to the tag, then readData does not run
    public void saveData(CompoundTag tag, IAbstractChangedEntity entity) {}
    public void readData(CompoundTag tag, IAbstractChangedEntity entity) {}

    // Broadcast changes to clients
    public final void setDirty(IAbstractChangedEntity entity) {
        CompoundTag data = new CompoundTag();
        entity.getAbilityInstance(this).saveData(data);

        if (entity.getLevel().isClientSide)
            Changed.PACKET_HANDLER.sendToServer(new SyncVariantAbilityPacket(this, data));
        else
            Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SyncVariantAbilityPacket(this, data, entity.getId()));
    }

    @Nullable
    public static <T extends AbstractAbilityInstance> T getAbilityInstance(LivingEntity livingEntity, AbstractAbility<T> ability) {
        if (livingEntity == null) return null;

        if (livingEntity instanceof ChangedEntity latex)
            return latex.getAbilityInstance(ability);
        else if (livingEntity instanceof Player player) {
            var latexInstance = ProcessTransfur.getPlayerTransfurVariant(player);
            if (latexInstance == null)
                return null;
            return latexInstance.getAbilityInstance(ability);
        }

        return null;
    }

    @NotNull
    public static <T extends AbstractAbilityInstance> Optional<T> getAbilityInstanceSafe(LivingEntity livingEntity, AbstractAbility<T> ability) {
        if (livingEntity == null) return Optional.empty();

        if (livingEntity instanceof ChangedEntity latex)
            return Optional.ofNullable(latex.getAbilityInstance(ability));
        else if (livingEntity instanceof Player player) {
            return ProcessTransfur.getPlayerTransfurVariantSafe(player).map(instance -> instance.getAbilityInstance(ability));
        }

        return Optional.empty();
    }
}
