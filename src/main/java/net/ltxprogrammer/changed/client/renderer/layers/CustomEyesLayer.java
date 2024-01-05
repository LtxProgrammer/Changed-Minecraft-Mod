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
    public static class ColorData {
        public final Color3 color;
        public final float alpha;
        public final boolean emissive;

        private ColorData(Color3 color, float alpha, boolean emissive) {
            this.color = color;
            this.alpha = alpha;
            this.emissive = emissive;
        }

        public static ColorData ofColor(Color3 color) {
            return new ColorData(color, 1.0f, false);
        }

        public static ColorData ofTranslucentColor(Color3 color, float alpha) {
            return new ColorData(color, alpha, false);
        }

        public static ColorData ofEmissiveColor(Color3 color) {
            return new ColorData(color, 1.0f, true);
        }

        public RenderType getRenderType(ResourceLocation texture) {
            return alpha < 1.0f ? RenderType.entityTranslucent(texture) : (emissive ? RenderType.eyes(texture) : RenderType.entityCutout(texture));
        }
    }

    public interface ColorFunction<T extends LatexEntity> extends BiFunction<T, BasicPlayerInfo, ColorData> {
        @Nullable
        ColorData getColor(T entity, BasicPlayerInfo bpi);

        @Override
        default ColorData apply(T entity, BasicPlayerInfo bpi) {
            return getColor(entity, bpi);
        }

        default Optional<ColorData> getColorSafe(T entity, BasicPlayerInfo bpi) {
            var safe = Optional.ofNullable(this.getColor(entity, bpi));
            if (safe.isPresent() && safe.get().alpha <= 0.0f)
                return Optional.empty();
            return safe;
        }
    }

    public static final ModelLayerLocation HEAD = new ModelLayerLocation(Changed.modResource("head"), "main");
    private final ModelPart head;
    private final ColorFunction<T> scleraColorFn;
    private final ColorFunction<T> irisColorFn;
    private final ColorFunction<T> eyeBrowsColorFn;

    public static <T extends LatexEntity> ColorData noRender(T entity, BasicPlayerInfo bpi) {
        return null;
    }

    public static <T extends LatexEntity> ColorData irisColor(T entity, BasicPlayerInfo bpi) {
        return ColorData.ofColor(bpi.getIrisColor());
    }

    public static <T extends LatexEntity> ColorData glowingIrisColor(T entity, BasicPlayerInfo bpi) {
        return ColorData.ofEmissiveColor(bpi.getIrisColor());
    }

    public static <T extends LatexEntity> ColorFunction<T> translucentIrisColor(float alpha) {
        return (entity, bpi) -> ColorData.ofTranslucentColor(bpi.getIrisColor(), alpha);
    }

    public static <T extends LatexEntity> ColorData scleraColor(T entity, BasicPlayerInfo bpi) {
        return ColorData.ofColor(bpi.getScleraColor());
    }

    public static <T extends LatexEntity> ColorData hairColor(T entity, BasicPlayerInfo bpi) {
        return ColorData.ofColor(bpi.getHairColor());
    }

    public static <T extends LatexEntity> ColorFunction<T> fixedColor(Color3 color) {
        return (entity, bpi) -> ColorData.ofColor(color);
    }

    public static <T extends LatexEntity> ColorFunction<T> fixedColorGlowing(Color3 color) {
        return (entity, bpi) -> ColorData.ofEmissiveColor(color);
    }

    public static <T extends LatexEntity> ColorFunction<T> fixedColor(Color3 color, float alpha) {
        return (entity, bpi) -> ColorData.ofTranslucentColor(color, alpha);
    }

    public static <T extends LatexEntity> ColorFunction<T> fixedIfNotDarkLatexOverride(Color3 color) {
        return (entity, bpi) -> ColorData.ofColor(bpi.isOverrideIrisOnDarkLatex() ? bpi.getIrisColor() : color);
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
        if (entity.isInvisible())
            return;

        var info = entity.getBasicPlayerInfo();
        var style = info.getEyeStyle();

        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);

        head.copyFrom(this.getParentModel().getHead());

        scleraColorFn.getColorSafe(entity, info).ifPresent(data -> {
            renderHead(pose, bufferSource.getBuffer(data.getRenderType(style.getSclera())), packedLight, overlay, data.color, data.alpha);
        });
        irisColorFn.getColorSafe(entity, info).ifPresent(data -> {
            renderHead(pose, bufferSource.getBuffer(data.getRenderType(style.getIris())), packedLight, overlay, data.color, data.alpha);
        });
        eyeBrowsColorFn.getColorSafe(entity, info).ifPresent(data -> {
            renderHead(pose, bufferSource.getBuffer(data.getRenderType(style.getEyeBrows())), packedLight, overlay, data.color, data.alpha);
        });
    }
}