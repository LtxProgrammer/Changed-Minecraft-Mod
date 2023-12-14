package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Random;

/**
 * This is basic info about the player, that they set. Currently, it is just colors for eyes, and maybe hair for latex variants.
 */
public class BasicPlayerInfo {
    // Default values here are based on Colin's properties
    // When the player is TF'd these values will copy over to the latex representative
    private Color3 hairColor = new Color3(0.36f, 0.28f, 0.26f);
    private Color3 irisColor = new Color3(0.0f, 0.5f, 1.0f);
    private Color3 scleraColor = Color3.WHITE;
    private boolean overrideIrisOnDarkLatex = false;
    private ResourceLocation eyeStyle = EyeStyle.V2.getId();

    public static final List<Color3> HAIR_COLORS = List.of(
            new Color3(0.98f, 0.85f, 0.48f), // Blond
            new Color3(0.77f, 0.49f, 0.28f), // Dirty Blond
            new Color3(0.36f, 0.28f, 0.26f), // Brown
            new Color3(0.63f, 0.26f, 0.15f), // Light-Brown
            new Color3(0.53f, 0.22f, 0.26f), // Dark-Red
            new Color3(0.08f, 0.08f, 0.08f), // Dark-Black
            new Color3(0.15f, 0.15f, 0.15f) // Black
    );
    public static final List<Color3> IRIS_COLORS = List.of(
            new Color3(0.0f, 0.5f, 1.0f), // Blue
            new Color3(0.42f, 0.63f, 0.76f), // Lightish-Blue
            new Color3(0.47f, 0.54f, 0.95f), // Blurple
            new Color3(0.0f, 1.0f, 0.25f), // Lime-Green
            new Color3(0.44f, 0.85f, 0.0f), // Green
            new Color3(0.38f, 0.22f, 0.14f), // Brown
            new Color3(1.0f, 0.76f, 0.0f), // Dark-Yellow
            new Color3(1.0f, 0.92f, 0.01f), // Yellow
            new Color3(1.0f, 0.63f, 0.0f), // Orange
            new Color3(0.85f, 0.0f, 1.0f), // Purple
            new Color3(1.0f, 0.0f, 0.4f), // Pink
            new Color3(1.0f, 0.15f, 0.0f), // Light-Red
            new Color3(0.88f, 0.07f, 0.0f) // Red
    );

    public BasicPlayerInfo() {}
    public BasicPlayerInfo(CompoundTag tag) {
        this.load(tag);
    }

    public static BasicPlayerInfo random(Random random) {
        BasicPlayerInfo info = new BasicPlayerInfo();
        info.hairColor = Util.getRandom(HAIR_COLORS, random);
        info.irisColor = Util.getRandom(IRIS_COLORS, random);
        return info;
    }

    public void setHairColor(Color3 hairColor) {
        this.hairColor = hairColor;
    }

    public void setIrisColor(Color3 irisColor) {
        this.irisColor = irisColor;
    }

    public void setScleraColor(Color3 scleraColor) {
        this.scleraColor = scleraColor;
    }

    public void setOverrideIrisOnDarkLatex(boolean overrideIrisOnDarkLatex) {
        this.overrideIrisOnDarkLatex = overrideIrisOnDarkLatex;
    }

    public void setEyeStyle(EyeStyle eyeStyle) {
        this.eyeStyle = eyeStyle.getRegistryName();
    }

    public Color3 getHairColor() {
        return hairColor;
    }

    public Color3 getIrisColor() {
        return irisColor;
    }

    public Color3 getScleraColor() {
        return scleraColor;
    }

    public boolean isOverrideIrisOnDarkLatex() {
        return overrideIrisOnDarkLatex;
    }

    public EyeStyle getEyeStyle() {
        return ChangedRegistry.EYE_STYLE.get().getValue(eyeStyle);
    }

    public void copyFrom(BasicPlayerInfo other) {
        var tag = new CompoundTag();
        other.save(tag);
        this.load(tag);
    }

    public void save(CompoundTag tag) {
        tag.putInt("hair", hairColor.toInt());
        tag.putInt("iris", irisColor.toInt());
        tag.putInt("sclera", scleraColor.toInt());
        tag.putBoolean("overrideIrisOnDarkLatex", overrideIrisOnDarkLatex);
    }

    public void load(CompoundTag tag) {
        this.hairColor = Color3.fromInt(tag.getInt("hair"));
        this.irisColor = Color3.fromInt(tag.getInt("iris"));
        this.scleraColor = Color3.fromInt(tag.getInt("sclera"));
        this.overrideIrisOnDarkLatex = tag.getBoolean("overrideIrisOnDarkLatex");
    }
}
