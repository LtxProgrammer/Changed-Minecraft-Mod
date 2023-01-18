package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class CentaurChestPackModel extends Model {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("chest_pack"), "main");
    private final ModelPart ChestPack;

    public CentaurChestPackModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.ChestPack = root.getChild("ChestPack");
    }

    public static LayerDefinition createPack() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition ChestPack = partdefinition.addOrReplaceChild("ChestPack", CubeListBuilder.create().texOffs(0, 16).addBox(2.5F, -1.0F, -3.5F, 4.0F, 8.0F, 8.0F, new CubeDeformation(-0.95F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition LowerTorso_r1 = ChestPack.addOrReplaceChild("LowerTorso_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-6.5F, -11.25F, -3.0F, 4.0F, 8.0F, 8.0F, new CubeDeformation(-0.95F)), PartPose.offsetAndRotation(0.0F, 2.0F, -7.0F, -1.5708F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack p_103111_, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_) {
        ChestPack.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
    }

    public ResourceLocation getTexture() {
        return Changed.modResource("textures/models/chest_pack.png");
    }
}
