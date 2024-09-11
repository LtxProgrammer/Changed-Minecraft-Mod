package net.ltxprogrammer.changed.client.renderer.model.armor;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.item.AbdomenArmor;
import net.ltxprogrammer.changed.item.QuadrupedalArmor;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.Map;

public abstract class ArmorModelPicker<T extends ChangedEntity> {
    public abstract LatexHumanoidArmorModel<T, ?> getModelForSlot(EquipmentSlot slot);
    public abstract Map<ArmorModel, LatexHumanoidArmorModel<T, ?>> getModelSetForSlot(EquipmentSlot slot);

    public abstract void prepareAndSetupModels(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch);

    public static <T extends ChangedEntity> ArmorModelPicker<T> basic(EntityModelSet models, ArmorModelSet<T, ?> full) {
        return new ArmorModelPicker<>() {
            final Map<ArmorModel, ? extends LatexHumanoidArmorModel<T, ?>> baked = full.createModels(models);

            @Override
            public LatexHumanoidArmorModel<T, ?> getModelForSlot(EquipmentSlot slot) {
                return baked.get(slot == EquipmentSlot.LEGS ? ArmorModel.ARMOR_INNER : ArmorModel.ARMOR_OUTER);
            }

            @Override
            public Map<ArmorModel, LatexHumanoidArmorModel<T, ?>> getModelSetForSlot(EquipmentSlot slot) {
                return Map.copyOf(baked);
            }

            @Override
            public void prepareAndSetupModels(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                final var inner = baked.get(ArmorModel.ARMOR_INNER);
                inner.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                inner.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                final var outer = baked.get(ArmorModel.ARMOR_OUTER);
                outer.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                outer.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            }
        };
    }

    public static <T extends ChangedEntity> ArmorModelPicker<T> centaur(EntityModelSet models, ArmorModelSet<T, ?> upperBody, ArmorModelSet<T, ?> lowerBody) {
        return new ArmorModelPicker<>() {
            final Map<ArmorModel, ? extends LatexHumanoidArmorModel<T, ?>> bakedUpper = upperBody.createModels(models);
            final Map<ArmorModel, ? extends LatexHumanoidArmorModel<T, ?>> bakedLower = lowerBody.createModels(models);

            @Override
            public LatexHumanoidArmorModel<T, ?> getModelForSlot(EquipmentSlot slot) {
                return QuadrupedalArmor.useQuadrupedalModel(slot) ?
                        bakedLower.get(QuadrupedalArmor.useInnerQuadrupedalModel(slot) ? ArmorModel.ARMOR_INNER : ArmorModel.ARMOR_OUTER) :
                        bakedUpper.get(slot == EquipmentSlot.LEGS ? ArmorModel.ARMOR_INNER : ArmorModel.ARMOR_OUTER);
            }

            @Override
            public Map<ArmorModel, LatexHumanoidArmorModel<T, ?>> getModelSetForSlot(EquipmentSlot slot) {
                return Map.copyOf(slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET ? bakedLower : bakedUpper);
            }

            @Override
            public void prepareAndSetupModels(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                final var innerUpper = bakedUpper.get(ArmorModel.ARMOR_INNER);
                innerUpper.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                innerUpper.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                final var outerUpper = bakedUpper.get(ArmorModel.ARMOR_OUTER);
                outerUpper.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                outerUpper.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                final var innerLower = bakedLower.get(ArmorModel.ARMOR_INNER);
                innerLower.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                innerLower.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                final var outerLower = bakedLower.get(ArmorModel.ARMOR_OUTER);
                outerLower.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                outerLower.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            }
        };
    }

    public static <T extends ChangedEntity> ArmorModelPicker<T> legless(EntityModelSet models, ArmorModelSet<T, ?> upperBody, ArmorModelSet<T, ?> lowerBody) {
        return new ArmorModelPicker<>() {
            final Map<ArmorModel, ? extends LatexHumanoidArmorModel<T, ?>> bakedUpper = upperBody.createModels(models);
            final Map<ArmorModel, ? extends LatexHumanoidArmorModel<T, ?>> bakedLower = lowerBody.createModels(models);

            @Override
            public LatexHumanoidArmorModel<T, ?> getModelForSlot(EquipmentSlot slot) {
                return AbdomenArmor.useAbdomenModel(slot) ?
                        bakedLower.get(AbdomenArmor.useInnerAbdomenModel(slot) ? ArmorModel.ARMOR_INNER : ArmorModel.ARMOR_OUTER) :
                        bakedUpper.get(slot == EquipmentSlot.LEGS ? ArmorModel.ARMOR_INNER : ArmorModel.ARMOR_OUTER);
            }

            @Override
            public Map<ArmorModel, LatexHumanoidArmorModel<T, ?>> getModelSetForSlot(EquipmentSlot slot) {
                return Map.copyOf(slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET ? bakedLower : bakedUpper);
            }

            @Override
            public void prepareAndSetupModels(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                final var innerUpper = bakedUpper.get(ArmorModel.ARMOR_INNER);
                innerUpper.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                innerUpper.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                final var outerUpper = bakedUpper.get(ArmorModel.ARMOR_OUTER);
                outerUpper.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                outerUpper.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                final var innerLower = bakedLower.get(ArmorModel.ARMOR_INNER);
                innerLower.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                innerLower.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                final var outerLower = bakedLower.get(ArmorModel.ARMOR_OUTER);
                outerLower.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                outerLower.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            }
        };
    }
}
