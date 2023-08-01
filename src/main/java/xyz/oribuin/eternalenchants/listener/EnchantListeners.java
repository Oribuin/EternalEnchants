package xyz.oribuin.eternalenchants.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalenchants.EternalEnchants;
import xyz.oribuin.eternalenchants.enchant.ContextHandler;
import xyz.oribuin.eternalenchants.manager.EnchantManager;

public class EnchantListeners implements Listener {

    private final EternalEnchants plugin;
    private final EnchantManager manager;

    public EnchantListeners(EternalEnchants plugin) {
        this.plugin = plugin;
        this.manager = plugin.getManager(EnchantManager.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        this.manager.runEnchants(item, enchant -> enchant.run(new ContextHandler(event, item, event.getPlayer())));
    }

}
