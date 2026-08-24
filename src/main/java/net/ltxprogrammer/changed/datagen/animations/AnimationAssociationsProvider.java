package net.ltxprogrammer.changed.datagen.animations;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.data.PackOutput;

public class AnimationAssociationsProvider extends AnimationAssociationsDataProvider {
    public AnimationAssociationsProvider(PackOutput packOutput) {
        super(packOutput, Changed.MODID);
    }

    @Override
    protected void registerAssociations() {}
}
