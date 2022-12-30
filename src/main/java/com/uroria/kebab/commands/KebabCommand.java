package com.uroria.kebab.commands;

import java.util.List;

public abstract class KebabCommand {
    public abstract String getCommandName();
    public abstract void execute(CommandSource commandSource, String[] arguments);
    public abstract List<String> getTabComplete(CommandSource commandSource, String[] arguments);
}
