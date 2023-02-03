package net.ltxprogrammer.changed.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ForgeIngameGui.class, remap = false)
public abstract class ForgeIngameGuiMixin extends Gui {
    public ForgeIngameGuiMixin(Minecraft p_93005_) {
        super(p_93005_);
    }

    @Inject(method = "renderAir", at = @At("HEAD"), cancellable = true)
    protected void renderAir(int width, int height, PoseStack poseStack, CallbackInfo callback) {
        if (Minecraft.getInstance().getCameraEntity() instanceof Player player &&
                ProcessTransfur.isPlayerLatex(player) && ProcessTransfur.getPlayerLatexVariant(player).breatheMode.canBreatheWater() &&
                player.getAirSupply() >= 300) {
            callback.cancel();
        }
    }
}
