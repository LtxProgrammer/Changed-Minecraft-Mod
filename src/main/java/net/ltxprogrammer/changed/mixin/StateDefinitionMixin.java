package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.block.AbstractLatexBlock;
import net.ltxprogrammer.changed.block.AbstractLatexCrystal;
import net.ltxprogrammer.changed.block.NonLatexCoverableBlock;
import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import org.lwjgl.system.CallbackI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

@Mixin(StateDefinition.Builder.class)
public abstract class StateDefinitionMixin<O, S extends StateHolder<O, S>> {
    @Inject(method = "create", at = @At("HEAD"))
    public void create(Function<O, S> p_61102_, StateDefinition.Factory<O, S> p_61103_, CallbackInfoReturnable<StateDefinition<O, S>> callbackInfoReturnable) {
        var self = (StateDefinition.Builder<O, S>)(Object)this;
        if (self.owner instanceof Block ownerBlock) {
            if (!(ownerBlock instanceof NonLatexCoverableBlock || ownerBlock instanceof AirBlock || ownerBlock instanceof BaseFireBlock ||
                    ownerBlock instanceof CampfireBlock || ownerBlock instanceof TorchBlock))
                self.add(COVERED);
        }
    }
}
