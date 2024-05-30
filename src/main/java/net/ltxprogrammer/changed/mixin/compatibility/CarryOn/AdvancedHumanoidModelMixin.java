package net.ltxprogrammer.changed.mixin.compatibility.CarryOn;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.client.event.RenderEvents;

@Mixin(value = AdvancedHumanoidModel.class, remap = false)
public abstract class AdvancedHumanoidModelMixin<T extends ChangedEntity> {
    @Unique
    private static final RenderEvents renderEvents = new RenderEvents();

    @Inject(method = "prepareMobModel", at = @At("RETURN"))
    private void fakeEvent(HumanoidAnimator<?, ? extends EntityModel<T>> animator, T entity, float p_102862_, float p_102863_, float partialTicks, CallbackInfo ci) {
        if (entity.getUnderlyingPlayer() != null) {
            Player player = entity.getUnderlyingPlayer();
            EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
            renderEvents.onEvent(new RenderPlayerEvent.Pre(player, (PlayerRenderer)renderer, partialTicks, null, null, 0));
        }
    }
}
