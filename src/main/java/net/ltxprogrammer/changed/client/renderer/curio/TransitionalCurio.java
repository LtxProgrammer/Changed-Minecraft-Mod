package net.ltxprogrammer.changed.client.renderer.curio;

import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Optional;

public interface TransitionalCurio {
    Optional<HumanoidModel<?>> getBeforeModel(ItemStack stack, SlotContext slotContext, RenderLayerParent<?,?> renderLayerParent);
    Optional<AdvancedHumanoidModel<?>> getAfterModel(ItemStack stack, SlotContext slotContext, RenderLayerParent<?,?> renderLayerParent);

    Optional<ResourceLocation> getModelTexture(ItemStack stack, SlotContext slotContext);
}
