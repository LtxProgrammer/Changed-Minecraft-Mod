package net.ltxprogrammer.changed.client.renderer.model.armor;

import net.ltxprogrammer.changed.data.DeferredModelLayerLocation;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArmorSpecialLatexModel<T extends LatexEntity> extends LatexHumanoidArmorModel<T> {
    public ArmorSpecialLatexModel(ModelPart modelPart, PatreonBenefits.SpecialLatexForm form) {
        super(
                modelPart.getChild("Head"),
                modelPart.getChild("Torso"),
                form.animationData().hasTail() ? modelPart.getChild("Torso").getChild("Tail") : null,
                modelPart.getChild("LeftLeg"),
                modelPart.getChild("RightLeg"),
                modelPart.getChild("LeftArm"),
                modelPart.getChild("RightArm"), builder -> {
                    if (form.tailAidsInSwim()) builder.tailAidsInSwim();
                });
    }
}
