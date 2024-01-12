package net.ltxprogrammer.changed.mixin.gui;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.BasicPlayerInfoScreen;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin extends Screen {
    protected PauseScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void initChangedBPI(CallbackInfo callback) {
        this.addRenderableWidget(new ImageButton((this.width / 2 - 102) - 24, this.height / 4 + 96 + -16, 20, 20, 0, 0, 20,
                Changed.modResource("textures/gui/basic_player_info.png"), 20, 40, (button) -> {
            this.minecraft.setScreen(new BasicPlayerInfoScreen(this, this.minecraft.player));
        }));
    }
}
