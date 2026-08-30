package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModelPicker;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModelSet;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AdvancedHumanoidRenderer<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends MobRenderer<T, M> {
    @Nullable
    private LatexHumanoidHairLayer<T, M> hairLayer;

    private LatexHumanoidArmorLayer<T, M> armorLayer;

    public @Nullable LatexHumanoidHairLayer<T, M> getHairLayer() {
        return hairLayer;
    }

    public LatexHumanoidArmorLayer<T, M> getArmorLayer() {
        return armorLayer;
    }

    private void addLayers(EntityRendererProvider.Context context, M main) {
        /*if (Changed.config.client.useNewModels.get())
            hairLayer = new LatexHumanoidHairLayer<>(this, context.getModelSet());*/
        if (armorLayer != null)
            this.addLayer(armorLayer);
        this.addLayer(new LatexItemInHandLayer<>(this, context.getItemInHandRenderer()));
        if (hairLayer != null)
            this.addLayer(hairLayer);
        this.addLayer(new LatexArrowLayer<>(context, this));
        //this.addLayer(new LatexCapeLayer<>(this));
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
        this.addLayer(new LatexElytraLayer<>(this, context.getModelSet()));
        this.addLayer(new LatexParrotOnShoulderLayer<>(this, context.getModelSet()));
        this.addLayer(new LatexBeeStingerLayer<>(this));
        this.addLayer(new LatexSpinAttackEffectLayer<>(this, context.getModelSet()));
        this.addLayer(new LatexHeldEntityLayer<>(this));

        this.addLayer(this.makeAccessoryLayer());
    }

    protected AccessoryLayer<T, M> makeAccessoryLayer() {
        return new AccessoryLayer<>(this);
    }

    public void setModelResetPoseStack(T entity, @Nullable PoseStack.Pose pose) {
        this.getModel(entity).resetPoseStack = pose;
        this.layers.forEach(layer -> {
            if (layer instanceof LatexHumanoidArmorLayer armorLayer) {
                armorLayer.getArmorModel(entity, EquipmentSlot.HEAD).resetPoseStack = pose;
                armorLayer.getArmorModel(entity, EquipmentSlot.CHEST).resetPoseStack = pose;
                armorLayer.getArmorModel(entity, EquipmentSlot.LEGS).resetPoseStack = pose;
                armorLayer.getArmorModel(entity, EquipmentSlot.FEET).resetPoseStack = pose;
            }
        });
    }

    public AdvancedHumanoidRenderer(EntityRendererProvider.Context context, M main,
                                    ArmorModelPicker<T, ? extends LatexHumanoidArmorModel<? super T, ?>> modelPicker, float shadowSize) {
        super(context, main, shadowSize);
        if (main == null) return;
        this.armorLayer = new LatexHumanoidArmorLayer<>(this, modelPicker, context.getModelManager());
        this.addLayers(context, main);
    }

    public AdvancedHumanoidRenderer(EntityRendererProvider.Context context, M main,
                                    ArmorModelSet<? super T, ? extends LatexHumanoidArmorModel<? super T, ?>> modelSet, float shadowSize) {
        this(context, main, ArmorModelPicker.basic(context.getModelSet(), modelSet), shadowSize);
    }

    protected boolean isEntityUprightType(@NotNull T entity) {
        return true;
    }

    @Override
    public @NotNull Vec3 getRenderOffset(@NotNull T entity, float partialTick) {
        // Hook for mixins
        return super.getRenderOffset(entity, partialTick);
    }

    @Deprecated
    protected void scaleForBPI(@NotNull T entity, BasicPlayerInfo bpi, PoseStack poseStack) {}

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTicks) {
        super.scale(entity, poseStack, partialTicks);
        float renderScale = entity.getRenderScale(partialTicks);
        if (renderScale != 1f)
            poseStack.scale(renderScale, renderScale, renderScale);
    }

    @Override
    protected void setupRotations(@NotNull T entity, PoseStack poseStack, float bob, float bodyYRot, float partialTicks) {
        this.setModelResetPoseStack(entity, null);
        this.scaleForBPI(entity, entity.getBasicPlayerInfo(), poseStack);

        float swimAmount = entity.getSwimAmount(partialTicks);
        boolean upright = isEntityUprightType(entity);
        if (upright && entity.isFallFlying()) {
            super.setupRotations(entity, poseStack, bob, bodyYRot, partialTicks);
            float f1 = (float)entity.getFallFlyingTicks() + partialTicks;
            float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
            if (!entity.isAutoSpinAttack()) {
                poseStack.mulPose(Axis.XP.rotationDegrees(f2 * (-90.0F - entity.getXRot())));
            }

            Vec3 vec3 = entity.getViewVector(partialTicks);
            Vec3 vec31 = entity.getDeltaMovement();
            double d0 = vec31.horizontalDistanceSqr();
            double d1 = vec3.horizontalDistanceSqr();
            if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
                double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
                poseStack.mulPose(Axis.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
            }
        } else if (upright && swimAmount > 0.0F) {
            super.setupRotations(entity, poseStack, bob, bodyYRot, partialTicks);
            float f3 = (entity.isInWater() || entity.canSwimInFluidType(ForgeMod.EMPTY_TYPE.get())) ? -90.0F - entity.getXRot() : -90.0F;
            float f4 = Mth.lerp(swimAmount, 0.0F, f3);
            poseStack.mulPose(Axis.XP.rotationDegrees(f4));
            if (entity.isVisuallySwimming()) {
                poseStack.translate(0.0D, -1.0D, (double)0.3F);
            }
        } else if (upright && entity.isSleeping()) {
            super.setupRotations(entity, poseStack, bob, bodyYRot, partialTicks);
        } else {
            super.setupRotations(entity, poseStack, bob, bodyYRot, partialTicks);
        }

        poseStack.translate(0, 0, getModel(entity).getAnimator(entity).forwardOffset / 16.0D);
    }

    public static HumanoidModel.ArmPose getArmPose(ChangedEntity p_117795_, InteractionHand p_117796_) {
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

                if (useanim == UseAnim.TOOT_HORN) {
                    return HumanoidModel.ArmPose.TOOT_HORN;
                }

                if (useanim == UseAnim.BRUSH) {
                    return HumanoidModel.ArmPose.BRUSH;
                }
            } else if (!p_117795_.swinging && itemstack.is(Items.CROSSBOW) && CrossbowItem.isCharged(itemstack)) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            return HumanoidModel.ArmPose.ITEM;
        }
    }

    public AdvancedHumanoidModel<T> getModel(ChangedEntity entity) {
        return this.getModel();
    }

    @Override
    public void render(T entity, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        model.getAnimator(entity).partialTicks = partialTicks;
        super.render(entity, yRot, partialTicks, poseStack, bufferSource, packedLight);
    }

    public boolean shouldRenderArmor(T entity) {
        return true;
    }

    @SuppressWarnings("unchecked")
    public void wrapLayer(RenderLayer<?, ?> layer) {
        this.addLayer(new PlayerLayerWrapper<>(this, (RenderLayer<? super Player, PlayerModel<? super Player>>) layer));
    }
}
