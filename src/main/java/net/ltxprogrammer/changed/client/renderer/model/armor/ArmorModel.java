package net.ltxprogrammer.changed.client.renderer.model.armor;

import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.resources.ResourceLocation;

public class ArmorModel {
    public enum ArmorModelType {
        ARMOR, CLOTHING;
    };

    public enum ArmorModelPosition {
        INNER, OUTER;
    };

    public final ResourceLocation id;
    public final String identifier;
    private final ArmorModelType modelType;
    private final ArmorModelPosition modelPosition;
    public final CubeDeformation deformation;
    public final CubeDeformation inverseDeformation;
    public final CubeDeformation slightDeformation;
    public final CubeDeformation altDeformation;
    public final CubeDeformation slightAltDeformation;
    public final CubeDeformation dualDeformation;

    public ArmorModel(ResourceLocation id, ArmorModelType modelType, ArmorModelPosition modelPosition, CubeDeformation deformation, CubeDeformation inverseDeformation, CubeDeformation slightDeformation, CubeDeformation altDeformation, CubeDeformation slightAltDeformation, CubeDeformation dualDeformation) {
        this.id = id;
        this.identifier = "%s_%s".formatted(id.getNamespace(), id.getPath());
        this.modelType = modelType;
        this.modelPosition = modelPosition;
        this.deformation = deformation;
        this.inverseDeformation = inverseDeformation;
        this.slightDeformation = slightDeformation;
        this.altDeformation = altDeformation;
        this.slightAltDeformation = slightAltDeformation;
        this.dualDeformation = dualDeformation;
    }

    public boolean isArmor() {
        return this.modelType == ArmorModelType.ARMOR;
    }

    public boolean isClothing() {
        return this.modelType == ArmorModelType.CLOTHING;
    }

    public boolean isInnerModel() {
        return this.modelPosition == ArmorModelPosition.INNER;
    }

    public boolean isOuterModel() {
        return this.modelPosition == ArmorModelPosition.OUTER;
    }

    public boolean isModelSlim(boolean slim) {
        return slim && isClothing();
    }
}
