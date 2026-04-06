package net.ltxprogrammer.changed.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemOverrides.class)
public abstract class ItemOverridesMixin {
    @WrapMethod(method = "resolve")
    public BakedModel forwardUnderlyingPlayer(BakedModel model, ItemStack itemStack, ClientLevel level, LivingEntity entity, int seed, Operation<BakedModel> original) {
        return original.call(model, itemStack, level,
                EntityUtil.maybeGetUnderlying(entity), seed);
    }
}
