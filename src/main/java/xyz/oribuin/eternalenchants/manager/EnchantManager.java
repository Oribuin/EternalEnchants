package xyz.oribuin.eternalenchants.manager;

import com.jeff_media.morepersistentdatatypes.DataType;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import xyz.oribuin.eternalenchants.EternalEnchants;
import xyz.oribuin.eternalenchants.enchant.ContextHandler;
import xyz.oribuin.eternalenchants.enchant.Enchant;
import xyz.oribuin.eternalenchants.enchant.impl.ExplodeEnchant;
import xyz.oribuin.eternalenchants.enchant.impl.SmeltEnchant;
import xyz.oribuin.eternalenchants.enchant.impl.TurboBreakerEnchant;
import xyz.oribuin.eternalenchants.event.EnchantLoadingEvent;
import xyz.oribuin.eternalenchants.manager.ConfigurationManager.Setting;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EnchantManager extends Manager implements Listener {

    private static final NamespacedKey ENCHANTS_KEY = new NamespacedKey(EternalEnchants.getInstance(), "enchants");
    private final Map<String, Enchant> enchants = new HashMap<>();

    public EnchantManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.rosePlugin.getServer().getPluginManager().registerEvents(this, this.rosePlugin);
    }

    @Override
    public void reload() {
        EnchantLoadingEvent event = new EnchantLoadingEvent();
        Bukkit.getPluginManager().callEvent(event);

        File folder = new File(this.rosePlugin.getDataFolder(), "enchants");
        if (!folder.exists())
            folder.mkdirs();

        event.getRegisteredEnchants().forEach((s, enchant) -> {
            // Don't register the enchant if it's disabled
            if (Setting.DISABLED_ENCHANTS.getStringList().contains(enchant.getId()))
                return;

            // Register the enchant
            enchant.register(folder);
            this.enchants.put(s, enchant);
        });
    }

    /**
     * Load all the enchants from the config
     *
     * @param event The event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onLoad(EnchantLoadingEvent event) {
        event.registerEnchant(new ExplodeEnchant());
        event.registerEnchant(new SmeltEnchant());
        event.registerEnchant(new TurboBreakerEnchant());
    }

    /**
     * Run enchantments for an item
     *
     * @param handler The context handler
     */
    public void runEnchants(ContextHandler handler) {
        ItemStack itemStack = handler.itemStack();

        // Check if the item has enchants
        for (Enchant enchant : this.getEnchants(itemStack)) {
            if (!enchant.isApplicable(itemStack))
                continue;

            enchant.run(handler);
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
