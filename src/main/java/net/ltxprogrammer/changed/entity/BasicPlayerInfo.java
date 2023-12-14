package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.nbt.CompoundTag;

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
