package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.DarkLatexWolfPupModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorNoneModel;
import net.ltxprogrammer.changed.entity.beast.DarkLatexWolfPup;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.NotNull;

public class DarkLatexWolfPupRenderer extends AdvancedHumanoidRenderer<DarkLatexWolfPup, DarkLatexWolfPupModel, ArmorNoneModel<DarkLatexWolfPup>> {
	public DarkLatexWolfPupRenderer(EntityRendererProvider.Context context) {
		super(context, new DarkLatexWolfPupModel(context.bakeLayer(DarkLatexWolfPupModel.LAYER_LOCATION)),
				ArmorNoneModel::new, ArmorNoneModel.INNER_ARMOR, ArmorNoneModel.OUTER_ARMOR, 0.4F);
		this.addLayer(new LatexParticlesLayer<>(this, getModel(), model::isPartNotMask));
		this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
				.withSclera(Color3.fromInt(0x242424))
				.withIris(CustomEyesLayer.fixedIfNotDarkLatexOverrideLeft(Color3.WHITE),
						CustomEyesLayer.fixedIfNotDarkLatexOverrideRight(Color3.WHITE))
				.build().setHeadShape(CustomEyesLayer.HeadShape.PUP));
	}

	@Override
	public ResourceLocation getTextureLocation(DarkLatexWolfPup entity) {
		return entity.isPuddle() ? Changed.modResource("textures/dark_latex_pup_puddle.png") : Changed.modResource("textures/dark_latex_wolf_pup.png");
	}

	@Override
	protected float getFlipDegrees(DarkLatexWolfPup entity) {
		return entity.getPose() == Pose.SLEEPING ? 0.0F : super.getFlipDegrees(entity);
	}

	@Override
	protected boolean isEntityUprightType(@NotNull DarkLatexWolfPup entity) {
		return false;
	}
}