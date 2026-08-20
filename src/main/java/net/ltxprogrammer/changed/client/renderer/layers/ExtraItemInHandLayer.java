package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.DoubleArmedModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ExtraItemInHandLayer<T extends ChangedEntity, M extends AdvancedHumanoidModel<T> & HeadedModel> extends ItemInHandLayer<T, M> {
    private final ItemInHandRenderer itemInHandRenderer;
    protected final int slotIndex;
    protected final Translator<T, M> translator;

    public interface Translator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> {
        void translateToHand(M model, T entity, HumanoidArm arm, PoseStack poseStack);
    }

    public ExtraItemInHandLayer(RenderLayerParent<T, M> parent, ItemInHandRenderer itemInHandRenderer, int slotIndex, Translator<T, M> translator) {
        super(parent, itemInHandRenderer);
        this.itemInHandRenderer = itemInHandRenderer;
        this.slotIndex = slotIndex;
        this.translator = translator;
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity, float p_117208_, float p_117209_, float p_117210_, float p_117211_, float p_117212_, float p_117213_) {
        boolean flag = entity.getMainArm() == HumanoidArm.RIGHT;
        var abilityInstance = entity.getAbilityInstance(ChangedAbilities.SWITCH_HANDS.get());
        if (abilityInstance == null) return;

        ItemStack leftHandStack = flag ? abilityInstance.getNthNextOffHandItem(slotIndex) : abilityInstance.getNthNextMainHandItem(slotIndex);
        ItemStack rightHandStack = flag ? abilityInstance.getNthNextMainHandItem(slotIndex) : abilityInstance.getNthNextOffHandItem(slotIndex);
        if (!leftHandStack.isEmpty() || !rightHandStack.isEmpty()) {
            poseStack.pushPose();
            if (this.getParentModel().young) {
                float f = 0.5F;
                poseStack.translate(0.0D, 0.75D, 0.0D);
                poseStack.scale(0.5F, 0.5F, 0.5F);
            }

            this.renderArmWithItem(entity, rightHandStack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, HumanoidArm.RIGHT, poseStack, bufferSource, packedLight);
            this.renderArmWithItem(entity, leftHandStack, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, HumanoidArm.LEFT, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }
    }

    protected void renderArmWithItem(T entity, ItemStack item, ItemDisplayContext transformType, HumanoidArm arm, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (!item.isEmpty()) {
            poseStack.pushPose();
            translator.translateToHand(this.getParentModel(), entity, arm, poseStack);
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            boolean flag = arm == HumanoidArm.LEFT;
            poseStack.translate((float)(flag ? -1 : 1) / 16.0F, 0.125D, -0.625D);
            itemInHandRenderer.renderItem(entity, item, transformType, flag, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }
    }
}
