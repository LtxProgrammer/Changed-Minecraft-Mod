package net.ltxprogrammer.changed.mixin.compatibility.CarryOn;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModelInterface;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.client.event.RenderEvents;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin(value = RenderEvents.class, remap = false)
public abstract class RenderEventsMixin {
    @Unique private static Player currentPlayer = null;
    @Unique private static MultiBufferSource currentSource = null;

    @Shadow protected abstract void renderArmPre(ModelPart arm);
    @Shadow protected abstract void renderArmPost(ModelPart arm, float x, float z, boolean right, boolean sneaking, int light, PoseStack matrix, VertexConsumer builder);

    @Shadow @NotNull private static PlayerRenderer getRenderPlayer(AbstractClientPlayer player) { return null; }

    private static final Pair<ModelPart, ResourceLocation> NULL_PART = Pair.of(new ModelPart(List.of(), Map.of()), null);
    private Optional<Pair<ModelPart, ResourceLocation>> remapHumanoidPart(ModelPart part) {
        PlayerModel<AbstractClientPlayer> playerModel = getRenderPlayer((AbstractClientPlayer) currentPlayer).getModel();

        return Optional.ofNullable(ProcessTransfur.ifPlayerTransfurred(currentPlayer, variant -> {
            EntityRenderer renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(variant.getChangedEntity());
            if (renderer instanceof AdvancedHumanoidRenderer<?,?,?> advanced) {
                var changedModel = advanced.getModel(variant.getChangedEntity());

                if (part == playerModel.rightArm)
                    return Pair.of(changedModel.getArm(HumanoidArm.RIGHT), renderer.getTextureLocation(variant.getChangedEntity()));
                else if (part == playerModel.leftArm)
                    return Pair.of(changedModel.getArm(HumanoidArm.LEFT), renderer.getTextureLocation(variant.getChangedEntity()));
                else if (part == playerModel.rightSleeve)
                    return NULL_PART;
                else if (part == playerModel.leftSleeve)
                    return NULL_PART;
                else
                    return null;
            } else {
                return null;
            }
        }));
    }

    @Inject(method = "drawArms", at = @At("HEAD"))
    public void copyPlayerForLater(Player player, float partialticks, PoseStack matrix, MultiBufferSource buffer, int light, CallbackInfo ci) {
        currentPlayer = player;
        currentSource = buffer;
    }

    @Inject(method = "onEvent", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/event/RenderPlayerEvent$Pre;getPlayer()Lnet/minecraft/world/entity/player/Player;"))
    public void copyPlayerForLater(RenderPlayerEvent.Pre event, CallbackInfo ci) {
        currentPlayer = event.getPlayer();
    }

    @Inject(method = "renderArmPre", at = @At("HEAD"), cancellable = true)
    public void renderChangedArmPre(ModelPart arm, CallbackInfo ci) {
        remapHumanoidPart(arm).ifPresent(pair -> {
            if (pair.getSecond() == null) {
                ci.cancel();
                return;
            }

            this.renderArmPre(pair.getFirst());
            ci.cancel();
        });
    }
    @Unique private static boolean applyYValue = false;
    @Unique private static float currentYValue = 0.0f;
    @Inject(method = "renderArmPost", at = @At("HEAD"), cancellable = true)
    public void renderChangedArmPost(ModelPart arm, float x, float z, boolean right, boolean sneaking, int light, PoseStack matrix, VertexConsumer builder, CallbackInfo ci) {
        remapHumanoidPart(arm).ifPresent(pair -> {
            if (pair.getSecond() == null) {
                ci.cancel();
                return;
            }

            VertexConsumer newTexture = currentSource.getBuffer(RenderType.entityCutout(pair.getSecond()));
            currentYValue = pair.getFirst().y;
            applyYValue = true;
            this.renderArmPost(pair.getFirst(), x, z, right, sneaking, light, matrix, newTexture);
            ci.cancel();
        });
    }

    @Inject(method = "renderArmPost", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    public void renderChangedArmOffset(ModelPart arm, float x, float z, boolean right, boolean sneaking, int light, PoseStack matrix, VertexConsumer builder, CallbackInfo ci) {
        if (applyYValue) {
            arm.y = currentYValue + (sneaking ? 14.0f : 22.0f);
            applyYValue = false;
        }
    }
}
