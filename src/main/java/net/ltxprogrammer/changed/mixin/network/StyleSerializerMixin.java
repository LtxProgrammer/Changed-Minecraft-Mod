package net.ltxprogrammer.changed.mixin.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.ltxprogrammer.changed.network.ChangedClickEvent;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;

import java.lang.reflect.Type;

@Mixin(Style.Serializer.class)
public abstract class StyleSerializerMixin {
    @WrapMethod(method = "getClickEvent")
    private static ClickEvent changed$extendClickEventDeserialize(JsonObject object, Operation<ClickEvent> original) {
        if (object.has("changedClickEvent")) {
            JsonObject jsonobject = GsonHelper.getAsJsonObject(object, "changedClickEvent");
            String s = GsonHelper.getAsString(jsonobject, "action", (String)null);
            ChangedClickEvent.ChangedAction clickevent$action = s == null ? null : ChangedClickEvent.ChangedAction.getByName(s);
            String s1 = GsonHelper.getAsString(jsonobject, "value", (String)null);
            if (clickevent$action != null && s1 != null && clickevent$action.isAllowedFromServer()) {
                return new ChangedClickEvent(clickevent$action, s1);
            }
        }

        return original.call(object);
    }

    @WrapMethod(method = "serialize(Lnet/minecraft/network/chat/Style;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;")
    private JsonElement changed$extendClickEventSerialize(Style style, Type type, JsonSerializationContext context, Operation<JsonElement> original) {
        var serial = original.call(style, type, context);
        if (serial instanceof JsonObject object) {
            if (style.getClickEvent() instanceof ChangedClickEvent changedClickEvent) {
                object.remove("clickEvent");

                JsonObject event = new JsonObject();
                event.addProperty("action", changedClickEvent.getChangedAction().getName());
                event.addProperty("value", changedClickEvent.getChangedValue());
                object.add("changedClickEvent", event);
            }
        }
        return serial;
    }
}
