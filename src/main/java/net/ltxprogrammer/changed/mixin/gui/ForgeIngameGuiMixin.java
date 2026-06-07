package net.ltxprogrammer.changed.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ForgeGui.class, remap = false)
public abstract class ForgeIngameGuiMixin extends Gui {
    public ForgeIngameGuiMixin(Minecraft p_232355_, ItemRenderer p_232356_) {
        super(p_232355_, p_232356_);
    }

    @WrapMethod(method = "renderAir")
    protected void renderAir(int width, int height, GuiGraphics guiGraphics, Operation<Void> original) {
        var variant = ProcessTransfur.getPlayerTransfurVariant(EntityUtil.playerOrNull(Minecraft.getInstance().getCameraEntity()));
        if (variant == null) {
            original.call(width, height, guiGraphics);
            return;
        }

        if (variant.getBreatheMode().canBreatheWater() && variant.getHost().getAirSupply() >= variant.getHost().getMaxAirSupply())
            return;

        original.call(width, height, guiGraphics);
    }

    @WrapOperation(method = "renderAir", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;getAirSupply()I",
            remap = true))
    public int getScaledAirSupply(Player player, Operation<Integer> original) {
        var variant = ProcessTransfur.getPlayerTransfurVariant(EntityUtil.playerOrNull(Minecraft.getInstance().getCameraEntity()));
        if (variant == null)
            return original.call(player);

        return (int) (((float)player.getAirSupply() / (float)player.getMaxAirSupply()) * 300f);
    }
}
