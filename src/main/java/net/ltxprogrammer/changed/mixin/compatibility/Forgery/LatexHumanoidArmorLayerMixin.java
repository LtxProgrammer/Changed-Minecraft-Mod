package net.ltxprogrammer.changed.mixin.compatibility.Forgery;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.fabrication.FabConf;
import com.unascribed.fabrication.interfaces.GetSuppressedSlots;
import net.ltxprogrammer.changed.client.renderer.layers.LatexHumanoidArmorLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LatexHumanoidArmorLayer.class, remap = false)
@RequiredMods("fabrication")
public abstract class LatexHumanoidArmorLayerMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>, A extends LatexHumanoidArmorModel<T, ?>> extends RenderLayer<T, M> {
    private LatexHumanoidArmorLayerMixin(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @Inject(method = "renderModel(Lnet/ltxprogrammer/changed/entity/ChangedEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IZLnet/ltxprogrammer/changed/client/renderer/model/armor/LatexHumanoidArmorModel;FFFLnet/minecraft/resources/ResourceLocation;)V", at = @At("HEAD"), cancellable = true)
    private void hideArmor(T entity, ItemStack stack, EquipmentSlot slot, PoseStack pose, MultiBufferSource buffers, int packedLight, boolean foil, LatexHumanoidArmorModel<T, ?> model, float red, float green, float blue, ResourceLocation armorResource, CallbackInfo ci) {
        if (FabConf.isEnabled("*.hide_armor") && entity.getUnderlyingPlayer() instanceof GetSuppressedSlots suppressedSlots
        && suppressedSlots.fabrication$getSuppressedSlots().contains(slot))
            ci.cancel();
    }
}
