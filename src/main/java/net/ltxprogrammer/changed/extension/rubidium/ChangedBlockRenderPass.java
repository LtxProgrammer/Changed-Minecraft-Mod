package net.ltxprogrammer.changed.extension.rubidium;

import me.jellysquid.mods.sodium.client.render.chunk.passes.BlockRenderPass;
import net.ltxprogrammer.changed.client.LatexCoveredBlocks;
import net.minecraft.client.renderer.RenderType;

import java.lang.reflect.Method;

public class ChangedBlockRenderPass {
    private static final Method ctor;
    private static BlockRenderPass create(String name, RenderType type, boolean translucent, float alphaCutoff) {
        try {
            return (BlockRenderPass) ctor.invoke(null, name, type, translucent, alphaCutoff);
        } catch (Exception ex) {
            return null;
        }
    }

    static {
        Method tmp = null;
        try {
             tmp = BlockRenderPass.class.getDeclaredMethod("create", String.class, RenderType.class, boolean.class, float.class);
             tmp.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        ctor = tmp;
    }

    public static final BlockRenderPass LATEX_SOLID = create("LATEX_SOLID", LatexCoveredBlocks.latexSolid(), false, 0.0F);
    public static final BlockRenderPass LATEX_CUTOUT = create("LATEX_CUTOUT", LatexCoveredBlocks.latexCutout(), false, 0.1F);
    public static final BlockRenderPass LATEX_CUTOUT_MIPPED = create("LATEX_CUTOUT_MIPPED", LatexCoveredBlocks.latexCutoutMipped(), false, 0.5F);
}
