package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModelInterface;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.EyeStyle;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer.fixedColor;

public class AdditionalEyesLayer<M extends AdvancedHumanoidModel<T>, T extends ChangedEntity> extends RenderLayer<T, M> {
    public static final ModelLayerLocation HEAD = CustomEyesLayer.HEAD;
    private final Map<CustomEyesLayer.HeadShape, ModelPart> shapedHeads;
    private final ResourceLocation additionalEyesId;
    private final CustomEyesLayer.ColorFunction<T> scleraColorFn;
    private final CustomEyesLayer.ColorFunction<T> irisColorLeftFn;
    private final CustomEyesLayer.ColorFunction<T> irisColorRightFn;
    private final Map<EyeStyle, ResourceLocation> scleraTextures = new HashMap<>();
    private final Map<EyeStyle, ResourceLocation> leftIrisTextures = new HashMap<>();
    private final Map<EyeStyle, ResourceLocation> rightIrisTextures = new HashMap<>();

    private CustomEyesLayer.HeadShape headShape = CustomEyesLayer.HeadShape.NORMAL;

    private ResourceLocation getEyesTexture(EyeStyle style, String part) {
        return new ResourceLocation(additionalEyesId.getNamespace(), "textures/eyes/" + additionalEyesId.getPath() + "/" + style.getId().getNamespace() + "/" + style.getId().getPath() + "_" + part + ".png");
    }

    private ResourceLocation getScleraTexture(EyeStyle style) {
        return getEyesTexture(style, "sclera");
    }

    private ResourceLocation getLeftIrisTexture(EyeStyle style) {
        return getEyesTexture(style, "iris_left");
    }

    private ResourceLocation getRightIrisTexture(EyeStyle style) {
        return getEyesTexture(style, "iris_right");
    }

    public AdditionalEyesLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet, ResourceLocation additionalEyesId) {
        this(parent, modelSet, additionalEyesId, CustomEyesLayer::scleraColor, CustomEyesLayer::irisColorLeft, CustomEyesLayer::irisColorRight);
    }

    public AdditionalEyesLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet, ResourceLocation additionalEyesId, CustomEyesLayer.ColorFunction<T> scleraColorFn, CustomEyesLayer.ColorFunction<T> irisColorLeftFn, CustomEyesLayer.ColorFunction<T> irisColorRightFn) {
        super(parent);
        var root = modelSet.bakeLayer(HEAD);
        this.shapedHeads = new EnumMap<>(CustomEyesLayer.HeadShape.class);
        Arrays.stream(CustomEyesLayer.HeadShape.values()).forEach(shape -> {
            this.shapedHeads.put(shape, root.getChild(shape.getSerializedName()));
        });
        this.scleraColorFn = scleraColorFn;
        this.irisColorLeftFn = irisColorLeftFn;
        this.irisColorRightFn = irisColorRightFn;
        this.additionalEyesId = additionalEyesId;
    }

    public AdditionalEyesLayer<M, T> setHeadShape(CustomEyesLayer.HeadShape shape) {
        headShape = shape;
        return this;
    }

    private void renderHead(PoseStack pose, VertexConsumer buffer, int packedLight, int overlay, Color3 color, float alpha) {
        this.shapedHeads.get(headShape).render(pose, buffer, packedLight, overlay, color.red(), color.green(), color.blue(), alpha);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible())
            return;

        BasicPlayerInfo info = new BasicPlayerInfo();
        info.copyFrom(entity.getBasicPlayerInfo());
        if (Changed.config.client.basicPlayerInfo.isOverrideOthersToMatchStyle())
            info.setEyeStyle(Changed.config.client.basicPlayerInfo.getEyeStyle());

        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);

        this.shapedHeads.get(headShape).copyFrom(this.getParentModel().getHead());

        float zFightOffset = CustomEyesLayer.getZFightingOffset(entity);

        pose.pushPose();
        if (this.getParentModel() instanceof AdvancedHumanoidModelInterface<?,?> modelInterface)
            modelInterface.scaleForHead(pose);

        pose.scale(zFightOffset + 1.0f, zFightOffset + 1.0f, zFightOffset + 1.0f);
        scleraColorFn.getColorSafe(entity, info).ifPresent(data -> {
            renderHead(pose, bufferSource.getBuffer(data.getRenderType(scleraTextures.computeIfAbsent(info.getEyeStyle(), this::getScleraTexture))), packedLight, overlay, data.color, data.alpha);
        });
        irisColorLeftFn.getColorSafe(entity, info).ifPresent(data -> {
            renderHead(pose, bufferSource.getBuffer(data.getRenderType(leftIrisTextures.computeIfAbsent(info.getEyeStyle(), this::getLeftIrisTexture))), packedLight, overlay, data.color, data.alpha);
        });
        irisColorRightFn.getColorSafe(entity, info).ifPresent(data -> {
            renderHead(pose, bufferSource.getBuffer(data.getRenderType(rightIrisTextures.computeIfAbsent(info.getEyeStyle(), this::getRightIrisTexture))), packedLight, overlay, data.color, data.alpha);
        });

        pose.popPose();
    }

    public static class Builder<M extends AdvancedHumanoidModel<T>, T extends ChangedEntity> {
        private final RenderLayerParent<T, M> parent;
        private final EntityModelSet modelSet;

        private CustomEyesLayer.ColorFunction<T> scleraColorFn;
        private CustomEyesLayer.ColorFunction<T> irisColorLeftFn;
        private CustomEyesLayer.ColorFunction<T> irisColorRightFn;

        public Builder(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
            this.parent = parent;
            this.modelSet = modelSet;

            this.scleraColorFn = CustomEyesLayer::scleraColor;
            this.irisColorLeftFn = CustomEyesLayer::irisColorLeft;
            this.irisColorRightFn = CustomEyesLayer::irisColorRight;
        }

        public Builder<M, T> withSclera(Color3 fixedColor) {
            this.scleraColorFn = fixedColor(fixedColor);
            return this;
        }

        public Builder<M, T> withSclera(CustomEyesLayer.ColorFunction<T> colorFn) {
            this.scleraColorFn = colorFn;
            return this;
        }

        public Builder<M, T> withIris(Color3 dualFixedColor) {
            this.irisColorLeftFn = fixedColor(dualFixedColor);
            this.irisColorRightFn = this.irisColorLeftFn;
            return this;
        }

        public Builder<M, T> withIris(CustomEyesLayer.ColorFunction<T> dualColorFn) {
            this.irisColorLeftFn = dualColorFn;
            this.irisColorRightFn = dualColorFn;
            return this;
        }

        public Builder<M, T> withIris(Color3 leftFixedColor, Color3 rightFixedColor) {
            this.irisColorLeftFn = fixedColor(leftFixedColor);
            this.irisColorRightFn = fixedColor(rightFixedColor);
            return this;
        }

        public Builder<M, T> withIris(CustomEyesLayer.ColorFunction<T> leftColorFn, CustomEyesLayer.ColorFunction<T> rightColorFn) {
            this.irisColorLeftFn = leftColorFn;
            this.irisColorRightFn = rightColorFn;
            return this;
        }

        public Builder<M, T> withLeftIris(Color3 fixedColor) {
            this.irisColorLeftFn = fixedColor(fixedColor);
            return this;
        }

        public Builder<M, T> withLeftIris(CustomEyesLayer.ColorFunction<T> colorFn) {
            this.irisColorLeftFn = colorFn;
            return this;
        }

        public Builder<M, T> withRightIris(Color3 fixedColor) {
            this.irisColorRightFn = fixedColor(fixedColor);
            return this;
        }

        public Builder<M, T> withRightIris(CustomEyesLayer.ColorFunction<T> colorFn) {
            this.irisColorRightFn = colorFn;
            return this;
        }

        public AdditionalEyesLayer<M, T> build(ResourceLocation additionalEyesId) {
            return new AdditionalEyesLayer<>(parent, modelSet, additionalEyesId,
                    scleraColorFn,
                    irisColorLeftFn,
                    irisColorRightFn);
        }
    }

    public static <M extends AdvancedHumanoidModel<T>, T extends ChangedEntity> Builder<M, T> builder(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        return new Builder<>(parent, modelSet);
    }
}