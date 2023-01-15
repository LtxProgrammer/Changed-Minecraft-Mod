package net.ltxprogrammer.changed.entity.variant;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenderedVariant<M extends LatexEntity & GenderedEntity, F extends LatexEntity & GenderedEntity> extends LatexVariant<LatexEntity> {
    final LatexVariant<M> male;
    final LatexVariant<F> female;

    @Override
    public LatexVariant<LatexEntity> clone() {
        throw new NotImplementedException();
    }

    public LatexVariant<M> male() {
        return male;
    }

    public LatexVariant<F> female() {
        return female;
    }

    public GenderedVariant(ResourceLocation base, LatexVariant<M> male, LatexVariant<F> female) {
        super(base, null,
                LatexType.NEUTRAL, 1.0f, 1.0f, 1.0f,
                BreatheMode.NORMAL, 0.7f, false, 0, 0,
                false, false, false, false, false,
                true, null, TransfurMode.REPLICATION, Optional.empty(),
                Optional.empty(), new ArrayList<>(), 0.0F, ChangedSounds.POISON.getLocation());
        this.male = male;
        this.female = female;
    }

    public static class Builder<M extends LatexEntity & GenderedEntity, F extends LatexEntity & GenderedEntity> extends LatexVariant.Builder<LatexEntity> {
        private final LatexVariant.Builder<M> maleBuilder;
        private final LatexVariant.Builder<F> femaleBuilder;

        public Builder(LatexVariant.Builder<M> maleBuilder, LatexVariant.Builder<F> femaleBuilder) {
            super(null);
            this.maleBuilder = maleBuilder;
            this.femaleBuilder = femaleBuilder;
        }


        public Builder<M, F> faction(LatexType type) {
            this.maleBuilder.type = type; this.femaleBuilder.type = type; return this;
        }

        public Builder<M, F> groundSpeed(float factor) {
            this.maleBuilder.groundSpeed = factor; this.femaleBuilder.groundSpeed = factor; return this;
        }

        public Builder<M, F> swimSpeed(float factor) {
            this.maleBuilder.swimSpeed = factor; this.femaleBuilder.swimSpeed = factor; return this;
        }

        public Builder<M, F> gills() {
            return gills(false);
        }

        public Builder<M, F> gills(boolean suffocate_on_land) {
            this.maleBuilder.breatheMode = suffocate_on_land ? BreatheMode.WATER : BreatheMode.ANY;
            this.femaleBuilder.breatheMode = suffocate_on_land ? BreatheMode.WATER : BreatheMode.ANY;return this;
        }

        public Builder<M, F> breatheMode(BreatheMode mode) {
            this.maleBuilder.breatheMode = mode; this.femaleBuilder.breatheMode = mode; return this;
        }

        public Builder<M, F> reducedFall() {
            this.maleBuilder.reducedFall = true; this.femaleBuilder.reducedFall = true; return this;
        }

        public Builder<M, F> reducedFall(boolean v) {
            this.maleBuilder.reducedFall = v; this.femaleBuilder.reducedFall = v; return this;
        }

        public Builder<M, F> canClimb() {
            this.maleBuilder.canClimb = true; this.femaleBuilder.canClimb = true; return this;
        }

        public Builder<M, F> nightVision() {
            this.maleBuilder.nightVision = true; this.femaleBuilder.nightVision = true; return this;
        }

        public Builder<M, F> nightVision(boolean v) {
            this.maleBuilder.nightVision = v; this.femaleBuilder.nightVision = v; return this;
        }
        public Builder<M, F> cannotWalk() {
            this.maleBuilder.cannotWalk = true; this.femaleBuilder.cannotWalk = true; return this;
        }

        public Builder<M, F> cannotWalk(boolean v) {
            this.maleBuilder.cannotWalk = v; this.femaleBuilder.cannotWalk = v; return this;
        }
        public Builder<M, F> noVision() {
            this.maleBuilder.noVision = true; this.femaleBuilder.noVision = true; return this;
        }

        public Builder<M, F> noVision(boolean v) {
            this.maleBuilder.noVision = v; this.femaleBuilder.noVision = v; return this;
        }

        public Builder<M, F> canClimb(boolean v) {
            this.maleBuilder.canClimb = v; this.femaleBuilder.canClimb = v; return this;
        }

        public <E extends PathfinderMob> Builder<M, F> scares(Class<E> type) {
            this.maleBuilder.scares.add(type); this.femaleBuilder.scares.add(type); return this;
        }

        public Builder<M, F> scares(List<Class<? extends PathfinderMob>> v) {
            this.maleBuilder.scares = v; this.femaleBuilder.scares = v; return this;
        }

        public Builder<M, F> weakLungs() {
            this.maleBuilder.weakLungs = true; this.femaleBuilder.weakLungs = true; return this;
        }

        public Builder<M, F> weakLungs(boolean v) {
            this.maleBuilder.weakLungs = v; this.femaleBuilder.weakLungs = v; return this;
        }

        public Builder<M, F> stepSize(float factor) {
            this.maleBuilder.stepSize = factor; this.femaleBuilder.stepSize = factor; return this;
        }

        public Builder<M, F> glide() {
            return glide(true);
        }

        public Builder<M, F> glide(boolean enable) {
            this.maleBuilder.canGlide = enable; this.femaleBuilder.canGlide = enable; return this;
        }

        public Builder<M, F> doubleJump() {
            return this.extraJumps(1);
        }

        public Builder<M, F> extraJumps(int count) {
            this.maleBuilder.extraJumpCharges = count; this.femaleBuilder.extraJumpCharges = count; return this;
        }

        public Builder<M, F> additionalHealth(int value) {
            this.maleBuilder.additionalHealth = value; this.femaleBuilder.additionalHealth = value; return this;
        }

        public Builder<M, F> addAbility(AbstractAbility<?> ability) {
            this.maleBuilder.addAbility(ability); this.femaleBuilder.addAbility(ability); return this;
        }
        public Builder<M, F> noLegs() {
            this.maleBuilder.noLegs(); this.femaleBuilder.noLegs(); return this;
        }
        public Builder<M, F> extraHands() {
            this.maleBuilder.extraHands(); this.femaleBuilder.extraHands(); return this;
        }

        public Builder<M, F> absorbing() {
            return transfurMode(TransfurMode.ABSORPTION);
        }

        public Builder<M, F> transfurMode(TransfurMode mode) {
            this.maleBuilder.transfurMode = mode; this.femaleBuilder.transfurMode = mode; return this;
        }

        public Builder<M, F> fusionOf(LatexVariant<?> variantA, LatexVariant<?> variantB) {
            this.maleBuilder.fusionOf(variantA, variantB); this.femaleBuilder.fusionOf(variantA, variantB); return this;
        }

        public Builder<M, F> split(Consumer<LatexVariant.Builder<M>> maleConsumer, Consumer<LatexVariant.Builder<F>> femaleConsumer) {
            maleConsumer.accept(maleBuilder); femaleConsumer.accept(femaleBuilder);
            return this;
        }

        public static <M extends LatexEntity & GenderedEntity, F extends LatexEntity & GenderedEntity> Builder<M, F> of(Supplier<EntityType<M>> entityTypeMale, Supplier<EntityType<F>> entityTypeFemale) {
            return new Builder<>(LatexVariant.Builder.of(entityTypeMale), LatexVariant.Builder.of(entityTypeFemale));
        }

        public static <M extends LatexEntity & GenderedEntity, F extends LatexEntity & GenderedEntity> Builder<M, F> of(LatexVariant<?> variant, Supplier<EntityType<M>> entityTypeMale, Supplier<EntityType<F>> entityTypeFemale) {
            return new Builder<>(LatexVariant.Builder.of(variant, entityTypeMale), LatexVariant.Builder.of(variant, entityTypeFemale));
        }

        public static <M extends LatexEntity & GenderedEntity, F extends LatexEntity & GenderedEntity> Builder<M, F> of(GenderedVariant<?, ?> variant, Supplier<EntityType<M>> entityTypeMale, Supplier<EntityType<F>> entityTypeFemale) {
            return new Builder<>(LatexVariant.Builder.of(variant.male(), entityTypeMale), LatexVariant.Builder.of(variant.female(), entityTypeFemale));
        }

        public GenderedVariant<M, F> buildGendered(ResourceLocation formId) {
            return new GenderedVariant<M, F>(formId,
                    maleBuilder.build(Gender.MALE.convertToGendered(formId)),
                    femaleBuilder.build(Gender.FEMALE.convertToGendered(formId)));
        }
    }
}
