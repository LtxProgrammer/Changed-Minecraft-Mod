package net.ltxprogrammer.changed.client.renderer.model.hair;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class Legacy {
    public static LayerDefinition createMaleHair() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Hair = partdefinition.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -5.25F, -3.5F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.4F))
                .texOffs(24, 0).addBox(-4.0F, -5.0F, -3.25F, 8.0F, 1.0F, 4.0F, new CubeDeformation(0.2F))
                .texOffs(0, 25).addBox(3.75F, -4.25F, -3.1F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.401F))
                .texOffs(32, 14).addBox(-3.5F, -3.45F, -4.35F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(0, 3).addBox(3.95F, -4.45F, -4.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(0, 0).addBox(-4.9F, -4.45F, -4.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(32, 7).addBox(3.75F, -1.0F, -0.1F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.4F))
                .texOffs(29, 29).addBox(-4.7F, -1.0F, -0.1F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.4F))
                .texOffs(16, 22).addBox(-4.7F, -4.25F, -3.1F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.401F))
                .texOffs(16, 16).addBox(-4.0F, -5.5F, -0.2F, 8.0F, 1.0F, 5.0F, new CubeDeformation(0.1F))
                .texOffs(24, 5).addBox(-4.0F, -4.55F, -4.6F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(26, 22).addBox(-4.0F, -4.25F, 4.05F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.4F)), PartPose.offset(0.0F, -4.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createFemaleHair() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Hair = partdefinition.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(24, 0).addBox(-4.5F, -8.0F, -5.0F, 9.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(-4.5F, -6.0F, -5.0F, 2.0F, 7.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(28, 30).addBox(-4.0F, -8.75F, 3.75F, 2.0F, 11.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(34, 30).addBox(2.0F, -8.75F, 3.75F, 2.0F, 11.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(18, 30).addBox(-2.0F, -9.0F, 4.0F, 4.0F, 12.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(36, 6).addBox(3.75F, -4.0F, 0.0F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 37).addBox(-4.75F, -4.0F, 0.0F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(16, 17).addBox(-5.0F, -8.0F, -4.0F, 1.0F, 4.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(0, 25).addBox(4.0F, -8.0F, -4.0F, 1.0F, 4.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(24, 8).addBox(-4.0F, -8.75F, -4.0F, 2.0F, 1.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(26, 21).addBox(2.0F, -8.75F, -4.0F, 2.0F, 1.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(24, 3).addBox(-4.0F, -0.75F, 2.0F, 8.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 16).addBox(-2.0F, -9.0F, -4.0F, 4.0F, 1.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 1.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createFemaleNoBangsHair() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Hair = partdefinition.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(24, 0).addBox(-4.5F, -8.1F, -4.75F, 9.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(28, 30).addBox(-4.0F, -8.75F, 3.75F, 2.0F, 11.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(34, 30).addBox(2.0F, -8.75F, 3.75F, 2.0F, 11.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(18, 30).addBox(-2.0F, -9.0F, 4.0F, 4.0F, 12.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(36, 6).addBox(3.75F, -4.0F, 0.0F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 37).addBox(-4.75F, -4.0F, 0.0F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(16, 17).addBox(-5.0F, -8.0F, -4.0F, 1.0F, 4.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(0, 25).addBox(4.0F, -8.0F, -4.0F, 1.0F, 4.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(24, 8).addBox(-4.0F, -8.75F, -4.0F, 2.0F, 1.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(26, 21).addBox(2.0F, -8.75F, -4.0F, 2.0F, 1.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(24, 3).addBox(-4.0F, -0.75F, 2.0F, 8.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 16).addBox(-2.0F, -9.0F, -4.0F, 4.0F, 1.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
    public static LayerDefinition createMohawkHair() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Hair = partdefinition.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -7.75F, -3.75F, 4.0F, 1.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(2.0F, -7.25F, -3.0F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.025F))
                .texOffs(4, 0).addBox(-3.0F, -7.0F, 3.25F, 1.0F, 5.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 16).addBox(-3.0F, -7.25F, -3.0F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.025F))
                .texOffs(0, 0).addBox(2.0F, -7.0F, 3.25F, 1.0F, 5.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(23, 24).addBox(-2.0F, -7.025F, 3.75F, 4.0F, 7.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 25).addBox(-2.0F, -7.25F, -4.25F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -1.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
    public static LayerDefinition createHeadFuzzHair() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Hair = partdefinition.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -9.75F, -2.5F, 4.0F, 1.0F, 6.0F, CubeDeformation.NONE)
                .texOffs(14, 16).addBox(-3.5F, -9.5F, -3.5F, 7.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(0, 23).addBox(1.5F, -9.725F, -0.5F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(16, 20).addBox(-3.5F, -9.725F, -0.525F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 1.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
    public static LayerDefinition createMaleStandardHair() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Hair = partdefinition.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -8.0F, -4.0F, 4.0F, 1.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(-4.0F, -7.5F, -3.5F, 8.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(32, 8).addBox(2.0F, -7.95F, 0.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 25).addBox(2.5F, -7.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.025F))
                .texOffs(24, 6).addBox(-3.5F, -6.0F, -4.25F, 7.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 3).addBox(3.5F, -7.0F, -4.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(-4.5F, -7.0F, -4.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(29, 30).addBox(2.5F, -4.0F, -1.0F, 2.0F, 2.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(20, 28).addBox(-4.5F, -4.0F, -1.0F, 2.0F, 2.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(16, 17).addBox(-4.5F, -7.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.025F))
                .texOffs(12, 28).addBox(-4.0F, -7.95F, 0.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(24, 4).addBox(-4.0F, -7.0F, -4.5F, 8.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(28, 16).addBox(-4.0F, -7.0F, 4.0F, 8.0F, 6.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -0.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
    public static LayerDefinition createMaleHairNWE() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Hair = partdefinition.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -8.0F, -4.0F, 4.0F, 1.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(-4.0F, -7.5F, -3.5F, 8.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(32, 8).addBox(2.0F, -7.95F, 0.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 25).addBox(2.5F, -7.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.025F))
                .texOffs(24, 6).addBox(-3.5F, -6.0F, -4.25F, 7.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 3).addBox(3.5F, -7.0F, -4.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(-4.5F, -7.0F, -4.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(29, 30).addBox(2.5F, -4.0F, -1.0F, 2.0F, 2.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(20, 28).addBox(-4.5F, -4.0F, -1.0F, 2.0F, 2.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(16, 17).addBox(-4.5F, -7.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.025F))
                .texOffs(12, 28).addBox(-4.0F, -7.95F, 0.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(24, 4).addBox(-4.0F, -7.0F, -4.5F, 8.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(28, 16).addBox(-4.0F, -7.0F, 4.0F, 8.0F, 6.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -0.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}
