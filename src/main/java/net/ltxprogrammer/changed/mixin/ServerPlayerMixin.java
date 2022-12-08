package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.process.ProcessTransfur;
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

    @Inject(method = "restoreFrom", at = @At("HEAD"))
    public void restoreFrom(ServerPlayer player, boolean restore, CallbackInfo callbackInfo) {
        ServerPlayer self = (ServerPlayer)(Object)this;
        if ((player.level.getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM) || restore) && ProcessTransfur.isPlayerLatex(player))
            ProcessTransfur.setPlayerLatexVariant(self, ProcessTransfur.getPlayerLatexVariant(player).clone());
    }
}
