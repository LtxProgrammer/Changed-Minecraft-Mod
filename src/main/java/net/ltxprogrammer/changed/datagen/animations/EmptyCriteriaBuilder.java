package net.ltxprogrammer.changed.datagen.animations;

import com.google.gson.JsonObject;

public final class EmptyCriteriaBuilder implements AnimationCriteriaBuilder {
    public static final AnimationCriteriaBuilder INSTANCE = new EmptyCriteriaBuilder();

    private EmptyCriteriaBuilder() {}

    @Override
    public JsonObject build() {
        return new JsonObject();
    }
}
