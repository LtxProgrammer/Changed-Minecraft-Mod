package net.ltxprogrammer.changed.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.item.SpecializedItemRendering;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements ResourceManagerReloadListener {
    private static final String TAGGED_SPECIAL_RENDER = "__tagged_special_render";

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void renderPre(ItemStack itemStack, ItemTransforms.TransformType type, boolean leftHand, PoseStack pose,
                       MultiBufferSource buffers, int packedLight, int packedOverlay, BakedModel model, CallbackInfo callback) {
        if (itemStack.getTag() != null && itemStack.getTag().contains(TAGGED_SPECIAL_RENDER))
            return;
        if (!(itemStack.getItem() instanceof SpecializedItemRendering special))
            return; // Don't override
        ItemRenderer self = (ItemRenderer)(Object)this;
        model = self.getItemModelShaper().getModelManager().getModel(special.getModelLocation(itemStack, type));
        ItemStack nStack = itemStack.copy();
        nStack.getOrCreateTag().putBoolean(TAGGED_SPECIAL_RENDER, true);
        self.render(nStack, type, leftHand, pose, buffers, packedLight, packedOverlay, model);
        callback.cancel();
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void renderPost(ItemStack itemStack, ItemTransforms.TransformType type, boolean leftHand, PoseStack pose,
                       MultiBufferSource buffers, int packedLight, int packedOverlay, BakedModel model, CallbackInfo callback) {
        if (itemStack.getTag() == null || !itemStack.getTag().contains(TAGGED_SPECIAL_RENDER))
            return;
        if (!(itemStack.getItem() instanceof SpecializedItemRendering special))
            return;
        ModelResourceLocation location = special.getEmissiveModelLocation(itemStack, type);
        if (location == null)
            return;
        ItemRenderer self = (ItemRenderer)(Object)this;
        model = self.getItemModelShaper().getModelManager().getModel(location);

        pose.pushPose();
        model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(pose, model, type, leftHand);
        pose.translate(-0.5D, -0.5D, -0.5D);

        RenderType renderType = ItemBlockRenderTypes.getRenderType(itemStack, true);
        self.renderModelLists(model, itemStack, LightTexture.FULL_BRIGHT, packedOverlay, pose,
                ItemRenderer.getFoilBufferDirect(buffers, renderType, true, itemStack.hasFoil()));

        pose.popPose();
    }
}
