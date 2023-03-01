package net.ltxprogrammer.changed.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.item.SpecializedItemRendering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ItemLayerModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin implements ResourceManagerReloadListener {
    @Unique private static final String TAGGED_SPECIAL_RENDER = "__tagged_special_render";
    @Unique private static final Map<ItemStack, LivingEntity> ENTITY_CACHE = new HashMap<>();
    @Unique private static final Map<ItemStack, ItemStack> ORIGINAL_STACK_CACHE = new HashMap<>();

    @Inject(method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V",
            at = @At("HEAD"))
    public void renderStaticPre(@Nullable LivingEntity entity, ItemStack itemStack, ItemTransforms.TransformType type, boolean p_174246_, PoseStack pose, MultiBufferSource buffers, @Nullable Level level, int p_174250_, int p_174251_, int p_174252_, CallbackInfo callback) {
        if (entity == null) return;
        if (ENTITY_CACHE.size() > 32) {
            ENTITY_CACHE.clear();
            Changed.LOGGER.error("Memory leak detected in ItemRendererMixin");
        }
        ENTITY_CACHE.put(itemStack, entity); // Cache entity for item holder to catch overrides later
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void renderPre(ItemStack itemStack, ItemTransforms.TransformType type, boolean leftHand, PoseStack pose,
                       MultiBufferSource buffers, int packedLight, int packedOverlay, BakedModel model, CallbackInfo callback) {
        if (itemStack.getTag() != null && itemStack.getTag().contains(TAGGED_SPECIAL_RENDER))
            return;
        if (!(itemStack.getItem() instanceof SpecializedItemRendering special))
            return; // Don't override
        ItemRenderer self = (ItemRenderer)(Object)this;
        model = self.getItemModelShaper().getModelManager().getModel(special.getModelLocation(itemStack, type));

        // Fetch model overrides
        LivingEntity holder = ENTITY_CACHE.remove(itemStack);
        model = model.getOverrides().resolve(model, itemStack, Minecraft.getInstance().level, holder, 0);

        // Recursion lock
        ItemStack nStack = itemStack.copy();
        nStack.getOrCreateTag().putBoolean(TAGGED_SPECIAL_RENDER, true);
        ENTITY_CACHE.put(nStack, holder);
        if (ORIGINAL_STACK_CACHE.size() > 32) {
            ORIGINAL_STACK_CACHE.clear();
            Changed.LOGGER.error("Memory leak detected in ItemRendererMixin");
        }
        ORIGINAL_STACK_CACHE.put(nStack, itemStack);
        if (model != null)
            self.render(nStack, type, leftHand, pose, buffers, packedLight, packedOverlay, model);
        callback.cancel();
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void renderPost(ItemStack itemStack, ItemTransforms.TransformType type, boolean leftHand, PoseStack pose,
                       MultiBufferSource buffers, int packedLight, int packedOverlay, BakedModel model, CallbackInfo callback) {
        LivingEntity holder = ENTITY_CACHE.remove(itemStack);
        ItemStack original = ORIGINAL_STACK_CACHE.remove(itemStack);
        if (itemStack.getTag() == null || !itemStack.getTag().contains(TAGGED_SPECIAL_RENDER))
            return;
        if (!(itemStack.getItem() instanceof SpecializedItemRendering special))
            return;
        ModelResourceLocation location = special.getEmissiveModelLocation(itemStack, type);
        if (location == null)
            return;
        ItemRenderer self = (ItemRenderer)(Object)this;
        model = self.getItemModelShaper().getModelManager().getModel(location);
        if (original != null)
            model = model.getOverrides().resolve(model, original, Minecraft.getInstance().level, holder, 0);

        pose.pushPose();
        model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(pose, model, type, leftHand);

        pose.translate(-0.5D, -0.5D, -0.5D);

        RenderType renderType = ItemLayerModel.getLayerRenderType(true);
        ForgeHooksClient.setRenderType(renderType); // needed for compatibility with MultiLayerModels
        VertexConsumer vertexBuilder = ItemRenderer.getFoilBufferDirect(buffers, renderType, true, itemStack.hasFoil());
        self.renderModelLists(model, itemStack, LightTexture.FULL_BRIGHT, packedOverlay, pose, vertexBuilder);
        ForgeHooksClient.setRenderType(null);

        pose.popPose();
    }
}
