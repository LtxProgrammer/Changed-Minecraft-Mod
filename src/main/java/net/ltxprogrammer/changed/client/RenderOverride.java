package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.DuctBlock;
import net.ltxprogrammer.changed.client.renderer.model.DuctPlayerModel;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.PlayerMover;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public abstract class RenderOverride {
    public interface Override {
        boolean wantToOverride(Player player, LatexVariantInstance<?> variant);
        void render(Player player, LatexVariantInstance<?> variant, PoseStack stack, MultiBufferSource buffer, int packedLight, float partialTick);
    }

    private static int EXPECTED_SIZE = 0;
    private static final List<Function<EntityModelSet, Override>> OVERRIDES_PRE_INIT = new ArrayList<>();
    private static List<Override> OVERRIDES = null;
    
    public static void registerOverride(Function<EntityModelSet, Override> fn) {
        OVERRIDES_PRE_INIT.add(fn);
        EXPECTED_SIZE = OVERRIDES_PRE_INIT.hashCode();
    }

    protected static void checkOverrides() {
        if (EXPECTED_SIZE == OVERRIDES_PRE_INIT.size())
            return;

        var modelSet = Minecraft.getInstance().getEntityModels();
        OVERRIDES = OVERRIDES_PRE_INIT.stream().map(fn -> fn.apply(modelSet)).collect(Collectors.toList());
        EXPECTED_SIZE = OVERRIDES_PRE_INIT.size();
    }
    
    public static boolean renderOverrides(Player player, LatexVariantInstance<?> variant, PoseStack stack, MultiBufferSource buffer, int packedLight, float partialTick) {
        checkOverrides();

        for (var override : OVERRIDES) {
            if (!override.wantToOverride(player, variant))
                continue;
            
            override.render(player, variant, stack, buffer, packedLight, partialTick);
            return true;
        }
        
        return false;
    }

    public static class DuctPlayerOverride implements Override {
        public static final Function<Direction, ResourceLocation> TEXTURE = Util.memoize(direction -> Changed.modResource("textures/blocks/duct_player_" + direction.toString().toLowerCase() + ".png"));
        public static final Function<Direction, RenderType> RENDER_TYPE = Util.memoize(direction -> RenderType.entityCutout(TEXTURE.apply(direction)));
        private final DuctPlayerModel playerModel;

        public DuctPlayerOverride(EntityModelSet modelSet) {
            this.playerModel = new DuctPlayerModel(modelSet.bakeLayer(DuctPlayerModel.LAYER_LOCATION));
        }

        public boolean wantToOverride(Player player, LatexVariantInstance<?> variant) {
            return player instanceof PlayerDataExtension ext && // Mixin worked
                    ext.getPlayerMover() != null && // Has player mover
                    ext.getPlayerMover().is(PlayerMover.DUCT_MOVER.get());
        }

        public void render(Player player, LatexVariantInstance<?> variant, PoseStack pose, MultiBufferSource buffer, int packedLight, float partialTick) {
            var look = DuctBlock.DuctMover.DuctMoverInstance.getClosestDirection(player.getLookAngle());

            pose.pushPose();

            pose.translate(0, -1, 0);

            playerModel.renderToBuffer(pose, buffer.getBuffer(RENDER_TYPE.apply(look)), packedLight, LivingEntityRenderer.getOverlayCoords(player, 0.0F), 1.0f, 1.0f, 1.0f, 1.0f);

            pose.popPose();
        }
    }
    
    static {
        registerOverride(DuctPlayerOverride::new);
    }
}
