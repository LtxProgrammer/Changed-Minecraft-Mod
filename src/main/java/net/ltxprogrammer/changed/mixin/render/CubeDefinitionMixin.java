package net.ltxprogrammer.changed.mixin.render;

import net.ltxprogrammer.changed.client.CubeDefinitionExtender;
import net.ltxprogrammer.changed.client.CubeExtender;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mixin(CubeDefinition.class)
public abstract class CubeDefinitionMixin implements CubeDefinitionExtender {
    @Unique
    private Set<Direction> removedDirections = null;

    @Override
    public void removeFaces(Direction... directions) {
        if (removedDirections == null)
            removedDirections = new HashSet<>();

        removedDirections.addAll(Arrays.asList(directions));
    }

    @Inject(method = "bake", at = @At("RETURN"))
    public void bakeWithRemoved(int u, int v, CallbackInfoReturnable<ModelPart.Cube> cubeCallback) {
        if (removedDirections == null) return;

        CubeExtender cubeExtender = (CubeExtender)cubeCallback.getReturnValue();
        cubeExtender.removeSides(removedDirections);
    }
}
