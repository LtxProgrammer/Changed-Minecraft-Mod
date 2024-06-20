package net.ltxprogrammer.changed.client.renderer.animate.ears;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class WolfEarsInitAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractEarsAnimator<T, M> {
    public WolfEarsInitAnimator(ModelPart leftEar, ModelPart rightEar) {
        super(leftEar, rightEar);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float tailDrag = entity.getTailDragAmount(this.core.partialTicks);

        rightEar.zRot = Mth.lerp(core.ageLerp * 0.85f, -0.08726646F, 0.04363323F);
        rightEar.yRot = Mth.clamp(tailDrag * 0.5f, -Mth.PI / 8.0f, Mth.PI / 4.0f);
        leftEar.zRot = -Mth.lerp(core.ageLerp * 0.85f, -0.08726646F, 0.04363323F);
        leftEar.yRot = Mth.clamp(tailDrag * 0.5f, -Mth.PI / 4.0f, Mth.PI / 8.0f);

        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(ChangedItems.GAS_MASK.get())) {
            rightEar.y = -6.15f;
            leftEar.y = -6.15f;
            rightEar.zRot = Mth.DEG_TO_RAD * -65.0f;
            leftEar.zRot = Mth.DEG_TO_RAD * 65.0f;
        } else {
            rightEar.y = -7.5f;
            leftEar.y = -7.5f;
        }
    }
}
