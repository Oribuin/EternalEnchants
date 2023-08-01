package xyz.oribuin.eternalenchants.enchant;

import dev.rosewood.rosegarden.config.CommentedConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Enchant implements Listener {

    private final String id; // The id of the enchantment
    private List<EnchantTarget> targets; // The targets of the enchantment
    private int maxLevel; // The max level of the enchantment
    private boolean pvpOnly; // If the enchantment can only be applied to players vs players

    public Enchant(String id, EnchantTarget... targets) {
        this.id = id;
        this.targets = new ArrayList<>(Arrays.asList(targets));
        this.maxLevel = 1;
        this.pvpOnly = false;
    }

    /**
     * Get the id of the enchantment
     */
    public abstract void run(ContextHandler context);

    /**
     * Load all the values from the config
     *
     * @param config The config to load from
     */
    public abstract void load(CommentedConfigurationSection config);

    /**
     * Set all the values to the config
     *
     * @param config The config to set to
     */
    public abstract void set(CommentedConfigurationSection config);

    /**
     * Check if the enchantment can be applied to the item
     *
     * @param item The item to check
     * @return If the enchantment can be applied
     */
    public boolean isApplicable(ItemStack item) {
        return this.targets.stream().anyMatch(target -> target.isApplicable(item));
    }

    public String getId() {
        return id;
    }

    public List<EnchantTarget> getTargets() {
        return targets;
    }

    public void setTargets(List<EnchantTarget> targets) {
        this.targets = targets;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public boolean isPvpOnly() {
        return pvpOnly;
    }

    public void setPvpOnly(boolean pvpOnly) {
        this.pvpOnly = pvpOnly;
    }

}
