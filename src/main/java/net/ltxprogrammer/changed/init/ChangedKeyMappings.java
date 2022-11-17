package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.network.ExtraJumpKeybind;
import net.ltxprogrammer.changed.network.VariantAbilityKeybind;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class ChangedKeyMappings {
    public static final KeyMapping VARIANT_ABILITY = new KeyMapping("key.changed.variant_ability", GLFW.GLFW_KEY_H, "key.categories.ui");

    @SubscribeEvent
    public static void registerKeyBindings(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(VARIANT_ABILITY);
    }

    @Mod.EventBusSubscriber({Dist.CLIENT})
    public static class KeyEventListener {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.KeyInputEvent event) {
            LocalPlayer local = Minecraft.getInstance().player;
            Options options = Minecraft.getInstance().options;
            if (local == null)
                return;
            if (Minecraft.getInstance().screen == null) {
                if (event.getKey() == VARIANT_ABILITY.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
                    Changed.PACKET_HANDLER.sendToServer(new VariantAbilityKeybind());
                }

                if (event.getKey() == options.keyJump.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS) {
                    if (!local.isOnGround() && ProcessTransfur.isPlayerLatex(local) && ProcessTransfur.getPlayerLatexVariant(local).canDoubleJump()) {
                        if (ProcessTransfur.getPlayerLatexVariant(local).getJumpCharges() > 0) {
                            ProcessTransfur.getPlayerLatexVariant(local).decJumpCharges();
                            local.jumpFromGround();
                            Changed.PACKET_HANDLER.sendToServer(new ExtraJumpKeybind());
                        }
                    }
                }
            }
        }
    }
}
