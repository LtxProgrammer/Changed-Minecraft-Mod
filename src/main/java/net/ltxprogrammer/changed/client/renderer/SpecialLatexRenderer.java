package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.EmissiveBodyLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.SpecialLatexModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorSpecialLatexModel;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.beast.SpecialLatex;
import net.ltxprogrammer.changed.util.DynamicClient;
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
    private static final Map<Pair<UUID, String>, SpecialLatexRenderer> SPECIAL_RENDERERS = new HashMap<>();
    private final EntityRendererProvider.Context context;
    private final boolean isDelegate;

    public SpecialLatexRenderer(EntityRendererProvider.Context context) {
        super(context, null, modelPart -> null, null, null, 0.0f);
        this.isDelegate = true;
        this.context = context;
    }

    private static PatreonBenefits.ModelData ensureModelIsLoaded(PatreonBenefits.ModelData modelData) {
        if (!modelData.model().isResolved()) {
            modelData.registerLayerDefinitions(DynamicClient::lateRegisterLayerDefinition);
            modelData.registerTextures(PatreonBenefits::registerOnlineTexture);
        }

        return modelData;
    }

    public SpecialLatexRenderer(EntityRendererProvider.Context context, PatreonBenefits.ModelData modelData) {
        super(context, new SpecialLatexModel(context.bakeLayer(ensureModelIsLoaded(modelData).modelLayerLocation().get()), modelData),
                (part) -> new ArmorSpecialLatexModel<>(part, modelData), modelData.armorModelLayerLocation().inner().get(),
                modelData.armorModelLayerLocation().outer().get(), modelData.shadowSize());
        if (modelData.emissive().isPresent())
            this.addLayer(new EmissiveBodyLayer<>(this, modelData.emissive().get()));
        this.isDelegate = false;
        this.context = context;
    }

    public SpecialLatexRenderer getAndCacheFor(SpecialLatex entity) {
        if (!entity.specialLatexForm.modelData().containsKey(entity.wantedState))
            return SPECIAL_RENDERERS.computeIfAbsent(new Pair<>(entity.getAssignedUUID(), entity.wantedState), pair ->
                    new SpecialLatexRenderer(this.context, entity.specialLatexForm.getDefaultModel()));

        return SPECIAL_RENDERERS.computeIfAbsent(new Pair<>(entity.getAssignedUUID(), entity.wantedState), pair ->
                new SpecialLatexRenderer(this.context, entity.specialLatexForm.modelData().get(pair.getSecond())));
    }

    // Returns true if continue with regular code, false if to return, accepts if delegate and valid
    protected boolean runIfValid(SpecialLatex entity, Consumer<SpecialLatexRenderer> rendererConsumer) {
        if (this.isDelegate) {
            if (entity.getAssignedUUID() == null) return false;
            PatreonBenefits.SpecialForm form = PatreonBenefits.getPlayerSpecialForm(entity.getAssignedUUID());
            if (form == null) return false;

            rendererConsumer.accept(getAndCacheFor(entity));
            return false;
        }

        else
            return true;
    }

    // Returns true if continue with regular code, false if to return, accepts if delegate and valid
    protected <R> Optional<R> runIfValid(SpecialLatex entity, Function<SpecialLatexRenderer, R> rendererConsumer) {
        if (this.isDelegate) {
            if (entity.getAssignedUUID() == null) return Optional.ofNullable(null);
            PatreonBenefits.SpecialForm form = PatreonBenefits.getPlayerSpecialForm(entity.getAssignedUUID());
            if (form == null) return Optional.ofNullable(null);

            return Optional.of(rendererConsumer.apply(getAndCacheFor(entity)));
        }

        else
            return Optional.empty();
    }

    @Nullable
    public RenderType getRenderType(SpecialLatex entity, boolean p_115323_, boolean p_115324_, boolean p_115325_) {
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
    public ResourceLocation getTextureLocation(SpecialLatex latex) {
        return latex.specialLatexForm != null ? latex.specialLatexForm.modelData().get(latex.wantedState).texture() :
                Changed.modResource("textures/delay_loaded_latex.png");
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
