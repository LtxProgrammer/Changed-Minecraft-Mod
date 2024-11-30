package net.ltxprogrammer.changed.extension.chiselsandbits;

import mod.chiselsandbits.client.model.baked.chiseled.ChiselRenderType;
import mod.chiselsandbits.client.model.baked.chiseled.VoxelType;
import net.ltxprogrammer.changed.client.ChangedShaders;
import net.minecraft.client.renderer.RenderType;

import java.lang.reflect.Method;

public abstract class ChangedChiselRenderTypes {
    private static final Method ctor;
    private static ChiselRenderType create(String name, RenderType type, VoxelType voxelType) {
        try {
            return (ChiselRenderType) ctor.invoke(null, name, type, voxelType);
        } catch (Exception ex) {
            return null;
        }
    }

    static {
        Method tmp = null;
        try {
            tmp = ChiselRenderType.class.getDeclaredMethod("create", String.class, RenderType.class, VoxelType.class);
            tmp.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        ctor = tmp;
    }

    public static final ChiselRenderType LATEX_SOLID = create("LATEX_SOLID", ChangedShaders.latexSolid(), VoxelType.SOLID);
    public static final ChiselRenderType LATEX_SOLID_FLUID = create("LATEX_SOLID_FLUID", ChangedShaders.latexSolid(), VoxelType.FLUID);
    public static final ChiselRenderType LATEX_CUTOUT = create("LATEX_CUTOUT", ChangedShaders.latexCutout(), VoxelType.UNKNOWN);
    public static final ChiselRenderType LATEX_CUTOUT_MIPPED = create("LATEX_CUTOUT_MIPPED", ChangedShaders.latexCutoutMipped(), VoxelType.UNKNOWN);
}
