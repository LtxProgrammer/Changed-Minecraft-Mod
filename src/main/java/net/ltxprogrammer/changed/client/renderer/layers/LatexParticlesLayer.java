package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.client.CubeExtender;
import net.ltxprogrammer.changed.client.PoseStackExtender;
import net.ltxprogrammer.changed.client.latexparticles.LatexDripParticle;
import net.ltxprogrammer.changed.client.latexparticles.SurfacePoint;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.data.MixedTexture;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LatexParticlesLayer<T extends LatexEntity, M extends LatexHumanoidModel<T>> extends RenderLayer<T, M> {
    private final RenderLayerParent<T, M> parent;
    private final M model;
    private final Minecraft minecraft;
    private final @Nullable ResourceLocation alternateTexture;

    private static final NativeImage MISSING_TEXTURE = new NativeImage(1, 1, false);
    private static final Map<ResourceLocation, NativeImage> cachedTextures = new HashMap<>();

    public LatexParticlesLayer(RenderLayerParent<T, M> parent, M model) {
        super(parent);
        this.parent = parent;
        this.model = model;
        this.minecraft = Minecraft.getInstance();
        this.alternateTexture = null;
    }

    public LatexParticlesLayer(RenderLayerParent<T, M> parent, M model, LatexTranslucentLayer<T, M> translucentLayer) {
        super(parent);
        this.parent = parent;
        this.model = model;
        this.minecraft = Minecraft.getInstance();
        this.alternateTexture = translucentLayer.getTexture();
    }

    public static void purgeTextureCache() {
        cachedTextures.values().forEach(NativeImage::close);
        cachedTextures.clear();
    }

    public NativeImage getTexture(T entity) {
        return getTexture(parent.getTextureLocation(entity));
    }

    public NativeImage getTexture(ResourceLocation name) {
        return cachedTextures.computeIfAbsent(name, resourceLocation -> {
            Resource resource = null;

            try {
                resource = minecraft.getResourceManager().getResource(resourceLocation);
                return NativeImage.read(resource.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (resource != null) {
                    try {
                        resource.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return MISSING_TEXTURE;
        });
    }

    public SurfacePoint findSurface(ModelPart part, LatexEntity entity) {
        var cube = part.getRandomCube(entity.level.random);
        var normal = new Vector3f(0, 0, 0);
        var tangent = new Vector3f(0, 0, 0);
        var vector = new Vector3f(entity.level.random.nextFloat(), entity.level.random.nextFloat(), entity.level.random.nextFloat());
        vector.normalize();
        var spherePoint = vector.copy();
        var cubePoint = vector.copy();
        var cubeMin = ((CubeExtender)cube).getVisualMin();
        var cubeMax = ((CubeExtender)cube).getVisualMax();
        var cubeSize = cubeMax.copy();
        cubeSize.sub(cubeMin);
        cubeSize.mul(1.0f / 16.0f);
        vector.mul(cubeSize.x(), cubeSize.y(), cubeSize.z());
        // vector is now currently on the surface of a sphere sized as the cube, need to bring one element to the nearest surface

        if (Math.abs(spherePoint.x()) > Math.abs(spherePoint.y()) && Math.abs(spherePoint.x()) > Math.abs(spherePoint.z())) {
            vector.setX(Math.signum(vector.x()) * cubeSize.x()); // X is the largest value
            normal.setX(Math.signum(vector.x()));
            cubePoint.setX(normal.x());
            tangent.setY(-1.0f);
        } else if (Math.abs(spherePoint.y()) > Math.abs(spherePoint.x()) && Math.abs(spherePoint.y()) > Math.abs(spherePoint.z())) {
            vector.setY(Math.signum(vector.y()) * cubeSize.y()); // Y is the largest value
            normal.setY(Math.signum(vector.y()));
            cubePoint.setY(Math.signum(vector.y()));
            tangent.setX(1.0f);
        } else {
            vector.setZ(Math.signum(vector.z()) * cubeSize.z()); // Z is the largest value
            normal.setZ(Math.signum(vector.z()));
            cubePoint.setZ(Math.signum(vector.z()));
            tangent.setY(-1.0f);
        }

        UVPair uv = ((CubeExtender)cube).getUV(cubePoint);
        vector.add(cubeMin.x() / 16.0f, cubeMin.y() / 16.0f, cubeMin.z() / 16.0f);
        return new SurfacePoint(normal, tangent, vector, uv);
    }

    public void createNewDripParticle(LatexEntity entity) {
        var partsWithCubes = model.getAllParts().filter(part -> !part.getLeaf().cubes.isEmpty()).toList();
        if (partsWithCubes.isEmpty())
            return;

        var partToAttach = partsWithCubes.get(entity.level.random.nextInt(partsWithCubes.size()));
        var surface = findSurface(partToAttach.getLeaf(), entity);

        Color3 color;
        float alpha = 1.0f;

        var image = getTexture((T)entity);

        if (image != MISSING_TEXTURE) {
            var rgba = MixedTexture.sampleNearest(image, null, surface.uv().u(), surface.uv().v());
            color = new Color3(rgba.r(), rgba.g(), rgba.b());
            alpha = rgba.a();
            if (alpha < 0.00001f && alternateTexture != null) {
                var altImage = getTexture(alternateTexture);

                if (altImage != MISSING_TEXTURE) {
                    rgba = MixedTexture.sampleNearest(altImage, null, surface.uv().u(), surface.uv().v());
                    color = new Color3(rgba.r(), rgba.g(), rgba.b());
                    alpha = rgba.a();
                }

                else {
                    color = entity.getDripColor();
                }
            }
        } else {
            color = entity.getDripColor();
        }

        ChangedClient.particleSystem.addParticle(LatexDripParticle.of(entity, this.model, partToAttach, surface, color, alpha, 100));
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float f1, float f2, float partialTicks, float bobAmount, float f3, float f4) {
        ChangedClient.particleSystem.getAllParticlesForEntity(entity).forEach(particle -> {
            particle.setupForRender(pose, partialTicks);
            particle.renderFromLayer(bufferSource, partialTicks);
        });
    }
}
