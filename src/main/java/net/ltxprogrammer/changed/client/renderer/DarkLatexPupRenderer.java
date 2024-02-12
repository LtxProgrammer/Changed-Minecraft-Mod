package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.BlackGooPupModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorNoneModel;
import net.ltxprogrammer.changed.entity.beast.BlackGooPup;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.NotNull;

public class DarkLatexPupRenderer extends AdvancedHumanoidRenderer<BlackGooPup, BlackGooPupModel, ArmorNoneModel<BlackGooPup>> {
	public DarkLatexPupRenderer(EntityRendererProvider.Context context) {
		super(context, new BlackGooPupModel(context.bakeLayer(BlackGooPupModel.LAYER_LOCATION)),
				ArmorNoneModel::new, ArmorNoneModel.INNER_ARMOR, ArmorNoneModel.OUTER_ARMOR, 0.4F);
	}

	@Override
	public ResourceLocation getTextureLocation(BlackGooPup entity) {
		return entity.isPuddle() ? Changed.modResource("textures/dark_latex_pup_puddle.png") : Changed.modResource("textures/dark_latex_pup.png");
	}

	@Override
	protected float getFlipDegrees(BlackGooPup entity) {
		return entity.getPose() == Pose.SLEEPING ? 0.0F : super.getFlipDegrees(entity);
	}

	@Override
	protected boolean isEntityUprightType(@NotNull BlackGooPup entity) {
		return false;
	}
}