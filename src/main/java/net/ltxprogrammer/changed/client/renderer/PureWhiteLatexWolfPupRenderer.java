package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.DarkLatexWolfPupModel;
import net.ltxprogrammer.changed.client.renderer.model.PureWhiteLatexWolfPupModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorNoneModel;
import net.ltxprogrammer.changed.entity.beast.DarkLatexWolfPup;
import net.ltxprogrammer.changed.entity.beast.PureWhiteLatexWolfPup;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.NotNull;

public class PureWhiteLatexWolfPupRenderer extends AdvancedHumanoidRenderer<PureWhiteLatexWolfPup, PureWhiteLatexWolfPupModel, ArmorNoneModel<PureWhiteLatexWolfPup>> {
	public PureWhiteLatexWolfPupRenderer(EntityRendererProvider.Context context) {
		super(context, new PureWhiteLatexWolfPupModel(context.bakeLayer(PureWhiteLatexWolfPupModel.LAYER_LOCATION)), ArmorNoneModel.MODEL_SET, 0.4F);
		this.addLayer(new LatexParticlesLayer<>(this, getModel()));
	}

	@Override
	public ResourceLocation getTextureLocation(PureWhiteLatexWolfPup entity) {
		return Changed.modResource("textures/pure_white_latex_wolf_pup.png");
	}

	@Override
	protected float getFlipDegrees(PureWhiteLatexWolfPup entity) {
		return entity.getPose() == Pose.SLEEPING ? 0.0F : super.getFlipDegrees(entity);
	}

	@Override
	protected boolean isEntityUprightType(@NotNull PureWhiteLatexWolfPup entity) {
		return false;
	}
}