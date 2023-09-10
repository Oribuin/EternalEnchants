package xyz.oribuin.eternalenchants.enchant;

import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Enchant implements Listener {

    private final String id; // The id of the enchantment
    protected String description; // The description of the enchantment
    protected CommentedFileConfiguration config; // The config of the enchantment
    protected Priority priority; // The priority of the enchantment
    protected List<EnchantTarget> targets; // The targets of the enchantment
    protected int maxLevel; // The max level of the enchantment

    public Enchant(String id, EnchantTarget target, EnchantTarget... targets) {
        this.id = id;
        this.description = "A custom enchantment.";
        this.priority = Priority.NORMAL;
        this.targets = new ArrayList<>(List.of(target));
        this.targets.addAll(Arrays.asList(targets));
        this.maxLevel = 1;
        this.config = null;
    }

    /**
     * Get the id of the enchantment
     */
    public abstract void run(ContextHandler context);

    /**
     * Register the enchantment config file
     *
     * @param primaryFolder The folder to register the config to
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public final void register(File primaryFolder) {
        File file = new File(primaryFolder, this.id.toLowerCase() + ".yml");
        boolean newFile = false;

        try {
            if (!file.exists()) {
                file.createNewFile();
                newFile = true;
            }

            CommentedFileConfiguration config = CommentedFileConfiguration.loadConfiguration(file);
            this.config = config;

            // Create the enchant config if needed.
            if (newFile || config.getKeys(false).isEmpty()) {
                this.set();
            }

            // Load the enchant config
            this.load();

            config.save(file);

            this.config = config;
        } catch (IOException ignored) {
            Bukkit.getLogger().severe("Unable to create enchant config for " + this.id + "!");
        }
    }

    /**
     * Load all the values from the config
     */
    public abstract void load();

    /**
     * Set all the values to the config
     */
    public abstract void set();

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

}
