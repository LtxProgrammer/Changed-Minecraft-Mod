package net.ltxprogrammer.changed.client.tfanimations;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface HelperModel extends Supplier<ModelPart> {
    ModelPart getModelPart();
    TransfurAnimator.ModelPose prepareModelPart(TransfurAnimator.ModelPose before, HumanoidModel<?> model);
    void transitionOriginal(HumanoidModel<?> model, float preMorphProgression);

    @Override
    default ModelPart get() {
        return getModelPart();
    }

    static HelperModel fixed(ModelPart part) {
        return new HelperModel() {
            @Override
            public ModelPart getModelPart() {
                return part;
            }

            @Override
            public TransfurAnimator.ModelPose prepareModelPart(TransfurAnimator.ModelPose before, HumanoidModel<?> model) {
                return before;
            }

            @Override
            public void transitionOriginal(HumanoidModel<?> model, float preMorphProgression) {

            }
        };
    }

    static HelperModel withPrepare(ModelPart part, TriFunction<TransfurAnimator.ModelPose, ModelPart, HumanoidModel<?>, TransfurAnimator.ModelPose> fn) {
        return new HelperModel() {
            @Override
            public ModelPart getModelPart() {
                return part;
            }

            @Override
            public TransfurAnimator.ModelPose prepareModelPart(TransfurAnimator.ModelPose before, HumanoidModel<?> model) {
                return fn.apply(before, part, model);
            }

            @Override
            public void transitionOriginal(HumanoidModel<?> model, float preMorphProgression) {

            }
        };
    }

    static HelperModel withPrepareAndTransition(ModelPart part,
                                                TriFunction<TransfurAnimator.ModelPose, ModelPart, HumanoidModel<?>, TransfurAnimator.ModelPose> fn,
                                                BiConsumer<HumanoidModel<?>, Float> transition) {
        return new HelperModel() {
            @Override
            public ModelPart getModelPart() {
                return part;
            }

            @Override
            public TransfurAnimator.ModelPose prepareModelPart(TransfurAnimator.ModelPose before, HumanoidModel<?> model) {
                return fn.apply(before, part, model);
            }

            @Override
            public void transitionOriginal(HumanoidModel<?> model, float preMorphProgression) {
                transition.accept(model, preMorphProgression);
            }
        };
    }

    interface TriFunction<P0, P1, P2, R> {
        R apply(P0 p0, P1 p1, P2 p2);
    }
}
