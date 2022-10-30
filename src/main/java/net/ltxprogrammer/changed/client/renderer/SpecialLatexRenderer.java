package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.EmissiveBodyLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.SpecialLatexModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorSpecialLatexModel;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.beast.SpecialLatex;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class SpecialLatexRenderer extends LatexHumanoidRenderer<SpecialLatex, SpecialLatexModel, ArmorSpecialLatexModel<SpecialLatex>> {
    private static final Map<UUID, SpecialLatexRenderer> SPECIAL_RENDERERS = new HashMap<>();
    private final EntityRendererProvider.Context context;
    private final boolean isDelegate;

    public SpecialLatexRenderer(EntityRendererProvider.Context context) {
        super(context, null, modelPart -> null, null, null, 0.0f);
        this.isDelegate = true;
        this.context = context;
    }

    public SpecialLatexRenderer(EntityRendererProvider.Context context, PatreonBenefits.SpecialLatexForm form) {
        super(context, new SpecialLatexModel(context.bakeLayer(form.modelLayerLocation().get()), form),
                (part) -> new ArmorSpecialLatexModel<>(part, form), form.armorModelLayerLocation().inner().get(), form.armorModelLayerLocation().outer().get(), form.shadowSize());
        if (form.emissive().isPresent())
            this.addLayer(new EmissiveBodyLayer<>(this, null));
        this.isDelegate = false;
        this.context = context;
    }

    // Returns true if continue with regular code, false if to return, accepts if delegate and valid
    protected boolean runIfValid(SpecialLatex entity, Consumer<SpecialLatexRenderer> rendererConsumer) {
        if (this.isDelegate) {
            if (entity.getUnderlyingPlayer() == null) return false;
            PatreonBenefits.SpecialLatexForm form = PatreonBenefits.getPlayerSpecialForm(entity.getUnderlyingPlayer().getUUID());
            if (form == null) return false;

            rendererConsumer.accept(SPECIAL_RENDERERS.computeIfAbsent(entity.getUnderlyingPlayer().getUUID(),
                    uuid -> new SpecialLatexRenderer(this.context, form)));
            return false;
        }

        else
            return true;
    }

    // Returns true if continue with regular code, false if to return, accepts if delegate and valid
    protected <R> Optional<R> runIfValid(SpecialLatex entity, Function<SpecialLatexRenderer, R> rendererConsumer) {
        if (this.isDelegate) {
            if (entity.getUnderlyingPlayer() == null) return Optional.ofNullable(null);
            PatreonBenefits.SpecialLatexForm form = PatreonBenefits.getPlayerSpecialForm(entity.getUnderlyingPlayer().getUUID());
            if (form == null) return Optional.ofNullable(null);

            return Optional.of(rendererConsumer.apply(SPECIAL_RENDERERS.computeIfAbsent(entity.getUnderlyingPlayer().getUUID(),
                    uuid -> new SpecialLatexRenderer(this.context, form))));
        }

        else
            return Optional.empty();
    }

    @Nullable
    protected RenderType getRenderType(SpecialLatex entity, boolean p_115323_, boolean p_115324_, boolean p_115325_) {
        var opt = runIfValid(entity, renderer -> { return renderer.getRenderType(entity, p_115323_, p_115324_, p_115325_); });
        if (opt.isEmpty())
            return super.getRenderType(entity, p_115323_, p_115324_, p_115325_);
        else
            return opt.get();
    }

    public void render(SpecialLatex entity, float p_115456_, float p_115457_, PoseStack pose, MultiBufferSource buffer, int p_115460_) {
        if (runIfValid(entity, renderer -> {
                renderer.render(entity, p_115456_, p_115457_, pose, buffer, p_115460_); }))
            super.render(entity, p_115456_, p_115457_, pose, buffer, p_115460_);
    }

    @Override
    public ResourceLocation getTextureLocation(SpecialLatex p_114482_) {
        return p_114482_.specialLatexForm != null ? p_114482_.specialLatexForm.texture() : Changed.modResource("textures/delay_loaded_latex.png");
    }

    public LatexHumanoidModel<SpecialLatex> getModel(LatexEntity entity) {
        if (entity instanceof SpecialLatex specialLatex) {
            var opt = runIfValid(specialLatex, renderer -> { return renderer.getModel(); });
            if (opt.isEmpty())
                return this.getModel();
            else
                return opt.get();
        }

        return null;
    }
}
