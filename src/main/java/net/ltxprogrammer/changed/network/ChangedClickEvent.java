package net.ltxprogrammer.changed.network;

import net.minecraft.network.chat.ClickEvent;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChangedClickEvent extends ClickEvent {
    private final ChangedAction action;
    private final String value;

    public ChangedClickEvent(ChangedAction action, String value) {
        super(Action.RUN_COMMAND, "");
        this.action = action;
        this.value = value;
    }

    public ChangedAction getChangedAction() {
        return action;
    }

    public String getChangedValue() {
        return value;
    }

    public String toString() {
        return "ChangedClickEvent{action=" + this.action + ", value='" + this.value + "'}";
    }

    public enum ChangedAction {
        FRIENDLY_TF_CONSENT("friendly_tf_consent", true);

        private static final Map<String, ChangedAction> LOOKUP = Arrays.stream(values()).collect(Collectors.toMap(ChangedAction::getName, Function.identity()));

        private final boolean allowFromServer;
        private final String name;

        ChangedAction(String name, boolean allowFromServer) {
            this.name = name;
            this.allowFromServer = allowFromServer;
        }

        public boolean isAllowedFromServer() {
            return this.allowFromServer;
        }

        public String getName() {
            return this.name;
        }

        public static ChangedAction getByName(String name) {
            return LOOKUP.get(name);
        }
    }
}
