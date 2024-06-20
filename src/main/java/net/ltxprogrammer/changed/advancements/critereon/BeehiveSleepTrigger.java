package net.ltxprogrammer.changed.advancements.critereon;

import com.google.gson.JsonObject;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class BeehiveSleepTrigger extends SimpleCriterionTrigger<BeehiveSleepTrigger.TriggerInstance> {
    static final ResourceLocation ID = Changed.modResource("beehive_sleep");

    public ResourceLocation getId() { return ID; }

    public TriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite predicate, DeserializationContext context) {
        return new TriggerInstance(predicate);
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, (predicate) -> true);
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        public TriggerInstance(EntityPredicate.Composite entityPredicate) {
            super(ID, entityPredicate);
        }

        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject jsonObject = super.serializeToJson(context);
            return jsonObject;
        }
    }
}
