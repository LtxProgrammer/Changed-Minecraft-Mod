package net.ltxprogrammer.changed.mixin.compatibility.Pehkui;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.SpecialLatex;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import virtuoel.pehkui.util.PehkuiEntityExtensions;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin(value = { ChangedEntity.class, SpecialLatex.class }, remap = false)
public abstract class ChangedEntityMixin extends Monster implements PehkuiEntityExtensions {
    protected ChangedEntityMixin(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true, remap = true)
    public void getPehkuiDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> callback) {
        float widthScale = ScaleUtils.getBoundingBoxWidthScale(this);
        float heightScale = ScaleUtils.getBoundingBoxHeightScale(this);
        if (widthScale != 1.0F || heightScale != 1.0F) {
            callback.setReturnValue(((EntityDimensions)callback.getReturnValue()).scale(widthScale, heightScale));
        }
    }
}
