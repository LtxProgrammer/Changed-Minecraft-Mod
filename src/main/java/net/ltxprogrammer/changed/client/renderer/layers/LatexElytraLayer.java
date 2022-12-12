package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModelInterface;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class LatexElytraLayer<T extends LatexEntity, M extends LatexHumanoidModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation WINGS_LOCATION = new ResourceLocation("textures/entity/elytra.png");
    private final ElytraModel<T> elytraModel;

    public LatexElytraLayer(RenderLayerParent<T, M> p_174493_, EntityModelSet p_174494_) {
        super(p_174493_);
        this.elytraModel = new ElytraModel<>(p_174494_.bakeLayer(ModelLayers.ELYTRA)){
            public void setupAnim(T p_102544_, float p_102545_, float p_102546_, float p_102547_, float p_102548_, float p_102549_) {
                float f = 0.2617994F;
                float f1 = -0.2617994F;
                float f2 = 0.0F;
                float f3 = 0.0F;
                if (p_102544_.isFallFlying()) {
                    float f4 = 1.0F;
                    Vec3 vec3 = p_102544_.getDeltaMovement();
                    if (vec3.y < 0.0D) {
                        Vec3 vec31 = vec3.normalize();
                        f4 = 1.0F - (float)Math.pow(-vec31.y, 1.5D);
                    }

                    f = f4 * 0.34906584F + (1.0F - f4) * f;
                    f1 = f4 * (-(float)Math.PI / 2F) + (1.0F - f4) * f1;
                } else if (p_102544_.isCrouching()) {
                    f = 0.6981317F;
                    f1 = (-(float)Math.PI / 4F);
                    f2 = 3.0F;
                    f3 = 0.08726646F;
                }

                this.leftWing.y = f2;
                if (p_102544_.getUnderlyingPlayer() instanceof AbstractClientPlayer abstractclientplayer) {
                    abstractclientplayer.elytraRotX += (f - abstractclientplayer.elytraRotX) * 0.1F;
                    abstractclientplayer.elytraRotY += (f3 - abstractclientplayer.elytraRotY) * 0.1F;
                    abstractclientplayer.elytraRotZ += (f1 - abstractclientplayer.elytraRotZ) * 0.1F;
                    this.leftWing.xRot = abstractclientplayer.elytraRotX;
                    this.leftWing.yRot = abstractclientplayer.elytraRotY;
                    this.leftWing.zRot = abstractclientplayer.elytraRotZ;
                } else {
                    this.leftWing.xRot = f;
                    this.leftWing.zRot = f1;
                    this.leftWing.yRot = f3;
                }

                this.rightWing.yRot = -this.leftWing.yRot;
                this.rightWing.y = this.leftWing.y;
                this.rightWing.xRot = this.leftWing.xRot;
                this.rightWing.zRot = -this.leftWing.zRot;
            }
        };
    }

    public void render(PoseStack p_116951_, MultiBufferSource p_116952_, int p_116953_, T p_116954_, float p_116955_, float p_116956_, float p_116957_, float p_116958_, float p_116959_, float p_116960_) {
        ItemStack itemstack = p_116954_.getItemBySlot(EquipmentSlot.CHEST);
        if (shouldRender(itemstack, p_116954_)) {
            ResourceLocation resourcelocation;
            if (p_116954_.getUnderlyingPlayer() instanceof AbstractClientPlayer clientPlayer) {
                if (clientPlayer.isElytraLoaded() && clientPlayer.getElytraTextureLocation() != null) {
                    resourcelocation = clientPlayer.getElytraTextureLocation();
                } else if (clientPlayer.isCapeLoaded() && clientPlayer.getCloakTextureLocation() != null && clientPlayer.isModelPartShown(PlayerModelPart.CAPE)) {
                    resourcelocation = clientPlayer.getCloakTextureLocation();
                } else {
                    resourcelocation = getElytraTexture(itemstack, p_116954_);
                }
            } else {
                resourcelocation = getElytraTexture(itemstack, p_116954_);
            }

            p_116951_.pushPose();
            if (this.getParentModel() instanceof LatexHumanoidModelInterface modelInterface) {
                p_116951_.translate(0.0D, 0.0D, 0.125D + (modelInterface.getController().forewardOffset / 16.0f));
            }

            else {
                p_116951_.translate(0.0D, 0.0D, 0.125D);
            }

            this.getParentModel().copyPropertiesTo(this.elytraModel);
            this.elytraModel.setupAnim(p_116954_, p_116955_, p_116956_, p_116958_, p_116959_, p_116960_);
            VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(p_116952_, RenderType.armorCutoutNoCull(resourcelocation), false, itemstack.hasFoil());
            this.elytraModel.renderToBuffer(p_116951_, vertexconsumer, p_116953_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            p_116951_.popPose();
        }
    }

    /**
     * Determines if the ElytraLayer should render.
     * ItemStack and Entity are provided for modder convenience,
     * For example, using the same ElytraLayer for multiple custom Elytra.
     *
     * @param stack  The Elytra ItemStack
     * @param entity The entity being rendered.
     * @return If the ElytraLayer should render.
     */
    public boolean shouldRender(ItemStack stack, T entity) {
        return stack.getItem() == Items.ELYTRA;
    }

    /**
     * Gets the texture to use with this ElytraLayer.
     * This assumes the vanilla Elytra model.
     *
     * @param stack  The Elytra ItemStack.
     * @param entity The entity being rendered.
     * @return The texture.
     */
    public ResourceLocation getElytraTexture(ItemStack stack, T entity) {
        return WINGS_LOCATION;
    }
}