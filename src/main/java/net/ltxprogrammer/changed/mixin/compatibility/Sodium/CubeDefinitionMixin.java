package net.ltxprogrammer.changed.mixin.compatibility.Sodium;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.datafixers.util.Pair;
import me.jellysquid.mods.sodium.client.model.ModelCuboidAccessor;
import net.ltxprogrammer.changed.client.CubeDefinitionExtender;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.extension.sodium.ModelCuboidExtender;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.core.Direction;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(CubeDefinition.class)
@RequiredMods("rubidium")
public abstract class CubeDefinitionMixin implements CubeDefinitionExtender {
    @Shadow @Final private Vector3f dimensions;
    @Shadow @Final private UVPair texScale;

    @WrapMethod(method = "bake")
    public ModelPart.Cube bakeWithExtraSodium(int texWidth, int texHeight, Operation<ModelPart.Cube> original) {
        var cube = original.call(texWidth, texHeight);

        var overrideFaceTexOffs = this.getOverrideFaceTexOffs();

        if (overrideFaceTexOffs != null && cube instanceof ModelCuboidAccessor cuboidAccessor) {
            ModelCuboidExtender cubeExtender = (ModelCuboidExtender)cuboidAccessor.sodium$copy();
            cubeExtender.overrideFaceTexOffs(overrideFaceTexOffs, texWidth * this.texScale.u(), texHeight * this.texScale.v(),
                    this.dimensions.x, this.dimensions.y, this.dimensions.z);
        }

        return cube;
    }
}
