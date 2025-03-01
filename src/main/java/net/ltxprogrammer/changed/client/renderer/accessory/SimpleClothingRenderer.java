package net.ltxprogrammer.changed.client.renderer.accessory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexHumanoidArmorLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorHumanModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.item.Clothing;
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

import java.util.Optional;
import java.util.function.Supplier;

public class SimpleClothingRenderer implements AccessoryRenderer, TransitionalAccessory {
    protected final HumanoidModel clothingModel;
    protected final ArmorModel armorModel;
    protected final EquipmentSlot renderAs;

    public SimpleClothingRenderer(ArmorModel armorModel, EquipmentSlot renderAs) {
        this.armorModel = armorModel;
        this.renderAs = renderAs;
        clothingModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ArmorHumanModel.MODEL_SET.getModelName(armorModel)));
    }

    @Override
    public Optional<HumanoidModel<?>> getBeforeModel(AccessorySlotContext<?> slotContext, RenderLayerParent<?,?> renderLayerParent) {
        return Optional.of(clothingModel);
    }

    @Override
    public Optional<AdvancedHumanoidModel<?>> getAfterModel(AccessorySlotContext<?> slotContext, RenderLayerParent<?,?> renderLayerParent) {
        if (renderLayerParent instanceof AdvancedHumanoidRenderer advancedHumanoidRenderer) {
            final LatexHumanoidArmorLayer layer = advancedHumanoidRenderer.getArmorLayer();
            return Optional.of((LatexHumanoidArmorModel<?, ?>) layer.modelPicker.getModelSetForSlot(renderAs)
                    .get(armorModel));
        }

        return Optional.empty();
    }

    @Override
    public Optional<ResourceLocation> getModelTexture(AccessorySlotContext<?> slotContext) {
        if (slotContext.stack().getItem() instanceof Clothing clothing)
            return Optional.ofNullable(clothing.getTexture(slotContext.stack(), slotContext.wearer()));
        else
            return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(AccessorySlotContext<T> slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer,
                                                                          int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack stack = slotContext.stack();
        if (stack.getItem() instanceof Clothing clothing) {
            final T entity = slotContext.wearer();
            ResourceLocation texture = clothing.getTexture(stack, entity);
            if (texture == null) return;

            if (entity instanceof ChangedEntity changedEntity && renderLayerParent instanceof AdvancedHumanoidRenderer advancedHumanoidRenderer) {
                final var layer = advancedHumanoidRenderer.getArmorLayer();
                final LatexHumanoidArmorModel model = (LatexHumanoidArmorModel<?, ?>) layer.modelPicker.getModelSetForSlot(renderAs)
                        .get(armorModel);

                model.prepareMobModel(changedEntity, limbSwing, limbSwingAmount, partialTicks);
                model.setupAnim(changedEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                model.prepareVisibility(renderAs, stack);
                model.renderForSlot(changedEntity, advancedHumanoidRenderer, stack, renderAs, matrixStack,
                        ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(texture), false, stack.hasFoil()),
                        light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                model.unprepareVisibility(renderAs, stack);
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

    public static Supplier<AccessoryRenderer> of(ArmorModel armorModel, EquipmentSlot renderAs) {
        return () -> new SimpleClothingRenderer(armorModel, renderAs);
    }
}
