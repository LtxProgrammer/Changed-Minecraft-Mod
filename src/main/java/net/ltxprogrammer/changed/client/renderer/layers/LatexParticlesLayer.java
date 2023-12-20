package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.client.CubeExtender;
import net.ltxprogrammer.changed.client.latexparticles.LatexDripParticle;
import net.ltxprogrammer.changed.client.latexparticles.SurfacePoint;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;

public class LatexParticlesLayer<T extends LatexEntity, M extends LatexHumanoidModel<T>> extends RenderLayer<T, M> {
    private final M model;

    public LatexParticlesLayer(RenderLayerParent<T, M> parent, M model) {
        super(parent);
        this.model = model;
    }

    public SurfacePoint findSurface(ModelPart part, LatexEntity entity) {
        var cube = part.getRandomCube(entity.level.random);
        var normal = new Vector3f(0, 0, 0);
        var tangent = new Vector3f(0, 0, 0);
        var vector = new Vector3f(entity.level.random.nextFloat(), entity.level.random.nextFloat(), entity.level.random.nextFloat());
        vector.normalize();
        var spherePoint = vector.copy();
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
            tangent.setY(-1.0f);
        } else if (Math.abs(spherePoint.y()) > Math.abs(spherePoint.x()) && Math.abs(spherePoint.y()) > Math.abs(spherePoint.z())) {
            vector.setY(Math.signum(vector.y()) * cubeSize.y()); // Y is the largest value
            normal.setY(Math.signum(vector.y()));
            tangent.setX(1.0f);
        } else {
            vector.setZ(Math.signum(vector.z()) * cubeSize.z()); // Z is the largest value
            normal.setZ(Math.signum(vector.z()));
            tangent.setY(-1.0f);
        }

        vector.add(cubeMin.x() / 16.0f, cubeMin.y() / 16.0f, cubeMin.z() / 16.0f);
        return new SurfacePoint(normal, tangent, vector);
    }

    public void createNewDripParticle(LatexEntity entity) {
        var partsWithCubes = model.getAllParts().filter(part -> !part.getLeaf().cubes.isEmpty()).toList();
        if (partsWithCubes.isEmpty())
            return;

        var partToAttach = partsWithCubes.get(entity.level.random.nextInt(partsWithCubes.size()));
        ChangedClient.particleSystem.addParticle(LatexDripParticle.of(entity, partToAttach, findSurface(partToAttach.getLeaf(), entity), entity.getDripColor().add(0.05f),
                1.0f, 100));
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float f1, float f2, float partialTicks, float bobAmount, float f3, float f4) {
        ChangedClient.particleSystem.getAllParticlesForEntity(entity).forEach(particle -> {
            particle.setupForRender(pose, partialTicks);
        });
    }
}
