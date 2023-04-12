package net.ltxprogrammer.changed.mixin.PresenceFootsteps;

import eu.ha3.presencefootsteps.sound.PFIsolator;
import eu.ha3.presencefootsteps.sound.SoundEngine;
import eu.ha3.presencefootsteps.util.ResourceUtils;
import eu.ha3.presencefootsteps.world.Lookup;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin implements PreparableReloadListener {
    @Unique
    private static final ResourceLocation BLOCK_MAP = Changed.modResource("presencefootsteps/blockmap.json");
    @Shadow(remap = false)
    private PFIsolator isolator;

    @Inject(method = "reloadEverything", at = @At("RETURN"), remap = false)
    public void reloadEverything(ResourceManager manager, CallbackInfo callbackInfo) {
        Lookup<BlockState> lookup = this.isolator.getBlockMap();
        Objects.requireNonNull(lookup);
        ResourceUtils.forEach(BLOCK_MAP, manager, lookup::load);
    }
}
