package net.ltxprogrammer.changed.client.latexparticles;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.logging.LogUtils;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleDescription;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class LatexParticleEngine implements PreparableReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation PARTICLES_ATLAS_INFO = Changed.modResource("latex_particles");
    private static final FileToIdConverter PARTICLE_LISTER = FileToIdConverter.json("latex_particles");

    private final Minecraft minecraft;
    private final TextureManager textureManager;
    private final Map<ResourceLocation, MutableSpriteSet> spriteSets = Maps.newHashMap();
    private final TextureAtlas textureAtlas;

    private final Map<ParticleRenderType, List<LatexParticle>> particles = new HashMap<>();
    private boolean isReloading = false;

    public LatexParticleEngine(Minecraft minecraft) {
        this.minecraft = minecraft;
        this.textureManager = minecraft.textureManager;
        this.textureAtlas = new TextureAtlas(LatexParticleRenderType.LOCATION_PARTICLES);
        textureManager.register(LatexParticleRenderType.LOCATION_PARTICLES, textureAtlas);
    }

    public int countParticles() {
        return this.particles.values().stream().mapToInt(Collection::size).sum();
    }

    public int getMaxParticles() {
        return switch (minecraft.options.particles().get()) {
            case ALL -> 8000;
            case DECREASED -> 2000;
            case MINIMAL -> 500;
        };
    }

    public void addParticle(LatexParticleProvider<? extends LatexParticle> particleProvider) {
        if (countParticles() >= getMaxParticles())
            return;
        var particle = particleProvider.create(this.spriteSets.get(ChangedRegistry.LATEX_PARTICLE_TYPE.getKey(particleProvider.getParticleType())));
        particles.computeIfAbsent(particle.getRenderType(), type -> new ArrayList<>()).add(particle);
    }

    private long lastLevelTick = -1;
    public boolean tick() {
        if (this.minecraft.level == null) {
            particles.clear();
            return false;
        }

        if (pauseForReload())
            return false;

        if (this.minecraft.level.getGameTime() == lastLevelTick)
            return false;
        lastLevelTick = this.minecraft.level.getGameTime();
        // Only proceed with particles tick if the level ticked

        for (var particleSet : particles.values())
            for (var particle : particleSet)
                particle.tick();

        particles.values().forEach(particleSet -> {
            particleSet.removeIf(LatexParticle::shouldExpire);
        });

        return true;
    }

    public void purgeParticles() {
        particles.clear();
    }

    public List<LatexParticle> getAllParticlesForEntity(Entity entity) {
        List<LatexParticle> result = new ArrayList<>();

        for (var particleSet : particles.values())
            for (var particle : particleSet)
                if (particle.isForEntity(entity))
                    result.add(particle);

        return result;
    }

    public void render(PoseStack poseStack, LightTexture lightTexture, Camera camera, float partialTicks, @Nullable Frustum clippingHelper, SetupContext context) {
        lightTexture.turnOnLightLayer();
        RenderSystem.enableDepthTest();
        RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE2);
        RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE0);
        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.mulPoseMatrix(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();

        for(ParticleRenderType particlerendertype : this.particles.keySet()) {
            if (particlerendertype == ParticleRenderType.NO_RENDER) continue;
            var particleSet = this.particles.get(particlerendertype);
            if (particleSet != null) {
                RenderSystem.setShader(GameRenderer::getParticleShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tesselator.getBuilder();
                particlerendertype.begin(bufferbuilder, this.textureManager);

                for(var particle : particleSet) {
                    if (particle.getRenderType() != particlerendertype) continue;
                    if (clippingHelper != null && particle.shouldCull() && !clippingHelper.isVisible(particle.getBoundingBox())) continue;
                    try {
                        particle.renderFromEvent(bufferbuilder, camera, partialTicks, context);
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering Latex Particle");
                        CrashReportCategory crashreportcategory = crashreport.addCategory("Latex Particle being rendered");
                        crashreportcategory.setDetail("LatexParticle", particle::toString);
                        crashreportcategory.setDetail("LatexParticle Type", particlerendertype::toString);
                        throw new ReportedException(crashreport);
                    }
                }

                particlerendertype.end(tesselator);
            }
        }

        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        lightTexture.turnOffLightLayer();
    }

    private void clearParticles() {
        this.particles.clear();
    }

    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier prepBarrier, ResourceManager resourceManager,
                                          ProfilerFiller profilerA, ProfilerFiller profilerB, Executor execA, Executor execB) {
        @OnlyIn(Dist.CLIENT)
        record ParticleDefinition(ResourceLocation id, Optional<List<ResourceLocation>> sprites) { }
        CompletableFuture<List<ParticleDefinition>> completablefuture = CompletableFuture.supplyAsync(() -> {
            return PARTICLE_LISTER.listMatchingResources(resourceManager);
        }, execA).thenCompose((resources) -> {
            List<CompletableFuture<ParticleDefinition>> list = new ArrayList<>(resources.size());
            resources.forEach((p_247903_, resource) -> {
                ResourceLocation resourcelocation = PARTICLE_LISTER.fileToId(p_247903_);
                list.add(CompletableFuture.supplyAsync(() -> {
                    this.spriteSets.put(resourcelocation, new MutableSpriteSet());
                    return new ParticleDefinition(resourcelocation, this.loadParticleDescription(resourcelocation, resource));
                }, execA));
            });
            return Util.sequence(list);
        });
        CompletableFuture<SpriteLoader.Preparations> completablefuture1 = SpriteLoader.create(this.textureAtlas).loadAndStitch(resourceManager, PARTICLES_ATLAS_INFO, 0, execA).thenCompose(SpriteLoader.Preparations::waitForUpload);

        return CompletableFuture.allOf(completablefuture1, completablefuture).thenCompose(prepBarrier::wait).thenAcceptAsync((p_247900_) -> {
            this.clearParticles();
            profilerB.startTick();
            profilerB.push("upload");
            SpriteLoader.Preparations spriteloader$preparations = completablefuture1.join();
            this.textureAtlas.upload(spriteloader$preparations);
            profilerB.popPush("bindSpriteSets");
            Set<ResourceLocation> set = new HashSet<>();
            TextureAtlasSprite textureatlassprite = spriteloader$preparations.missing();
            completablefuture.join().forEach((definition) -> {
                Optional<List<ResourceLocation>> optional = definition.sprites();
                if (!optional.isEmpty()) {
                    List<TextureAtlasSprite> list = new ArrayList<>();

                    for(ResourceLocation resourcelocation : optional.get()) {
                        TextureAtlasSprite textureatlassprite1 = spriteloader$preparations.regions().get(resourcelocation);
                        if (textureatlassprite1 == null) {
                            set.add(resourcelocation);
                            list.add(textureatlassprite);
                        } else {
                            list.add(textureatlassprite1);
                        }
                    }

                    if (list.isEmpty()) {
                        list.add(textureatlassprite);
                    }

                    this.spriteSets.get(definition.id()).rebind(list);
                }
            });
            if (!set.isEmpty()) {
                LOGGER.warn("Missing particle sprites: {}", set.stream().sorted().map(ResourceLocation::toString).collect(Collectors.joining(",")));
            }

            profilerB.pop();
            profilerB.endTick();
        }, execB);
    }

    private Optional<List<ResourceLocation>> loadParticleDescription(ResourceLocation location, Resource resource) {
        if (!this.spriteSets.containsKey(location)) {
            LOGGER.debug("Redundant texture list for particle: {}", (Object)location);
            return Optional.empty();
        } else {
            try (Reader reader = resource.openAsReader()) {
                ParticleDescription particledescription = ParticleDescription.fromJson(GsonHelper.parse(reader));
                return Optional.of(particledescription.getTextures());
            } catch (IOException ioexception) {
                throw new IllegalStateException("Failed to load description for particle " + location, ioexception);
            }
        }
    }

    public void close() {
        this.textureAtlas.clearTextureData();
    }

    public boolean pauseForReload() {
        return isReloading;
    }

    @OnlyIn(Dist.CLIENT)
    static class MutableSpriteSet implements SpriteSet {
        private List<TextureAtlasSprite> sprites;

        public TextureAtlasSprite get(int index, int maxIndex) {
            return this.sprites.get(index * (this.sprites.size() - 1) / maxIndex);
        }

        public TextureAtlasSprite get(RandomSource random) {
            return this.sprites.get(random.nextInt(this.sprites.size()));
        }

        public void rebind(List<TextureAtlasSprite> sprites) {
            this.sprites = ImmutableList.copyOf(sprites);
        }
    }
}
