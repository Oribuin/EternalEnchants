package xyz.oribuin.eternalenchants.listener;

import dev.rosewood.rosegarden.utils.NMSUtil;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalenchants.EternalEnchants;
import xyz.oribuin.eternalenchants.enchant.ContextHandler;
import xyz.oribuin.eternalenchants.event.EnchantExplodeEvent;
import xyz.oribuin.eternalenchants.manager.EnchantManager;

public class EnchantListeners implements Listener {

    private final EternalEnchants plugin;
    private final EnchantManager manager;

    public EnchantListeners(EternalEnchants plugin) {
        this.plugin = plugin;
        this.manager = plugin.getManager(EnchantManager.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBreak(BlockBreakEvent event) {
        this.manager.runEnchants(new ContextHandler(event, event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onExplode(EnchantExplodeEvent event) {
        final ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        this.manager.runEnchants(new ContextHandler(event, tool, event.getPlayer()));

        for (final Block block : event.getToExplode()) {
            // Simulate the natural block destruction
            if (NMSUtil.isPaper()) {

                // If the block is air, don't do anything
                if (block.breakNaturally(tool, true, true)) {

                    // If the player is in creative mode, don't damage the tool
                    if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        System.out.println("DAMAGING TOOL");
                        this.manager.damage(event.getPlayer(), tool);
                    }
                }

                continue;
            }

            // stinky spigot method :(
            if (block.breakNaturally(tool) && tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
                this.manager.createEXP(block);

                // If the player is in creative mode, don't damage the tool
                if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                    this.manager.damage(event.getPlayer(), tool);
                }
            }

        }

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onDrop(BlockDropItemEvent event) {
        this.manager.runEnchants(new ContextHandler(event, event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer()));
    }

}
