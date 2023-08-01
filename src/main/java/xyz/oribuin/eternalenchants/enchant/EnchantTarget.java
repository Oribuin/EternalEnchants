package xyz.oribuin.eternalenchants.enchant;

import org.bukkit.inventory.ItemStack;

public enum EnchantTarget {
    // Armor
    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS,

    // Weapons
    SWORD,
    BOW,
    CROSSBOW,

    // Tools
    PICKAXE,
    AXE,
    SHOVEL,
    HOE,
    FISHING_ROD;

    /**
     * Check if the item is a target of the enchantment
     *
     * @param itemStack The item to check
     * @return If the item is a target
     */
    public boolean isApplicable(ItemStack itemStack) {
        return itemStack.getType().name().contains(this.name());
    }

}
