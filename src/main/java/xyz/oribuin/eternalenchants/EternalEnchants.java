package xyz.oribuin.eternalenchants;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import org.bukkit.plugin.PluginManager;
import xyz.oribuin.eternalenchants.listener.EnchantListeners;
import xyz.oribuin.eternalenchants.listener.LoadingListener;
import xyz.oribuin.eternalenchants.manager.CommandManager;
import xyz.oribuin.eternalenchants.manager.ConfigurationManager;
import xyz.oribuin.eternalenchants.manager.EnchantManager;
import xyz.oribuin.eternalenchants.manager.LocaleManager;

import java.util.List;

public class EternalEnchants extends RosePlugin {

    private static EternalEnchants instance;

    public EternalEnchants() {
        super(-1, -1, ConfigurationManager.class, null, LocaleManager.class, CommandManager.class);

        instance = this;
    }

    public static EternalEnchants getInstance() {
        return instance;
    }

    @Override
    protected void enable() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new LoadingListener(), this);
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
