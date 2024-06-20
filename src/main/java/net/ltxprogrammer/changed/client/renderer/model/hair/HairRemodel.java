package net.ltxprogrammer.changed.client.renderer.model.hair;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.HairModel;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class HairRemodel {
    public static LayerDefinition createUpperHair() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition FemaleHair = partdefinition.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.3F))
                .texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.45F)), PartPose.offset(0.0F, -4.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public static LayerDefinition createLowerHair() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition FemaleLowerHair = partdefinition.addOrReplaceChild("LowerHair", CubeListBuilder.create().texOffs(0, 20).addBox(-4.0F, 4.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.4F))
                .texOffs(20, 24).addBox(-4.0F, 4.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.65F)), PartPose.offset(0.0F, -4.0F, 0.0F));

        PartDefinition hair3_r1 = FemaleLowerHair.addOrReplaceChild("hair3_r1", CubeListBuilder.create().texOffs(40, 22).addBox(-4.0F, -2.25F, -1.0F, 8.0F, 4.0F, 2.0F, new CubeDeformation(0.35F)), PartPose.offsetAndRotation(0.0F, 6.5F, -2.0F, -0.4363F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    public record Entry(ModelLayerLocation head, ModelLayerLocation lower, ResourceLocation[] textures) {}

    private static final Map<HairStyle, Entry> MODEL_OVERRIDES = new HashMap<>();
    private static ModelLayerLocation remodelLocation(String name) {
        return new ModelLayerLocation(Changed.modResource("hair/remodel/" + name), "main");
    }

    public static final ModelLayerLocation RIG_UPPER_LOCATION = remodelLocation("rig_upper");
    public static final ModelLayerLocation RIG_LOWER_LOCATION = remodelLocation("rig_lower");

    public static final Function<EntityModelSet, HairModel> RIG_UPPER = new Function<EntityModelSet, HairModel>() {
        @Nullable HairModel cache = null;

        @Override
        public HairModel apply(EntityModelSet entityModelSet) {
            if (cache != null)
                return cache;
            cache = new HairModel(entityModelSet.bakeLayer(RIG_UPPER_LOCATION));
            return cache;
        }
    };

    public static final Function<EntityModelSet, HairModel> RIG_LOWER = new Function<EntityModelSet, HairModel>() {
        @Nullable HairModel cache = null;

        @Override
        public HairModel apply(EntityModelSet entityModelSet) {
            if (cache != null)
                return cache;
            cache = new HairModel(entityModelSet.bakeLayer(RIG_LOWER_LOCATION));
            return cache;
        }
    };
}
