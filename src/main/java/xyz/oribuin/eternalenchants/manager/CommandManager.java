package xyz.oribuin.eternalenchants.manager;

import xyz.oribuin.eternalenchants.command.EnchantCommandWrapper;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.manager.AbstractCommandManager;

import java.util.List;

public class CommandManager extends AbstractCommandManager {

    public CommandManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public List<Class<? extends RoseCommandWrapper>> getRootCommands() {
        return List.of(EnchantCommandWrapper.class);
    }

    @Override
    public List<String> getArgumentHandlerPackages() {
        return List.of("xyz.oribuin.eternalenchants.command.argument");
    }

}
