package net.ltxprogrammer.changed.mixin.render;

import net.ltxprogrammer.changed.client.CubeListBuilderExtender;
import net.ltxprogrammer.changed.client.ModelPartExtender;
import net.ltxprogrammer.changed.client.Triangle;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PartDefinition.class)
public abstract class PartDefinitionMixin {
    public List<Triangle.Definition> triangles;

    @Inject(method = "addOrReplaceChild", at = @At("RETURN"))
    public void andTriangles(String name, CubeListBuilder list, PartPose partPose, CallbackInfoReturnable<PartDefinition> callback) {
        ((PartDefinitionMixin)(Object)callback.getReturnValue()).triangles = ((CubeListBuilderExtender)list).getTriangles();
    }

    @Inject(method = "bake", at = @At("RETURN"))
    public void withTriangles(int textureWidth, int textureHeight, CallbackInfoReturnable<ModelPart> callback) {
        ModelPartExtender extender = (ModelPartExtender)(Object)callback.getReturnValue();
        if (triangles != null)
            triangles.forEach(definition -> extender.addTriangle(definition.bake(textureWidth, textureHeight)));
    }
}
