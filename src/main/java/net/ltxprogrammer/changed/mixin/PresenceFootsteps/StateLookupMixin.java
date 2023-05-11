package net.ltxprogrammer.changed.mixin.PresenceFootsteps;

import eu.ha3.presencefootsteps.world.Lookup;
import eu.ha3.presencefootsteps.world.StateLookup;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

@Mixin(value = StateLookup.class, remap = false)
public abstract class StateLookupMixin implements Lookup<BlockState> {
    @Shadow public abstract String getAssociation(BlockState state, String substrate);

    @Inject(method = "getAssociation(Lnet/minecraft/world/level/block/state/BlockState;Ljava/lang/String;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    public void getAssociation(BlockState state, String substrate, CallbackInfoReturnable<String> callback) {
        if (state.getProperties().contains(COVERED) && state.getValue(COVERED) != LatexType.NEUTRAL) {
            callback.setReturnValue(this.getAssociation(state.getValue(COVERED).block.get().defaultBlockState(), substrate));
        }
    }
}
