package net.ltxprogrammer.changed.client.renderer.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.item.Shorts;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class LatexHumanoidArmorLayer<T extends LatexEntity, M extends LatexHumanoidModel<T>, A extends LatexHumanoidArmorModel<T>> extends RenderLayer<T, M> {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
    private final A innerModel;
    private final A outerModel;

    public LatexHumanoidArmorLayer(RenderLayerParent<T, M> parentModel, A innerModel, A outerModel) {
        super(parentModel);
        this.innerModel = innerModel;
        this.outerModel = outerModel;
    }

    public void render(PoseStack pose, MultiBufferSource buffers, int packedLight, T entity, float limbSwing, float limgSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.innerModel.prepareMobModel(entity, limbSwing, limgSwingAmount, partialTicks);
        this.innerModel.setupAnim(entity, limbSwing, limgSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.outerModel.prepareMobModel(entity, limbSwing, limgSwingAmount, partialTicks);
        this.outerModel.setupAnim(entity, limbSwing, limgSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.renderArmorPiece(pose, buffers, entity, EquipmentSlot.CHEST, packedLight, this.getArmorModel(EquipmentSlot.CHEST));
        this.renderArmorPiece(pose, buffers, entity, EquipmentSlot.LEGS, packedLight, this.getArmorModel(EquipmentSlot.LEGS));
        this.renderArmorPiece(pose, buffers, entity, EquipmentSlot.FEET, packedLight, this.getArmorModel(EquipmentSlot.FEET));
        this.renderArmorPiece(pose, buffers, entity, EquipmentSlot.HEAD, packedLight, this.getArmorModel(EquipmentSlot.HEAD));
    }

    private void renderArmorPiece(PoseStack pose, MultiBufferSource buffers, T entity, EquipmentSlot slot, int packedLight, A model) {
        ItemStack itemstack = entity.getItemBySlot(slot);
        if (itemstack.getItem() instanceof ArmorItem) {
            ArmorItem armoritem = (ArmorItem)itemstack.getItem();
            if (armoritem.getSlot() == slot) {
                this.getParentModel().copyPropertiesTo(model);
                this.setPartVisibility(entity, model, slot);
                boolean foil = itemstack.hasFoil();
                if (armoritem instanceof net.minecraft.world.item.DyeableLeatherItem) {
                    int i = ((net.minecraft.world.item.DyeableLeatherItem)armoritem).getColor(itemstack);
                    float red = (float)(i >> 16 & 255) / 255.0F;
                    float green = (float)(i >> 8 & 255) / 255.0F;
                    float blue = (float)(i & 255) / 255.0F;
                    this.renderModel(pose, buffers, packedLight, foil, model, red, green, blue, this.getArmorResource(entity, itemstack, slot, null));
                    this.renderModel(pose, buffers, packedLight, foil, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(entity, itemstack, slot, "overlay"));
                } else {
                    this.renderModel(pose, buffers, packedLight, foil, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(entity, itemstack, slot, null));
                }

            }
        }
    }

    protected void setPartVisibility(T entity, A model, EquipmentSlot slot) {
        model.setAllVisible(false);
        switch(slot) {
            case HEAD:
                model.head.visible = true;
                break;
            case CHEST:
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
                if (model.rightArm2 != null)
                    model.rightArm2.visible = true;
                if (model.leftArm2 != null)
                    model.leftArm2.visible = true;
                if (model.rightArm3 != null)
                    model.rightArm3.visible = true;
                if (model.leftArm3 != null)
                    model.leftArm3.visible = true;
                break;
            case LEGS:
                if (entity.getItemBySlot(slot).getItem() instanceof Shorts) {
                    model.body.getAllParts().forEach(part -> part.visible = false);
                    model.rightLeg.getAllParts().forEach(part -> part.visible = false);
                    model.leftLeg.getAllParts().forEach(part -> part.visible = false);
                    if (model.rightLeg2 != null)
                        model.rightLeg2.getAllParts().forEach(part -> part.visible = false);
                    if (model.leftLeg2 != null)
                        model.leftLeg2.getAllParts().forEach(part -> part.visible = false);

                    model.body.visible = true;
                    model.rightLeg.visible = true;
                    model.leftLeg.visible = true;
                    if (model.rightLeg2 != null)
                        model.rightLeg2.visible = true;
                    if (model.leftLeg2 != null)
                        model.leftLeg2.visible = true;

                    model.prepareForShorts();
                    break;
                }
                model.body.getAllParts().forEach(part -> part.visible = true);
                model.rightLeg.getAllParts().forEach(part -> part.visible = true);
                model.leftLeg.getAllParts().forEach(part -> part.visible = true);
                if (model.lowerTorso != null)
                    model.lowerTorso.visible = true;
                if (model.rightLeg2 != null)
                    model.rightLeg2.getAllParts().forEach(part -> part.visible = true);
                if (model.leftLeg2 != null)
                    model.leftLeg2.getAllParts().forEach(part -> part.visible = true);
                break;
            case FEET:
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
                if (model.rightLeg2 != null)
                    model.rightLeg2.visible = true;
                if (model.leftLeg2 != null)
                    model.leftLeg2.visible = true;
        }

    }

    private void renderModel(PoseStack pose, MultiBufferSource buffers, int packedLight, boolean p_117111_, A model, float red, float green, float blue, ResourceLocation armorResource) {
        model.renderToBuffer(pose,
                ItemRenderer.getArmorFoilBuffer(buffers, RenderType.armorCutoutNoCull(armorResource), false, p_117111_),
                packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
    }

    private A getArmorModel(EquipmentSlot p_117079_) {
        return (A)(this.usesInnerModel(p_117079_) ? this.innerModel : this.outerModel);
    }

    private boolean usesInnerModel(EquipmentSlot p_117129_) {
        return p_117129_ == EquipmentSlot.LEGS;
    }

    /*=================================== FORGE START =========================================*/

    /**
     * More generic ForgeHook version of the above function, it allows for Items to have more control over what texture they provide.
     *
     * @param entity Entity wearing the armor
     * @param stack ItemStack for the armor
     * @param slot Slot ID that the item is in
     * @param type Subtype, can be null or "overlay"
     * @return ResourceLocation pointing at the armor's texture
     */
    public ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @Nullable String type) {
        ArmorItem item = (ArmorItem)stack.getItem();
        String texture = item.getMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String s1 = String.format(java.util.Locale.ROOT, "%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (usesInnerModel(slot) ? 2 : 1), type == null ? "" : String.format(java.util.Locale.ROOT, "_%s", type));

        s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
        ResourceLocation resourcelocation = ARMOR_LOCATION_CACHE.get(s1);

        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(s1);
            ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
        }

        return resourcelocation;
    }
    /*=================================== FORGE END ===========================================*/
}