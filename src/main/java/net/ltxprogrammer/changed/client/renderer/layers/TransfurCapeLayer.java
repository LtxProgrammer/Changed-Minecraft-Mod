package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.TorsoedModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class TransfurCapeLayer<T extends ChangedEntity, M extends AdvancedHumanoidModel<T> & TorsoedModel> extends RenderLayer<T, M> {
   public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("cape"), "main");
   public static final ModelLayerLocation LAYER_LOCATION_SHORT = new ModelLayerLocation(Changed.modResource("cape_short"), "main");
   private final ModelPart Cloak;
   private final ModelPart Joint;

   private static List<UUID> DEV_CAPES = List.of(UUID.fromString("380df991-f603-344c-a090-369bad2a924a"));
   private static final ResourceLocation DEV_CAPE = Changed.modResource("textures/items/3d/dev_cape.png");

   private TransfurCapeLayer(RenderLayerParent<T, M> parent, ModelPart cloak) {
      super(parent);
      this.Cloak = cloak.getChild("Cloak");
      this.Joint = Cloak.getChild("Joint");
   }

   public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T> & TorsoedModel> TransfurCapeLayer<T, M> normalCape(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
      return new TransfurCapeLayer<>(parent, modelSet.bakeLayer(LAYER_LOCATION));
   }

   public static <T extends ChangedEntity, M extends AdvancedHumanoidModel<T> & TorsoedModel> TransfurCapeLayer<T, M> shortCape(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
      return new TransfurCapeLayer<>(parent, modelSet.bakeLayer(LAYER_LOCATION_SHORT));
   }

   public static LayerDefinition createCape() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();

      PartDefinition Cloak = partdefinition.addOrReplaceChild("Cloak", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));

      PartDefinition Joint = Cloak.addOrReplaceChild("Joint", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 2.0F));

      PartDefinition cube_r1 = Joint.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -0.5F, 10.0F, 16.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 8.0F, 0.5F, 0.0F, 3.1416F, 0.0F));

      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   public static LayerDefinition createShortCape() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();

      PartDefinition Cloak = partdefinition.addOrReplaceChild("Cloak", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));

      PartDefinition Joint = Cloak.addOrReplaceChild("Joint", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 2.0F));

      PartDefinition cube_r1 = Joint.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -0.5F, 10.0F, 10.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 8.0F, 0.5F, 0.0F, 3.1416F, 0.0F));

      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   private boolean isCapeLoaded(T entity) {
      Player player = entity.getUnderlyingPlayer();
      if (!(player instanceof AbstractClientPlayer clientPlayer))
         return false;
      return clientPlayer.isCapeLoaded();
   }

   private boolean isCapeShown(T entity) {
       Player player = entity.getUnderlyingPlayer();
       if (!(player instanceof AbstractClientPlayer clientPlayer))
          return false;
       return clientPlayer.isModelPartShown(PlayerModelPart.CAPE);
   }

   @Nullable
   public static ResourceLocation getCloakTextureLocation(ChangedEntity entity) {
      Player player = entity.getUnderlyingPlayer();
      if (!(player instanceof AbstractClientPlayer clientPlayer))
         return null;
      if (DEV_CAPES.contains(clientPlayer.getUUID()))
         return DEV_CAPE;
      return clientPlayer.getCloakTextureLocation();
   }

   public void render(PoseStack pose, MultiBufferSource buffer, int packedLight, T entity, float p_116619_, float p_116620_, float partialTicks, float p_116622_, float p_116623_, float p_116624_) {
      if (isCapeLoaded(entity) && !entity.isInvisible() && isCapeShown(entity) && getCloakTextureLocation(entity) != null) {
         ItemStack itemstack = entity.getItemBySlot(EquipmentSlot.CHEST);
         if (!itemstack.is(Items.ELYTRA)) {
            pose.pushPose();
            double d0 = Mth.lerp((double)partialTicks, entity.xCloakO, entity.xCloak) - Mth.lerp((double)partialTicks, entity.xo, entity.getX());
            double d1 = Mth.lerp((double)partialTicks, entity.yCloakO, entity.yCloak) - Mth.lerp((double)partialTicks, entity.yo, entity.getY());
            double d2 = Mth.lerp((double)partialTicks, entity.zCloakO, entity.zCloak) - Mth.lerp((double)partialTicks, entity.zo, entity.getZ());
            double d3 = (double)Mth.sin(entity.yBodyRot * ((float)Math.PI / 180F));
            double d4 = (double)(-Mth.cos(entity.yBodyRot * ((float)Math.PI / 180F)));
            float f1 = (float)d1 * 10.0F;
            f1 = Mth.clamp(f1, -6.0F, 32.0F);
            float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
            f2 = Mth.clamp(f2, 0.0F, 150.0F);
            float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
            f3 = Mth.clamp(f3, -20.0F, 20.0F);
            if (f2 < 0.0F) {
               f2 = 0.0F;
            }

            float f4 = Mth.lerp(partialTicks, entity.oBob, entity.bob);
            f1 += Mth.sin(Mth.lerp(partialTicks, entity.walkDistO, entity.walkDist) * 6.0F) * 32.0F * f4;
            f1 += Math.toDegrees(this.getParentModel().getTorso().xRot);

            this.Cloak.copyFrom(this.getParentModel().getTorso());
            this.Cloak.xRot = 0.0F;
            this.Cloak.zRot = 0.0F;
            this.Joint.z = itemstack.isEmpty() ? 2.0F : 3.0F;
            this.Joint.xRot = (float)Math.toRadians(Math.min(6.0F + f2 / 2.0F + f1, 81.0f));
            this.Joint.zRot = (float)Math.toRadians(f3 / 2.0F);
            this.Joint.yRot = (float)Math.toRadians(f3 / 2.0F);
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entitySolid(getCloakTextureLocation(entity)));
            this.Cloak.render(pose, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
            pose.popPose();
         }
      }
   }
}