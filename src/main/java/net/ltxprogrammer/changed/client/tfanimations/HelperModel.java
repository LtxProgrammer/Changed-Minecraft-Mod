package net.ltxprogrammer.changed.client.tfanimations;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface HelperModel extends Supplier<ModelPart> {
    ModelPart getModelPart();
    void prepareModelPart(HumanoidModel<?> model);

    @Override
    default ModelPart get() {
        return getModelPart();
    }

    default ModelPart prepareAndGet(HumanoidModel<?> model) {
        prepareModelPart(model);
        return getModelPart();
    }

    static HelperModel fixed(ModelPart part) {
        return new HelperModel() {
            @Override
            public ModelPart getModelPart() {
                return part;
            }

            @Override
            public void prepareModelPart(HumanoidModel<?> model) {

            }
        };
    }

    static HelperModel withPrepare(ModelPart part, BiConsumer<ModelPart, HumanoidModel<?>> fn) {
        return new HelperModel() {
            @Override
            public ModelPart getModelPart() {
                return part;
            }

            @Override
            public void prepareModelPart(HumanoidModel<?> model) {
                fn.accept(part, model);
            }
        };
    }
}
