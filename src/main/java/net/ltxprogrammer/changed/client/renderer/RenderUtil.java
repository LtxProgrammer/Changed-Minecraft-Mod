package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.util.StackUtil;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderUtil {
    /**
     * Used to determine whether to render head armor, for first person mods
     * @param renderSubject entity that the camera may be attached to
     * @return true - if minecraft is rendering the entity while in first person mode
     */
    public static boolean isFirstPerson(LatexEntity renderSubject) {
        var camEnt = Minecraft.getInstance().cameraEntity;
        if (renderSubject != camEnt && renderSubject.getUnderlyingPlayer() != camEnt)
            return false;
        if (StackUtil.callStackContainsClass(Screen.class, 50))
            return false;
        return Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;
    }
}
