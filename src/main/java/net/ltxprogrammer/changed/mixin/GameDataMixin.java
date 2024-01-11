package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.item.AbstractLatexGoo;
import net.minecraftforge.registries.GameData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameData.class, remap = false)
public abstract class GameDataMixin {
    @Inject(method = "freezeData", at = @At("HEAD"))
    private static void removeLatexCoveredStates(CallbackInfo callback) {
        AbstractLatexGoo.removeLatexCoveredStates();
    }
}
