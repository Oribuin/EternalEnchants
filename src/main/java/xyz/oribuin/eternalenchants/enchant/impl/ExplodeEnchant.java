package xyz.oribuin.eternalenchants.enchant.impl;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import xyz.oribuin.eternalenchants.enchant.ContextHandler;
import xyz.oribuin.eternalenchants.enchant.Enchant;
import xyz.oribuin.eternalenchants.enchant.EnchantTarget;
import xyz.oribuin.eternalenchants.enchant.Priority;
import xyz.oribuin.eternalenchants.event.EnchantExplodeEvent;

import java.util.List;
import java.util.stream.Collectors;

public class ExplodeEnchant extends Enchant {

    private double triggerChance; // The chance of the enchantment triggering
    private int explodeRadius; // The radius of the explosion
    private List<Material> blacklisted; // The list of blacklisted blocks

    public ExplodeEnchant() {
        super("explode", EnchantTarget.PICKAXE, EnchantTarget.SHOVEL, EnchantTarget.AXE);

        this.description = "Explodes blocks around the broken block.";
        this.priority = Priority.HIGHEST; // This enchant should be the first to run
        this.triggerChance = 50;
        this.explodeRadius = 1;
        this.blacklisted = List.of(
                Material.BEDROCK,
                Material.END_PORTAL_FRAME,
                Material.END_PORTAL,
                Material.END_GATEWAY,
                Material.BARRIER
        );
    }

    @Override
    public void run(ContextHandler context) {
        final BlockBreakEvent event = context.as(BlockBreakEvent.class);
        if (event == null)
            return;

        if (this.triggerChance != 100 && Math.random() * 100 >= this.triggerChance)
            return;

        // Play sound effect
        event.getBlock().getWorld().playSound(event.getBlock().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 25, 1);

        // Break the blocks in the radius
        final EnchantExplodeEvent explodeEvent = new EnchantExplodeEvent(event.getPlayer());

        for (int x = -this.explodeRadius; x <= this.explodeRadius; x++) {
            for (int z = -this.explodeRadius; z <= this.explodeRadius; z++) {
                for (int y = -this.explodeRadius; y <= this.explodeRadius; y++) {
                    final Block relative = event.getBlock().getRelative(x, y, z);
                    if (relative.getType().isAir() || relative.isLiquid())
                        continue;

                    // Check if the block is blacklisted
                    if (this.blacklisted.contains(relative.getType()))
                        continue;

                    // TODO: Check if the block is protected by a plugin like WorldGuard
//                    if (isProtected)
//                        return;

                    explodeEvent.add(relative); // Add the block to the list of blocks to explode

                    relative.getWorld().createExplosion(relative.getLocation(), -1, false);
                    event.getPlayer().incrementStatistic(Statistic.MINE_BLOCK, relative.getType(), 1);
                }
            }
        }

        // Call the event
        Bukkit.getPluginManager().callEvent(explodeEvent);
    }

    @Override
    public void load() {
        super.load();

        this.triggerChance = this.config.getDouble("trigger-chance", this.triggerChance);
        this.explodeRadius = this.config.getInt("explode-radius", this.explodeRadius);
        this.blacklisted = this.config.getStringList("blacklisted").stream()
                .map(Material::matchMaterial)
                .filter(material -> material != null && material.isBlock())
                .collect(Collectors.toList());
    }

    @Override
    public void set() {
        super.set();

        this.config.set("trigger-chance", this.triggerChance);
        this.config.set("explode-radius", this.explodeRadius);
        this.config.set("blacklisted", this.blacklisted.stream().map(Enum::name).collect(Collectors.toList()));
    }

}
