package net.ltxprogrammer.changed.mixin.render;

import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

@Mixin(MultiPart.class)
public abstract class MultiPartMixin implements UnbakedModel, Supplier<StateDefinition<Block, BlockState>> {
    @Final @Shadow private StateDefinition<Block, BlockState> definition;

    @Unique
    public StateDefinition<Block, BlockState> get() {
        return definition;
    }
}
