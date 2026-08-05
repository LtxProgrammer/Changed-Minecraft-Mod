package net.ltxprogrammer.changed.client.renderer.layers;

import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.DoubleArmedModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;

public class DoubleItemInHandLayer<T extends ChangedEntity, M extends AdvancedHumanoidModel<T> & DoubleArmedModel<T> & HeadedModel> extends ExtraItemInHandLayer<T, M> {
    public DoubleItemInHandLayer(RenderLayerParent<T, M> parent, ItemInHandRenderer itemInHandRenderer) {
        super(parent, itemInHandRenderer, 0, DoubleArmedModel::translateToLowerHand);
    }
}
