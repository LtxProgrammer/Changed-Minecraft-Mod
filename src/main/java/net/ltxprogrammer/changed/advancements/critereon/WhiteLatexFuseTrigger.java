package net.ltxprogrammer.changed.advancements.critereon;

import com.google.gson.JsonObject;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

public class WhiteLatexFuseTrigger extends SimpleCriterionTrigger<WhiteLatexFuseTrigger.TriggerInstance> {
    static final ResourceLocation ID = Changed.modResource("white_latex_fuse");

    public ResourceLocation getId() { return ID; }

    public TriggerInstance createInstance(JsonObject jsonObject, EntityPredicate.Composite predicate, DeserializationContext context) {
        return new TriggerInstance(predicate, jsonObject.has("ticks") ? GsonHelper.getAsInt(jsonObject, "ticks") : 0);
    }

    public void trigger(ServerPlayer player, int ticks) {
        this.trigger(player, (predicate) -> predicate.matches(ticks));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final int ticks;

        public TriggerInstance(EntityPredicate.Composite entityPredicate, int ticks) {
            super(ID, entityPredicate);
            this.ticks = ticks;
        }

        public boolean matches(int ticks) {
            return this.ticks <= ticks;
        }

        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject jsonObject = super.serializeToJson(context);
            jsonObject.addProperty("ticks", this.ticks);
            return jsonObject;
        }
    }
}
