package net.ltxprogrammer.changed.mixin.render;

import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.client.CubeDefinitionExtender;
import net.ltxprogrammer.changed.client.CubeListBuilderExtender;
import net.ltxprogrammer.changed.client.Triangle;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mixin(CubeListBuilder.class)
public abstract class CubeListBuilderMixin implements CubeListBuilderExtender {
    @Shadow @Final private List<CubeDefinition> cubes;
    @Unique private List<Triangle.Definition> triangles = null;

    @Override
    public CubeListBuilder removeLastFaces(Direction... directions) {
        CubeDefinitionExtender cube = (CubeDefinitionExtender)(Object)this.cubes.get(this.cubes.size() - 1);
        cube.removeFaces(directions);
        return (CubeListBuilder)(Object)this;
    }

    @Override
    public void addTriangle(String comment, Vector3f p1, UVPair uv1, Vector3f p2, UVPair uv2, Vector3f p3, UVPair uv3) {
        if (triangles == null)
            triangles = new ArrayList<>();
        triangles.add(new Triangle.Definition(comment, p1, p2, p3, uv1, uv2, uv3));
    }

    @Nullable
    @Override
    public List<Triangle.Definition> getTriangles() {
        return triangles;
    }
}
