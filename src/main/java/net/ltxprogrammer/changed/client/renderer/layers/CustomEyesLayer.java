package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class CustomEyesLayer<M extends LatexHumanoidModel<T>, T extends LatexEntity> extends RenderLayer<T, M> {
    public interface ColorFunction<T extends LatexEntity> extends BiFunction<T, BasicPlayerInfo, Color3> {
        @Nullable
        Color3 getColor(T entity, BasicPlayerInfo bpi);

        @Override
        default Color3 apply(T entity, BasicPlayerInfo bpi) {
            return getColor(entity, bpi);
        }

        default Optional<Color3> getColorSafe(T entity, BasicPlayerInfo bpi) {
            return Optional.ofNullable(this.getColor(entity, bpi));
        }
    }

    public static final ModelLayerLocation HEAD = new ModelLayerLocation(Changed.modResource("head"), "main");
    private final ModelPart head;
    private final ColorFunction<T> scleraColorFn;
    private final ColorFunction<T> irisColorFn;
    private final ColorFunction<T> eyeBrowsColorFn;

    public static <T extends LatexEntity> Color3 noRender(T entity, BasicPlayerInfo bpi) {
        return null;
    }

    public static <T extends LatexEntity> Color3 irisColor(T entity, BasicPlayerInfo bpi) {
        return bpi.getIrisColor();
    }

    public static <T extends LatexEntity> Color3 scleraColor(T entity, BasicPlayerInfo bpi) {
        return bpi.getScleraColor();
    }

    public static <T extends LatexEntity> Color3 hairColor(T entity, BasicPlayerInfo bpi) {
        return bpi.getHairColor();
    }

    public static <T extends LatexEntity> ColorFunction<T> fixedColor(Color3 color) {
        return (entity, bpi) -> color;
    }

    public static <T extends LatexEntity> ColorFunction<T> fixedIfNotDarkLatexOverride(Color3 color) {
        return (entity, bpi) -> bpi.isOverrideIrisOnDarkLatex() ? bpi.getIrisColor() : color;
    }

    public CustomEyesLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        this(parent, modelSet, CustomEyesLayer::scleraColor, CustomEyesLayer::irisColor, CustomEyesLayer::noRender);
    }

    public CustomEyesLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet, ColorFunction<T> scleraColorFn, ColorFunction<T> irisColorFn) {
        this(parent, modelSet, scleraColorFn, irisColorFn, CustomEyesLayer::noRender);
    }

    public CustomEyesLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet,
                           ColorFunction<T> scleraColorFn, ColorFunction<T> irisColorFn, ColorFunction<T> eyeBrowsColorFn) {
        super(parent);
        this.head = modelSet.bakeLayer(HEAD);
        this.scleraColorFn = scleraColorFn;
        this.irisColorFn = irisColorFn;
        this.eyeBrowsColorFn = eyeBrowsColorFn;
    }

    public static LayerDefinition createHead() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.ZERO);

        return LayerDefinition.create(mesh, 32, 32);
    }

    private void renderHead(PoseStack pose, VertexConsumer buffer, int packedLight, int overlay, Color3 color, float alpha) {
        head.render(pose, buffer, packedLight, overlay, color.red(), color.green(), color.blue(), alpha);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        var info = entity.getBasicPlayerInfo();
        var style = info.getEyeStyle();

        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);

        head.copyFrom(this.getParentModel().getHead());

        scleraColorFn.getColorSafe(entity, info).ifPresent(color -> {
            renderHead(pose, bufferSource.getBuffer(RenderType.entityCutout(style.getSclera())), packedLight, overlay, color, 1.0F);
        });
        irisColorFn.getColorSafe(entity, info).ifPresent(color -> {
            renderHead(pose, bufferSource.getBuffer(RenderType.entityCutout(style.getIris())), packedLight, overlay, color, 1.0F);
        });
        eyeBrowsColorFn.getColorSafe(entity, info).ifPresent(color -> {
            renderHead(pose, bufferSource.getBuffer(RenderType.entityCutout(style.getEyeBrows())), packedLight, overlay, color, 1.0F);
        });
    }
}