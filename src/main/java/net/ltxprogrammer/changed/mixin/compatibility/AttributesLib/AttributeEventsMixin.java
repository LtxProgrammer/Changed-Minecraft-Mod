package net.ltxprogrammer.changed.mixin.compatibility.AttributesLib;

import dev.shadowsoffire.attributeslib.impl.AttributeEvents;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AttributeEvents.class, remap = false)
@RequiredMods("attributeslib")
public class AttributeEventsMixin {
    @Inject(method = "fixChangedAttributes", at = @At("TAIL"))
    public void fixAttributesForChanged(PlayerEvent.PlayerLoggedInEvent event, CallbackInfo ci) {
        ProcessTransfur.ifPlayerTransfurred(event.getEntity(), variant -> {
            // Let TransfurVariantInstance handle attributes
            variant.refreshAttributes();
        });
    }
}
