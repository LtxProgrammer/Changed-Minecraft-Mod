package net.ltxprogrammer.changed.mixin.client;

import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Consumer;

@Mixin(value = ForgeHooksClient.class, remap = false)
public abstract class ForgeHooksClientMixin {
    @Unique
    private static void getMaterialFromSlot(@Nullable ResourceLocation slotTexture, Consumer<Material> next) {
        if (slotTexture != null)
            next.accept(new Material(InventoryMenu.BLOCK_ATLAS, slotTexture));
    }

    @Inject(method = "gatherFluidTextures", at = @At("HEAD"))
    private static void gatherChangedRegistryTextures(Set<Material> textures, CallbackInfo ci) {
        ChangedRegistry.ACCESSORY_SLOTS.get().getValues().stream()
                .map(AccessorySlotType::getNoItemIcon)
                .mapMulti(ForgeHooksClientMixin::getMaterialFromSlot)
                .forEach(textures::add);
    }
}
