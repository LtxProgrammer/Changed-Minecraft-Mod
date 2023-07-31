package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.network.packet.SyncVariantAbilityPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.commons.lang3.function.TriFunction;

public abstract class AbstractAbility<Instance extends AbstractAbilityInstance> extends ForgeRegistryEntry<AbstractAbility<?>> {
    public static class Controller {
        private final AbstractAbilityInstance abilityInstance;
        private int chargeTicks = 0;
        private int holdTicks = 0;
        private int coolDownTicksRemaining = 0;
        private boolean keyStateO = false;

        public Controller(AbstractAbilityInstance abilityInstance) {
            this.abilityInstance = abilityInstance;
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
            abilityInstance.startUsing();
        }

        public void tickAbility() {
            holdTicks++;
            abilityInstance.tick();
        }

        public void tickCharge() {
            abilityInstance.ability.tickCharge(abilityInstance.player, abilityInstance.variant,
                    (float)chargeTicks);
        }

        public void deactivateAbility() {
            abilityInstance.stopUsing();
        }

        public void applyCoolDown() {
            coolDownTicksRemaining = abilityInstance.ability.getCoolDown(abilityInstance.player, abilityInstance.variant);
        }

        public void tickCoolDown() {
            if (coolDownTicksRemaining > 0)
                coolDownTicksRemaining--;
        }

        public boolean exchangeKeyState(boolean keyState) {
            boolean oState = keyStateO;
            keyStateO = keyState;
            return oState;
        }

        public boolean chargeAbility() {
            chargeTicks++;
            return chargeTicks >= abilityInstance.ability.getChargeTime(abilityInstance.player, abilityInstance.variant);
        }

        public void resetCharge() {
            chargeTicks = 0;
        }

        public float chargePercent() {
            return (float)chargeTicks / (float)abilityInstance.ability.getChargeTime(abilityInstance.player, abilityInstance.variant);
        }

        public float coolDownPercent() {
            var ttl = abilityInstance.ability.getCoolDown(abilityInstance.player, abilityInstance.variant);
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
        void check(boolean currentKeyState, boolean oldKeyState, Controller controller);
    }

    public interface UseProgressActive {
        float activePercent(boolean currentKeyState, Controller controller);
    }

    public enum UseType implements UseActivate, UseProgressActive {
        /**
         * Indicates the ability should activate upon keypress
         */
        INSTANT((keyState, oldState, controller) -> {
            if (!oldState && keyState) {
                controller.activateAbility();
                controller.deactivateAbility();
                controller.applyCoolDown();
            }
        }, (keyState, controller) -> keyState ? 1.0F : 0.0F),
        /**
         * Indicates the ability needs to charge while key is pressed for some time, then activates
         */
        CHARGE_TIME((keyState, oldState, controller) -> {
            if (keyState) {
                if (!controller.chargeAbility())
                    return;

                controller.activateAbility();
                controller.deactivateAbility();

                controller.resetCharge();
                controller.applyCoolDown();
            }

            else {
                controller.resetCharge();
            }
        }, (keyState, controller) -> controller.chargePercent()),
        /**
         * Indicates the ability activates when the key is released
         */
        CHARGE_RELEASE((keyState, oldState, controller) -> {
            if (keyState) {
                controller.tickCharge();
            }

            if (!keyState && oldState) {
                controller.activateAbility();
                controller.deactivateAbility();
                controller.applyCoolDown();
            }
        }, (keyState, controller) -> keyState ? 1.0F : 0.0F),
        /**
         * Indicates the ability activates upon keypress, and continues to fire per tick while key is down
         */
        HOLD((keyState, oldState, controller) -> {
            if (keyState && !oldState)
                controller.activateAbility();
            else if (keyState)
                controller.tickAbility();
            else {
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
        public void check(boolean keyState, boolean oldKeyState, Controller controller) {
            activate.check(keyState, oldKeyState, controller);
        }

        @Override
        public float activePercent(boolean currentKeyState, Controller controller) {
            return progressActive.activePercent(currentKeyState, controller);
        }
    }

    private final TriFunction<AbstractAbility<Instance>, Player, LatexVariantInstance<?>, Instance> ctor;

    public AbstractAbility(TriFunction<AbstractAbility<Instance>, Player, LatexVariantInstance<?>, Instance> ctor) {
        this.ctor = ctor;
    }

    public Instance makeInstance(Player player, LatexVariantInstance<?> variant) {
        return ctor.apply(this, player, variant);
    }

    public TranslatableComponent getDisplayName(Player player, LatexVariantInstance<?> variant) {
        return new TranslatableComponent("ability." + getRegistryName().toString().replace(':', '.'));
    }

    public UseType getUseType(Player player, LatexVariantInstance<?> variant) { return UseType.INSTANT; }
    public int getChargeTime(Player player, LatexVariantInstance<?> variant) { return 0; }
    public int getCoolDown(Player player, LatexVariantInstance<?> variant) { return 0; }

    public boolean canUse(Player player, LatexVariantInstance<?> variant) { return false; }
    public boolean canKeepUsing(Player player, LatexVariantInstance<?> variant) { return false; }

    public void startUsing(Player player, LatexVariantInstance<?> variant) {}
    public void tick(Player player, LatexVariantInstance<?> variant) {}
    public void stopUsing(Player player, LatexVariantInstance<?> variant) {}

    public void tickCharge(Player player, LatexVariantInstance<?> variant, float ticks) {}

    // Called when the player loses the variant (death or untransfur)
    public void onRemove(Player player, LatexVariantInstance<?> variant) {}

    // A unique tag for the ability is provided when saving/reading data. If no data is saved to the tag, then readData does not run
    public void saveData(CompoundTag tag, Player player, LatexVariantInstance<?> variant) {}
    public void readData(CompoundTag tag, Player player, LatexVariantInstance<?> variant) {}

    public ResourceLocation getTexture(Player player, LatexVariantInstance<?> variant) {
        return new ResourceLocation(getRegistryName().getNamespace(), "textures/abilities/" + getRegistryName().getPath() + ".png");
    }

    // Broadcast changes to clients
    public final void setDirty(Player player, LatexVariantInstance<?> variant) {
        CompoundTag data = new CompoundTag();
        saveData(data, player, variant);

        int id = ChangedRegistry.ABILITY.get().getID(this);
        if (player.level.isClientSide)
            Changed.PACKET_HANDLER.sendToServer(new SyncVariantAbilityPacket(id, data));
        else
            Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SyncVariantAbilityPacket(id, data, player.getUUID()));
    }

    public final void setDirty(AccessSaddleAbilityInstance instance) {
        CompoundTag data = new CompoundTag();
        instance.saveData(data);

        int id = ChangedRegistry.ABILITY.get().getID(this);
        if (instance.player.level.isClientSide)
            Changed.PACKET_HANDLER.sendToServer(new SyncVariantAbilityPacket(id, data));
        else
            Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SyncVariantAbilityPacket(id, data, instance.player.getUUID()));
    }
}
