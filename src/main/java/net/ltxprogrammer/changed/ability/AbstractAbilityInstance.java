package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedKeyMappings;
import net.ltxprogrammer.changed.network.packet.AbilityPayloadPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public abstract class AbstractAbilityInstance {
    public final AbstractAbility<?> ability;
    public final IAbstractChangedEntity entity;
    private final AbstractAbility.Controller controller;

    public AbstractAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        this.ability = ability;
        this.entity = entity;

        this.controller = new AbstractAbility.Controller(this);
    }

    public enum KeyReference {
        ABILITY(() -> ChangedKeyMappings.USE_ABILITY.getTranslatedKeyMessage(), () -> ChangedKeyMappings.USE_ABILITY.isDown()),
        ATTACK(() -> Minecraft.getInstance().options.keyAttack.getTranslatedKeyMessage(), () -> Minecraft.getInstance().options.keyAttack.isDown()),
        USE(() -> Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage(), () -> Minecraft.getInstance().options.keyUse.isDown()),

        MOVE_FORWARD(() -> Minecraft.getInstance().options.keyUp.getTranslatedKeyMessage(), () -> Minecraft.getInstance().options.keyUp.isDown()),
        MOVE_BACKWARD(() -> Minecraft.getInstance().options.keyDown.getTranslatedKeyMessage(), () -> Minecraft.getInstance().options.keyDown.isDown()),
        MOVE_LEFT(() -> Minecraft.getInstance().options.keyLeft.getTranslatedKeyMessage(), () -> Minecraft.getInstance().options.keyLeft.isDown()),
        MOVE_RIGHT(() -> Minecraft.getInstance().options.keyRight.getTranslatedKeyMessage(), () -> Minecraft.getInstance().options.keyRight.isDown());

        private final Supplier<Component> getName;
        private final Supplier<Boolean> isDown;

        KeyReference(Supplier<Component> getName, Supplier<Boolean> isDown) {
            this.getName = getName;
            this.isDown = isDown;
        }

        public Component getName(Level level) {
            if (level.isClientSide)
                return getName.get();
            else
                return TextComponent.EMPTY;
        }

        public boolean isDown(Level level) {
            if (level.isClientSide)
                return isDown.get();
            else
                return false;
        }
    }

    public abstract boolean canUse();
    public abstract boolean canKeepUsing();

    public abstract void startUsing();
    public abstract void tick();
    public abstract void stopUsing();

    // Called when the player loses the variant (death or untransfur)
    public void onRemove() {}

    // Called when the player selects the ability
    public void onSelected() {}

    // A unique tag for the ability is provided when saving/reading data. If no data is saved to the tag, then readData does not run
    public void saveData(CompoundTag tag) {
        var controllerTag = new CompoundTag();
        controller.saveData(controllerTag);
        tag.put("Controller", controllerTag);
    }
    public void readData(CompoundTag tag) {
        if (tag.contains("Controller"))
            controller.readData(tag.getCompound("Controller"));
    }

    public void acceptPayload(CompoundTag tag) {}

    public void sendPayload(CompoundTag tag) {
        if (this.entity.getLevel().isClientSide) {
            Changed.PACKET_HANDLER.sendToServer(
                    new AbilityPayloadPacket(this.entity.getEntity().getId(), this.ability, tag));
        } else {
            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(this.entity::getEntity),
                    new AbilityPayloadPacket(this.entity.getEntity().getId(), this.ability, tag));
        }
    }

    public void sendPayload(CompoundTag tag, Player destination) {
        if (destination instanceof ServerPlayer serverPlayer) {
            Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new AbilityPayloadPacket(this.entity.getEntity().getId(), this.ability, tag));
        }
    }

    public AbstractAbility.UseType getUseType() {
        return ability.getUseType(entity);
    }

    public final ResourceLocation getTexture() {
        return ability.getTexture(entity);
    }

    public AbstractAbility.Controller getController() {
        return controller;
    }
}
