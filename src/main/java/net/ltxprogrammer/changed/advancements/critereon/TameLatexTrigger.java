package net.ltxprogrammer.changed.advancements.critereon;

import com.google.gson.JsonObject;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;

public class TameLatexTrigger extends SimpleCriterionTrigger<TameLatexTrigger.TriggerInstance> {
    static final ResourceLocation ID = Changed.modResource("tame_latex");

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject json, EntityPredicate.Composite composite, DeserializationContext context) {
        EntityPredicate.Composite entitypredicate$composite = EntityPredicate.Composite.fromJson(json, "entity", context);
        return new TriggerInstance(composite, entitypredicate$composite);
    }

    public void trigger(ServerPlayer player, LatexEntity entity) {
        LootContext lootcontext = EntityPredicate.createContext(player, entity);
        this.trigger(player, instance -> instance.matches(lootcontext));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final EntityPredicate.Composite entity;

        public TriggerInstance(EntityPredicate.Composite p_68846_, EntityPredicate.Composite p_68847_) {
            super(TameLatexTrigger.ID, p_68846_);
            this.entity = p_68847_;
        }

        public static TriggerInstance tamedAnimal() {
            return new TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY);
        }

        public static TriggerInstance tamedAnimal(EntityPredicate entityPredicate) {
            return new TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(entityPredicate));
        }

        public boolean matches(LootContext p_68853_) {
            return this.entity.matches(p_68853_);
        }

        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject jsonobject = super.serializeToJson(context);
            jsonobject.add("entity", this.entity.toJson(context));
            return jsonobject;
        }
    }
}