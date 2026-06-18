package net.ltxprogrammer.changed.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AutotoolAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @Shadow @Final public Player player;

    @WrapOperation(method = "getDestroySpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;get(I)Ljava/lang/Object;"))
    @SuppressWarnings("unchecked")
    public <E> E changed$getAutotoolItem(NonNullList<E> instance, int slot, Operation<E> original, @Local(argsOnly = true) BlockState blockState) {
        var autotool = AbstractAbility.getAbilityInstance(this.player, ChangedAbilities.AUTOTOOL.get());
        if (autotool == null || !autotool.isActive())
            return original.call(instance, slot);

        return (E) AutotoolAbility.getItemToUse(IAbstractChangedEntity.forPlayer(this.player), blockState);
    }
}
