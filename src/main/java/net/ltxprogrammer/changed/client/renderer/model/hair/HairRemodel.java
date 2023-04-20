package net.ltxprogrammer.changed.client.renderer.model.hair;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class HairRemodel {
    public static LayerDefinition createFemaleHair() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition FemaleHair = partdefinition.addOrReplaceChild("FemaleHair", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.3F))
                .texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.45F)), PartPose.offset(0.0F, -4.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public static LayerDefinition createFemaleLowerHair() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition FemaleLowerHair = partdefinition.addOrReplaceChild("FemaleLowerHair", CubeListBuilder.create().texOffs(0, 20).addBox(-4.0F, 4.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.4F))
                .texOffs(20, 24).addBox(-4.0F, 4.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.65F)), PartPose.offset(0.0F, -4.0F, 0.0F));

        PartDefinition hair3_r1 = FemaleLowerHair.addOrReplaceChild("hair3_r1", CubeListBuilder.create().texOffs(40, 22).addBox(-4.0F, -2.25F, -1.0F, 8.0F, 4.0F, 2.0F, new CubeDeformation(0.35F)), PartPose.offsetAndRotation(0.0F, 6.5F, -2.0F, -0.4363F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public record Entry(ModelLayerLocation head, ModelLayerLocation lower, ResourceLocation[] textures) {}

    private static final Map<HairStyle, Entry> MODEL_OVERRIDES = new HashMap<>();
    private static ModelLayerLocation remodelLocation(String name) {
        return new ModelLayerLocation(Changed.modResource("hair/remodel/" + name), "main");
    }

    public static final ModelLayerLocation MALE_RIG = remodelLocation("male_rig");
    public static final ModelLayerLocation FEMALE_RIG = remodelLocation("female_rig");
    public static final ModelLayerLocation FEMALE_RIG_LOWER = remodelLocation("female_rig_lower");

    private static final Entry F_LONG_MESSY = new Entry(FEMALE_RIG, FEMALE_RIG_LOWER, new ResourceLocation[]{
            Changed.modResource("textures/remodel/hair/f_long_messy.png")
    });

    static {
        MODEL_OVERRIDES.put(HairStyle.FEMALE_SIDE_BANGS, F_LONG_MESSY);
        MODEL_OVERRIDES.put(HairStyle.FEMALE_SIDE_BANGS_L, F_LONG_MESSY);
        MODEL_OVERRIDES.put(HairStyle.FEMALE_SIDE_BANGS_S, F_LONG_MESSY);
        MODEL_OVERRIDES.put(HairStyle.FEMALE_SIDE_BANGS_S_L, F_LONG_MESSY);
        MODEL_OVERRIDES.put(HairStyle.LEGACY_FEMALE_RIGHT_BANG, F_LONG_MESSY);
        MODEL_OVERRIDES.put(HairStyle.LEGACY_FEMALE_RIGHT_BANG_S, F_LONG_MESSY);
        MODEL_OVERRIDES.put(HairStyle.LEGACY_FEMALE_RIGHT_BANG_L, F_LONG_MESSY);
        MODEL_OVERRIDES.put(HairStyle.LEGACY_FEMALE_RIGHT_BANG_S_L, F_LONG_MESSY);
    }

    @Nullable
    public static Entry getOverride(HairStyle style) {
        return MODEL_OVERRIDES.getOrDefault(style, null);
    }
}
