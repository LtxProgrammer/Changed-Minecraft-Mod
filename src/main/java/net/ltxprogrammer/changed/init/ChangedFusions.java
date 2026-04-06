package net.ltxprogrammer.changed.init;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.ILatexAssimilatedEntity;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.ai.AssimilationBehavior;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.process.TransfurEvents;
import net.ltxprogrammer.changed.util.ResourceUtil;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ChangedFusions extends SimplePreparableReloadListener<List<ChangedFusions.FusionDefinition>> {
    public record FusionDefinition(ResourceLocation name, TransfurVariant<?> fusion, TransfurVariant<?> variant, Either<TransfurVariant<?>, Class<? extends LivingEntity>> other) {
        public static class Builder {
            private TransfurVariant<?> fusion = null;
            private TransfurVariant<?> variant = null;
            private TransfurVariant<?> otherVariant = null;
            private Class<? extends LivingEntity> mob = null;

            public Builder withFusion(ResourceLocation id) {
                this.fusion = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(id);
                return this;
            }

            public Builder withVariant(ResourceLocation id) {
                if (variant == null)
                    this.variant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(id);
                else
                    this.otherVariant = ChangedRegistry.TRANSFUR_VARIANT.get().getValue(id);
                return this;
            }

            public Builder withMob(String className) throws ClassNotFoundException {
                this.mob = (Class<? extends LivingEntity>) Class.forName(className);
                return this;
            }

            public FusionDefinition build(ResourceLocation name) {
                Objects.requireNonNull(fusion);
                Objects.requireNonNull(variant);
                if (otherVariant == null && mob == null)
                    throw new NullPointerException();

                return new FusionDefinition(name, fusion, variant, otherVariant != null ? Either.left(otherVariant) : Either.right(mob));
            }
        }

        public boolean matches(TransfurVariant<?> variantA, TransfurVariant<?> variantB) {
            return other.left().filter(transfurVariant -> (variantA == variant && variantB == transfurVariant) ||
                    (variantA == transfurVariant && variantB == variant)).isPresent();
        }

        public boolean matches(TransfurVariant<?> variant, Class<? extends LivingEntity> mob) {
            return other.right().filter(mobClass -> variant == this.variant && mob == mobClass).isPresent();
        }
    }

    public static final ChangedFusions INSTANCE = new ChangedFusions();

    private final List<FusionDefinition> fusionDefinitions = new ArrayList<>();

    public Stream<FusionDefinition> getFusionDefinitions() {
        return fusionDefinitions.stream();
    }

    public Stream<TransfurVariant<?>> getFusionsFor(TransfurVariant<?> variantA, TransfurVariant<?> variantB) {
        return getFusionDefinitions().filter(fusionDefinition -> {
            return fusionDefinition.matches(variantA, variantB);
        }).map(FusionDefinition::fusion);
    }

    public Stream<TransfurVariant<?>> getFusionsFor(TransfurVariant<?> variant, Class<? extends LivingEntity> mob) {
        return getFusionDefinitions().filter(fusionDefinition -> {
            return fusionDefinition.matches(variant, mob);
        }).map(FusionDefinition::fusion);
    }

    private void killOrDiscard(LivingEntity pvpLoser, IAbstractChangedEntity damageSource) {
        if (pvpLoser instanceof Player player) {
            ProcessTransfur.killPlayerByAbsorption(player, damageSource.getEntity());
        } else {
            pvpLoser.discard();
        }
    }

    private AssimilationBehavior getLatexFusionBehaviorForChangedEntity(TransfurVariant<?> fusionVariant, IAbstractChangedEntity assimVictim, IAbstractChangedEntity transfurSource) {
        var event = new TransfurEvents.ChangedEntityFusionWithChangedEntityDecisionEvent(transfurSource, assimVictim, fusionVariant);
        if (Changed.postModEvent(event))
            return null;
        final TransfurVariant<?> newFusionVariant = event.getFusionVariant();

        if (transfurSource.isPlayer() || !assimVictim.isPlayer()) {
            return AssimilationBehavior.instant(assimVictim.getLevel(), () -> {
                IAbstractChangedEntity oldSourceEntity = transfurSource.copyAbstraction();

                transfurSource.replaceVariant(newFusionVariant);

                if (!Changed.postModEvent(new TransfurEvents.ChangedEntityFusionWithChangedEntityEvent(oldSourceEntity, assimVictim, transfurSource, newFusionVariant)))
                    ProcessTransfur.onNewlyFused(transfurSource);

                ChangedSounds.broadcastSound(transfurSource.getEntity(), ChangedSounds.LATEX_FUSE_ENTITY, 1f, 1f);
                killOrDiscard(assimVictim.getEntity(), transfurSource);

                ChangedAnimationEvents.broadcastTransfurAnimation(transfurSource.getEntity(), newFusionVariant, transfurSource.absorb());
                return transfurSource;
            });
        } else {
            return AssimilationBehavior.instant(assimVictim.getLevel(), () -> {
                IAbstractChangedEntity oldAssimEntity = assimVictim.copyAbstraction();

                assimVictim.replaceVariant(newFusionVariant);

                if (!Changed.postModEvent(new TransfurEvents.ChangedEntityFusionWithChangedEntityEvent(transfurSource, oldAssimEntity, assimVictim, newFusionVariant)))
                    ProcessTransfur.onNewlyFused(assimVictim);

                ChangedSounds.broadcastSound(assimVictim.getEntity(), ChangedSounds.LATEX_FUSE_ENTITY, 1f, 1f);
                killOrDiscard(transfurSource.getEntity(), assimVictim);

                ChangedAnimationEvents.broadcastTransfurAnimation(assimVictim.getEntity(), newFusionVariant, assimVictim.absorb());
                return assimVictim;
            });
        }
    }

    private AssimilationBehavior getLatexFusionBehaviorForLatexEntity(TransfurVariant<?> fusionVariant, IAbstractChangedEntity assimVictim, ILatexAssimilatedEntity transfurSource) {
        var event = new TransfurEvents.AssimilatedEntityFusionWithChangedEntityDecisionEvent(transfurSource, assimVictim, fusionVariant);
        if (Changed.postModEvent(event))
            return null;
        final TransfurVariant<?> newFusionVariant = event.getFusionVariant();

        return AssimilationBehavior.instant(assimVictim.getLevel(), () -> {
            IAbstractChangedEntity oldAssimEntity = assimVictim.copyAbstraction();

            assimVictim.replaceVariant(newFusionVariant);

            if (!Changed.postModEvent(new TransfurEvents.AssimilatedEntityFusionWithChangedEntityEvent(transfurSource, oldAssimEntity, assimVictim, newFusionVariant)))
                ProcessTransfur.onNewlyFused(assimVictim);

            ChangedSounds.broadcastSound(assimVictim.getEntity(), ChangedSounds.LATEX_FUSE_ENTITY, 1f, 1f);
            killOrDiscard(transfurSource.getEntity(), assimVictim);

            ChangedAnimationEvents.broadcastTransfurAnimation(assimVictim.getEntity(), newFusionVariant, assimVictim.absorb());
            return assimVictim;
        });
    }

    private AssimilationBehavior getLatexFusionBehavior(TransfurVariant<?> fusionVariant, IAbstractChangedEntity assimVictim, Either<IAbstractChangedEntity, ILatexAssimilatedEntity> transfurSource) {
        Level level = assimVictim.getEntity().level();

        if (transfurSource.left().isPresent()) { // Check if attacker can't fuse
            final IAbstractChangedEntity sourceLatex = transfurSource.left().get();
            var instance = sourceLatex.getTransfurVariantInstance();
            if (instance != null && instance.ageAsVariant > level.getGameRules().getInt(ChangedGameRules.RULE_FUSABILITY_DURATION_PLAYER))
                return null;
        }

        { // Check if attackee can't fuse
            var instance = assimVictim.getTransfurVariantInstance();
            if (instance != null && instance.ageAsVariant > level.getGameRules().getInt(ChangedGameRules.RULE_FUSABILITY_DURATION_PLAYER))
                return null;
        }

        if (assimVictim.isPlayer() && !transfurSource.map(IAbstractChangedEntity::isPlayer, assimilatedEntity -> false)) {
            if (!level.getGameRules().getBoolean(ChangedGameRules.RULE_NPC_WANT_FUSE_PLAYER))
                return null;
        }

        return transfurSource.map(
                changedEntity -> getLatexFusionBehaviorForChangedEntity(fusionVariant, assimVictim, changedEntity),
                assimilatedEntity -> getLatexFusionBehaviorForLatexEntity(fusionVariant, assimVictim, assimilatedEntity));
    }

    private AssimilationBehavior getMobFusionBehavior(TransfurVariant<?> fusionVariant, LivingEntity assimVictim, Either<IAbstractChangedEntity, ILatexAssimilatedEntity> transfurSource) {
        Level level = assimVictim.level();

        if (transfurSource.left().isPresent()) { // Check if attacker can't fuse
            final IAbstractChangedEntity sourceLatex = transfurSource.left().get();
            var instance = sourceLatex.getTransfurVariantInstance();
            if (instance != null && instance.ageAsVariant > level.getGameRules().getInt(ChangedGameRules.RULE_FUSABILITY_DURATION_PLAYER))
                return null;

            var event = new TransfurEvents.ChangedEntityFusionWithMobDecisionEvent(sourceLatex, assimVictim, fusionVariant);
            if (Changed.postModEvent(event))
                return null;
            final TransfurVariant<?> newFusionVariant = event.getFusionVariant();

            return AssimilationBehavior.instant(assimVictim.level(), () -> {
                IAbstractChangedEntity oldSourceEntity = sourceLatex.copyAbstraction();

                sourceLatex.replaceVariant(newFusionVariant);

                if (!Changed.postModEvent(new TransfurEvents.ChangedEntityFusionWithMobEvent(oldSourceEntity, assimVictim, sourceLatex, newFusionVariant)))
                    ProcessTransfur.onNewlyFused(sourceLatex);

                ChangedSounds.broadcastSound(sourceLatex.getEntity(), ChangedSounds.LATEX_FUSE_ENTITY, 1f, 1f);
                killOrDiscard(assimVictim, sourceLatex);

                ChangedAnimationEvents.broadcastTransfurAnimation(sourceLatex.getEntity(), newFusionVariant, sourceLatex.absorb());
                return sourceLatex;
            });
        } else {
            final ILatexAssimilatedEntity sourceLatex = transfurSource.right().get();

            var event = new TransfurEvents.AssimilatedEntityFusionWithMobDecisionEvent(sourceLatex, assimVictim, fusionVariant);
            if (Changed.postModEvent(event))
                return null;
            final TransfurVariant<?> newFusionVariant = event.getFusionVariant();

            return AssimilationBehavior.instant(assimVictim.level(), () -> {
                var newEntity = newFusionVariant.replaceEntity(assimVictim, sourceLatex.getEntity());

                if (!Changed.postModEvent(new TransfurEvents.AssimilatedEntityFusionWithMobEvent(sourceLatex, assimVictim, newEntity, newFusionVariant)))
                    ProcessTransfur.onNewlyFused(newEntity);

                ChangedSounds.broadcastSound(sourceLatex.getEntity(), ChangedSounds.LATEX_FUSE_ENTITY, 1f, 1f);
                killOrDiscard(sourceLatex.getEntity(), newEntity);

                ChangedAnimationEvents.broadcastTransfurAnimation(newEntity.getEntity(), newFusionVariant, sourceLatex.absorb());
                return newEntity;
            });
        }
    }

    public AssimilationBehavior getFusionBehavior(LivingEntity assimVictim, TransfurContext transfurContext) {
        if (transfurContext.source() == null)
            return null;
        var sourceEntity = transfurContext.source().map(IAbstractChangedEntity::getEntity, ILatexAssimilatedEntity::getEntity);

        IAbstractChangedEntity assimVictimVariant = IAbstractChangedEntity.forEither(assimVictim);
        if (transfurContext.source().left().isPresent() && assimVictimVariant != null) {
            Optional<TransfurVariant<?>> latexFusion = Util.getRandomSafe(
                    getFusionsFor(transfurContext.source().left().get().getSelfVariant(), assimVictimVariant.getSelfVariant()).toList(), sourceEntity.getRandom());

            if (latexFusion.isPresent()) {
                return getLatexFusionBehavior(latexFusion.get(), assimVictimVariant, transfurContext.source());
            }
        }

        if (transfurContext.source().left().isPresent()) {
            Optional<TransfurVariant<?>> latexFusion = Util.getRandomSafe(
                    getFusionsFor(transfurContext.source().left().get().getSelfVariant(), assimVictim.getClass()).toList(), sourceEntity.getRandom());

            if (latexFusion.isPresent()) {
                return getMobFusionBehavior(latexFusion.get(), assimVictim, transfurContext.source());
            }
        }

        if (transfurContext.source().right().isPresent() && assimVictimVariant != null) {
            Optional<TransfurVariant<?>> latexFusion = Util.getRandomSafe(
                    getFusionsFor(assimVictimVariant.getSelfVariant(), sourceEntity.getClass()).toList(), sourceEntity.getRandom());

            if (latexFusion.isPresent()) {
                return getMobFusionBehavior(latexFusion.get(), assimVictim, transfurContext.source());
            }
        }

        return null;
    }

    private FusionDefinition processJSONFile(ResourceLocation name, JsonObject root) throws ClassNotFoundException {
        FusionDefinition.Builder builder = new FusionDefinition.Builder();

        if (root.has("fusion")) builder.withFusion(ResourceLocation.parse(root.get("fusion").getAsString()));
        if (root.has("variant")) builder.withVariant(ResourceLocation.parse(root.get("variant").getAsString()));
        if (root.has("otherVariant")) builder.withVariant(ResourceLocation.parse(root.get("otherVariant").getAsString()));
        if (root.has("mob")) builder.withMob(root.get("mob").getAsString());

        return builder.build(name);
    }

    @Override
    @NotNull
    protected List<FusionDefinition> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        return ResourceUtil.processJSONResources(new ArrayList<>(),
                resourceManager, "latex_fusions",
                (list, filename, id, json) -> list.add(processJSONFile(id, json)),
                (exception, filename) -> Changed.LOGGER.error("Failed to load latex fusions from \"{}\" : {}", filename, exception));
    }

    @Override
    protected void apply(@NotNull List<FusionDefinition> output, @NotNull ResourceManager resources, @NotNull ProfilerFiller profiler) {
        fusionDefinitions.clear();
        fusionDefinitions.addAll(output);
    }
}
