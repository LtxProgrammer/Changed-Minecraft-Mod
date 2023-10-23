package net.ltxprogrammer.changed.client.latexparticles;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    private final Minecraft minecraft;
    private final TextureManager textureManager;
    private final Map<ResourceLocation, MutableSpriteSet> spriteSets = Maps.newHashMap();
    private final TextureAtlas textureAtlas;

    private final Map<ParticleRenderType, List<LatexParticle>> particles = new HashMap<>();

    public LatexParticleEngine(Minecraft minecraft) {
        this.minecraft = minecraft;
        this.textureManager = minecraft.textureManager;
        this.textureAtlas = new TextureAtlas(LatexParticleRenderType.LOCATION_PARTICLES);
        textureManager.register(LatexParticleRenderType.LOCATION_PARTICLES, textureAtlas);
    }

    public void addParticle(LatexParticleProvider<? extends LatexParticle> particleProvider) {
        // TODO use chance to limit active particles before ctor
        var particle = particleProvider.create(this.spriteSets.get(particleProvider.getParticleType().getRegistryName()));
        particles.computeIfAbsent(particle.getRenderType(), type -> new ArrayList<>()).add(particle);
    }

    public void tick() {
        for (var particleSet : particles.values())
            for (var particle : particleSet)
                particle.tick();

        particles.values().forEach(particleSet -> {
            particleSet.removeIf(LatexParticle::shouldExpire);
        });
    }

    public List<LatexParticle> getAllParticlesForEntity(Entity entity) {
        List<LatexParticle> result = new ArrayList<>();

        for (var particleSet : particles.values())
            for (var particle : particleSet)
                if (particle.isForEntity(entity))
                    result.add(particle);

        return result;
    }

    public void render(PoseStack poseStack, LightTexture lightTexture, Camera camera, float partialTicks, @Nullable Frustum clippingHelper) {
        lightTexture.turnOnLightLayer();
        RenderSystem.enableDepthTest();
        RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE2);
        RenderSystem.enableTexture();
        RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE0);
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.mulPoseMatrix(poseStack.last().pose());
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
                    if (clippingHelper != null && particle.shouldCull() && !clippingHelper.isVisible(particle.getBoundingBox())) continue;
                    try {
                        particle.render(bufferbuilder, camera, partialTicks);
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

        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        lightTexture.turnOffLightLayer();
    }

    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier prepBarrier, ResourceManager resourceManager,
                                          ProfilerFiller profilerA, ProfilerFiller profilerB, Executor execA, Executor execB) {
        Map<ResourceLocation, List<ResourceLocation>> map = Maps.newConcurrentMap();
        CompletableFuture<?>[] completablefuture = ChangedRegistry.LATEX_PARTICLE_TYPE.get().getKeys().stream().map((particleKey) -> {
            return CompletableFuture.runAsync(() -> {
                this.spriteSets.put(particleKey, new MutableSpriteSet());
                this.loadParticleDescription(resourceManager, particleKey, map);
            }, execA);
        }).toArray((p_107303_) -> {
            return new CompletableFuture[p_107303_];
        });

        return CompletableFuture.allOf(completablefuture).thenApplyAsync((p_107324_) -> {
            profilerA.startTick();
            profilerA.push("stitching");
            TextureAtlas.Preparations textureatlas$preparations = this.textureAtlas.prepareToStitch(resourceManager, map.values().stream().flatMap(Collection::stream), profilerA, 0);
            profilerA.pop();
            profilerA.endTick();
            return textureatlas$preparations;
        }, execA).thenCompose(prepBarrier::wait).thenAcceptAsync((p_107328_) -> {
            this.particles.clear();
            profilerB.startTick();
            profilerB.push("upload");
            this.textureAtlas.reload(p_107328_);
            profilerB.popPush("bindSpriteSets");
            TextureAtlasSprite sprite = this.textureAtlas.getSprite(MissingTextureAtlasSprite.getLocation());
            map.forEach((p_172268_, p_172269_) -> {
                ImmutableList<TextureAtlasSprite> immutablelist = p_172269_.isEmpty() ? ImmutableList.of(sprite) : p_172269_.stream().map(this.textureAtlas::getSprite).collect(ImmutableList.toImmutableList());
                this.spriteSets.get(p_172268_).rebind(immutablelist);
            });
            profilerB.pop();
            profilerB.endTick();
        }, execB);
    }

    private void loadParticleDescription(ResourceManager resourceManager, ResourceLocation registryName, Map<ResourceLocation, List<ResourceLocation>> spritesToLoad) {
        ResourceLocation resourcelocation = new ResourceLocation(registryName.getNamespace(), "particles/" + registryName.getPath() + ".json");

        try {
            Resource resource = resourceManager.getResource(resourcelocation);

            try {
                Reader reader = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8);

                try {
                    ParticleDescription particledescription = ParticleDescription.fromJson(GsonHelper.parse(reader));
                    List<ResourceLocation> list = particledescription.getTextures();
                    boolean flag = this.spriteSets.containsKey(registryName);
                    if (list == null) {
                        if (flag) {
                            throw new IllegalStateException("Missing texture list for particle " + registryName);
                        }
                    } else {
                        if (!flag) {
                            throw new IllegalStateException("Redundant texture list for particle " + registryName);
                        }

                        spritesToLoad.put(registryName, list.stream().map((p_107387_) -> {
                            return new ResourceLocation(p_107387_.getNamespace(), "particle/" + p_107387_.getPath());
                        }).collect(Collectors.toList()));
                    }
                } catch (Throwable throwable2) {
                    try {
                        reader.close();
                    } catch (Throwable throwable1) {
                        throwable2.addSuppressed(throwable1);
                    }

                    throw throwable2;
                }

                reader.close();
            } catch (Throwable throwable3) {
                if (resource != null) {
                    try {
                        resource.close();
                    } catch (Throwable throwable) {
                        throwable3.addSuppressed(throwable);
                    }
                }

                throw throwable3;
            }

            if (resource != null) {
                resource.close();
            }

        } catch (IOException ioexception) {
            throw new IllegalStateException("Failed to load description for particle " + registryName, ioexception);
        }
    }

    public void close() {
        this.textureAtlas.clearTextureData();
    }

    @OnlyIn(Dist.CLIENT)
    static class MutableSpriteSet implements SpriteSet {
        private List<TextureAtlasSprite> sprites;

        public TextureAtlasSprite get(int p_107413_, int p_107414_) {
            return this.sprites.get(p_107413_ * (this.sprites.size() - 1) / p_107414_);
        }

        public TextureAtlasSprite get(Random p_107418_) {
            return this.sprites.get(p_107418_.nextInt(this.sprites.size()));
        }

        public void rebind(List<TextureAtlasSprite> p_107416_) {
            this.sprites = ImmutableList.copyOf(p_107416_);
        }
    }
}
