package net.ltxprogrammer.changed.client.renderer.model;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;

public interface DoubleHeadedModel<T extends ChangedEntity> extends AdvancedArmedModel<T> {
    ModelPart getHead();
    ModelPart getOtherHead();
}
