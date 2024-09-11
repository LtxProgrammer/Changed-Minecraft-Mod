package net.ltxprogrammer.changed.client.renderer.model.armor;

import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.builders.CubeDeformation;

public enum ArmorModel {
    ARMOR_OUTER(
            "armor_outer",
            LayerDefinitions.OUTER_ARMOR_DEFORMATION,
            LayerDefinitions.INNER_ARMOR_DEFORMATION,
            new CubeDeformation(0.95F),
            new CubeDeformation(-0.25F),
            new CubeDeformation(-0.25F),
            LayerDefinitions.OUTER_ARMOR_DEFORMATION),
    ARMOR_INNER(
            "armor_inner",
            new CubeDeformation(-0.25F),
            LayerDefinitions.OUTER_ARMOR_DEFORMATION,
            new CubeDeformation(-0.25F),
            LayerDefinitions.INNER_ARMOR_DEFORMATION,
            new CubeDeformation(0.45F),
            LayerDefinitions.INNER_ARMOR_DEFORMATION),

    CLOTHING_OUTER(
            "clothing_outer",
            ARMOR_OUTER.deformation.extend(-0.66f),
            ARMOR_OUTER.inverseDeformation.extend(-0.66f),
            ARMOR_OUTER.slightDeformation.extend(-0.66f),
            ARMOR_OUTER.altDeformation.extend(-0.66f),
            ARMOR_OUTER.slightAltDeformation.extend(-0.66f),
            ARMOR_OUTER.dualDeformation.extend(-0.66f)),
    CLOTHING_INNER(
            "clothing_inner",
            ARMOR_INNER.deformation.extend(-0.2f),
            ARMOR_INNER.inverseDeformation.extend(-0.2f),
            ARMOR_INNER.slightDeformation.extend(-0.2f),
            ARMOR_INNER.altDeformation.extend(-0.2f),
            ARMOR_INNER.slightAltDeformation.extend(-0.2f),
            ARMOR_INNER.dualDeformation.extend(-0.2f));

    public final String identifier;
    public final CubeDeformation deformation;
    public final CubeDeformation inverseDeformation;
    public final CubeDeformation slightDeformation;
    public final CubeDeformation altDeformation;
    public final CubeDeformation slightAltDeformation;
    public final CubeDeformation dualDeformation;
    ArmorModel(String identifier, CubeDeformation deformation, CubeDeformation inverseDeformation, CubeDeformation slightDeformation, CubeDeformation altDeformation, CubeDeformation slightAltDeformation, CubeDeformation dualDeformation) {
        this.identifier = identifier;
        this.deformation = deformation;
        this.inverseDeformation = inverseDeformation;
        this.slightDeformation = slightDeformation;
        this.altDeformation = altDeformation;
        this.slightAltDeformation = slightAltDeformation;
        this.dualDeformation = dualDeformation;
    }
}
