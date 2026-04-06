package net.ltxprogrammer.changed.mixin.compatibility.CreatureChat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.owlmaddie.chat.EntityChatData;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = EntityChatData.class, remap = false)
@RequiredMods("creaturechat")
public abstract class EntityChatDataMixin {
    @WrapOperation(method = "lambda$generateMessage$7",
            at = @At(value = "INVOKE", target = "Lcom/owlmaddie/controls/SpeedControls;getMaxSpeed(Lnet/minecraft/world/entity/Mob;)F"))
    private float changed$overrideMaxSpeed(Mob entity, Operation<Float> original) {
        if (entity instanceof ChangedEntity)
            return 0.3f;

        return original.call(entity);
    }

    @WrapOperation(method = "lambda$generateMessage$7",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F", remap = true))
    private float changed$widenSpeedClamp(float entitySpeed, float minimum, float maximum, Operation<Float> original,
                                          @Local Mob entity) {
        if (entity instanceof ChangedEntity)
            return original.call(entitySpeed, 0.3f, maximum);

        return original.call(entitySpeed, minimum, maximum);
    }
}
