package net.ltxprogrammer.changed.extension.vivecraft;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.joml.Vector4f;

public class VivecraftHelperClient {
    public static float getModelRenderScale(ChangedEntity entity, float partialTick) {
        float renderScale = 0.9375F;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getEntityRenderDispatcher().getRenderer(entity) instanceof RendererScaleAccessor scaleAccessor) {
            PoseStack poseStack = new PoseStack();
            Vector4f result = new Vector4f();
            scaleAccessor.vivecraft$scale(entity, poseStack, partialTick);
            poseStack.last().pose().transform(0.0f, 1.0f, 0.0f, 0.0f, result);
            var variant = ProcessTransfur.getPlayerTransfurVariant(entity.getUnderlyingPlayer());
            if (variant != null)
                return Mth.lerp(variant.getMorphProgression(partialTick), renderScale, result.y());
            else
                return result.y();
        }

        return renderScale;
    }

    public static float getModelNeckPos(ChangedEntity entity, HumanoidAnimator<?,?> animator, float partialTick) {
        float neckPos = 1.501F;

        float torsoPositionY = animator.calculateTorsoPositionY();
        float modelNeckPos = ((24.0F - torsoPositionY) / 16.0F + 0.001F);

        var variant = ProcessTransfur.getPlayerTransfurVariant(entity.getUnderlyingPlayer());
        if (variant != null)
            return Mth.lerp(variant.getMorphProgression(partialTick), neckPos, modelNeckPos);
        else
            return modelNeckPos;
    }
}
