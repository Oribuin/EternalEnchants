package xyz.oribuin.eternalenchants.event;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class EnchantExplodeEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final List<Block> toExplode;

    /**
     * Explode Enchant Constructor
     *
     * @param player The player who triggered the enchant
     */
    public EnchantExplodeEvent(Player player) {
        super(player);
        this.toExplode = new ArrayList<>();
    }

    public void add(Block block) {
        this.toExplode.add(block);
    }

    public List<Block> getToExplode() {
        return toExplode;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
