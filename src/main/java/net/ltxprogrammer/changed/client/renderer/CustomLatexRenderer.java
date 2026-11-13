package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.CustomLatexModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.*;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.CustomLatexEntity;
import net.ltxprogrammer.changed.item.AbdomenArmor;
import net.ltxprogrammer.changed.item.QuadrupedalArmor;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CustomLatexRenderer extends AdvancedHumanoidRenderer<CustomLatexEntity, CustomLatexModel> {
	public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/custom_latex.png");

	public static class CustomArmorPicker extends ArmorModelPicker<CustomLatexEntity, LatexHumanoidArmorModel<CustomLatexEntity, ?>> {
		// Upper
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedMaleCanine;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedMaleFeline;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedMaleShark;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedMaleDragon;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedChiseledCanine;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedChiseledFeline;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedChiseledShark;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedChiseledDragon;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedFemaleCanine;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedFemaleFeline;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedFemaleShark;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedFemaleDragon;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedHeavyCanine;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedHeavyFeline;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedHeavyShark;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedHeavyDragon;

		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedMaleTaurUpper;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedMaleBuffTaurUpper;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedFemaleTaurUpper;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedHeavyTaurUpper;

		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedMaleMerUpper;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedMaleBuffMerUpper;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedFemaleMerUpper;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedHeavyMerUpper;

		// Lower
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedTaur;
		private final Map<ArmorModel, ? extends LatexHumanoidArmorModel<ChangedEntity, ?>> bakedMer;

		public CustomArmorPicker(EntityModelSet models) {
			this.bakedMaleCanine = ArmorLatexMaleWolfModel.MODEL_SET.createModels(models);
			this.bakedMaleFeline = ArmorLatexMaleCatModel.MODEL_SET.createModels(models);
			this.bakedMaleShark = ArmorLatexMaleSharkModel.MODEL_SET.createModels(models);
			this.bakedMaleDragon = ArmorLatexMaleDragonModel.MODEL_SET.createModels(models);
			this.bakedChiseledCanine = ArmorLatexMaleBuffWolfModel.MODEL_SET.createModels(models);
			this.bakedChiseledFeline = ArmorLatexMaleBuffCatModel.MODEL_SET.createModels(models);
			this.bakedChiseledShark = ArmorLatexMaleBuffSharkModel.MODEL_SET.createModels(models);
			this.bakedChiseledDragon = ArmorLatexMaleBuffDragonModel.MODEL_SET.createModels(models);
			this.bakedFemaleCanine = ArmorLatexFemaleWolfModel.MODEL_SET.createModels(models);
			this.bakedFemaleFeline = ArmorLatexFemaleCatModel.MODEL_SET.createModels(models);
			this.bakedFemaleShark = ArmorLatexFemaleSharkModel.MODEL_SET.createModels(models);
			this.bakedFemaleDragon = ArmorLatexFemaleDragonModel.MODEL_SET.createModels(models);
			this.bakedHeavyCanine = ArmorLatexHeavyWolfModel.MODEL_SET.createModels(models);
			this.bakedHeavyFeline = ArmorLatexHeavyCatModel.MODEL_SET.createModels(models);
			this.bakedHeavyShark = ArmorLatexHeavySharkModel.MODEL_SET.createModels(models);
			this.bakedHeavyDragon = ArmorLatexHeavyDragonModel.MODEL_SET.createModels(models);

			this.bakedMaleTaurUpper = ArmorLatexMaleTaurUpperModel.MODEL_SET.createModels(models);
			this.bakedMaleBuffTaurUpper = ArmorLatexMaleBuffTaurUpperModel.MODEL_SET.createModels(models);
			this.bakedFemaleTaurUpper = ArmorLatexFemaleTaurUpperModel.MODEL_SET.createModels(models);
			this.bakedHeavyTaurUpper = ArmorLatexHeavyTaurUpperModel.MODEL_SET.createModels(models);

			this.bakedMaleMerUpper = ArmorMermaidSharkUpperBodyModel.MODEL_SET.createModels(models);
			this.bakedMaleBuffMerUpper = ArmorBuffMermaidSharkUpperBodyModel.MODEL_SET.createModels(models);
			this.bakedFemaleMerUpper = ArmorSirenUpperBodyModel.MODEL_SET.createModels(models);
			this.bakedHeavyMerUpper = ArmorHeavyMermaidSharkUpperBodyModel.MODEL_SET.createModels(models);

			this.bakedTaur = ArmorLatexCentaurLowerModel.MODEL_SET_WITH_TORSO.createModels(models);
			this.bakedMer = ArmorSirenAbdomenModel.MODEL_SET.createModels(models);
		}

		@Override
		public LatexHumanoidArmorModel<CustomLatexEntity, ?> getModelForSlot(CustomLatexEntity entity, EquipmentSlot slot) {
			final var set = this.getModelSetForSlot(entity, slot);

			return switch (entity.getLegType()) {
				case BIPEDAL ->	set.get(slot == EquipmentSlot.LEGS ? ArmorModels.ARMOR_INNER : ArmorModels.ARMOR_OUTER);
				case CENTAUR -> set.get(QuadrupedalArmor.useInnerQuadrupedalModel(slot) ? ArmorModels.ARMOR_INNER : ArmorModels.ARMOR_OUTER);
				case MERMAID -> set.get(AbdomenArmor.useInnerAbdomenModel(slot) ? ArmorModels.ARMOR_INNER : ArmorModels.ARMOR_OUTER);
			};
		}

		private Map<ArmorModel, ? extends LatexHumanoidArmorModel<?, ?>> getModelSetByTorso(CustomLatexEntity.TorsoType torsoType,
																							Map<ArmorModel, ? extends LatexHumanoidArmorModel<?, ?>> male,
																							Map<ArmorModel, ? extends LatexHumanoidArmorModel<?, ?>> maleBuff,
																							Map<ArmorModel, ? extends LatexHumanoidArmorModel<?, ?>> female,
																							Map<ArmorModel, ? extends LatexHumanoidArmorModel<?, ?>> heavy) {
			return switch (torsoType) {
				case CHISELED -> maleBuff;
				case FEMALE -> female;
				case HEAVY -> heavy;
				default -> male;
			};
		}

		private Map<ArmorModel, ? extends LatexHumanoidArmorModel<?, ?>> getModelSetByTailAndTorso(CustomLatexEntity.TailType tailType, CustomLatexEntity.TorsoType torsoType) {
			return switch (tailType) {
				case WOLF -> getModelSetByTorso(torsoType, this.bakedMaleCanine, this.bakedChiseledCanine, this.bakedFemaleCanine, this.bakedHeavyCanine);
				case CAT -> getModelSetByTorso(torsoType, this.bakedMaleFeline, this.bakedChiseledFeline, this.bakedFemaleFeline, this.bakedHeavyFeline);
				case SHARK -> getModelSetByTorso(torsoType, this.bakedMaleShark, this.bakedChiseledShark, this.bakedFemaleShark, this.bakedHeavyShark);
				case DRAGON -> getModelSetByTorso(torsoType, this.bakedMaleDragon, this.bakedChiseledDragon, this.bakedFemaleDragon, this.bakedHeavyDragon);
				default -> Map.of();
			};
		}

		@Override
		public Map<ArmorModel, LatexHumanoidArmorModel<CustomLatexEntity, ?>> getModelSetForSlot(CustomLatexEntity entity, EquipmentSlot slot) {
			Map<ArmorModel, ? extends LatexHumanoidArmorModel<?, ?>> found = Map.of();

			switch (entity.getLegType()) {
				case BIPEDAL -> {
					found = getModelSetByTailAndTorso(entity.getTailType(), entity.getTorsoType());
				}
				case CENTAUR -> {
					found = QuadrupedalArmor.useQuadrupedalModel(slot) ? this.bakedTaur :
							getModelSetByTorso(entity.getTorsoType(), this.bakedMaleTaurUpper, this.bakedMaleBuffTaurUpper, this.bakedFemaleTaurUpper, this.bakedHeavyTaurUpper);
				}
				case MERMAID -> {
					found = QuadrupedalArmor.useQuadrupedalModel(slot) ? this.bakedMer :
							getModelSetByTorso(entity.getTorsoType(), this.bakedMaleMerUpper, this.bakedMaleBuffMerUpper, this.bakedFemaleMerUpper, this.bakedHeavyMerUpper);
				}
			}

			return (Map<ArmorModel, LatexHumanoidArmorModel<CustomLatexEntity, ?>>) Map.copyOf(found);
		}

		@Override
		public boolean usesSlimTextures(CustomLatexEntity entity, EquipmentSlot slot) {
			return false;
		}

		@Override
		public void forEach(CustomLatexEntity entity, Predicate<ArmorModel> predicate, BiConsumer<ArmorModel, ? super LatexHumanoidArmorModel<CustomLatexEntity, ?>> consumer) {
			Stream.of(EquipmentSlot.values()).filter(slot -> slot.getType() == EquipmentSlot.Type.ARMOR)
					.map(slot -> this.getModelSetForSlot(entity, slot))
					.forEach(set -> {
						set.forEach((layer, model) -> {
							if (predicate.test(layer))
								consumer.accept(layer, model);
						});
					});
		}

		@Override
		public void prepareAndSetupModels(CustomLatexEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
			Stream.of(EquipmentSlot.values()).filter(slot -> slot.getType() == EquipmentSlot.Type.ARMOR)
					.map(slot -> this.getModelSetForSlot(entity, slot))
					.forEach(set -> {
						final var inner = set.get(ArmorModels.ARMOR_INNER);
						inner.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
						inner.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

						final var outer = set.get(ArmorModels.ARMOR_OUTER);
						outer.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
						outer.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
					});
		}
	}

	public CustomLatexRenderer(EntityRendererProvider.Context context) {
		super(context, new CustomLatexModel(context.bakeLayer(CustomLatexModel.LAYER_LOCATION)),
				new CustomArmorPicker(context.getModelSet()), 0.5f);
		this.addLayer(new LatexParticlesLayer<>(this, getModel()));
		this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
		this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
		this.addLayer(new SaddleLayer<>(this, getModel(), Changed.modResource("textures/custom_latex_saddle.png")));
		this.addLayer(new TaurChestPackLayer<>(this, context.getModelSet()));
		this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(CustomLatexEntity entity) {
		return DEFAULT_SKIN_LOCATION;
	}
}