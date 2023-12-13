package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.DarkLatexPupModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorNoneModel;
import net.ltxprogrammer.changed.entity.beast.DarkLatexPup;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Pose;
import org.jetbrains.annotations.NotNull;

public class DarkLatexPupRenderer extends LatexHumanoidRenderer<DarkLatexPup, DarkLatexPupModel, ArmorNoneModel<DarkLatexPup>> {
	public DarkLatexPupRenderer(EntityRendererProvider.Context context) {
		super(context, new DarkLatexPupModel(context.bakeLayer(DarkLatexPupModel.LAYER_LOCATION)),
				ArmorNoneModel::new, ArmorNoneModel.INNER_ARMOR, ArmorNoneModel.OUTER_ARMOR, 0.4F);
	}

	@Override
	public ResourceLocation getTextureLocation(DarkLatexPup entity) {
		return entity.isPuddle() ? Changed.modResource("textures/dark_latex_pup_puddle.png") : Changed.modResource("textures/dark_latex_pup.png");
	}

	@Override
	protected float getFlipDegrees(DarkLatexPup entity) {
		return entity.getPose() == Pose.SLEEPING ? 0.0F : super.getFlipDegrees(entity);
	}

	@Override
	protected boolean isEntityUprightType(@NotNull DarkLatexPup entity) {
		return false;
	}
}