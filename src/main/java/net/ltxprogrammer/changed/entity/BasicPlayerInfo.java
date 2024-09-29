package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Random;

/**
 * This is basic info about the player, that they set.
 */
public class BasicPlayerInfo {
    public static float getSizeTolerance() {
        return (float) Changed.config.server.bpiSizeTolerance.get().doubleValue();
    }

    // Default values here are based on Colin's properties
    // When the player is TF'd these values will copy over to the latex representative
    private Color3 hairColor = new Color3(0.36f, 0.28f, 0.26f);
    private Color3 irisLeftColor = new Color3(0.0f, 0.5f, 1.0f);
    private Color3 irisRightColor = new Color3(0.0f, 0.5f, 1.0f);
    private Color3 scleraColor = Color3.WHITE;
    private boolean overrideIrisOnDarkLatex = false;
    private EyeStyle eyeStyle = EyeStyle.V2;
    private boolean overrideOthersToMatchStyle = false;
    private float size = 1.0f;

    public static final List<Color3> HAIR_COLORS = List.of(
            new Color3(0.98f, 0.85f, 0.48f), // Blond
            new Color3(0.77f, 0.49f, 0.28f), // Dirty Blond
            new Color3(0.36f, 0.28f, 0.26f), // Brown
            new Color3(0.39f, 0.40f, 0.40f), // Gray
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
            new Color3(0.14f, 0.90f, 0.64f), // Mint-Green
            new Color3(0.12f, 0.74f, 0.74f), // Cyan
            new Color3(0.38f, 0.22f, 0.14f), // Brown
            new Color3(0.64f, 0.70f, 0.54f), // Hazel
            new Color3(1.0f, 0.76f, 0.0f), // Dark-Yellow
            new Color3(1.0f, 0.92f, 0.01f), // Yellow
            new Color3(1.0f, 0.63f, 0.0f), // Orange
            new Color3(0.82f, 0.31f, 0.16f), // Dark-Orange
            new Color3(0.85f, 0.0f, 1.0f), // Purple
            new Color3(1.0f, 0.0f, 0.4f), // Pink
            new Color3(1.0f, 0.15f, 0.0f), // Light-Red
            new Color3(0.88f, 0.07f, 0.0f), // Red
            new Color3(0.75f, 0.55f, 0.90f) // Lavender
    );

    public BasicPlayerInfo() {}
    public BasicPlayerInfo(CompoundTag tag) {
        this.load(tag);
    }

    public static BasicPlayerInfo random(Random random) {
        BasicPlayerInfo info = new BasicPlayerInfo();
        info.hairColor = Util.getRandom(HAIR_COLORS, random);
        info.irisLeftColor = Util.getRandom(IRIS_COLORS, random);
        info.irisRightColor = random.nextFloat() > 0.05f ? info.irisLeftColor : Util.getRandom(IRIS_COLORS, random); // 5% for dichrome eyes
        info.eyeStyle = Util.getRandom(EyeStyle.values(), random);
        info.overrideOthersToMatchStyle = false;
        info.size = (random.nextFloat() * (random.nextBoolean() ? getSizeTolerance() : -getSizeTolerance())) + 1.0f;
        return info;
    }

    public void setHairColor(Color3 hairColor) {
        this.hairColor = hairColor;
    }

    public void setLeftIrisColor(Color3 irisColor) {
        this.irisLeftColor = irisColor;
    }

    public void setRightIrisColor(Color3 irisColor) {
        this.irisRightColor = irisColor;
    }

    public void setScleraColor(Color3 scleraColor) {
        this.scleraColor = scleraColor;
    }

    public void setOverrideIrisOnDarkLatex(boolean overrideIrisOnDarkLatex) {
        this.overrideIrisOnDarkLatex = overrideIrisOnDarkLatex;
    }

    public void setOverrideOthersToMatchStyle(boolean overrideOthersToMatchStyle) {
        this.overrideOthersToMatchStyle = overrideOthersToMatchStyle;
    }

    public void setEyeStyle(EyeStyle eyeStyle) {
        this.eyeStyle = eyeStyle;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public Color3 getHairColor() {
        return hairColor;
    }

    public Color3 getLeftIrisColor() {
        return irisLeftColor;
    }

    public Color3 getRightIrisColor() {
        return irisRightColor;
    }

    public Color3 getScleraColor() {
        return scleraColor;
    }

    public boolean isOverrideIrisOnDarkLatex() {
        return overrideIrisOnDarkLatex;
    }

    public boolean isOverrideOthersToMatchStyle() {
        return overrideOthersToMatchStyle;
    }

    public EyeStyle getEyeStyle() {
        return eyeStyle;
    }

    public float getSize() {
        return Mth.clamp(size, 1.0f - getSizeTolerance(), 1.0f + getSizeTolerance());
    }

    public double getSizeValueForConfiguration() {
        return (size - 1.0f + getSizeTolerance()) / (getSizeTolerance() * 2);
    }

    public void copyFrom(BasicPlayerInfo other) {
        var tag = new CompoundTag();
        other.save(tag);
        this.load(tag);
    }

    public void save(CompoundTag tag) {
        tag.putInt("hair", hairColor.toInt());
        tag.putInt("irisLeft", irisLeftColor.toInt());
        tag.putInt("irisRight", irisRightColor.toInt());
        tag.putInt("sclera", scleraColor.toInt());
        tag.putBoolean("overrideIrisOnDarkLatex", overrideIrisOnDarkLatex);
        tag.putInt("eyeStyle", eyeStyle.ordinal());
        tag.putBoolean("overrideOthersToMatchStyle", overrideOthersToMatchStyle);
        tag.putFloat("scale", size);
    }

    public void load(CompoundTag tag) {
        this.hairColor = Color3.fromInt(tag.getInt("hair"));
        if (tag.contains("iris")) {
            this.irisLeftColor = Color3.fromInt(tag.getInt("iris"));
            this.irisRightColor = this.irisLeftColor;
        } else {
            this.irisLeftColor = Color3.fromInt(tag.getInt("irisLeft"));
            this.irisRightColor = Color3.fromInt(tag.getInt("irisRight"));
        }
        this.scleraColor = Color3.fromInt(tag.getInt("sclera"));
        this.overrideIrisOnDarkLatex = tag.getBoolean("overrideIrisOnDarkLatex");
        this.eyeStyle = EyeStyle.values()[tag.getInt("eyeStyle")];
        this.overrideOthersToMatchStyle = tag.getBoolean("overrideOthersToMatchStyle");
        if (tag.contains("scale"))
            this.size = tag.getFloat("scale");
    }
}
