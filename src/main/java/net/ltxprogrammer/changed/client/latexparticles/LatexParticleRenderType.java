package net.ltxprogrammer.changed.client.latexparticles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public abstract class LatexParticleRenderType {
    public static final ResourceLocation LOCATION_PARTICLES = Changed.modResource("textures/atlas/latex_particles.png");

    public static ParticleRenderType LATEX_PARTICLE_SHEET_OPAQUE = new ParticleRenderType() {
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, LOCATION_PARTICLES);
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        public void end(Tesselator tesselator) {
            tesselator.end();
        }

        public String toString() {
            return "LATEX_PARTICLE_SHEET_OPAQUE";
        }
    };
    public static ParticleRenderType LATEX_PARTICLE_SHEET_3D_OPAQUE = new ParticleRenderType() {
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutShader);
            RenderSystem.setShaderTexture(0, LOCATION_PARTICLES);
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
        }

        public void end(Tesselator tesselator) {
            tesselator.end();
        }

        public String toString() {
            return "LATEX_PARTICLE_SHEET_3D_OPAQUE";
        }
    };
}
