package net.ltxprogrammer.changed.client;

import net.minecraft.client.model.geom.ModelPart;

public interface AgeableListModelExtender {
    Iterable<ModelPart> changed$headParts();
    Iterable<ModelPart> changed$bodyParts();
}
