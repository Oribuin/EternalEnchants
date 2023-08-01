package xyz.oribuin.eternalenchants;

import org.bukkit.plugin.PluginManager;
import xyz.oribuin.eternalenchants.listener.EnchantListeners;
import xyz.oribuin.eternalenchants.manager.CommandManager;
import xyz.oribuin.eternalenchants.manager.ConfigurationManager;
import xyz.oribuin.eternalenchants.manager.EnchantManager;
import xyz.oribuin.eternalenchants.manager.LocaleManager;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;

import java.util.List;

public class EternalEnchants extends RosePlugin {

    private static EternalEnchants instance;

    public static EternalEnchants getInstance() {
        return instance;
    }

    public EternalEnchants() {
        super(-1, -1, ConfigurationManager.class, null, LocaleManager.class, CommandManager.class);

        instance = this;
    }

    @Override
    protected void enable() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new EnchantListeners(this), this);
    }

    @Override
    protected void disable() {

    }

    @Override
    protected List<Class<? extends Manager>> getManagerLoadPriority() {
        return List.of(EnchantManager.class);
    }

}
