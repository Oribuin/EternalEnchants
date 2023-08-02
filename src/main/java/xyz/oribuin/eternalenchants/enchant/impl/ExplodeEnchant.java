package xyz.oribuin.eternalenchants.enchant.impl;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalenchants.enchant.ContextHandler;
import xyz.oribuin.eternalenchants.enchant.Enchant;
import xyz.oribuin.eternalenchants.enchant.EnchantTarget;

public class ExplodeEnchant extends Enchant {

    private double triggerChance = 50; // The chance of the enchantment triggering
    private int explodeRadius = 1; // The radius of the explosion

    public ExplodeEnchant() {
        super("explode", EnchantTarget.PICKAXE, EnchantTarget.SHOVEL, EnchantTarget.AXE);

        this.description = "Explodes blocks around the broken block.";
    }

    @Override
    public void run(ContextHandler context) {
        BlockBreakEvent event = context.as(BlockBreakEvent.class);
        if (event == null) return;

        if (this.triggerChance != 100 && Math.random() * 100 >= this.triggerChance)
            return;

        // Destroy blocks nearby the broken block
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        // Play sound effect
        event.getBlock().getWorld().playSound(event.getBlock().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 25, 1);

        // Break the blocks in the radius
        for (int x = -this.explodeRadius; x <= this.explodeRadius; x++) {
            for (int z = -this.explodeRadius; z <= this.explodeRadius; z++) {
                for (int y = -this.explodeRadius; y <= this.explodeRadius; y++) {
                    Block relative = event.getBlock().getRelative(x, y, z);
                    if (relative.getType().isAir() || relative.isLiquid())
                        continue;

                    // TODO: Maybe look into a way to make this more efficient, Block#breakNaturally() is so slow
                    if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
                        relative.setType(Material.AIR);
                    else
                        relative.breakNaturally(item);

                    event.getPlayer().incrementStatistic(Statistic.MINE_BLOCK, relative.getType());
                }
            }
        }

    }

    @Override
    public void load() {
        this.triggerChance = this.config.getDouble("trigger-chance", this.triggerChance);
        this.explodeRadius = this.config.getInt("explode-radius", this.explodeRadius);
    }

    @Override
    public void set() {
        this.config.set("trigger-chance", this.triggerChance);
        this.config.set("explode-radius", this.explodeRadius);
    }

}
