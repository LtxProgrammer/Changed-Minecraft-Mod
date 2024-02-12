package net.ltxprogrammer.changed.mixin;

import com.mojang.datafixers.DataFixer;
import net.ltxprogrammer.changed.world.ChangedDataFixer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NbtUtils.class)
public abstract class NbtUtilsMixin {
    @Inject(method = "update(Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/util/datafix/DataFixTypes;Lnet/minecraft/nbt/CompoundTag;II)Lnet/minecraft/nbt/CompoundTag;", at = @At("RETURN"))
    private static void dataFixForChanged(DataFixer dataFixer, DataFixTypes types, CompoundTag tag, int taggedVersion, int loadedVersion, CallbackInfoReturnable<CompoundTag> callback) {
        ChangedDataFixer.updateCompoundTag(types, tag);
    }
}
