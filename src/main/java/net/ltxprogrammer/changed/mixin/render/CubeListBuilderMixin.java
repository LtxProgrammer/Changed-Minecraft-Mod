package net.ltxprogrammer.changed.mixin.render;

import net.ltxprogrammer.changed.client.CubeDefinitionExtender;
import net.ltxprogrammer.changed.client.CubeListBuilderExtender;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(CubeListBuilder.class)
public abstract class CubeListBuilderMixin implements CubeListBuilderExtender {
    @Shadow @Final private List<CubeDefinition> cubes;

    @Override
    public CubeListBuilder removeLastFaces(Direction... directions) {
        CubeDefinitionExtender cube = (CubeDefinitionExtender)(Object)this.cubes.get(this.cubes.size() - 1);
        cube.removeFaces(directions);
        return (CubeListBuilder)(Object)this;
    }
}
