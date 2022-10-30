package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.world.inventory.SpecialLoadingMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Inject(method = "initMenu", at = @At("TAIL"))
    public void initMenu(AbstractContainerMenu p_143400_, CallbackInfo ci) {
        if (p_143400_ instanceof SpecialLoadingMenu special)
            special.afterInit(p_143400_);
    }
}
