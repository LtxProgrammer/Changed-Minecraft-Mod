package net.ltxprogrammer.changed.client;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class AbilityRenderer implements ResourceManagerReloadListener {
    public static final ResourceLocation ENCHANT_GLINT_LOCATION = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public static final Set<ResourceLocation> IGNORED = Sets.newHashSet(
            ChangedAbilities.SELECT_HAIRSTYLE.getId(),
            ChangedAbilities.SELECT_SPECIAL_STATE.getId()
    );
    public float blitOffset;
    private final AbilityModelShaper abilityModelShaper;
    private final TextureManager textureManager;
    private final AbilityColors abilityColors;

    public AbilityRenderer(TextureManager textureManager, ModelManager modelManager, AbilityColors abilityColors) {
        this.textureManager = textureManager;
        this.abilityModelShaper = new AbilityModelShaper(modelManager);

        for(AbstractAbility<?> ability : ChangedRegistry.ABILITY.get().getValues()) {
            if (!IGNORED.contains(ability.delegate.name())) {
                this.abilityModelShaper.register(ability, new ModelResourceLocation(ability.delegate.name(), "ability"));
            }
        }

        this.abilityColors = abilityColors;
    }

    public AbilityModelShaper getAbilityModelShaper() {
        return this.abilityModelShaper;
    }

    public void renderModelLists(BakedModel model, AbstractAbilityInstance abilityInstance, int packedLight, int packedOverlay, PoseStack poseStack, VertexConsumer buffer) {
        Random random = new Random();
        long seed = 42L;

        for(Direction direction : Direction.values()) {
            random.setSeed(seed);
            this.renderQuadList(poseStack, buffer, model.getQuads(null, direction, random), abilityInstance, packedLight, packedOverlay);
        }

        random.setSeed(seed);
        this.renderQuadList(poseStack, buffer, model.getQuads(null, null, random), abilityInstance, packedLight, packedOverlay);
    }

    public void render(AbstractAbilityInstance abilityInstance, ItemTransforms.TransformType transformType, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, BakedModel model) {
        poseStack.pushPose();

        model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(poseStack, model, transformType, leftHand);
        poseStack.translate(-0.5D, -0.5D, -0.5D);
        if (!model.isCustomRenderer()) {
            if (model.isLayered()) {
                //net.minecraftforge.client.ForgeHooksClient.drawItemLayered(this, model, abilityInstance, poseStack, bufferSource, packedLight, packedOverlay, flag1);
            } else {
                RenderType rendertype = Sheets.translucentItemSheet();//ItemBlockRenderTypes.getRenderType(abilityInstance, flag1);
                VertexConsumer buffer = getFoilBufferDirect(bufferSource, rendertype, true, abilityInstance.hasFoil());

                this.renderModelLists(model, abilityInstance, packedLight, packedOverlay, poseStack, buffer);
            }
        } else {
            //net.minecraftforge.client.RenderProperties.get(abilityInstance).getItemStackRenderer().renderByItem(abilityInstance, transformType, poseStack, bufferSource, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    public static VertexConsumer getArmorFoilBuffer(MultiBufferSource bufferSource, RenderType renderType, boolean p_115187_, boolean p_115188_) {
        return p_115188_ ? VertexMultiConsumer.create(bufferSource.getBuffer(p_115187_ ? RenderType.armorGlint() : RenderType.armorEntityGlint()), bufferSource.getBuffer(renderType)) : bufferSource.getBuffer(renderType);
    }

    public static VertexConsumer getCompassFoilBuffer(MultiBufferSource bufferSource, RenderType renderType, PoseStack.Pose pose) {
        return VertexMultiConsumer.create(new SheetedDecalTextureGenerator(bufferSource.getBuffer(RenderType.glint()), pose.pose(), pose.normal()), bufferSource.getBuffer(renderType));
    }

    public static VertexConsumer getCompassFoilBufferDirect(MultiBufferSource bufferSource, RenderType renderType, PoseStack.Pose pose) {
        return VertexMultiConsumer.create(new SheetedDecalTextureGenerator(bufferSource.getBuffer(RenderType.glintDirect()), pose.pose(), pose.normal()), bufferSource.getBuffer(renderType));
    }

    public static VertexConsumer getFoilBuffer(MultiBufferSource bufferSource, RenderType renderType, boolean p_115214_, boolean p_115215_) {
        if (p_115215_) {
            return Minecraft.useShaderTransparency() && renderType == Sheets.translucentItemSheet() ? VertexMultiConsumer.create(bufferSource.getBuffer(RenderType.glintTranslucent()), bufferSource.getBuffer(renderType)) : VertexMultiConsumer.create(bufferSource.getBuffer(p_115214_ ? RenderType.glint() : RenderType.entityGlint()), bufferSource.getBuffer(renderType));
        } else {
            return bufferSource.getBuffer(renderType);
        }
    }

    public static VertexConsumer getFoilBufferDirect(MultiBufferSource bufferSource, RenderType renderType, boolean p_115225_, boolean p_115226_) {
        return p_115226_ ? VertexMultiConsumer.create(bufferSource.getBuffer(p_115225_ ? RenderType.glintDirect() : RenderType.entityGlintDirect()), bufferSource.getBuffer(renderType)) : bufferSource.getBuffer(renderType);
    }

    public void renderQuadList(PoseStack poseStack, VertexConsumer buffer, List<BakedQuad> quads, AbstractAbilityInstance abilityInstance, int packedLight, int packedOverlay) {
        PoseStack.Pose pose = poseStack.last();

        Int2ObjectMap<Optional<Integer>> cachedColors = new Int2ObjectOpenHashMap<>(4);

        for(BakedQuad quad : quads) {
            int color = AbilityColors.DEFAULT;
            if (quad.isTinted()) {
                var tinted = cachedColors.computeIfAbsent(quad.getTintIndex(),
                        index -> this.abilityColors.getColor(abilityInstance, index));
                if (tinted.isEmpty())
                    continue;

                color = tinted.get();
            }

            float red = (float)(color >> 16 & 255) / 255.0F;
            float green = (float)(color >> 8 & 255) / 255.0F;
            float blue = (float)(color & 255) / 255.0F;
            buffer.putBulkData(pose, quad, red, green, blue, packedLight, packedOverlay, true);
        }

    }

    public BakedModel getModel(AbstractAbilityInstance abilityInstance, @Nullable Level level, @Nullable LivingEntity entity, int id) {
        BakedModel model = this.abilityModelShaper.getAbilityModel(abilityInstance);

        /*ClientLevel clientlevel = level instanceof ClientLevel ? (ClientLevel)level : null;
        BakedModel override = model.getOverrides().resolve(model, abilityInstance, clientlevel, entity, id);
        return override == null ? this.abilityModelShaper.getModelManager().getMissingModel() : model;*/

        return model;
    }

    public void renderStatic(AbstractAbilityInstance abilityInstance, ItemTransforms.TransformType transformType, int packedLight, int packedOverlay, PoseStack poseStack, MultiBufferSource bufferSource, int id) {
        this.renderStatic(null, abilityInstance, transformType, false, poseStack, bufferSource, null, packedLight, packedOverlay, id);
    }

    public void renderStatic(@Nullable LivingEntity entity, AbstractAbilityInstance abilityInstance, ItemTransforms.TransformType transformType, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, @Nullable Level level, int packedLight, int packedOverlay, int id) {
        BakedModel model = this.getModel(abilityInstance, level, entity, id);
        this.render(abilityInstance, transformType, leftHand, poseStack, bufferSource, packedLight, packedOverlay, model);
    }

    public void renderGuiAbility(AbstractAbilityInstance abilityInstance, int x, int y) {
        this.renderGuiAbility(abilityInstance, x, y, 16,1.0f, false, this.getModel(abilityInstance, null, null, 0));
    }

    public void renderGuiAbility(AbstractAbilityInstance abilityInstance, int x, int y, int scale) {
        this.renderGuiAbility(abilityInstance, x, y, scale, 1.0f, false, this.getModel(abilityInstance, null, null, 0));
    }

    public void renderGuiAbility(AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha) {
        this.renderGuiAbility(abilityInstance, x, y, scale, alpha, false, this.getModel(abilityInstance, null, null, 0));
    }

    public void renderGuiAbility(AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow) {
        this.renderGuiAbility(abilityInstance, x, y, scale, alpha, shadow, this.getModel(abilityInstance, null, null, 0));
    }

    protected void renderGuiAbility(AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow, BakedModel model) {
        this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.translate((double)x, (double)y, (double)(100.0F + this.blitOffset));
        modelViewStack.translate(scale * 0.5D, scale * 0.5D, 0.0D);
        PoseStack poseStack = new PoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flatLight = !model.usesBlockLight();
        if (flatLight) {
            Lighting.setupForFlatItems();
        }

        if (shadow) {
            modelViewStack.pushPose();

            modelViewStack.translate(4.0D, 4.0D, -10.0D);
            modelViewStack.scale(1.0F, -1.0F, 1.0F);
            modelViewStack.scale(scale, scale, scale);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, alpha * 0.5F);
            this.render(abilityInstance, ItemTransforms.TransformType.GUI, false, poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, model);

            modelViewStack.popPose();
        }

        modelViewStack.scale(1.0F, -1.0F, 1.0F);
        modelViewStack.scale(scale, scale, scale);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        this.render(abilityInstance, ItemTransforms.TransformType.GUI, false, poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, model);

        bufferSource.endBatch();
        RenderSystem.enableDepthTest();
        if (flatLight) {
            Lighting.setupFor3DItems();
        }

        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public void renderAndDecorateAbility(AbstractAbilityInstance abilityInstance, int x, int y) {
        this.tryRenderGuiAbility(Minecraft.getInstance().player, abilityInstance, x, y, 16, 1.0f, false, 0);
    }

    public void renderAndDecorateAbility(AbstractAbilityInstance abilityInstance, int x, int y, int scale) {
        this.tryRenderGuiAbility(Minecraft.getInstance().player, abilityInstance, x, y, scale, 1.0f, false, 0);
    }

    public void renderAndDecorateAbility(AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha) {
        this.tryRenderGuiAbility(Minecraft.getInstance().player, abilityInstance, x, y, scale, 1.0f, false, 0);
    }

    public void renderAndDecorateAbility(AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow) {
        this.tryRenderGuiAbility(Minecraft.getInstance().player, abilityInstance, x, y, scale, alpha, shadow, 0);
    }

    public void renderAndDecorateAbility(AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow, int id) {
        this.tryRenderGuiAbility(Minecraft.getInstance().player, abilityInstance, x, y, scale, alpha, shadow, id);
    }

    public void renderAndDecorateAbility(AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow, int id, int zOffset) {
        this.tryRenderGuiAbility(Minecraft.getInstance().player, abilityInstance, x, y, scale, alpha, shadow, id, zOffset);
    }

    public void renderAndDecorateFakeAbility(AbstractAbilityInstance abilityInstance, int x, int y) {
        this.tryRenderGuiAbility(null, abilityInstance, x, y, 16, 1.0f, false, 0);
    }

    public void renderAndDecorateFakeAbility(AbstractAbilityInstance abilityInstance, int x, int y, int scale) {
        this.tryRenderGuiAbility(null, abilityInstance, x, y, scale, 1.0f, false, 0);
    }

    public void renderAndDecorateFakeAbility(AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha) {
        this.tryRenderGuiAbility(null, abilityInstance, x, y, scale, alpha, false, 0);
    }

    public void renderAndDecorateFakeAbility(AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow) {
        this.tryRenderGuiAbility(null, abilityInstance, x, y, scale, alpha, shadow, 0);
    }

    public void renderAndDecorateAbility(LivingEntity entity, AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow, int id) {
        this.tryRenderGuiAbility(entity, abilityInstance, x, y, scale, alpha, shadow, id);
    }

    private void tryRenderGuiAbility(@Nullable LivingEntity entity, AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow, int id) {
        this.tryRenderGuiAbility(entity, abilityInstance, x, y, scale, alpha, shadow, id, 0);
    }

    private void tryRenderGuiAbility(@Nullable LivingEntity entity, AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow, int id, int zOffset) {
        BakedModel model = this.getModel(abilityInstance, null, entity, id);
        this.blitOffset = model.isGui3d() ? this.blitOffset + 50.0F + (float)zOffset : this.blitOffset + 50.0F;

        try {
            this.renderGuiAbility(abilityInstance, x, y, scale, alpha, shadow, model);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering ability");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Ability being rendered");
            crashreportcategory.setDetail("Ability Type", () -> {
                return String.valueOf(abilityInstance.getAbility());
            });
            crashreportcategory.setDetail("Registry Name", () -> String.valueOf(abilityInstance.getAbility().getRegistryName()));
            crashreportcategory.setDetail("Ability NBT", () -> {
                CompoundTag tag = new CompoundTag();
                abilityInstance.saveData(tag);
                return String.valueOf(tag);
            });
            crashreportcategory.setDetail("Ability Foil", () -> {
                return String.valueOf(abilityInstance.hasFoil());
            });
            throw new ReportedException(crashreport);
        }

        this.blitOffset = model.isGui3d() ? this.blitOffset - 50.0F - (float)zOffset : this.blitOffset - 50.0F;
    }

    public void renderGuiAbilityDecorations(Font font, AbstractAbilityInstance abilityInstance, int x, int y) {
        this.renderGuiAbilityDecorations(font, abilityInstance, x, y, null);
    }

    public void renderGuiAbilityDecorations(Font font, AbstractAbilityInstance abilityInstance, int x, int y, @Nullable String text) {
        PoseStack posestack = new PoseStack();
        if (text != null) {
            posestack.translate(0.0D, 0.0D, (double)(this.blitOffset + 200.0F));
            MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            font.drawInBatch(text, (float)(x + 19 - 2 - font.width(text)), (float)(y + 6 + 3), 16777215, true, posestack.last().pose(), multibuffersource$buffersource, false, 0, 15728880);
            multibuffersource$buffersource.endBatch();
        }
    }

    private void fillRect(BufferBuilder builder, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        builder.vertex((double)(x + 0), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        builder.vertex((double)(x + 0), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        builder.vertex((double)(x + width), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
        builder.vertex((double)(x + width), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
        builder.end();
        BufferUploader.end(builder);
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager manager) {
        this.abilityModelShaper.rebuildCache();
    }
}
