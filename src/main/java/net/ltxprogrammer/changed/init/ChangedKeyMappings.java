package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.network.OpenExtraHandsKeybind;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class ChangedKeyMappings {
    public static final KeyMapping OPEN_EXTRA_HANDS = new KeyMapping("key.changed.open_extra_hands", GLFW.GLFW_KEY_H, "key.categories.ui");

    @SubscribeEvent
    public static void registerKeyBindings(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(OPEN_EXTRA_HANDS);
    }

    @Mod.EventBusSubscriber({Dist.CLIENT})
    public static class KeyEventListener {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.KeyInputEvent event) {
            if (Minecraft.getInstance().screen == null) {
                if (event.getKey() == OPEN_EXTRA_HANDS.getKey().getValue() && event.getAction() == 0)
                    Changed.PACKET_HANDLER.sendToServer(new OpenExtraHandsKeybind());
            }
        }
    }
}
