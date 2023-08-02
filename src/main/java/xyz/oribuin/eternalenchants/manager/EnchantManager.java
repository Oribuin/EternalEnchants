package xyz.oribuin.eternalenchants.manager;

import com.jeff_media.morepersistentdatatypes.DataType;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.config.CommentedFileConfiguration;
import dev.rosewood.rosegarden.manager.Manager;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import xyz.oribuin.eternalenchants.EternalEnchants;
import xyz.oribuin.eternalenchants.enchant.Enchant;
import xyz.oribuin.eternalenchants.enchant.impl.ExplodeEnchant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EnchantManager extends Manager {

    private final Map<String, Enchant> enchants = new HashMap<>();
    private static final NamespacedKey ENCHANTS_KEY = new NamespacedKey(EternalEnchants.getInstance(), "enchants");

    public EnchantManager(RosePlugin rosePlugin) {
        super(rosePlugin);
    }

    @Override
    public void reload() {
        File folder = new File(this.rosePlugin.getDataFolder(), "enchants");
        if (!folder.exists())
            folder.mkdirs();

        // Register all the enchantments
        this.enchants.put("explode", new ExplodeEnchant());

        // Load all the enchant configs
        for (Map.Entry<String, Enchant> enchants : new HashMap<>(this.enchants).entrySet()) {
            Enchant enchant = enchants.getValue();
            File file = new File(folder, enchants.getKey() + ".yml");
            boolean newFile = false;

            try {
                if (!file.exists()) {
                    file.createNewFile();
                    newFile = true;
                }

                CommentedFileConfiguration config = CommentedFileConfiguration.loadConfiguration(file);

                // Create the enchant config if needed.
                if (newFile) {
                    enchant.set(config);
                }

                // Load the enchant config
                enchant.load(config);
                config.save(file);
            } catch (IOException ignored) {
                this.rosePlugin.getLogger().severe("Unable to create enchant config for " + enchants.getKey() + "!");
            }
        }
    }

    /**
     * Run all the enchants for an item
     *
     * @param itemStack The item to run the enchants for
     */
    public void runEnchants(ItemStack itemStack, Consumer<Enchant> consumer) {
        for (Enchant enchant : this.getEnchants(itemStack)) {
            if (!enchant.isApplicable(itemStack))
                continue;

            if (!this.hasEnchant(itemStack, enchant.getId()))
                continue;

            consumer.accept(enchant);
        }
    }

    /**
     * Apply an enchant to an item
     *
     * @param itemStack The item to apply the enchant to
     * @param enchant   The enchant to apply
     * @return If the enchant was applied
     */
    public boolean apply(ItemStack itemStack, Enchant enchant) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return false;

        // Make sure the enchant exists and is applicable
        if (enchant == null || !enchant.isApplicable(itemStack))
            return false;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        List<String> enchants = container.get(ENCHANTS_KEY, DataType.asList(DataType.STRING));
        if (enchants == null)
            enchants = new ArrayList<>();

        if (enchants.contains(enchant.getId()))
            return false;

        // Copy and modify the list
        enchants.add(enchant.getId());

        container.set(ENCHANTS_KEY, DataType.asList(DataType.STRING), enchants);
        itemStack.lore(List.of(Component.text("Enchantments: " + enchants)));
        itemStack.setItemMeta(meta);
        return true;
    }

    /**
     * Check if an item has an enchant
     *
     * @param itemStack the item to check
     * @param enchant   the enchant to check for
     * @return if the item has the enchant
     */
    public boolean hasEnchant(ItemStack itemStack, String enchant) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        List<String> enchants = container.get(ENCHANTS_KEY, DataType.asList(DataType.STRING));
        if (enchants == null || enchants.isEmpty())
            return false;

        return enchants.contains(enchant);
    }

    /**
     * Strip an enchant from an item
     *
     * @param itemStack the item to strip the enchant from
     * @param enchant   the enchant to strip
     * @return if the enchant was stripped
     */
    public boolean strip(ItemStack itemStack, String enchant) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        List<String> enchants = container.get(ENCHANTS_KEY, DataType.asList(DataType.STRING));
        if (enchants == null || enchants.isEmpty())
            return false;

        // Copy and modify the list
        enchants = new ArrayList<>(enchants);
        enchants.remove(enchant);

        container.set(ENCHANTS_KEY, DataType.asList(DataType.STRING), enchants);
        itemStack.setItemMeta(meta);
        return true;
    }

    /**
     * Get all the enchants on an item
     *
     * @param itemStack the item to get the enchants for
     * @return the enchants on the item
     */
    public List<Enchant> getEnchants(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return List.of();

        PersistentDataContainer container = meta.getPersistentDataContainer();
        List<String> enchants = container.get(ENCHANTS_KEY, DataType.asList(DataType.STRING));
        if (enchants == null || enchants.isEmpty())
            return List.of();

        return this.enchants.values().stream().filter(enchant -> enchants.contains(enchant.getId())).toList();
    }

    @Override
    public void disable() {
        this.enchants.clear();
    }

    public Map<String, Enchant> getEnchants() {
        return enchants;
    }

}
