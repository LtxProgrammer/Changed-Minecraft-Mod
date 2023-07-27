package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.network.ExtraJumpKeybind;
import net.ltxprogrammer.changed.network.VariantAbilityActivate;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class ChangedKeyMappings {
    public static final KeyMapping SELECT_ABILITY = new KeyMapping("key.changed.variant_ability", GLFW.GLFW_KEY_R, "key.categories.ui");
    public static final KeyMapping USE_ABILITY = new KeyMapping("key.changed.use_ability", GLFW.GLFW_KEY_Z, "key.categories.movement");

    @SubscribeEvent
    public static void registerKeyBindings(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(SELECT_ABILITY);
        ClientRegistry.registerKeyBinding(USE_ABILITY);
    }

    @Mod.EventBusSubscriber({Dist.CLIENT})
    public static class KeyEventListener {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.KeyInputEvent event) {
            LocalPlayer local = Minecraft.getInstance().player;
            Options options = Minecraft.getInstance().options;
            if (local == null)
                return;
            if (event.getKey() == USE_ABILITY.getKey().getValue()) {
                USE_ABILITY.consumeClick();

                ProcessTransfur.ifPlayerLatex(local, variant -> {
                    var newState = event.getAction() != GLFW.GLFW_RELEASE;
                    if (newState != variant.abilityKeyState) {
                        variant.abilityKeyState = newState;
                        Changed.PACKET_HANDLER.sendToServer(new VariantAbilityActivate(newState, variant.selectedAbility));
                    }
                });
            }

            if (Minecraft.getInstance().screen == null) {
                if (event.getKey() == SELECT_ABILITY.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
                    SELECT_ABILITY.consumeClick();
                    Changed.PACKET_HANDLER.sendToServer(VariantAbilityActivate.CONTROL_OPEN_RADIAL);
                }

                if (event.getKey() == options.keyJump.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
                    if (!local.isOnGround())
                        ProcessTransfur.ifPlayerLatex(local, variant -> {
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
