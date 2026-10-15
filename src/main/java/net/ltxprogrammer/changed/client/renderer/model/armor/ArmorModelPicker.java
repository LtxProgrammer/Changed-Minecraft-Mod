package net.ltxprogrammer.changed.client.renderer.model.armor;

import net.ltxprogrammer.changed.item.AbdomenArmor;
import net.ltxprogrammer.changed.item.QuadrupedalArmor;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import java.util.Arrays;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public abstract class ArmorModelPicker<T extends LivingEntity, M extends EntityModel<? super T>> {
    public abstract M getModelForSlot(T entity, EquipmentSlot slot);
    public abstract Map<ArmorModel, ? extends M> getModelSetForSlot(T entity, EquipmentSlot slot);

    public abstract boolean usesSlimTextures(T entity, EquipmentSlot slot);

    public abstract void forEach(T entity, Predicate<ArmorModel> predicate, BiConsumer<ArmorModel, ? super M> consumer);

    public abstract void prepareAndSetupModels(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch);

    public static <T extends LivingEntity, M extends EntityModel<? super T>> ArmorModelPicker<T, M> basic(EntityModelSet models, ArmorModelSet<? super T, ? extends M> full) {
        return new ArmorModelPicker<>() {
            final Map<ArmorModel, ? extends M> baked = full.createModels(models);

            @Override
            public M getModelForSlot(T entity, EquipmentSlot slot) {
                return baked.get(slot == EquipmentSlot.LEGS ? ArmorModels.ARMOR_INNER : ArmorModels.ARMOR_OUTER);
            }

            @Override
            public Map<ArmorModel, ? extends M> getModelSetForSlot(T entity, EquipmentSlot slot) {
                return Map.copyOf(baked);
            }

            @Override
            public boolean usesSlimTextures(T entity, EquipmentSlot slot) {
                return full.usesSlimTextures();
            }

            @Override
            public void forEach(T entity, Predicate<ArmorModel> predicate, BiConsumer<ArmorModel, ? super M> consumer) {
                ArmorModels.streamModels().filter(predicate).forEach(model -> {
                    consumer.accept(model, baked.get(model));
                });
            }

            @Override
            public void prepareAndSetupModels(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                final var inner = baked.get(ArmorModels.ARMOR_INNER);
                inner.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                inner.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                final var outer = baked.get(ArmorModels.ARMOR_OUTER);
                outer.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                outer.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            }
        };
    }

    public static <T extends LivingEntity, M extends EntityModel<? super T>> ArmorModelPicker<T, M> centaur(EntityModelSet models, ArmorModelSet<? super T, ? extends M> upperBody, ArmorModelSet<? super T, ? extends M> lowerBody) {
        return new ArmorModelPicker<>() {
            final Map<ArmorModel, ? extends M> bakedUpper = upperBody.createModels(models);
            final Map<ArmorModel, ? extends M> bakedLower = lowerBody.createModels(models);

            @Override
            public M getModelForSlot(T entity, EquipmentSlot slot) {
                return QuadrupedalArmor.useQuadrupedalModel(slot) ?
                        bakedLower.get(QuadrupedalArmor.useInnerQuadrupedalModel(slot) ? ArmorModels.ARMOR_INNER : ArmorModels.ARMOR_OUTER) :
                        bakedUpper.get(slot == EquipmentSlot.LEGS ? ArmorModels.ARMOR_INNER : ArmorModels.ARMOR_OUTER);
            }

            @Override
            public Map<ArmorModel, ? extends M> getModelSetForSlot(T entity, EquipmentSlot slot) {
                return Map.copyOf(slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET ? bakedLower : bakedUpper);
            }

            @Override
            public boolean usesSlimTextures(T entity, EquipmentSlot slot) {
                return (slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET ? lowerBody : upperBody).usesSlimTextures();
            }

            @Override
            public void forEach(T entity, Predicate<ArmorModel> predicate, BiConsumer<ArmorModel, ? super M> consumer) {
                ArmorModels.streamModels().filter(predicate).forEach(model -> {
                    consumer.accept(model, bakedUpper.get(model));
                    consumer.accept(model, bakedLower.get(model));
                });
            }

            @Override
            public void prepareAndSetupModels(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                final var innerUpper = bakedUpper.get(ArmorModels.ARMOR_INNER);
                innerUpper.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                innerUpper.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                final var outerUpper = bakedUpper.get(ArmorModels.ARMOR_OUTER);
                outerUpper.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                outerUpper.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                final var innerLower = bakedLower.get(ArmorModels.ARMOR_INNER);
                innerLower.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                innerLower.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                final var outerLower = bakedLower.get(ArmorModels.ARMOR_OUTER);
                outerLower.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                outerLower.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            }
        };
    }

    public static <T extends LivingEntity, M extends EntityModel<? super T>> ArmorModelPicker<T, M> legless(EntityModelSet models, ArmorModelSet<? super T, ? extends M> upperBody, ArmorModelSet<? super T, ? extends M> lowerBody) {
        return new ArmorModelPicker<>() {
            final Map<ArmorModel, ? extends M> bakedUpper = upperBody.createModels(models);
            final Map<ArmorModel, ? extends M> bakedLower = lowerBody.createModels(models);

            @Override
            public M getModelForSlot(T entity, EquipmentSlot slot) {
                return AbdomenArmor.useAbdomenModel(slot) ?
                        bakedLower.get(AbdomenArmor.useInnerAbdomenModel(slot) ? ArmorModels.ARMOR_INNER : ArmorModels.ARMOR_OUTER) :
                        bakedUpper.get(slot == EquipmentSlot.LEGS ? ArmorModels.ARMOR_INNER : ArmorModels.ARMOR_OUTER);
            }

            @Override
            public Map<ArmorModel, ? extends M> getModelSetForSlot(T entity, EquipmentSlot slot) {
                return Map.copyOf(slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET ? bakedLower : bakedUpper);
            }

            @Override
            public boolean usesSlimTextures(T entity, EquipmentSlot slot) {
                return (slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET ? lowerBody : upperBody).usesSlimTextures();
            }

            @Override
            public void forEach(T entity, Predicate<ArmorModel> predicate, BiConsumer<ArmorModel, ? super M> consumer) {
                ArmorModels.streamModels().filter(predicate).forEach(model -> {
                    consumer.accept(model, bakedUpper.get(model));
                    consumer.accept(model, bakedLower.get(model));
                });
            }

            @Override
            public void prepareAndSetupModels(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                final var innerUpper = bakedUpper.get(ArmorModels.ARMOR_INNER);
                innerUpper.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                innerUpper.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                final var outerUpper = bakedUpper.get(ArmorModels.ARMOR_OUTER);
                outerUpper.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                outerUpper.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                final var innerLower = bakedLower.get(ArmorModels.ARMOR_INNER);
                innerLower.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                innerLower.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

                final var outerLower = bakedLower.get(ArmorModels.ARMOR_OUTER);
                outerLower.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
                outerLower.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            }
        };
    }
}
