package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class LatexHumanoidRenderer<T extends LatexEntity, M extends LatexHumanoidModel<T>, A extends LatexHumanoidArmorModel<T>> extends MobRenderer<T, M> {
    private LatexHumanoidHairLayer<T, M> hairLayer;

    public LatexHumanoidHairLayer<T, M> getHairLayer() {
        return hairLayer;
    }

    private void addLayers(EntityRendererProvider.Context context, M main) {
        hairLayer = new LatexHumanoidHairLayer<>(this, context.getModelSet());
        this.addLayer(new LatexItemInHandLayer<>(this));
        this.addLayer(hairLayer);
        this.addLayer(new LatexArrowLayer<>(context, this));
        //this.addLayer(new LatexCapeLayer<>(this));
        this.addLayer(new LatexElytraLayer<>(this, context.getModelSet()));
        this.addLayer(new LatexParrotOnShoulderLayer<>(this, context.getModelSet()));
        this.addLayer(new LatexBeeStingerLayer<>(this));
        this.addLayer(new LatexSpinAttackEffectLayer<>(this, context.getModelSet()));
    }

    public LatexHumanoidRenderer(EntityRendererProvider.Context context, M main,
                                 Function<ModelPart, A> ctorA, ModelLayerLocation armorInner, ModelLayerLocation armorOuter, float shadowSize) {
        super(context, main, shadowSize);
        if (main == null) return;
        this.addLayer(new LatexHumanoidArmorLayer<>(this, ctorA.apply(context.bakeLayer(armorInner)), ctorA.apply(context.bakeLayer(armorOuter))));
        this.addLayers(context, main);
    }

    public <B extends LatexHumanoidArmorModel<T>> LatexHumanoidRenderer(EntityRendererProvider.Context context, M main,
                                                                        Function<ModelPart, A> ctorA, ModelLayerLocation armorInner, ModelLayerLocation armorOuter,
                                                                        Function<ModelPart, B> ctorB, ModelLayerLocation armorInnerOther, ModelLayerLocation armorOuterOther,
                                                                        Predicate<EquipmentSlot> useOther, Predicate<EquipmentSlot> useInner,
                                                                        TriConsumer<T, LatexHumanoidArmorModel<T>, EquipmentSlot> setVisibility, float shadowSize) {
        super(context, main, shadowSize);
        if (main == null) return;
        this.addLayer(new LatexHumanoidSplitArmorLayer<>(this, ctorA.apply(context.bakeLayer(armorInner)), ctorA.apply(context.bakeLayer(armorOuter)),
                ctorB.apply(context.bakeLayer(armorInnerOther)), ctorB.apply(context.bakeLayer(armorOuterOther)), useOther, useInner, setVisibility));
        this.addLayers(context, main);
    }

    protected void setupRotations(@NotNull T p_117802_, PoseStack p_117803_, float p_117804_, float p_117805_, float p_117806_) {
        float f = p_117802_.getSwimAmount(p_117806_);
        if (p_117802_.isFallFlying()) {
            super.setupRotations(p_117802_, p_117803_, p_117804_, p_117805_, p_117806_);
            float f1 = (float)p_117802_.getFallFlyingTicks() + p_117806_;
            float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
            if (!p_117802_.isAutoSpinAttack()) {
                p_117803_.mulPose(Vector3f.XP.rotationDegrees(f2 * (-90.0F - p_117802_.getXRot())));
            }

            Vec3 vec3 = p_117802_.getViewVector(p_117806_);
            Vec3 vec31 = p_117802_.getDeltaMovement();
            double d0 = vec31.horizontalDistanceSqr();
            double d1 = vec3.horizontalDistanceSqr();
            if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
                double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
                p_117803_.mulPose(Vector3f.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
            }
        } else if (f > 0.0F) {
            super.setupRotations(p_117802_, p_117803_, p_117804_, p_117805_, p_117806_);
            float f3 = p_117802_.isInWater() ? -90.0F - p_117802_.getXRot() : -90.0F;
            float f4 = Mth.lerp(f, 0.0F, f3);
            p_117803_.mulPose(Vector3f.XP.rotationDegrees(f4));
            if (p_117802_.isVisuallySwimming()) {
                p_117803_.translate(0.0D, -1.0D, (double)0.3F);
            }
        } else {
            super.setupRotations(p_117802_, p_117803_, p_117804_, p_117805_, p_117806_);
        }
    }

    public static HumanoidModel.ArmPose getArmPose(LatexEntity p_117795_, InteractionHand p_117796_) {
        ItemStack itemstack = p_117795_.getItemInHand(p_117796_);
        if (itemstack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        } else {
            if (p_117795_.getUsedItemHand() == p_117796_ && p_117795_.getUseItemRemainingTicks() > 0) {
                UseAnim useanim = itemstack.getUseAnimation();
                if (useanim == UseAnim.BLOCK) {
                    return HumanoidModel.ArmPose.BLOCK;
                }

                if (useanim == UseAnim.BOW) {
                    return HumanoidModel.ArmPose.BOW_AND_ARROW;
                }

                if (useanim == UseAnim.SPEAR) {
                    return HumanoidModel.ArmPose.THROW_SPEAR;
                }

                if (useanim == UseAnim.CROSSBOW && p_117796_ == p_117795_.getUsedItemHand()) {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }

                if (useanim == UseAnim.SPYGLASS) {
                    return HumanoidModel.ArmPose.SPYGLASS;
                }
            } else if (!p_117795_.swinging && itemstack.is(Items.CROSSBOW) && CrossbowItem.isCharged(itemstack)) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            return HumanoidModel.ArmPose.ITEM;
        }
    }

    public LatexHumanoidModel<T> getModel(LatexEntity entity) {
        return this.getModel();
    }
}
