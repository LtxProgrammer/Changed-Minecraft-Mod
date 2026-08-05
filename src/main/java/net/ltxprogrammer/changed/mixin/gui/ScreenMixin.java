package net.ltxprogrammer.changed.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.ltxprogrammer.changed.ability.active.GrabEntityAbility;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.network.ChangedClickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow @Nullable protected Minecraft minecraft;

    @WrapMethod(method = "handleComponentClicked")
    public boolean changed$extendClickEvent(Style style, Operation<Boolean> original) {
        if (style == null || Screen.hasShiftDown())
            return original.call(style);

        if (style.getClickEvent() instanceof ChangedClickEvent changedClickEvent) {
            if (changedClickEvent.getChangedAction() == ChangedClickEvent.ChangedAction.FRIENDLY_TF_CONSENT) {
                var grabber = GrabEntityAbility.getGrabber(minecraft.player);
                if (grabber == null)
                    return true;
                var ability = grabber.getAbilityInstance(ChangedAbilities.FRIENDLY_TRANSFUR.get());
                if (ability == null)
                    return true;

                ability.provideConsent(minecraft.player);

                return true;
            }
        }

        return original.call(style);
    }
}
