package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.entity.beast.SpecialLatex;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

public class DeferredSpecialLatexModel extends LatexHumanoidModel<SpecialLatex> {
    public DeferredSpecialLatexModel(ModelPart root) {
        super(root);
    }

    @Override
    public void translateToHand(HumanoidArm p_102108_, PoseStack p_102109_) {

    }

    @Override
    public void setupAnim(SpecialLatex p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {

    }

    @Override
    public ModelPart getHead() {
        return null;
    }

    @Override
    public void renderToBuffer(PoseStack p_103111_, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_) {

    }
}
