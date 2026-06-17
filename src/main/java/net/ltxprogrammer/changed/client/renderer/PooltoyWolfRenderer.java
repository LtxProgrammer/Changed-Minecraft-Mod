package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.PooltoyWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorPooltoyWolfModel;
import net.ltxprogrammer.changed.entity.SpringType;
import net.ltxprogrammer.changed.entity.beast.PooltoyWolf;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class PooltoyWolfRenderer extends AdvancedHumanoidRenderer<PooltoyWolf, PooltoyWolfModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/pooltoy_wolf.png");

    public PooltoyWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new PooltoyWolfModel(context.bakeLayer(PooltoyWolfModel.LAYER_LOCATION)), ArmorPooltoyWolfModel.MODEL_SET, 0.5f);
        var translucent = new LatexTranslucentLayer<>(this, this.model, Changed.modResource("textures/pooltoy_wolf_translucent.png"));
        this.addLayer(translucent);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.fromInt(0xdbdbdb)), CustomEyesLayer.translucentIrisColorLeft(0.5f), CustomEyesLayer.translucentIrisColorRight(0.5f)));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(PooltoyWolf entity) {
        return DEFAULT_SKIN_LOCATION;
    }

    @Override
    protected void scale(PooltoyWolf entity, PoseStack pose, float deltaTime) {
        super.scale(entity, pose, deltaTime);
        float spring = entity.getSimulatedSpring(SpringType.MODERATE_STRONG, SpringType.Direction.VERTICAL, deltaTime) * -0.125f;
        pose.scale(1.0f - spring, 1.0f + spring, 1.0f - spring);
    }
}