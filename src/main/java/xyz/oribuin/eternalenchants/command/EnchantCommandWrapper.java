package xyz.oribuin.eternalenchants.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;

import java.util.List;

public class EnchantCommandWrapper extends RoseCommandWrapper {

    public EnchantCommandWrapper(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public String getDefaultName() {
        return "enchant";
    }

    @Override
    public List<String> getDefaultAliases() {
        return List.of("eenchants", "enchants");
    }

    @Override
    public List<String> getCommandPackages() {
        return List.of("xyz.oribuin.eternalenchants.command.command");
    }

    @Override
    public boolean includeBaseCommand() {
        return true;
    }

    @Override
    public boolean includeHelpCommand() {
        return true;
    }

    @Override
    public boolean includeReloadCommand() {
        return true;
    }

}
