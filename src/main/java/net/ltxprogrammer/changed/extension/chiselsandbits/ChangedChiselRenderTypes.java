package net.ltxprogrammer.changed.extension.chiselsandbits;

import mod.chiselsandbits.client.model.baked.chiseled.ChiselRenderType;
import mod.chiselsandbits.client.model.baked.chiseled.VoxelType;
import net.ltxprogrammer.changed.client.LatexCoveredBlocks;
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

    public static final ChiselRenderType LATEX_SOLID = create("LATEX_SOLID", LatexCoveredBlocks.latexSolid(), VoxelType.SOLID);
    public static final ChiselRenderType LATEX_SOLID_FLUID = create("LATEX_SOLID_FLUID", LatexCoveredBlocks.latexSolid(), VoxelType.FLUID);
    public static final ChiselRenderType LATEX_CUTOUT = create("LATEX_CUTOUT", LatexCoveredBlocks.latexCutout(), VoxelType.UNKNOWN);
    public static final ChiselRenderType LATEX_CUTOUT_MIPPED = create("LATEX_CUTOUT_MIPPED", LatexCoveredBlocks.latexCutoutMipped(), VoxelType.UNKNOWN);
}
