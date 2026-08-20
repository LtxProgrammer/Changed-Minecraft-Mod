package net.ltxprogrammer.changed.client.renderer.accessory;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexHumanoidArmorLayer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorHumanModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.item.Clothing;
import net.ltxprogrammer.changed.util.Color3;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SimpleClothingRenderer implements AccessoryRenderer, TransitionalAccessory {
    public static record ModelComponent(ArmorModel armorModel, EquipmentSlot renderAs) {}

    protected final ArmorModel humanoid;
    protected final HumanoidModel clothingModel;
    protected final Set<ModelComponent> components;

    public SimpleClothingRenderer(ArmorModel humanoid, Set<ModelComponent> components) {
        this.humanoid = humanoid;
        this.components = components;
        clothingModel = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ArmorHumanModel.MODEL_SET.getModelName(humanoid)));
    }

    @Override
    public Optional<HumanoidModel<?>> getBeforeModel(AccessorySlotContext<?> slotContext, RenderLayerParent<?,?> renderLayerParent) {
        return Optional.of(clothingModel);
    }

    @Override
    public Stream<AdvancedHumanoidModel<?>> getAfterModels(AccessorySlotContext<?> slotContext, RenderLayerParent<?,?> renderLayerParent) {
        if (renderLayerParent instanceof AdvancedHumanoidRenderer advancedHumanoidRenderer && EntityUtil.maybeGetOverlaying(slotContext.wearer()) instanceof ChangedEntity wearer) {
            final LatexHumanoidArmorLayer layer = advancedHumanoidRenderer.getArmorLayer();
            return components.stream().map(component -> Optional.of((LatexHumanoidArmorModel<?,?>) layer.modelPicker.getModelSetForSlot(wearer, component.renderAs)
                    .get(component.armorModel))).filter(Optional::isPresent).map(Optional::get);
        }

        return Stream.empty();
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
            if (entity instanceof ChangedEntity changedEntity && renderLayerParent instanceof AdvancedHumanoidRenderer advancedHumanoidRenderer) {
                final var layer = advancedHumanoidRenderer.getArmorLayer();
                for (var component : components) {
                    final LatexHumanoidArmorModel model = (LatexHumanoidArmorModel<?, ?>) layer.modelPicker.getModelSetForSlot(changedEntity, component.renderAs)
                            .get(component.armorModel);

                    model.getAnimator(changedEntity).copyProperties(advancedHumanoidRenderer.getModel(changedEntity).getAnimator(changedEntity));
                    model.prepareMobModel(changedEntity, limbSwing, limbSwingAmount, partialTicks);
                    model.setupAnim(changedEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                    List<ModelPart> hiddenParts = new ObjectArrayList<>();
                    clothing.hideModelParts(stack, entity, component.renderAs, partIdentifier -> {
                        var part = partIdentifier.getModelPart(model, changedEntity);
                        if (part != null)
                            hiddenParts.add(part);
                    });
                    hiddenParts.forEach(part -> part.visible = false);

                    model.prepareVisibility(component.renderAs, stack);
                    if (stack.getItem() instanceof DyeableLeatherItem dyeable) {
                        Color3 color = Color3.fromInt(dyeable.getColor(stack));
                        final ResourceLocation texture = clothing.getTexture(stack, entity, component.renderAs, null);
                        if (texture != null)
                            model.renderForSlot(changedEntity, advancedHumanoidRenderer, stack, component.renderAs, matrixStack,
                                    ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(texture), false, stack.hasFoil()),
                                    light, OverlayTexture.NO_OVERLAY, color.red(), color.green(), color.blue(), 1.0F);
                        final ResourceLocation overlay = clothing.getTexture(stack, entity, component.renderAs, "overlay");
                        if (overlay != null)
                            model.renderForSlot(changedEntity, advancedHumanoidRenderer, stack, component.renderAs, matrixStack,
                                    ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(overlay), false, stack.hasFoil()),
                                    light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                    } else {
                        final ResourceLocation texture = clothing.getTexture(stack, entity, component.renderAs, null);
                        if (texture != null)
                            model.renderForSlot(changedEntity, advancedHumanoidRenderer, stack, component.renderAs, matrixStack,
                                    ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(texture), false, stack.hasFoil()),
                                    light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                    }

                    hiddenParts.forEach(part -> part.visible = true);
                }
            } else if (renderLayerParent.getModel() instanceof HumanoidModel<?> baseModel) {
                // TODO: Multiple humanoid layers
                EquipmentSlot equipmentSlot = (humanoid == ArmorModel.CLOTHING_OUTER || humanoid == ArmorModel.ARMOR_OUTER) ? EquipmentSlot.CHEST : EquipmentSlot.LEGS;
                baseModel.copyPropertiesTo(clothingModel);

                List<ModelPart> hiddenParts = new ObjectArrayList<>();
                clothing.hideModelParts(stack, entity, equipmentSlot, partIdentifier -> {
                    var part = partIdentifier.getModelPart(baseModel);
                    if (part != null)
                        hiddenParts.add(part);
                });
                hiddenParts.forEach(part -> part.visible = false);

                if (stack.getItem() instanceof DyeableLeatherItem dyeable) {
                    Color3 color = Color3.fromInt(dyeable.getColor(stack));
                    final ResourceLocation texture = clothing.getTexture(stack, entity, equipmentSlot, null);
                    if (texture != null)
                        clothingModel.renderToBuffer(matrixStack,
                                ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(texture), false, stack.hasFoil()),
                                light, OverlayTexture.NO_OVERLAY, color.red(), color.green(), color.blue(), 1.0F);
                    final ResourceLocation overlay = clothing.getTexture(stack, entity, equipmentSlot, "overlay");
                    if (overlay != null)
                        clothingModel.renderToBuffer(matrixStack,
                                ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(overlay), false, stack.hasFoil()),
                                light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    final ResourceLocation texture = clothing.getTexture(stack, entity, equipmentSlot, null);
                    if (texture != null)
                        clothingModel.renderToBuffer(matrixStack,
                                ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(texture), false, stack.hasFoil()),
                                light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                }

                hiddenParts.forEach(part -> part.visible = true);
            }
        }
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void renderFirstPersonOnArms(AccessorySlotContext<T> slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, HumanoidArm arm, PartPose armPose, float partialTicks) {
        ItemStack stack = slotContext.stack();
        if (stack.getItem() instanceof Clothing clothing) {
            final T entity = slotContext.wearer();

            if (entity instanceof ChangedEntity changedEntity && renderLayerParent instanceof AdvancedHumanoidRenderer advancedHumanoidRenderer) {
                final var layer = advancedHumanoidRenderer.getArmorLayer();
                for (var component : components) {
                    if (component.renderAs != EquipmentSlot.CHEST) continue;

                    final LatexHumanoidArmorModel model = (LatexHumanoidArmorModel<?, ?>) layer.modelPicker.getModelSetForSlot(changedEntity, component.renderAs)
                            .get(component.armorModel);

                    model.prepareMobModel(changedEntity, 0f, 0f, partialTicks);
                    /*model.setupAnim(changedEntity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
                    model.setupHand(changedEntity);*/
                    model.prepareVisibility(component.renderAs, stack);

                    List<ModelPart> hiddenParts = new ObjectArrayList<>();
                    clothing.hideModelParts(stack, entity, component.renderAs, partIdentifier -> {
                        var part = partIdentifier.getModelPart(model, changedEntity);
                        if (part != null)
                            hiddenParts.add(part);
                    });
                    hiddenParts.forEach(part -> part.visible = false);

                    var armPart = model.getArm(arm);
                    armPart.loadPose(armPose);
                    if (stack.getItem() instanceof DyeableLeatherItem dyeable) {
                        Color3 color = Color3.fromInt(dyeable.getColor(stack));
                        final ResourceLocation texture = clothing.getTexture(stack, entity, component.renderAs, null);
                        if (texture != null)
                            FormRenderHandler.renderModelPartWithTexture(model.getArm(arm),
                                    matrixStack, ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(texture), false, stack.hasFoil()),
                                    light, color.red(), color.green(), color.blue(), 1F);
                        final ResourceLocation overlay = clothing.getTexture(stack, entity, component.renderAs, "overlay");
                        if (overlay != null)
                            FormRenderHandler.renderModelPartWithTexture(model.getArm(arm),
                                    matrixStack, ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(overlay), false, stack.hasFoil()),
                                    light, 1F, 1F, 1F, 1F);
                    } else {
                        final ResourceLocation texture = clothing.getTexture(stack, entity, component.renderAs, null);
                        if (texture != null)
                            FormRenderHandler.renderModelPartWithTexture(model.getArm(arm),
                                    matrixStack, ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(texture), false, stack.hasFoil()),
                                    light, 1F, 1F, 1F, 1F);
                    }

                    hiddenParts.forEach(part -> part.visible = true);
                }
            } else if (renderLayerParent.getModel() instanceof HumanoidModel<?> baseModel) {
                // TODO: Multiple humanoid layers
                baseModel.copyPropertiesTo(clothingModel);

                List<ModelPart> hiddenParts = new ObjectArrayList<>();
                clothing.hideModelParts(stack, entity, EquipmentSlot.CHEST, partIdentifier -> {
                    var part = partIdentifier.getModelPart(baseModel);
                    if (part != null)
                        hiddenParts.add(part);
                });
                hiddenParts.forEach(part -> part.visible = false);

                var armPart = arm == HumanoidArm.RIGHT ? clothingModel.rightArm : clothingModel.leftArm;
                armPart.loadPose(armPose);
                if (stack.getItem() instanceof DyeableLeatherItem dyeable) {
                    Color3 color = Color3.fromInt(dyeable.getColor(stack));
                    final ResourceLocation texture = clothing.getTexture(stack, entity, EquipmentSlot.CHEST, null);
                    if (texture != null)
                        FormRenderHandler.renderVanillaModelPartWithTexture(armPart,
                                matrixStack, ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(texture), false, stack.hasFoil()),
                                light, color.red(), color.green(), color.blue(), 1F);
                    final ResourceLocation overlay = clothing.getTexture(stack, entity, EquipmentSlot.CHEST, "overlay");
                    if (overlay != null)
                        FormRenderHandler.renderVanillaModelPartWithTexture(armPart,
                                matrixStack, ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(overlay), false, stack.hasFoil()),
                                light, 1F, 1F, 1F, 1F);
                } else {
                    final ResourceLocation texture = clothing.getTexture(stack, entity, EquipmentSlot.CHEST, null);
                    if (texture != null)
                        FormRenderHandler.renderVanillaModelPartWithTexture(armPart,
                                matrixStack, ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.armorCutoutNoCull(texture), false, stack.hasFoil()),
                                light, 1F, 1F, 1F, 1F);
                }

                hiddenParts.forEach(part -> part.visible = true);
            }
        }
    }

    public static Supplier<AccessoryRenderer> of(ArmorModel armorModel, EquipmentSlot renderAs) {
        return () -> new SimpleClothingRenderer(armorModel, Set.of(new ModelComponent(armorModel, renderAs)));
    }

    public static Supplier<AccessoryRenderer> of(ArmorModel humanoidModel, Set<ModelComponent> components) {
        return () -> new SimpleClothingRenderer(humanoidModel, components);
    }
}
