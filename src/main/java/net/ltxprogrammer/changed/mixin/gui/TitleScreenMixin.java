package net.ltxprogrammer.changed.mixin.gui;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.BasicPlayerInfoScreen;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void initChangedBPI(CallbackInfo callback) {
        int l = this.height / 4 + 48;

        this.addRenderableWidget(new ImageButton(this.width / 2 + 128, l + 72 + 12, 20, 20, 0, 0, 20,
                Changed.modResource("textures/gui/basic_player_info.png"), 20, 40, (button) -> {
            this.minecraft.setScreen(new BasicPlayerInfoScreen(this));
        }));
    }
}
