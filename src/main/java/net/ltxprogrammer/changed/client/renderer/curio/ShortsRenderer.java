package net.ltxprogrammer.changed.client.renderer.curio;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexHumanoidArmorLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorHumanModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.item.Shorts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.Optional;

public class ShortsRenderer implements ICurioRenderer, TransitionalCurio {
    private final HumanoidModel clothingModel;

    public ShortsRenderer() {
        clothingModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ArmorHumanModel.MODEL_SET.getModelName(ArmorModel.CLOTHING_INNER)));
    }

    @Override
    public Optional<HumanoidModel<?>> getBeforeModel(ItemStack stack, SlotContext slotContext, RenderLayerParent<?,?> renderLayerParent) {
        return Optional.of(clothingModel);
    }

    @Override
    public Optional<AdvancedHumanoidModel<?>> getAfterModel(ItemStack stack, SlotContext slotContext, RenderLayerParent<?,?> renderLayerParent) {
        if (renderLayerParent instanceof AdvancedHumanoidRenderer advancedHumanoidRenderer) {
            final LatexHumanoidArmorLayer layer = advancedHumanoidRenderer.getArmorLayer();
            return Optional.of((LatexHumanoidArmorModel<?, ?>) layer.modelPicker.getModelSetForSlot(EquipmentSlot.LEGS)
                    .get(ArmorModel.CLOTHING_INNER));
        }

        return Optional.empty();
    }

    @Override
    public Optional<ResourceLocation> getModelTexture(ItemStack stack, SlotContext slotContext) {
        if (stack.getItem() instanceof Shorts clothing)
            return Optional.ofNullable(clothing.getTexture(stack, slotContext.entity()));
        else
            return Optional.empty();
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer,
                                                                          int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (stack.getItem() instanceof Shorts clothing) {
            final T entity = (T)slotContext.entity();
            ResourceLocation texture = clothing.getTexture(stack, entity);
            if (texture == null) return;

            if (entity instanceof ChangedEntity changedEntity && renderLayerParent instanceof AdvancedHumanoidRenderer advancedHumanoidRenderer) {
                final var layer = advancedHumanoidRenderer.getArmorLayer();
                final LatexHumanoidArmorModel model = (LatexHumanoidArmorModel<?, ?>) layer.modelPicker.getModelSetForSlot(EquipmentSlot.LEGS)
                        .get(ArmorModel.CLOTHING_INNER);

                model.prepareMobModel(changedEntity, limbSwing, limbSwingAmount, partialTicks);
                model.setupAnim(changedEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                model.renderForSlot(changedEntity, stack, EquipmentSlot.LEGS, matrixStack,
                        ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(texture), false, stack.hasFoil()),
                        light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            } else if (renderLayerParent.getModel() instanceof HumanoidModel<?> baseModel) {
                baseModel.copyPropertiesTo(clothingModel);

                clothingModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                clothingModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                clothingModel.renderToBuffer(matrixStack,
                        ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(texture), false, stack.hasFoil()),
                        light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
