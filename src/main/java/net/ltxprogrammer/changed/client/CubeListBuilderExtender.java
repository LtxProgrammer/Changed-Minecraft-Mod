package net.ltxprogrammer.changed.client;

import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.core.Direction;

public interface CubeListBuilderExtender {
    CubeListBuilder removeLastFaces(Direction... directions);
}
