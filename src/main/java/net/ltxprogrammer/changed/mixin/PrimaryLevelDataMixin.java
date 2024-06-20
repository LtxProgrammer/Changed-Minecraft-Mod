package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.world.ChangedDataFixer;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(PrimaryLevelData.class)
public abstract class PrimaryLevelDataMixin {
    @Shadow @Final private int playerDataVersion;

    @Shadow @Nullable private CompoundTag loadedPlayerTag;

    @Inject(method = "updatePlayerTag", at = @At("RETURN"))
    private void updateChangedTag(CallbackInfo callback) {
        if (this.playerDataVersion >= SharedConstants.getCurrentVersion().getWorldVersion())
            ChangedDataFixer.updateCompoundTag(DataFixTypes.PLAYER, this.loadedPlayerTag);
    }

    // FORGE

    @Inject(method = "hasConfirmedExperimentalWarning", at = @At("RETURN"), cancellable = true, remap = false)
    public void hasConfirmedExperimentalWarning(CallbackInfoReturnable<Boolean> callback) {
        callback.setReturnValue(true);
    }
}
