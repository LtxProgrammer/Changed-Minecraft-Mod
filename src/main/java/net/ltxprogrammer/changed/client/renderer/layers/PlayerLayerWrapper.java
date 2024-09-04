package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class PlayerLayerWrapper<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends RenderLayer<T, M> {
    private static AdvancedHumanoidModel<?> overrideLayer = null;
    private boolean failedRender = false;
    private final RenderLayer<? super Player, PlayerModel<? super Player>> wrapped;

    public PlayerLayerWrapper(RenderLayerParent<T, M> parent, RenderLayer<? super Player, PlayerModel<? super Player>> wrapped) {
        super(parent);
        this.wrapped = wrapped;
    }

    public static boolean isWrappable(RenderLayer<?, ?> layer) {
        if (layer instanceof HumanoidArmorLayer<?,?,?>) return false;
        if (layer instanceof CustomHeadLayer<?,?>) return false;
        if (layer instanceof PlayerItemInHandLayer<?,?>) return false;
        if (layer instanceof CapeLayer) return false;
        if (layer instanceof ElytraLayer<?,?>) return false;
        if (layer instanceof StuckInBodyLayer<?,?>) return false;
        if (layer instanceof ParrotOnShoulderLayer<?>) return false;
        if (layer instanceof SpinAttackEffectLayer<?>) return false;
        if (layer instanceof DarkLatexMaskLayer<?,?>) return false;
        if (layer instanceof GasMaskLayer<?,?>) return false;
        return true;
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource bufferSource, int light, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getUnderlyingPlayer() != null && !hasFailedRender()) {
            overrideLayer = this.getParentModel();

            try {
                wrapped.render(pose, bufferSource, light, entity.getUnderlyingPlayer(), limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }

            catch (Exception e) {
                failedRender = true;
                e.printStackTrace();
            }

            overrideLayer = null;
        }
    }

    public static Optional<AdvancedHumanoidModel<?>> getOverrideLayer() {
        return Optional.ofNullable(overrideLayer);
    }

    public boolean hasFailedRender() {
        return failedRender;
    }
}
