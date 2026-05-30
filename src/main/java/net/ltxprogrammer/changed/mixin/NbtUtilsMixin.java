package net.ltxprogrammer.changed.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(NbtUtils.class)
public abstract class NbtUtilsMixin {
    @WrapOperation(method = "setValueHelper", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/properties/Property;getValue(Ljava/lang/String;)Ljava/util/Optional;"))
    private static <T extends Comparable<T>> Optional<T> getValueAndUpdate(Property<T> instance, String s, Operation<Optional<T>> original) {
        if (Changed.dataFixer != null)
            return original.call(instance, s).or(() -> Changed.dataFixer.updateBlockState(instance, s));
        else
            return original.call(instance, s);
    }
}
