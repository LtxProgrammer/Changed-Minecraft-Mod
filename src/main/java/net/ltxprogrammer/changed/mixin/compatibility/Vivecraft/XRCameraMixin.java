package net.ltxprogrammer.changed.mixin.compatibility.Vivecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.vivecraft.RendererScaleAccessor;
import net.ltxprogrammer.changed.extension.vivecraft.VivecraftHelperClient;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client_vr.VRData;
import org.vivecraft.client_vr.render.XRCamera;

@Mixin(XRCamera.class)
public abstract class XRCameraMixin {
    @Unique
    public Vec3 changed$getDeltaNeckPos(Entity entity, float partialTick) {
        float neckPos = 1.501F;
        float renderScale = 1.0F;
        if (entity instanceof LivingEntity living && EntityUtil.maybeGetOverlaying(living) instanceof ChangedEntity changedEntity) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.getEntityRenderDispatcher().getRenderer(changedEntity) instanceof AdvancedHumanoidRenderer advancedRenderer) {
                neckPos = VivecraftHelperClient.getModelNeckPos(changedEntity, advancedRenderer.getModel(changedEntity).getAnimator(changedEntity), partialTick);
            }

            renderScale = VivecraftHelperClient.getModelRenderScale(changedEntity, partialTick);
        }


        return new Vec3(0.0, (neckPos - 1.501F) * renderScale, 0.0);
    }

    @WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lorg/vivecraft/client_vr/VRData$VRDevicePose;getPosition()Lnet/minecraft/world/phys/Vec3;", remap = false))
    public Vec3 changed$offsetViewToModel(VRData.VRDevicePose instance, Operation<Vec3> original,
                                          @Local(argsOnly = true) Entity entity,
                                          @Local(argsOnly = true) float partialTick,
                                          @Local RenderPass renderpass) {
        if (!RenderPass.isThirdPerson(renderpass))
            return changed$getDeltaNeckPos(entity, partialTick).add(original.call(instance));
        else
            return original.call(instance);
    }
}
