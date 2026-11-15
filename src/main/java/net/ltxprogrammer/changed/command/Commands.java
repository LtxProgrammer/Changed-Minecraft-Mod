package net.ltxprogrammer.changed.command;

import net.minecraftforge.event.RegisterCommandsEvent;

public class Commands {
    private Commands() {}

    public static void registerCommands(RegisterCommandsEvent event) {
        CommandAbilityTree.registerCommands(event);
        CommandAnimate.registerCommands(event);
        CommandEmote.registerCommands(event);
        CommandPale.registerCommands(event);
        CommandTame.registerCommands(event);
        CommandTransfur.registerCommands(event);
        CommandUUID.registerCommands(event);
    }
}
