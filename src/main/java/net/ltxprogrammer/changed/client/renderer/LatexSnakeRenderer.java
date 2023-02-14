package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexSnakeModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorAbdomenModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorUpperBodyModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.LatexHumanoidArmorModel;
import net.ltxprogrammer.changed.entity.beast.LatexSnake;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

public class LatexSnakeRenderer extends LatexHumanoidRenderer<LatexSnake, LatexSnakeModel, ArmorUpperBodyModel<LatexSnake>> {
    public static boolean useAbdomenModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
    }

    public static boolean useInnerAbdomenModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.FEET;
    }

    public static void setVisibility(LatexSnake snake, LatexHumanoidArmorModel<LatexSnake> model, EquipmentSlot slot) {
        switch (slot) {
            case LEGS:
                model.body.visible = true;
                model.abdomen.visible = true;
                model.lowerAbdomen.visible = true;
                model.tail.getAllParts().forEach(modelPart -> modelPart.visible = false);
                break;
            case FEET:
                model.abdomen.visible = true;
                model.lowerAbdomen.getAllParts().forEach(modelPart -> modelPart.visible = true);
            default: break;
        }
    }

    public LatexSnakeRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSnakeModel(context.bakeLayer(LatexSnakeModel.LAYER_LOCATION)),
                ArmorUpperBodyModel::new, ArmorUpperBodyModel.INNER_ARMOR, ArmorUpperBodyModel.OUTER_ARMOR,
                ArmorAbdomenModel::new, ArmorAbdomenModel.INNER_ARMOR, ArmorAbdomenModel.OUTER_ARMOR,
                LatexSnakeRenderer::useAbdomenModel, LatexSnakeRenderer::useInnerAbdomenModel, LatexSnakeRenderer::setVisibility, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSnake p_114482_) {
        return Changed.modResource("textures/latex_snake.png");
    }
}
