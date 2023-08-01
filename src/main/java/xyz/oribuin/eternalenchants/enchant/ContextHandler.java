package xyz.oribuin.eternalenchants.enchant;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public record ContextHandler(Event event, ItemStack itemStack, Player player) {

    /**
     * Cast the event to the specified class
     *
     * @param clazz The class to cast to
     * @param <T>   The type of the event
     * @return The casted event
     */
    public <T extends Event> T as(Class<T> clazz) {
        if (!clazz.isInstance(this.event))
            return null;

        return clazz.cast(this.event);
    }

}
