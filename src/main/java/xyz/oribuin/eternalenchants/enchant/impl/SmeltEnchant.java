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
import xyz.oribuin.eternalenchants.enchant.ContextHandler;
import xyz.oribuin.eternalenchants.enchant.Enchant;
import xyz.oribuin.eternalenchants.enchant.EnchantTarget;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SmeltEnchant extends Enchant {

    private final Set<Block> toSmelt = Sets.newConcurrentHashSet();
    private final Map<Material, Material> materialMapping = new HashMap<>();

    public SmeltEnchant() {
        super("smelt", EnchantTarget.PICKAXE);

        this.description = "Smelts any blocks broken.";
    }

    @Override
    public void run(ContextHandler context) {
        BlockBreakEvent breakEvent = context.as(BlockBreakEvent.class);

        if (breakEvent != null) {
            if (breakEvent.getPlayer().getGameMode() == GameMode.CREATIVE)
                return;

            this.toSmelt.add(breakEvent.getBlock());
            return;
        }

        BlockDropItemEvent dropEvent = context.as(BlockDropItemEvent.class);
        if (dropEvent != null && this.toSmelt.remove(dropEvent.getBlock())) {
            for (Item item : dropEvent.getItems()) {
                Material smelted = this.getSmelted(item.getItemStack().getType());

                if (smelted == item.getItemStack().getType())
                    continue;

                item.setItemStack(new ItemStack(smelted, item.getItemStack().getAmount()));
            }
        }

    }

    /**
     * Get the smelted material
     *
     * @param material The material to smelt
     * @return The smelted material
     */
    public Material getSmelted(Material material) {
        return this.materialMapping.getOrDefault(material, material);
    }

    @Override
    public void load() {
        ConfigurationSection section = this.config.getConfigurationSection("materials");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            String value = section.getString(key);
            if (value == null)
                continue;

            Material before = Material.getMaterial(key);
            Material after = Material.getMaterial(value);

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

        for (Map.Entry<Material, Material> entry : this.materialMapping.entrySet()) {
            this.config.set("materials." + entry.getKey().name(), entry.getValue().name());
        }
    }

}
