package net.ltxprogrammer.changed.client.renderer.model.armor;

import com.google.common.collect.Maps;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

public class ArmorModels {
    private static final Map<ResourceLocation, ArmorModel> REGISTRY = Maps.newHashMap();

    public static ArmorModel ARMOR_OUTER = registerArmor(Changed.modResource("armor_outer"),
            ArmorModel.ArmorModelPosition.OUTER,
            LayerDefinitions.OUTER_ARMOR_DEFORMATION,
            LayerDefinitions.INNER_ARMOR_DEFORMATION,
            new CubeDeformation(0.95F),
            LatexHumanoidArmorModel.HIDDEN_CUBE,
            LatexHumanoidArmorModel.HIDDEN_CUBE,
            LayerDefinitions.OUTER_ARMOR_DEFORMATION);
    public static ArmorModel ARMOR_INNER = registerArmor(Changed.modResource("armor_inner"),
            ArmorModel.ArmorModelPosition.INNER,
            LatexHumanoidArmorModel.HIDDEN_CUBE,
            LayerDefinitions.OUTER_ARMOR_DEFORMATION,
            LatexHumanoidArmorModel.HIDDEN_CUBE,
            LayerDefinitions.INNER_ARMOR_DEFORMATION,
            new CubeDeformation(0.45F),
            LayerDefinitions.INNER_ARMOR_DEFORMATION);

    public static ArmorModel CLOTHING_OUTER = registerClothing(Changed.modResource("clothing_outer"),
            ArmorModel.ArmorModelPosition.OUTER,
            ARMOR_OUTER.deformation.extend(-0.75f),
            ARMOR_OUTER.inverseDeformation.extend(-0.66f),
            ARMOR_OUTER.slightDeformation.extend(-0.66f),
            ARMOR_OUTER.altDeformation.extend(-0.66f),
            ARMOR_OUTER.slightAltDeformation.extend(-0.66f),
            ARMOR_OUTER.dualDeformation.extend(-0.66f));
    public static ArmorModel CLOTHING_MIDDLE = registerClothing(Changed.modResource("clothing_middle"),
            ArmorModel.ArmorModelPosition.INNER,
            ARMOR_INNER.deformation.extend(-0.2f),
            ARMOR_INNER.inverseDeformation.extend(-0.15f),
            ARMOR_INNER.slightDeformation.extend(-0.15f),
            ARMOR_INNER.altDeformation.extend(-0.15f),
            ARMOR_INNER.slightAltDeformation.extend(-0.15f),
            ARMOR_INNER.dualDeformation.extend(-0.15f));
    public static ArmorModel CLOTHING_INNER = registerClothing(Changed.modResource("clothing_inner"),
            ArmorModel.ArmorModelPosition.INNER,
            ARMOR_INNER.deformation.extend(-0.25f),
            ARMOR_INNER.inverseDeformation.extend(-0.2f),
            ARMOR_INNER.slightDeformation.extend(-0.2f),
            ARMOR_INNER.altDeformation.extend(-0.2f),
            ARMOR_INNER.slightAltDeformation.extend(-0.2f),
            ARMOR_INNER.dualDeformation.extend(-0.2f));

    public static ArmorModel registerArmor(ResourceLocation id, ArmorModel.ArmorModelPosition position, CubeDeformation deformation, CubeDeformation inverseDeformation, CubeDeformation slightDeformation, CubeDeformation altDeformation, CubeDeformation slightAltDeformation, CubeDeformation dualDeformation) {
        ArmorModel model = new ArmorModel(id, ArmorModel.ArmorModelType.ARMOR, position, deformation, inverseDeformation, slightDeformation, altDeformation, slightAltDeformation, dualDeformation);
        REGISTRY.put(id, model);
        return model;
    }

    public static ArmorModel registerClothing(ResourceLocation id, ArmorModel.ArmorModelPosition position, CubeDeformation deformation, CubeDeformation inverseDeformation, CubeDeformation slightDeformation, CubeDeformation altDeformation, CubeDeformation slightAltDeformation, CubeDeformation dualDeformation) {
        ArmorModel model = new ArmorModel(id, ArmorModel.ArmorModelType.CLOTHING, position, deformation, inverseDeformation, slightDeformation, altDeformation, slightAltDeformation, dualDeformation);
        REGISTRY.put(id, model);
        return model;
    }

    public static ArmorModel getModel(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    public static Collection<ArmorModel> getModels() {
        return REGISTRY.values();
    }

    public static Stream<ArmorModel> streamModels() {
        return REGISTRY.values().stream();
    }
}
