package xyz.oribuin.eternalenchants.manager;

import com.jeff_media.morepersistentdatatypes.DataType;
import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.manager.Manager;
import dev.rosewood.rosegarden.utils.NMSUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class EnchantManager extends Manager implements Listener {

    private static final NamespacedKey ENCHANTS_KEY = new NamespacedKey(EternalEnchants.getInstance(), "enchants");
    private static final Random RANDOM = new Random();
    private final Map<String, Enchant> enchants = new HashMap<>();

    public EnchantManager(RosePlugin rosePlugin) {
        super(rosePlugin);

        this.rosePlugin.getServer().getPluginManager().registerEvents(this, this.rosePlugin);
    }

    @Override
    public void reload() {
        final EnchantLoadingEvent event = new EnchantLoadingEvent();
        Bukkit.getPluginManager().callEvent(event);

        final File folder = new File(this.rosePlugin.getDataFolder(), "enchants");
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
        final ItemStack itemStack = handler.itemStack();

        // Get all the enchants on the item
        final List<Enchant> enchants = this.getEnchants(itemStack);
        if (enchants.isEmpty())
            return;

        // Sort the enchants by priority
        enchants.sort(Comparator.comparingInt(value -> value.getPriority().getOrder()));
//        enchants.sort((e1, e2) -> e2.getPriority().compareTo(e1.getPriority()));

        // Run the enchants
        for (final Enchant enchant : enchants) {
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
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return false;

        // Make sure the enchant exists and is applicable
        if (enchant == null || !enchant.isApplicable(itemStack))
            return false;

        final PersistentDataContainer container = meta.getPersistentDataContainer();
        List<String> enchants = container.getOrDefault(
                ENCHANTS_KEY,
                DataType.asList(DataType.STRING),
                new ArrayList<>()
        );

        if (enchants.contains(enchant.getId()))
            return false;

        // Copy and modify the list
        enchants.add(enchant.getId());

        // TODO: Make spigot compatible
        container.set(ENCHANTS_KEY, DataType.asList(DataType.STRING), enchants);
        meta.lore(List.of(Component.text("Enchantments: " + enchants)));
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
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return false;

        final PersistentDataContainer container = meta.getPersistentDataContainer();
        final List<String> enchants = container.get(ENCHANTS_KEY, DataType.asList(DataType.STRING));
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
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return false;

        final PersistentDataContainer container = meta.getPersistentDataContainer();
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
        final ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return new ArrayList<>();

        final PersistentDataContainer container = meta.getPersistentDataContainer();
        final List<String> enchants = container.get(ENCHANTS_KEY, DataType.asList(DataType.STRING));
        if (enchants == null || enchants.isEmpty())
            return new ArrayList<>();

        return this.enchants.values()
                .stream()
                .filter(enchant -> enchants.contains(enchant.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Take durability off an item
     *
     * @param player Who is damaging the item
     * @param stack  The item to damage
     */
    public void damage(final Player player, final ItemStack stack) {
        final ItemMeta meta = stack.getItemMeta();
        if (!(meta instanceof Damageable damageable) || damageable.isUnbreakable())
            return;

        if (NMSUtil.isPaper()) {
            player.damageItemStack(stack, 1);
            return;
        }

        final int level = stack.getEnchantmentLevel(Enchantment.DURABILITY);
        final int percentage = level == 0 ? 100 : (100 / (level + 1));

        if (percentage >= RANDOM.nextDouble(0, 100)) {
            damageable.setDamage(damageable.getDamage() + 1);
            stack.setItemMeta(damageable);
        }
    }

    /**
     * Spawn an experience orb at a block
     *
     * @param block The block to spawn the orb at
     */
    public void createEXP(final Block block) {
        block.getWorld().spawn(
                block.getLocation(),
                ExperienceOrb.class,
                orb -> orb.setExperience(RANDOM.nextInt(1, 6))
        );
    }

    @Override
    public void disable() {
        this.enchants.clear();
    }

    public Map<String, Enchant> getEnchants() {
        return enchants;
    }

}
