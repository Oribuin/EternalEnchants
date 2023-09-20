package xyz.oribuin.eternalenchants.enchant.impl;

import com.google.common.collect.Sets;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalenchants.enchant.ContextHandler;
import xyz.oribuin.eternalenchants.enchant.Enchant;
import xyz.oribuin.eternalenchants.enchant.EnchantTarget;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SmeltEnchant extends Enchant {

    private final Set<Block> toSmelt;
    private final Map<Material, Material> materialMapping;

    public SmeltEnchant() {
        super("smelt", EnchantTarget.PICKAXE);

        this.description = "Smelts any blocks broken.";
        this.toSmelt = Sets.newConcurrentHashSet();
        this.materialMapping = new HashMap<>();
    }

    @Override
    public void run(ContextHandler context) {
        // TODO: Support explode enchant
        final BlockBreakEvent breakEvent = context.as(BlockBreakEvent.class);

        if (breakEvent != null) {
            if (breakEvent.getPlayer().getGameMode() == GameMode.CREATIVE)
                return;

            this.toSmelt.add(breakEvent.getBlock());
            return;
        }

        final BlockDropItemEvent dropEvent = context.as(BlockDropItemEvent.class);
        if (dropEvent == null)
            return;

        if (!this.toSmelt.remove(dropEvent.getBlock()))
            return;

        for (final Item item : dropEvent.getItems()) {
            final Material smelted = this.getSmelted(item.getItemStack().getType());
            if (smelted == item.getItemStack().getType())
                continue;

            item.setItemStack(new ItemStack(smelted, item.getItemStack().getAmount()));
        }

    }

    /**
     * Get the smelted material
     *
     * @param material The material to smelt
     * @return The smelted material
     */
    @NotNull
    public Material getSmelted(final Material material) {
        return this.materialMapping.getOrDefault(material, material);
    }

    @Override
    public void load() {
        final ConfigurationSection section = this.config.getConfigurationSection("materials");
        if (section == null) return;

        for (final String key : section.getKeys(false)) {
            final String value = section.getString(key);
            if (value == null)
                continue;

            final Material before = Material.getMaterial(key);
            final Material after = Material.getMaterial(value);

            if (before == null || after == null)
                continue;

            this.materialMapping.put(before, after);
        }
    }

    @Override
    public void set() {

        if (this.materialMapping.isEmpty()) {
            this.materialMapping.put(Material.COBBLESTONE, Material.STONE);
            this.materialMapping.put(Material.RAW_IRON, Material.IRON_INGOT);
            this.materialMapping.put(Material.RAW_GOLD, Material.GOLD_INGOT);
            this.materialMapping.put(Material.RAW_COPPER, Material.COPPER_INGOT);
        }

        for (final Map.Entry<Material, Material> entry : this.materialMapping.entrySet()) {
            this.config.set("materials." + entry.getKey().name(), entry.getValue().name());
        }
    }

}
