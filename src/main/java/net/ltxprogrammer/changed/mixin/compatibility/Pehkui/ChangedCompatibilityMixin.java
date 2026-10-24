package net.ltxprogrammer.changed.mixin.compatibility.Pehkui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.world.entity.EntityDimensions;
import org.spongepowered.asm.mixin.Mixin;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin(value = { ChangedCompatibility.class }, remap = false)
@RequiredMods("pehkui")
public abstract class ChangedCompatibilityMixin {
    @WrapMethod(method = "maybeAdjustEntitySize")
    private static EntityDimensions pehkui$maybeAdjustEntitySize(ChangedEntity entity, EntityDimensions size, Operation<EntityDimensions> original) {
        float widthScale = ScaleUtils.getBoundingBoxWidthScale(entity);
        float heightScale = ScaleUtils.getBoundingBoxHeightScale(entity);
        if (widthScale != 1.0F || heightScale != 1.0F) {
            return size.scale(widthScale, heightScale);
        }

        return size;
    }
}
