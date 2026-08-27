package net.ltxprogrammer.changed.mixin.compatibility.Vivecraft;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.vivecraft.client.utils.ScaleHelper;

@Mixin(value = ScaleHelper.class, remap = false)
@RequiredMods("vivecraft")
public abstract class ScaleHelperMixin {
    @WrapMethod(method = "getEntityEyeHeightScale")
    private static float changed$getEntityEyeHeightScale(LivingEntity entity, float partialTick, Operation<Float> original) {
        float scale = original.call(entity, partialTick);

        var variant = ProcessTransfur.getPlayerTransfurVariant(EntityUtil.playerOrNull(entity));
        if (variant != null) {
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
