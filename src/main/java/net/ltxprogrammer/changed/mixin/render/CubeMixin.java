package net.ltxprogrammer.changed.mixin.render;

import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.client.CubeExtender;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModelPart.Cube.class)
public abstract class CubeMixin implements CubeExtender {
    @Shadow @Final private ModelPart.Polygon[] polygons;

    @Override
    public Vector3f getVisualMin() {
        return this.polygons[1].vertices[0].pos;
    }

    @Override
    public Vector3f getVisualMax() {
        return this.polygons[0].vertices[3].pos;
    }
}
