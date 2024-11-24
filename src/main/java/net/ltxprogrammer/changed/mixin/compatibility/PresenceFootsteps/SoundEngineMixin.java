package net.ltxprogrammer.changed.mixin.compatibility.PresenceFootsteps;

import eu.ha3.presencefootsteps.sound.PFIsolator;
import eu.ha3.presencefootsteps.sound.SoundEngine;
import eu.ha3.presencefootsteps.sound.generator.Locomotion;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.extension.presencefootsteps.ChangedPresenceFootsteps;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SoundEngine.class, remap = false)
public abstract class SoundEngineMixin implements PreparableReloadListener {
    @Shadow private PFIsolator isolator;

    @Inject(method = "reloadEverything", at = @At("RETURN"))
    public void reloadEverything(ResourceManager manager, CallbackInfo callbackInfo) {
        var event = new ChangedPresenceFootsteps.LoadModdedFootstepsEvent(manager, isolator);
        event.loadBlockMap(ChangedPresenceFootsteps.BLOCK_MAP);
        event.loadLocomotionMap(ChangedPresenceFootsteps.LOCOMOTION_MAP);

        Changed.postModEvent(event);
    }

    @Inject(method = "getLocomotion", at = @At("HEAD"), cancellable = true)
    public void getLocomotion(LivingEntity entity, CallbackInfoReturnable<Locomotion> callbackInfo) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(entity), variant -> {
            callbackInfo.setReturnValue(this.isolator.getLocomotionMap().lookup(variant.getChangedEntity()));
        });
    }
}
