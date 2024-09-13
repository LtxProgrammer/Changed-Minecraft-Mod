package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModelInterface;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
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
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;

public class CustomEyesLayer<M extends AdvancedHumanoidModel<T>, T extends ChangedEntity> extends RenderLayer<T, M> {
    public enum HeadShape implements StringRepresentable {
        NORMAL("normal", 0, 0, -4, -8, -4, 8, 8, 8, new CubeDeformation(0.0025f)),
        PUP("pup", 2, 2, -4, -4, -6, 8, 8, 6, new CubeDeformation(0.0025f));
        final String serializedName;
        final int texX, texY;
        final int x, y, z, width, height, depth;
        final CubeDeformation deformation;

        HeadShape(String serializedName, int texX, int texY, int x, int y, int z, int width, int height, int depth) {
            this(serializedName, texX, texY, x, y, z, width, height, depth, CubeDeformation.NONE);
        }

        HeadShape(String serializedName, int texX, int texY, int x, int y, int z, int width, int height, int depth, CubeDeformation deformation) {
            this.serializedName = serializedName;
            this.texX = texX;
            this.texY = texY;
            this.x = x;
            this.y = y;
            this.z = z;
            this.width = width;
            this.height = height;
            this.depth = depth;
            this.deformation = deformation;
        }

        CubeListBuilder create() {
            return CubeListBuilder.create().texOffs(texX, texY).addBox(x, y, z, width, height, depth, deformation);
        }

        @Override
        public @NotNull String getSerializedName() {
            return serializedName;
        }
    }

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

    public interface ColorFunction<T extends ChangedEntity> extends BiFunction<T, BasicPlayerInfo, ColorData> {
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
    private final Map<HeadShape, ModelPart> shapedHeads;
    private final ColorFunction<T> scleraColorFn;
    private final ColorFunction<T> irisColorLeftFn;
    private final ColorFunction<T> irisColorRightFn;
    private final ColorFunction<T> eyeBrowsColorFn;
    private final ColorFunction<T> eyeLashesColorFn;

    private HeadShape headShape = HeadShape.NORMAL;

    public CustomEyesLayer<M, T> setHeadShape(HeadShape shape) {
        headShape = shape;
        return this;
    }

    public static <T extends ChangedEntity> ColorData noRender(T entity, BasicPlayerInfo bpi) {
        return null;
    }

    public static <T extends ChangedEntity> ColorData irisColorLeft(T entity, BasicPlayerInfo bpi) {
        return ColorData.ofColor(bpi.getLeftIrisColor());
    }

    public static <T extends ChangedEntity> ColorData irisColorRight(T entity, BasicPlayerInfo bpi) {
        return ColorData.ofColor(bpi.getRightIrisColor());
    }

    public static <T extends ChangedEntity> ColorData glowingIrisColorLeft(T entity, BasicPlayerInfo bpi) {
        return ColorData.ofEmissiveColor(bpi.getLeftIrisColor());
    }

    public static <T extends ChangedEntity> ColorData glowingIrisColorRight(T entity, BasicPlayerInfo bpi) {
        return ColorData.ofEmissiveColor(bpi.getRightIrisColor());
    }

    public static <T extends ChangedEntity> ColorFunction<T> translucentIrisColorLeft(float alpha) {
        return (entity, bpi) -> ColorData.ofTranslucentColor(bpi.getLeftIrisColor(), alpha);
    }

    public static <T extends ChangedEntity> ColorFunction<T> translucentIrisColorRight(float alpha) {
        return (entity, bpi) -> ColorData.ofTranslucentColor(bpi.getRightIrisColor(), alpha);
    }

    public static <T extends ChangedEntity> ColorData scleraColor(T entity, BasicPlayerInfo bpi) {
        return ColorData.ofColor(bpi.getScleraColor());
    }

    public static <T extends ChangedEntity> ColorData hairColor(T entity, BasicPlayerInfo bpi) {
        return ColorData.ofColor(bpi.getHairColor());
    }

    public static <T extends ChangedEntity> ColorFunction<T> fixedColor(Color3 color) {
        return (entity, bpi) -> ColorData.ofColor(color);
    }

    public static <T extends ChangedEntity> ColorFunction<T> fixedColorGlowing(Color3 color) {
        return (entity, bpi) -> ColorData.ofEmissiveColor(color);
    }

    public static <T extends ChangedEntity> ColorFunction<T> fixedColor(Color3 color, float alpha) {
        return (entity, bpi) -> ColorData.ofTranslucentColor(color, alpha);
    }

    public static <T extends ChangedEntity> ColorFunction<T> fixedIfNotDarkLatexOverrideLeft(Color3 color) {
        return (entity, bpi) -> ColorData.ofColor(bpi.isOverrideIrisOnDarkLatex() ? bpi.getLeftIrisColor() : color);
    }

    public static <T extends ChangedEntity> ColorFunction<T> fixedIfNotDarkLatexOverrideRight(Color3 color) {
        return (entity, bpi) -> ColorData.ofColor(bpi.isOverrideIrisOnDarkLatex() ? bpi.getRightIrisColor() : color);
    }

    public CustomEyesLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        this(parent, modelSet, CustomEyesLayer::scleraColor, CustomEyesLayer::irisColorLeft, CustomEyesLayer::irisColorRight, CustomEyesLayer::noRender);
    }

    @Deprecated
    public CustomEyesLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet, ColorFunction<T> scleraColorFn, ColorFunction<T> irisColorDualFn) {
        this(parent, modelSet, scleraColorFn, irisColorDualFn, irisColorDualFn);
    }

    @Deprecated
    public CustomEyesLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet, ColorFunction<T> scleraColorFn, ColorFunction<T> irisColorLeftFn, ColorFunction<T> irisColorRightFn) {
        this(parent, modelSet, scleraColorFn, irisColorLeftFn, irisColorRightFn, CustomEyesLayer::noRender);
    }

    @Deprecated
    public CustomEyesLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet, ColorFunction<T> scleraColorFn, ColorFunction<T> irisColorLeftFn, ColorFunction<T> irisColorRightFn, ColorFunction<T> eyeBrowsColorFn) {
        this(parent, modelSet, scleraColorFn, irisColorLeftFn, irisColorRightFn, eyeBrowsColorFn, CustomEyesLayer::noRender);
    }

    public CustomEyesLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet,
                           ColorFunction<T> scleraColorFn, ColorFunction<T> irisColorLeftFn, ColorFunction<T> irisColorRightFn, ColorFunction<T> eyeBrowsColorFn, ColorFunction<T> eyeLashesColorFn) {
        super(parent);
        var root = modelSet.bakeLayer(HEAD);
        this.shapedHeads = new EnumMap<>(HeadShape.class);
        Arrays.stream(HeadShape.values()).forEach(shape -> {
            this.shapedHeads.put(shape, root.getChild(shape.getSerializedName()));
        });
        this.scleraColorFn = scleraColorFn;
        this.irisColorLeftFn = irisColorLeftFn;
        this.irisColorRightFn = irisColorRightFn;
        this.eyeBrowsColorFn = eyeBrowsColorFn;
        this.eyeLashesColorFn = eyeLashesColorFn;
    }

    public static LayerDefinition createHead() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        Arrays.stream(HeadShape.values()).forEach(shape -> {
            root.addOrReplaceChild(shape.getSerializedName(), shape.create(), PartPose.ZERO);
        });

        return LayerDefinition.create(mesh, 32, 32);
    }

    private void renderHead(PoseStack pose, VertexConsumer buffer, int packedLight, int overlay, Color3 color, float alpha) {
        this.shapedHeads.get(headShape).render(pose, buffer, packedLight, overlay, color.red(), color.green(), color.blue(), alpha);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible())
            return;
        if (ChangedCompatibility.isFirstPersonRendering())
            return;

        BasicPlayerInfo info = new BasicPlayerInfo();
        info.copyFrom(entity.getBasicPlayerInfo());
        if (Changed.config.client.basicPlayerInfo.isOverrideOthersToMatchStyle())
            info.setEyeStyle(Changed.config.client.basicPlayerInfo.getEyeStyle());

        var style = info.getEyeStyle();

        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);

        this.shapedHeads.get(headShape).copyFrom(this.getParentModel().getHead());

        pose.pushPose();
        if (this.getParentModel() instanceof AdvancedHumanoidModelInterface<?,?> modelInterface)
            modelInterface.scaleForHead(pose);

        scleraColorFn.getColorSafe(entity, info).ifPresent(data -> {
            renderHead(pose, bufferSource.getBuffer(data.getRenderType(style.getSclera())), packedLight, overlay, data.color, data.alpha);
        });
        irisColorLeftFn.getColorSafe(entity, info).ifPresent(data -> {
            renderHead(pose, bufferSource.getBuffer(data.getRenderType(style.getLeftIris())), packedLight, overlay, data.color, data.alpha);
        });
        irisColorRightFn.getColorSafe(entity, info).ifPresent(data -> {
            renderHead(pose, bufferSource.getBuffer(data.getRenderType(style.getRightIris())), packedLight, overlay, data.color, data.alpha);
        });
        eyeBrowsColorFn.getColorSafe(entity, info).ifPresent(data -> {
            renderHead(pose, bufferSource.getBuffer(data.getRenderType(style.getEyeBrows())), packedLight, overlay, data.color, data.alpha);
        });
        eyeLashesColorFn.getColorSafe(entity, info).ifPresent(data -> {
            renderHead(pose, bufferSource.getBuffer(data.getRenderType(style.getEyeLashes())), packedLight, overlay, data.color, data.alpha);
        });

        pose.popPose();
    }

    public static class Builder<M extends AdvancedHumanoidModel<T>, T extends ChangedEntity> {
        private final RenderLayerParent<T, M> parent;
        private final EntityModelSet modelSet;

        private ColorFunction<T> scleraColorFn;
        private ColorFunction<T> irisColorLeftFn;
        private ColorFunction<T> irisColorRightFn;
        private ColorFunction<T> eyeBrowsColorFn;
        private ColorFunction<T> eyeLashesColorFn;

        public Builder(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
            this.parent = parent;
            this.modelSet = modelSet;

            this.scleraColorFn = CustomEyesLayer::scleraColor;
            this.irisColorLeftFn = CustomEyesLayer::irisColorLeft;
            this.irisColorRightFn = CustomEyesLayer::irisColorRight;
            this.eyeBrowsColorFn = CustomEyesLayer::noRender;
            this.eyeLashesColorFn = CustomEyesLayer::noRender;
        }

        public Builder<M, T> withSclera(Color3 fixedColor) {
            this.scleraColorFn = fixedColor(fixedColor);
            return this;
        }

        public Builder<M, T> withSclera(ColorFunction<T> colorFn) {
            this.scleraColorFn = colorFn;
            return this;
        }

        public Builder<M, T> withIris(Color3 dualFixedColor) {
            this.irisColorLeftFn = fixedColor(dualFixedColor);
            this.irisColorRightFn = this.irisColorLeftFn;
            return this;
        }

        public Builder<M, T> withIris(ColorFunction<T> dualColorFn) {
            this.irisColorLeftFn = dualColorFn;
            this.irisColorRightFn = dualColorFn;
            return this;
        }

        public Builder<M, T> withIris(Color3 leftFixedColor, Color3 rightFixedColor) {
            this.irisColorLeftFn = fixedColor(leftFixedColor);
            this.irisColorRightFn = fixedColor(rightFixedColor);
            return this;
        }

        public Builder<M, T> withIris(ColorFunction<T> leftColorFn, ColorFunction<T> rightColorFn) {
            this.irisColorLeftFn = leftColorFn;
            this.irisColorRightFn = rightColorFn;
            return this;
        }

        public Builder<M, T> withLeftIris(Color3 fixedColor) {
            this.irisColorLeftFn = fixedColor(fixedColor);
            return this;
        }

        public Builder<M, T> withLeftIris(ColorFunction<T> colorFn) {
            this.irisColorLeftFn = colorFn;
            return this;
        }

        public Builder<M, T> withRightIris(Color3 fixedColor) {
            this.irisColorRightFn = fixedColor(fixedColor);
            return this;
        }

        public Builder<M, T> withRightIris(ColorFunction<T> colorFn) {
            this.irisColorRightFn = colorFn;
            return this;
        }

        public Builder<M, T> withEyebrows(Color3 fixedColor) {
            this.eyeBrowsColorFn = fixedColor(fixedColor);
            return this;
        }

        public Builder<M, T> withEyebrows(ColorFunction<T> colorFn) {
            this.eyeBrowsColorFn = colorFn;
            return this;
        }

        public Builder<M, T> withEyelashes(Color3 fixedColor) {
            this.eyeLashesColorFn = fixedColor(fixedColor);
            return this;
        }

        public Builder<M, T> withEyelashes(ColorFunction<T> colorFn) {
            this.eyeLashesColorFn = colorFn;
            return this;
        }

        public CustomEyesLayer<M, T> build() {
            return new CustomEyesLayer<>(parent, modelSet,
                    scleraColorFn,
                    irisColorLeftFn,
                    irisColorRightFn,
                    eyeBrowsColorFn,
                    eyeLashesColorFn);
        }
    }

    public static <M extends AdvancedHumanoidModel<T>, T extends ChangedEntity> Builder<M, T> builder(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        return new Builder<>(parent, modelSet);
    }
}