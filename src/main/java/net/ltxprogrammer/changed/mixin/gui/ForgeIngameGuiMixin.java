package net.ltxprogrammer.changed.mixin.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
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
        var entity = Minecraft.getInstance().getCameraEntity();
        ProcessTransfur.ifPlayerLatex(EntityUtil.playerOrNull(entity), (player, variant) -> {
            if (variant.getParent().breatheMode.canBreatheWater() && player.getAirSupply() >= 300)
                callback.cancel();
        });
    }
}
