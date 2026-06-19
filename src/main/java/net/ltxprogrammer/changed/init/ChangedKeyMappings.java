package net.ltxprogrammer.changed.init;

import com.mojang.blaze3d.platform.InputConstants;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.active.GrabEntityAbility;
import net.ltxprogrammer.changed.network.ExtraJumpKeybind;
import net.ltxprogrammer.changed.network.VariantAbilityActivate;
import net.ltxprogrammer.changed.network.packet.AbilityTreeMenuPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.tutorial.ChangedTutorial;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class ChangedKeyMappings {
    public static final KeyMapping SELECT_ABILITY = new KeyMapping("key.changed.variant_ability",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R, "key.categories.ui") {
        @Override
        public void setDown(boolean newState) {
            super.setDown(newState);
            if (!newState) return;

            LocalPlayer local = Minecraft.getInstance().player;
            GrabEntityAbility.getGrabberSafe(local).ifPresent(entity -> {
                if (entity.getAbilityInstanceSafe(ChangedAbilities.GRAB_ENTITY_ABILITY.get())
                        .map(ability -> ability.grabbedHasControl).orElse(false))
                    Changed.PACKET_HANDLER.sendToServer(VariantAbilityActivate.openRadial(local));
            });

            ProcessTransfur.ifPlayerTransfurred(local, variant -> {
                if (variant.isTemporaryFromSuit())
                    return;

                Changed.PACKET_HANDLER.sendToServer(VariantAbilityActivate.openRadial(local));
                ChangedTutorial.triggerOnOpenRadial();
            });
        }
    };
    public static final KeyMapping OPEN_ABILITY_TREE = new KeyMapping("key.changed.variant_ability_tree",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B, "key.categories.ui") {
        @Override
        public void setDown(boolean newState) {
            super.setDown(newState);
            if (!newState) return;

            Changed.PACKET_HANDLER.sendToServer(new AbilityTreeMenuPacket(AbilityTreeMenuPacket.Opcode.OPEN_MENU,
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()));
        }
    };
    public static final KeyMapping USE_ABILITY = new KeyMapping("key.changed.use_ability",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z, "key.categories.movement") {
        @Override
        public void setDown(boolean newState) {
            super.setDown(newState);
            LocalPlayer local = Minecraft.getInstance().player;
            ProcessTransfur.ifPlayerTransfurred(local, variant -> {
                assert local != null;
                if (variant.isTemporaryFromSuit())
                    return;

                // KeyStateTracker will check if the state has changed
                if (variant.abilityKey.queueKeyState(newState)) {
                    ChangedTutorial.triggerOnUseAbility(variant.getSelectedAbility());
                    Changed.PACKET_HANDLER.sendToServer(new VariantAbilityActivate(local, newState, variant.selectedAbility));
                }
            });
        }
    };

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(SELECT_ABILITY);
        event.register(USE_ABILITY);
    }

    @Mod.EventBusSubscriber({Dist.CLIENT})
    public static class KeyEventListener {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            LocalPlayer local = Minecraft.getInstance().player;
            Options options = Minecraft.getInstance().options;
            if (local == null)
                return;

            if (Minecraft.getInstance().screen == null) {
                if (event.getKey() == options.keyJump.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
                    if (!local.onGround())
                        ProcessTransfur.ifPlayerTransfurred(local, variant -> {
                            if (!variant.getParent().canDoubleJump())
                                return;
                            if (variant.getJumpCharges() > 0) {
                                variant.decJumpCharges();
                                local.jumpFromGround();
                                Changed.PACKET_HANDLER.sendToServer(new ExtraJumpKeybind());
                            }
                        });
                }
            }
        }
    }
}
