package net.ltxprogrammer.changed.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.block.Pillow;
import net.ltxprogrammer.changed.block.entity.PillowBlockEntity;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ChangedBlockEntityWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {
    private final PillowBlockEntity pillow = new PillowBlockEntity(BlockPos.ZERO, ChangedBlocks.PILLOWS.get(DyeColor.WHITE).get().defaultBlockState());
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    
    public ChangedBlockEntityWithoutLevelRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
        this.blockEntityRenderDispatcher = dispatcher;
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        super.renderByItem(itemStack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
        Item item = itemStack.getItem();
        if (item instanceof BlockItem) {
            Block block = ((BlockItem)item).getBlock();
            BlockState blockstate = block.defaultBlockState();
            BlockEntity blockentity;
            if (block instanceof Pillow pillowBlock) {
                this.pillow.setColor(pillowBlock.getColor());
                blockentity = this.pillow;
            } else {
                return;
            }

            this.blockEntityRenderDispatcher.renderItem(blockentity, poseStack, bufferSource, packedLight, packedOverlay);
        }
    }
}
