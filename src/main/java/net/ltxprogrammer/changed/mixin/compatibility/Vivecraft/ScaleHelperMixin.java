package net.ltxprogrammer.changed.mixin.compatibility.Vivecraft;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.extension.vivecraft.RendererScaleAccessor;
import net.ltxprogrammer.changed.extension.vivecraft.VivecraftHelperClient;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.vivecraft.client.utils.ScaleHelper;

@Mixin(value = ScaleHelper.class, remap = false)
@RequiredMods("vivecraft")
public abstract class ScaleHelperMixin {
    @Unique
    private static float changed$computeModelScale(ChangedEntity entity, float partialTick) {
        float renderScale = VivecraftHelperClient.getModelRenderScale(entity, partialTick);

        return renderScale / 0.9375F;
    }

    @WrapMethod(method = "getEntityEyeHeightScale")
    private static float changed$getEntityEyeHeightScale(LivingEntity entity, float partialTick, Operation<Float> original) {
        float scale = original.call(entity, partialTick);

        var variant = ProcessTransfur.getPlayerTransfurVariant(EntityUtil.playerOrNull(entity));
        if (variant != null) {
            if (entity.level().isClientSide()) // Only call on client, uses renderer / model info
                scale *= changed$computeModelScale(variant.getChangedEntity(), partialTick);
            else
                scale *= variant.getTransfurEyeHeight(Pose.STANDING, Player.DEFAULT_EYE_HEIGHT) / Player.DEFAULT_EYE_HEIGHT;
        }

        return scale;
    }

    @WrapMethod(method = "getEntityBbScale")
    private static float changed$getEntityBbScale(LivingEntity entity, float partialTick, Operation<Float> original) {
        float scale = original.call(entity, partialTick);

        var variant = ProcessTransfur.getPlayerTransfurVariant(EntityUtil.playerOrNull(entity));
        if (variant != null) {
            scale *= variant.getTransfurDimensions(Pose.STANDING, Player.STANDING_DIMENSIONS).height / Player.STANDING_DIMENSIONS.height;
        }

        return scale;
    }
}
