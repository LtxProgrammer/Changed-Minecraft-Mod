package net.ltxprogrammer.changed.client.latexparticles;

import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.builders.UVPair;

public record SurfacePoint(Vector3f normal, Vector3f tangent, Vector3f position, UVPair uv) {
}
