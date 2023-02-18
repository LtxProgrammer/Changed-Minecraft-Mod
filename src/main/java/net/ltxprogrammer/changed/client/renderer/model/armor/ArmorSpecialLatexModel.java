package net.ltxprogrammer.changed.client.renderer.model.armor;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.client.model.geom.ModelPart;

public class ArmorSpecialLatexModel<T extends LatexEntity> extends LatexHumanoidArmorModel<T> {
    public ArmorSpecialLatexModel(ModelPart modelPart, PatreonBenefits.ModelData model) {
        super(new Builder(
                modelPart.getChild("Head"),
                modelPart.getChild("Torso"),
                model.animationData().hasTail() ? modelPart.getChild("Torso").getChild("Tail") : null,
                modelPart.getChild("LeftLeg"),
                modelPart.getChild("RightLeg"),
                modelPart.getChild("LeftArm"),
                modelPart.getChild("RightArm")), builder -> {
                    if (model.animationData().swimTail())
                        builder.tailAidsInSwim();
                });
    }

    @Override
    public void prepareForShorts() {
        try {
            body.visible = true;
            leftLeg.getChild("LeftUpperLeg_r1").visible = true;
            rightLeg.getChild("RightUpperLeg_r1").visible = true;
        } catch (Exception ignored) {}
    }
}
