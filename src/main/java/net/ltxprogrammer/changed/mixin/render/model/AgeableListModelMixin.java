package net.ltxprogrammer.changed.mixin.render.model;

import net.ltxprogrammer.changed.client.AgeableListModelExtender;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AgeableListModel.class)
public abstract class AgeableListModelMixin implements AgeableListModelExtender {
    @Shadow protected abstract Iterable<ModelPart> headParts();
    @Shadow protected abstract Iterable<ModelPart> bodyParts();

    @Override
    public Iterable<ModelPart> changed$headParts() {
        return headParts();
    }

    @Override
    public Iterable<ModelPart> changed$bodyParts() {
        return bodyParts();
    }
}
