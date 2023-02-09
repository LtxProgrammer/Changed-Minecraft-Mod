package net.ltxprogrammer.changed.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedEffects;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class TscStaff extends TscWeapon implements SpecializedItemRendering, SpecializedAnimations {
    public TscStaff() {
        super(new Properties().durability(500));
    }

    private static final ModelResourceLocation STAFF_INVENTORY =
            new ModelResourceLocation(Changed.modResource("tsc_staff"), "inventory");
    private static final ModelResourceLocation STAFF_IN_HAND =
            new ModelResourceLocation(Changed.modResource("tsc_staff_in_hand"), "inventory");
    private static final ModelResourceLocation STAFF_IN_HAND_EMISSIVE =
            new ModelResourceLocation(Changed.modResource("tsc_staff_in_hand_emissive"), "inventory");

    @Nullable
    @Override
    public ModelResourceLocation getEmissiveModelLocation(ItemStack itemStack, ItemTransforms.TransformType type) {
        return SpecializedItemRendering.isGUI(type) ? null : STAFF_IN_HAND_EMISSIVE;
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack itemStack, ItemTransforms.TransformType type) {
        return SpecializedItemRendering.isGUI(type) ? STAFF_INVENTORY : STAFF_IN_HAND;
    }

    @Override
    public void loadSpecialModels(Consumer<ResourceLocation> loader) {
        loader.accept(STAFF_IN_HAND);
        loader.accept(STAFF_IN_HAND_EMISSIVE);
    }

    @Override
    public AnimationHandler getAnimationHandler() {
        return ANIMATION_CACHE.computeIfAbsent(this, StaffAnimation::new);
    }

    public boolean hurtEnemy(ItemStack itemStack, LivingEntity enemy, LivingEntity source) {
        sweepWeapon(source);
        enemy.addEffect(new MobEffectInstance(ChangedEffects.SHOCK, 10, 0, false, false, true));
        itemStack.hurtAndBreak(1, source, (entity) -> {
            entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
        Material material = blockState.getMaterial();
        return material != Material.PLANT && material != Material.REPLACEABLE_PLANT && !blockState.is(BlockTags.LEAVES) && material != Material.VEGETABLE ? 1.0F : 1.5F;
    }

    public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity entity) {
        if (blockState.getDestroySpeed(level, blockPos) != 0.0F) {
            itemStack.hurtAndBreak(2, entity, (living) -> {
                living.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        return true;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity.swingTime > 0)
            return true;
        sweepWeapon(entity);
        return super.onEntitySwing(stack, entity);
    }

    @Override
    public double attackDamage() {
        return Tiers.IRON.getAttackDamageBonus();
    }

    @Override
    public double attackSpeed() {
        return -2.6;
    }

    @Override
    public int getEnchantmentValue() {
        return Tiers.IRON.getEnchantmentValue();
    }

    @OnlyIn(Dist.CLIENT)
    static class StaffAnimation extends AnimationHandler {
        public StaffAnimation(Item item) {
            super(item);
        }

        @Override
        public void setupAttackAnimation(ItemStack itemStack, EntityStateContext entity, UpperModelContext model) {
            if (entity.livingEntity.isVisuallySwimming())
                return;
            else if (!entity.fullEquippedItem(item)) {
                super.setupAttackAnimation(itemStack, entity, model);
                return;
            }

            model.rightArm.xRot = model.rightArm.xRot * 0.125F - 1.0F;
            model.rightArm.yRot = ((float)Math.PI / 18F);
            model.rightArm.zRot = (-(float)Math.PI / 4.2F);
            model.leftArm.xRot = model.rightArm.xRot * 0.125F - 0.3F;
            model.leftArm.yRot = 0.0F;
            model.leftArm.zRot = ((float)Math.PI / 2.5F);
            if (!(entity.attackTime <= 0.0F)) {
                HumanoidArm arm = entity.getAttackArm();
                ModelPart armModel = model.getArm(entity.getAttackArm());
                float f = entity.attackTime;
                model.body.yRot = Mth.sin(Mth.sqrt(f) * ((float)Math.PI * 2F)) * 0.2F;
                if (arm == HumanoidArm.LEFT) {
                    model.body.yRot *= -1.0F;
                }

                model.rightArm.z = Mth.sin(model.body.yRot) * 5.0F;
                model.rightArm.x = -Mth.cos(model.body.yRot) * 5.0F;
                model.leftArm.z = -Mth.sin(model.body.yRot) * 5.0F;
                model.leftArm.x = Mth.cos(model.body.yRot) * 5.0F;
                model.rightArm.yRot += model.body.yRot;
                model.leftArm.yRot += model.body.yRot;
                model.leftArm.xRot += model.body.yRot;
                f = 1.0F - entity.attackTime;
                f *= f;
                f *= f;
                f = 1.0F - f;
                float f1 = Mth.sin(f * (float)Math.PI);
                float f2 = Mth.sin(entity.attackTime * (float)Math.PI) * -(model.head.xRot - 0.7F) * 0.75F;
                armModel.xRot -= f1 * 1.8F + f2;
                armModel.yRot += model.body.yRot * 3.0F;
                armModel.zRot += Mth.sin(entity.attackTime * (float)Math.PI) * -0.8F;
            }
        }

        @Override
        public void adjustGrip(EntityStateContext entity, ItemStack itemStack, ItemTransforms.TransformType type, PoseStack pose) {
            super.adjustGrip(entity, itemStack, type, pose);
            if (!entity.fullEquippedItem(item))
                return;
            if (type != ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND && type != ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
                return;

            pose.translate(0, -0.4, 0);
        }
    }
}
