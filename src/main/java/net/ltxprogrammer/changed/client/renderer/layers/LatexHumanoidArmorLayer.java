package net.ltxprogrammer.changed.client.renderer.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.RenderUtil;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
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
public class LatexHumanoidArmorLayer<T extends LatexEntity, M extends LatexHumanoidModel<T>, A extends LatexHumanoidArmorModel<T, ?>> extends RenderLayer<T, M> {
    private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();
    final A innerModel;
    final A outerModel;

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
        boolean firstPerson = RenderUtil.isFirstPerson(entity);

        if (!firstPerson || !entity.isSwimming()) // Don't render chest-plate if swimming in first person
            this.renderArmorPiece(pose, buffers, entity, EquipmentSlot.CHEST, packedLight, this.getArmorModel(EquipmentSlot.CHEST));
        this.renderArmorPiece(pose, buffers, entity, EquipmentSlot.LEGS, packedLight, this.getArmorModel(EquipmentSlot.LEGS));
        this.renderArmorPiece(pose, buffers, entity, EquipmentSlot.FEET, packedLight, this.getArmorModel(EquipmentSlot.FEET));
        if (!firstPerson) // Don't render helmet if first person; only really applies to first person mods
            this.renderArmorPiece(pose, buffers, entity, EquipmentSlot.HEAD, packedLight, this.getArmorModel(EquipmentSlot.HEAD));
    }

    private void renderArmorPiece(PoseStack pose, MultiBufferSource buffers, T entity, EquipmentSlot slot, int packedLight, LatexHumanoidArmorModel<T, ?> model) {
        ItemStack itemstack = entity.getItemBySlot(slot);
        if (itemstack.getItem() instanceof ArmorItem) {
            ArmorItem armoritem = (ArmorItem)itemstack.getItem();
            if (armoritem.getSlot() == slot) {
                this.getParentModel().copyPropertiesTo(model);
                boolean foil = itemstack.hasFoil();
                if (armoritem instanceof net.minecraft.world.item.DyeableLeatherItem) {
                    int i = ((net.minecraft.world.item.DyeableLeatherItem)armoritem).getColor(itemstack);
                    float red = (float)(i >> 16 & 255) / 255.0F;
                    float green = (float)(i >> 8 & 255) / 255.0F;
                    float blue = (float)(i & 255) / 255.0F;
                    this.renderModel(entity, itemstack, slot, pose, buffers, packedLight,
                            foil, model, red, green, blue, this.getArmorResource(entity, itemstack, slot, null));
                    this.renderModel(entity, itemstack, slot, pose, buffers, packedLight,
                            foil, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(entity, itemstack, slot, "overlay"));
                } else {
                    this.renderModel(entity, itemstack, slot, pose, buffers, packedLight,
                            foil, model, 1.0F, 1.0F, 1.0F, this.getArmorResource(entity, itemstack, slot, null));
                }

            }
        }
    }

    private void renderModel(T entity, ItemStack stack, EquipmentSlot slot,
                             PoseStack pose, MultiBufferSource buffers, int packedLight, boolean foil, LatexHumanoidArmorModel<T, ?> model,
                             float red, float green, float blue, ResourceLocation armorResource) {
        model.renderForSlot(entity, stack, slot, pose,
                ItemRenderer.getArmorFoilBuffer(buffers, RenderType.armorCutoutNoCull(armorResource), false, foil),
                packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
    }

    LatexHumanoidArmorModel<T, ?> getArmorModel(EquipmentSlot slot) {
        return this.usesInnerModel(slot) ? this.innerModel : this.outerModel;
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