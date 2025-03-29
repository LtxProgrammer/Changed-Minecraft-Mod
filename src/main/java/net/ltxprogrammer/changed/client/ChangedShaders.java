package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ChangedShaders {
    public static class PrefixedShaderShard extends RenderStateShard.ShaderStateShard {
        private final Consumer<ShaderInstance> onBindShader;

        public PrefixedShaderShard(Supplier<ShaderInstance> shader, Consumer<ShaderInstance> onBindShader) {
            super(shader);
            this.onBindShader = onBindShader;
        }

        @Override
        public void setupRenderState() {
            super.setupRenderState();
            onBindShader.accept(RenderSystem.getShader());
        }
    }

    public static Optional<Uniform> getUniform(ShaderInstance shader, String name) {
        return Optional.ofNullable(shader.getUniform(name));
    }

    @Nullable
    private static ShaderInstance rendertypeWaveVisionEntity;
    @Nullable
    private static ShaderInstance rendertypeWaveVisionSolid;
    @Nullable
    private static ShaderInstance rendertypeWaveVisionCutout;
    @Nullable
    private static ShaderInstance rendertypeWaveVisionCutoutMipped;
    @Nullable
    private static ShaderInstance rendertypeWaveVisionResonantEntity;

    public static ShaderInstance getWaveVisionEntityShader() {
        return Objects.requireNonNull(rendertypeWaveVisionEntity, "Attempted to call getWaveVisionEntityShader before shaders have finished loading.");
    }

    public static ShaderInstance getWaveVisionSolidShader() {
        return Objects.requireNonNull(rendertypeWaveVisionSolid, "Attempted to call getWaveVisionSolidShader before shaders have finished loading.");
    }

    public static ShaderInstance getWaveVisionCutoutShader() {
        return Objects.requireNonNull(rendertypeWaveVisionCutout, "Attempted to call getWaveVisionCutoutShader before shaders have finished loading.");
    }

    public static ShaderInstance getWaveVisionCutoutMippedShader() {
        return Objects.requireNonNull(rendertypeWaveVisionCutoutMipped, "Attempted to call getWaveVisionCutoutMippedShader before shaders have finished loading.");
    }

    public static ShaderInstance getWaveVisionResonantEntityShader() {
        return Objects.requireNonNull(rendertypeWaveVisionResonantEntity, "Attempted to call getWaveVisionResonantEntityShader before shaders have finished loading.");
    }

    public static RenderType waveVisionEntity(ResourceLocation shader) {
        return WAVE_VISION.apply(shader);
    }

    public static RenderType waveVisionEntityResonant(ResourceLocation shader, Vector3f resonance) {
        return WAVE_VISION_RESONANT.apply(shader, resonance);
    }

    public static RenderType waveVisionSolid() {
        return WAVE_VISION_SOLID.apply(BLOCK_SHEET_MIPPED);
    }

    public static RenderType waveVisionCutout() {
        return WAVE_VISION_CUTOUT.apply(BLOCK_SHEET);
    }

    public static RenderType waveVisionCutoutMipped() {
        return WAVE_VISION_CUTOUT_MIPPED.apply(BLOCK_SHEET_MIPPED);
    }

    public static RenderType waveVisionResonantSolid(Vector3f resonance) {
        return WAVE_VISION_RESONANT_SOLID.apply(BLOCK_SHEET_MIPPED_RESONANT, resonance);
    }

    public static RenderType waveVisionResonantCutout(Vector3f resonance) {
        return WAVE_VISION_RESONANT_CUTOUT.apply(BLOCK_SHEET_RESONANT, resonance);
    }

    public static RenderType waveVisionResonantCutoutMipped(Vector3f resonance) {
        return WAVE_VISION_RESONANT_CUTOUT_MIPPED.apply(BLOCK_SHEET_MIPPED_RESONANT, resonance);
    }

    public static RenderType latexWaveVisionSolid() {
        return WAVE_VISION_SOLID.apply(LATEX_SHEET_MIPPED);
    }

    public static RenderType latexWaveVisionCutout() {
        return WAVE_VISION_CUTOUT.apply(LATEX_SHEET);
    }

    public static RenderType latexWaveVisionCutoutMipped() {
        return WAVE_VISION_CUTOUT_MIPPED.apply(LATEX_SHEET_MIPPED);
    }

    public static void reloadShaders(ResourceManager resourceManager, Consumer<Pair<ShaderInstance, Consumer<ShaderInstance>>> loader) throws IOException {
        loader.accept(Pair.of(new ShaderInstance(resourceManager, Changed.modResource("rendertype_wave_vision_entity"), DefaultVertexFormat.NEW_ENTITY), (instance) -> {
            rendertypeWaveVisionEntity = instance;
        }));
        loader.accept(Pair.of(new ShaderInstance(resourceManager, Changed.modResource("rendertype_wave_vision_resonant_entity"), DefaultVertexFormat.NEW_ENTITY), (instance) -> {
            rendertypeWaveVisionResonantEntity = instance;
        }));
        loader.accept(Pair.of(new ShaderInstance(resourceManager, Changed.modResource("rendertype_wave_vision_solid"), DefaultVertexFormat.BLOCK), (instance) -> {
            rendertypeWaveVisionSolid = instance;
        }));
        loader.accept(Pair.of(new ShaderInstance(resourceManager, Changed.modResource("rendertype_wave_vision_cutout"), DefaultVertexFormat.BLOCK), (instance) -> {
            rendertypeWaveVisionCutout = instance;
        }));
        loader.accept(Pair.of(new ShaderInstance(resourceManager, Changed.modResource("rendertype_wave_vision_cutout_mipped"), DefaultVertexFormat.BLOCK), (instance) -> {
            rendertypeWaveVisionCutoutMipped = instance;
        }));
    }
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_WAVE_VISION_ENTITY_SHADER = new RenderStateShard.ShaderStateShard(ChangedShaders::getWaveVisionEntityShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_WAVE_VISION_SOLID_SHADER = new RenderStateShard.ShaderStateShard(ChangedShaders::getWaveVisionSolidShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_WAVE_VISION_CUTOUT_SHADER = new RenderStateShard.ShaderStateShard(ChangedShaders::getWaveVisionCutoutShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_WAVE_VISION_CUTOUT_MIPPED_SHADER = new RenderStateShard.ShaderStateShard(ChangedShaders::getWaveVisionCutoutMippedShader);

    protected static final Function<Vector3f, RenderStateShard.ShaderStateShard> RENDERTYPE_WAVE_VISION_RESONANT_ENTITY_SHADER = Util.memoize(resonance -> {
        return new PrefixedShaderShard(ChangedShaders::getWaveVisionResonantEntityShader,
                shader -> getUniform(shader, "WaveResonance").ifPresent(uniform -> uniform.set(resonance)));
    });
    protected static final Function<Vector3f, RenderStateShard.ShaderStateShard> RENDERTYPE_WAVE_VISION_RESONANT_SOLID_SHADER = Util.memoize(resonance -> {
        return new PrefixedShaderShard(ChangedShaders::getWaveVisionSolidShader,
                shader -> getUniform(shader, "WaveResonance").ifPresent(uniform -> uniform.set(resonance)));
    });
    protected static final Function<Vector3f, RenderStateShard.ShaderStateShard> RENDERTYPE_WAVE_VISION_RESONANT_CUTOUT_SHADER = Util.memoize(resonance -> {
        return new PrefixedShaderShard(ChangedShaders::getWaveVisionCutoutShader,
                shader -> getUniform(shader, "WaveResonance").ifPresent(uniform -> uniform.set(resonance)));
    });
    protected static final Function<Vector3f, RenderStateShard.ShaderStateShard> RENDERTYPE_WAVE_VISION_RESONANT_CUTOUT_MIPPED_SHADER = Util.memoize(resonance -> {
        return new PrefixedShaderShard(ChangedShaders::getWaveVisionCutoutMippedShader,
                shader -> getUniform(shader, "WaveResonance").ifPresent(uniform -> uniform.set(resonance)));
    });

    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_SOLID_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeSolidShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_CUTOUT_MIPPED_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeCutoutMippedShader);
    protected static final RenderStateShard.ShaderStateShard RENDERTYPE_CUTOUT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeCutoutShader);

    protected static final RenderStateShard.TransparencyStateShard NO_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("no_transparency", RenderSystem::disableBlend, () -> {});
    protected static final RenderStateShard.LightmapStateShard LIGHTMAP = new RenderStateShard.LightmapStateShard(true);
    protected static final RenderStateShard.LightmapStateShard NO_LIGHTMAP = new RenderStateShard.LightmapStateShard(false);
    protected static final RenderStateShard.OverlayStateShard OVERLAY = new RenderStateShard.OverlayStateShard(true);
    protected static final RenderStateShard.OverlayStateShard NO_OVERLAY = new RenderStateShard.OverlayStateShard(false);
    protected static final RenderStateShard.CullStateShard CULL = new RenderStateShard.CullStateShard(true);
    protected static final RenderStateShard.CullStateShard NO_CULL = new RenderStateShard.CullStateShard(false);

    public static final RenderStateShard.TextureStateShard BLOCK_SHEET_MIPPED = new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, true);
    public static final RenderStateShard.TextureStateShard BLOCK_SHEET = new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false);
    public static final RenderStateShard.TextureStateShard LATEX_SHEET_MIPPED = new RenderStateShard.TextureStateShard(LatexCoveredBlockRenderer.LATEX_COVER_ATLAS, false, true);
    public static final RenderStateShard.TextureStateShard LATEX_SHEET = new RenderStateShard.TextureStateShard(LatexCoveredBlockRenderer.LATEX_COVER_ATLAS, false, false);

    public static final RenderStateShard.MultiTextureStateShard BLOCK_SHEET_MIPPED_RESONANT = RenderStateShard.MultiTextureStateShard.builder().add(TextureAtlas.LOCATION_BLOCKS, false, true).add(WaveVisionRenderer.WAVE_RESONANCE_BLOCK_MASK, false, true).build();
    public static final RenderStateShard.MultiTextureStateShard BLOCK_SHEET_RESONANT = RenderStateShard.MultiTextureStateShard.builder().add(TextureAtlas.LOCATION_BLOCKS, false, false).add(WaveVisionRenderer.WAVE_RESONANCE_BLOCK_MASK, false, false).build();

    private static final RenderType LATEX_SOLID = RenderType.create("changed:latex_solid", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, false, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_SOLID_SHADER).setTextureState(LATEX_SHEET_MIPPED).createCompositeState(true));
    private static final RenderType LATEX_CUTOUT_MIPPED = RenderType.create("changed:latex_cutout_mipped", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 131072, true, false, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_CUTOUT_MIPPED_SHADER).setTextureState(LATEX_SHEET_MIPPED).createCompositeState(true));
    private static final RenderType LATEX_CUTOUT = RenderType.create("changed:latex_cutout", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 131072, true, false, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_CUTOUT_SHADER).setTextureState(LATEX_SHEET).createCompositeState(true));

    public static RenderType latexSolid() { return LATEX_SOLID; }
    public static RenderType latexCutoutMipped() { return LATEX_CUTOUT_MIPPED; }
    public static RenderType latexCutout() { return LATEX_CUTOUT; }

    private static final Function<ResourceLocation, RenderType> WAVE_VISION = Util.memoize((texture) -> {
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_WAVE_VISION_ENTITY_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(NO_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setCullState(NO_CULL)
                .createCompositeState(true);
        return RenderType.create(Changed.modResourceStr("wave_vision_entity"), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$compositestate);
    });

    private static final BiFunction<ResourceLocation, Vector3f, RenderType> WAVE_VISION_RESONANT = Util.memoize((texture, resonance) -> {
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_WAVE_VISION_RESONANT_ENTITY_SHADER.apply(resonance))
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(NO_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setCullState(NO_CULL)
                .createCompositeState(true);
        return RenderType.create(Changed.modResourceStr("wave_vision_resonant_entity"), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$compositestate);
    });

    private static final Function<RenderStateShard.EmptyTextureStateShard, RenderType> WAVE_VISION_SOLID = Util.memoize((textureStateShard) ->
            RenderType.create(Changed.modResourceStr("wave_vision_solid"), DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, false, RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(RENDERTYPE_WAVE_VISION_SOLID_SHADER)
                    .setTextureState(textureStateShard)
                    .createCompositeState(true)));
    private static final Function<RenderStateShard.EmptyTextureStateShard, RenderType> WAVE_VISION_CUTOUT_MIPPED = Util.memoize((textureStateShard) ->
            RenderType.create(Changed.modResourceStr("wave_vision_cutout_mipped"), DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, false, RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(RENDERTYPE_WAVE_VISION_CUTOUT_MIPPED_SHADER)
                    .setTextureState(textureStateShard)
                    .createCompositeState(true)));
    private static final Function<RenderStateShard.EmptyTextureStateShard, RenderType> WAVE_VISION_CUTOUT = Util.memoize((textureStateShard) ->
            RenderType.create(Changed.modResourceStr("wave_vision_cutout"), DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, false, RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(RENDERTYPE_WAVE_VISION_CUTOUT_SHADER)
                    .setTextureState(textureStateShard)
                    .createCompositeState(true)));

    private static final BiFunction<RenderStateShard.EmptyTextureStateShard, Vector3f, RenderType> WAVE_VISION_RESONANT_SOLID = Util.memoize((textureStateShard, resonance) ->
            RenderType.create(Changed.modResourceStr("wave_vision_resonant_solid"), DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, false, RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(RENDERTYPE_WAVE_VISION_RESONANT_SOLID_SHADER.apply(resonance))
                    .setTextureState(textureStateShard)
                    .createCompositeState(true)));
    private static final BiFunction<RenderStateShard.EmptyTextureStateShard, Vector3f, RenderType> WAVE_VISION_RESONANT_CUTOUT_MIPPED = Util.memoize((textureStateShard, resonance) ->
            RenderType.create(Changed.modResourceStr("wave_vision_resonant_cutout_mipped"), DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, false, RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(RENDERTYPE_WAVE_VISION_RESONANT_CUTOUT_MIPPED_SHADER.apply(resonance))
                    .setTextureState(textureStateShard)
                    .createCompositeState(true)));
    private static final BiFunction<RenderStateShard.EmptyTextureStateShard, Vector3f, RenderType> WAVE_VISION_RESONANT_CUTOUT = Util.memoize((textureStateShard, resonance) ->
            RenderType.create(Changed.modResourceStr("wave_vision_resonant_cutout"), DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, false, RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(RENDERTYPE_WAVE_VISION_RESONANT_CUTOUT_SHADER.apply(resonance))
                    .setTextureState(textureStateShard)
                    .createCompositeState(true)));
}
