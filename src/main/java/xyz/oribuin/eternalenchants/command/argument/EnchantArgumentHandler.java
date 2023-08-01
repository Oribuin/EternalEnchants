package xyz.oribuin.eternalenchants.command.argument;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.ArgumentParser;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentHandler;
import dev.rosewood.rosegarden.command.framework.RoseCommandArgumentInfo;
import dev.rosewood.rosegarden.utils.StringPlaceholders;
import xyz.oribuin.eternalenchants.enchant.Enchant;
import xyz.oribuin.eternalenchants.manager.EnchantManager;

import java.util.List;

public class EnchantArgumentHandler extends RoseCommandArgumentHandler<Enchant> {

    public EnchantArgumentHandler(RosePlugin rosePlugin) {
        super(rosePlugin, Enchant.class);
    }

    @Override
    protected Enchant handleInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) throws HandledArgumentException {
        String input = argumentParser.next();

        Enchant enchant = this.rosePlugin.getManager(EnchantManager.class).getEnchants().get(input.toLowerCase());
        if (enchant == null)
            throw new HandledArgumentException("argument-handler-enchant", StringPlaceholders.of("input", input.toLowerCase()));

        return enchant;
    }

    @Override
    protected List<String> suggestInternal(RoseCommandArgumentInfo argumentInfo, ArgumentParser argumentParser) {
        argumentParser.next();

        return this.rosePlugin.getManager(EnchantManager.class).getEnchants().keySet().stream().toList();
    }

}
