package net.ltxprogrammer.changed.client;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.ModelEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AbilityRenderer implements ResourceManagerReloadListener {
    public static final ResourceLocation ENCHANT_GLINT_LOCATION = ResourceLocation.parse("textures/misc/enchanted_item_glint.png");
    public static final Set<ResourceLocation> IGNORED = Sets.newHashSet();
    public float blitOffset;
    private final AbilityModelShaper abilityModelShaper;
    private final TextureManager textureManager;
    private final AbilityColors abilityColors;

    public AbilityRenderer(TextureManager textureManager, ModelManager modelManager, AbilityColors abilityColors) {
        this.textureManager = textureManager;
        this.abilityModelShaper = new AbilityModelShaper(modelManager);

        for(ResourceLocation ability : ChangedRegistry.ABILITY.getKeys()) {
            if (!IGNORED.contains(ability)) {
                this.abilityModelShaper.register(ability, new ModelResourceLocation(ability, "ability"));
            }
        }

        this.abilityColors = abilityColors;
    }

    public AbilityModelShaper getAbilityModelShaper() {
        return this.abilityModelShaper;
    }

    public static RenderType getRenderType(AbstractAbilityInstance abilityInstance, boolean fabulous) {
        return fabulous ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet();
    }

    public static void onRegisterModels(ModelEvent.RegisterAdditional event) {
        for(ResourceLocation resourcelocation : ChangedRegistry.ABILITY.get().getKeys()) {
            if (AbilityRenderer.IGNORED.contains(resourcelocation))
                continue;

            ModelResourceLocation key = new ModelResourceLocation(resourcelocation, "ability");
            event.register(key);
        }
    }

    public void renderModelLists(BakedModel model, AbstractAbilityInstance abilityInstance, int packedLight, int packedOverlay, PoseStack poseStack, VertexConsumer buffer, float redMul, float greenMul, float blueMul, float alpha) {
        RandomSource random = RandomSource.create();
        long seed = 42L;

        for(Direction direction : Direction.values()) {
            random.setSeed(seed);
            this.renderQuadList(poseStack, buffer, model.getQuads(null, direction, random), abilityInstance, packedLight, packedOverlay, redMul, greenMul, blueMul, alpha);
        }

        random.setSeed(seed);
        this.renderQuadList(poseStack, buffer, model.getQuads(null, null, random), abilityInstance, packedLight, packedOverlay, redMul, greenMul, blueMul, alpha);
    }

    public void render(AbstractAbilityInstance abilityInstance, ItemDisplayContext transformType, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, BakedModel model, float redMul, float greenMul, float blueMul, float alpha) {
        poseStack.pushPose();

        model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(poseStack, model, transformType, leftHand);
        poseStack.translate(-0.5D, -0.5D, -0.5D);
        if (!model.isCustomRenderer()) {
            boolean fabulous = true;

            RenderType rendertype = getRenderType(abilityInstance, fabulous);
            VertexConsumer buffer;
            if (fabulous)
                buffer = ItemRenderer.getFoilBufferDirect(bufferSource, rendertype, true, abilityInstance.hasFoil());
            else {
                buffer = ItemRenderer.getFoilBuffer(bufferSource, rendertype, true, abilityInstance.hasFoil());
            }

            this.renderModelLists(model, abilityInstance, packedLight, packedOverlay, poseStack, buffer, redMul, greenMul, blueMul, alpha);
        } else {
            //net.minecraftforge.client.RenderProperties.get(abilityInstance).getItemStackRenderer().renderByItem(abilityInstance, transformType, poseStack, bufferSource, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    public static VertexConsumer getArmorFoilBuffer(MultiBufferSource bufferSource, RenderType renderType, boolean p_115187_, boolean p_115188_) {
        return p_115188_ ? VertexMultiConsumer.create(bufferSource.getBuffer(p_115187_ ? RenderType.armorGlint() : RenderType.armorEntityGlint()), bufferSource.getBuffer(renderType)) : bufferSource.getBuffer(renderType);
    }

    public void renderQuadList(PoseStack poseStack, VertexConsumer buffer, List<BakedQuad> quads, AbstractAbilityInstance abilityInstance, int packedLight, int packedOverlay, float redMul, float greenMul, float blueMul, float alpha) {
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
            buffer.putBulkData(pose, quad, red * redMul, green * greenMul, blue * blueMul, alpha, packedLight, packedOverlay, true);
        }

    }

    public BakedModel getModel(AbstractAbilityInstance abilityInstance, @Nullable Level level, @Nullable LivingEntity entity, int id) {
        BakedModel model = this.abilityModelShaper.getAbilityModel(abilityInstance);

        /*ClientLevel clientlevel = level instanceof ClientLevel ? (ClientLevel)level : null;
        BakedModel override = model.getOverrides().resolve(model, abilityInstance, clientlevel, entity, id);
        return override == null ? this.abilityModelShaper.getModelManager().getMissingModel() : model;*/

        return model;
    }

    public void renderStatic(AbstractAbilityInstance abilityInstance, ItemDisplayContext transformType, int packedLight, int packedOverlay, PoseStack poseStack, MultiBufferSource bufferSource, int id) {
        this.renderStatic(null, abilityInstance, transformType, false, poseStack, bufferSource, null, packedLight, packedOverlay, id);
    }

    public void renderStatic(@Nullable LivingEntity entity, AbstractAbilityInstance abilityInstance, ItemDisplayContext transformType, boolean leftHand, PoseStack poseStack, MultiBufferSource bufferSource, @Nullable Level level, int packedLight, int packedOverlay, int id) {
        BakedModel model = this.getModel(abilityInstance, level, entity, id);
        this.render(abilityInstance, transformType, leftHand, poseStack, bufferSource, packedLight, packedOverlay, model, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void renderGuiAbility(GuiGraphics graphics, AbstractAbilityInstance abilityInstance, int x, int y) {
        this.renderGuiAbility(graphics, abilityInstance, x, y, 16,1.0f, false, this.getModel(abilityInstance, null, null, 0));
    }

    public void renderGuiAbility(GuiGraphics graphics, AbstractAbilityInstance abilityInstance, int x, int y, int scale) {
        this.renderGuiAbility(graphics, abilityInstance, x, y, scale, 1.0f, false, this.getModel(abilityInstance, null, null, 0));
    }

    public void renderGuiAbility(GuiGraphics graphics, AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha) {
        this.renderGuiAbility(graphics, abilityInstance, x, y, scale, alpha, false, this.getModel(abilityInstance, null, null, 0));
    }

    public void renderGuiAbility(GuiGraphics graphics, AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow) {
        this.renderGuiAbility(graphics, abilityInstance, x, y, scale, alpha, shadow, this.getModel(abilityInstance, null, null, 0));
    }

    protected void renderGuiAbility(GuiGraphics graphics, AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow, BakedModel model) {
        this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.translate((double)x, (double)y, (double)(100.0F + this.blitOffset));
        modelViewStack.translate(scale * 0.5D, scale * 0.5D, 0.0D);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flatLight = !model.usesBlockLight();
        if (flatLight) {
            Lighting.setupForFlatItems();
        }

        if (shadow) {
            modelViewStack.pushPose();

            modelViewStack.translate(scale / 16.0D, scale / 16.0D, -10.0D);
            modelViewStack.scale(1.0F, -1.0F, 1.0F);
            modelViewStack.scale(scale, scale, scale);
            RenderSystem.applyModelViewMatrix();
            this.render(abilityInstance, ItemDisplayContext.GUI, false, graphics.pose(), bufferSource, 0, OverlayTexture.NO_OVERLAY, model, 0.0f, 0.0f, 0.0f, alpha * 0.5F);
            bufferSource.endBatch();

            modelViewStack.popPose();
        }

        modelViewStack.scale(1.0F, -1.0F, 1.0F);
        modelViewStack.scale(scale, scale, scale);
        RenderSystem.applyModelViewMatrix();
        graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        this.render(abilityInstance, ItemDisplayContext.GUI, false, graphics.pose(), bufferSource, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, model, 1.0f, 1.0f, 1.0f, alpha);

        bufferSource.endBatch();
        RenderSystem.enableDepthTest();
        if (flatLight) {
            Lighting.setupFor3DItems();
        }

        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public void renderAbility(GuiGraphics graphics, AbstractAbilityInstance abilityInstance, int x, int y) {
        this.tryRenderGuiAbility(graphics, Minecraft.getInstance().player, abilityInstance, x, y, 16, 1.0f, false, 0);
    }

    public void renderAbility(GuiGraphics graphics, AbstractAbilityInstance abilityInstance, int x, int y, int scale) {
        this.tryRenderGuiAbility(graphics, Minecraft.getInstance().player, abilityInstance, x, y, scale, 1.0f, false, 0);
    }

    public void renderAbility(GuiGraphics graphics, AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha) {
        this.tryRenderGuiAbility(graphics, Minecraft.getInstance().player, abilityInstance, x, y, scale, 1.0f, false, 0);
    }

    public void renderAbility(GuiGraphics graphics, AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow) {
        this.tryRenderGuiAbility(graphics, Minecraft.getInstance().player, abilityInstance, x, y, scale, alpha, shadow, 0);
    }

    public void renderAbility(GuiGraphics graphics, AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow, int id) {
        this.tryRenderGuiAbility(graphics, Minecraft.getInstance().player, abilityInstance, x, y, scale, alpha, shadow, id);
    }

    public void renderAbility(GuiGraphics graphics, AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow, int id, int zOffset) {
        this.tryRenderGuiAbility(graphics, Minecraft.getInstance().player, abilityInstance, x, y, scale, alpha, shadow, id, zOffset);
    }

    public void renderFakeAbility(GuiGraphics graphics, AbstractAbilityInstance abilityInstance, int x, int y) {
        this.tryRenderGuiAbility(graphics, null, abilityInstance, x, y, 16, 1.0f, false, 0);
    }

    public void renderFakeAbility(GuiGraphics graphics, AbstractAbilityInstance abilityInstance, int x, int y, int scale) {
        this.tryRenderGuiAbility(graphics, null, abilityInstance, x, y, scale, 1.0f, false, 0);
    }

    public void renderFakeAbility(GuiGraphics graphics, AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha) {
        this.tryRenderGuiAbility(graphics, null, abilityInstance, x, y, scale, alpha, false, 0);
    }

    public void renderFakeAbility(GuiGraphics graphics, AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow) {
        this.tryRenderGuiAbility(graphics, null, abilityInstance, x, y, scale, alpha, shadow, 0);
    }

    public void renderAbility(GuiGraphics graphics, LivingEntity entity, AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow, int id) {
        this.tryRenderGuiAbility(graphics, entity, abilityInstance, x, y, scale, alpha, shadow, id);
    }

    private void tryRenderGuiAbility(GuiGraphics graphics, @Nullable LivingEntity entity, AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow, int id) {
        this.tryRenderGuiAbility(graphics, entity, abilityInstance, x, y, scale, alpha, shadow, id, 0);
    }

    private void tryRenderGuiAbility(GuiGraphics graphics, @Nullable LivingEntity entity, AbstractAbilityInstance abilityInstance, int x, int y, int scale, float alpha, boolean shadow, int id, int zOffset) {
        BakedModel model = this.getModel(abilityInstance, null, entity, id);
        this.blitOffset = model.isGui3d() ? this.blitOffset + 50.0F + (float)zOffset : this.blitOffset + 50.0F;

        try {
            this.renderGuiAbility(graphics, abilityInstance, x, y, scale, alpha, shadow, model);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering ability");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Ability being rendered");
            crashreportcategory.setDetail("Ability Type", () -> {
                return String.valueOf(abilityInstance.getAbility());
            });
            crashreportcategory.setDetail("Registry Name", () -> String.valueOf(ChangedRegistry.ABILITY.getKey(abilityInstance.getAbility())));
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

    public void renderGuiAbilityDecorations(GuiGraphics graphics, Font font, AbstractAbilityInstance abilityInstance, int x, int y, int scale) {
        this.renderGuiAbilityDecorations(graphics, font, abilityInstance, x, y, scale, null);
    }

    public void renderGuiAbilityDecorations(GuiGraphics graphics, Font font, AbstractAbilityInstance abilityInstance, int x, int y, int scale, @Nullable String text) {
        Integer charges = abilityInstance.getCharges();
        if (charges != null || text != null) {
            String s = text == null ? String.valueOf(charges) : text;
            graphics.pose().pushPose();
            graphics.pose().translate(
                    scale - 16.0F,
                    scale - 16.0F,
                    200.0F);
            graphics.drawString(font, s, x + 19 - 2 - font.width(s), y + 6 + 3, 0xFFFFFF, true);
            graphics.pose().popPose();
        }
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager manager) {
        this.abilityModelShaper.rebuildCache();
    }
}
