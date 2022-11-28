package net.ltxprogrammer.changed.client.renderer.model.armor;

import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.builders.CubeDeformation;

public enum ArmorModel {
    OUTER(LayerDefinitions.OUTER_ARMOR_DEFORMATION, LayerDefinitions.INNER_ARMOR_DEFORMATION, new CubeDeformation(0.95F), new CubeDeformation(-0.25F), new CubeDeformation(-0.25F),
            LayerDefinitions.OUTER_ARMOR_DEFORMATION),
    INNER(new CubeDeformation(-0.25F), LayerDefinitions.OUTER_ARMOR_DEFORMATION, new CubeDeformation(-0.25F), LayerDefinitions.INNER_ARMOR_DEFORMATION, new CubeDeformation(0.45F),
            LayerDefinitions.INNER_ARMOR_DEFORMATION);

    final CubeDeformation deformation;
    final CubeDeformation inverseDeformation;
    final CubeDeformation slightDeformation;
    final CubeDeformation altDeformation;
    final CubeDeformation slightAltDeformation;
    final CubeDeformation dualDeformation;
    ArmorModel(CubeDeformation deformation, CubeDeformation inverseDeformation, CubeDeformation slightDeformation, CubeDeformation altDeformation, CubeDeformation slightAltDeformation, CubeDeformation dualDeformation) {
        this.deformation = deformation;
        this.inverseDeformation = inverseDeformation;
        this.slightDeformation = slightDeformation;
        this.altDeformation = altDeformation;
        this.slightAltDeformation = slightAltDeformation;
        this.dualDeformation = dualDeformation;
    }
}
