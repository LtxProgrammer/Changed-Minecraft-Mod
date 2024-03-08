package net.ltxprogrammer.changed.mixin.render;

import net.ltxprogrammer.changed.block.CustomFallable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FallingBlockRenderer.class)
public abstract class FallingBlockRendererMixin {
    @Redirect(method = "render(Lnet/minecraft/world/entity/item/FallingBlockEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;getBlockModel(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/resources/model/BakedModel;"))
    public BakedModel overrideBlockModel(BlockRenderDispatcher instance, BlockState state) {
        BakedModel original = instance.getBlockModel(state);

        if (state.getBlock() instanceof CustomFallable customFallable) {
            return Minecraft.getInstance().getModelManager().getModel(customFallable.getModelName());
        } else {
            return original;
        }
    }
}
