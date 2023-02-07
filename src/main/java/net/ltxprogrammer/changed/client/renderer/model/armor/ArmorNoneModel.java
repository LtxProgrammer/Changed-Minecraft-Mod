package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModelController;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;

public class ArmorNoneModel<T extends LatexEntity> extends LatexHumanoidArmorModel<T> {
    public static final ModelLayerLocation INNER_ARMOR = ArmorModelLayerLocation.createInnerArmorLocation(Changed.modResource("armor_none")).get();
    public static final ModelLayerLocation OUTER_ARMOR = ArmorModelLayerLocation.createOuterArmorLocation(Changed.modResource("armor_none")).get();

    public ArmorNoneModel(ModelPart root) {
        super(new Builder(
                root, root, root, root, root, root, root), null);
    }

    public static LayerDefinition createArmorLayer(ArmorModel layer) {
        MeshDefinition meshdefinition = new MeshDefinition();
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void prepareForShorts() {}

    public void setAllVisible(boolean b) {
        return;
    }

    protected void setPartVisibility(T p_117126_, EquipmentSlot p_117127_) {

    }

    public void prepareMobModel(LatexHumanoidModelController controller, T p_102861_, float p_102862_, float p_102863_, float p_102864_) {}

    @Override
    public void prepareMobModel(T p_102861_, float p_102862_, float p_102863_, float p_102864_) {}

    public ModelPart getArm(HumanoidArm p_102852_) {
        return null;
    }

    public void translateToHand(HumanoidArm p_102854_, PoseStack p_102855_) {}

    @Override
    public void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {}

    @Override
    public ModelPart getHead() {
        return null;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {}
}