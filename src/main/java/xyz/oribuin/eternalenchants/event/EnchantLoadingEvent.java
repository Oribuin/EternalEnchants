package xyz.oribuin.eternalenchants.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalenchants.enchant.Enchant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EnchantLoadingEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Map<String, Enchant> registeredEnchants;

    public EnchantLoadingEvent() {
        this.registeredEnchants = new HashMap<>();
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * Register an enchant to be loaded
     *
     * @param enchant The enchant to register
     * @return true if registering the enchant overwrote an existing enchant, false otherwise
     */
    public boolean registerEnchant(Enchant enchant) {
        Objects.requireNonNull(enchant, "Enchant cannot be null for enchant: " + enchant.getId());
        Objects.requireNonNull(enchant.getId(), "Enchant ID cannot be null for enchant: " + enchant.getId());

        if (enchant.getTargets().isEmpty()) {
            throw new IllegalArgumentException("You must specify at least one target for the enchant: " + enchant.getId());
        }

        return this.registeredEnchants.put(enchant.getId(), enchant) != null;
    }

    @NotNull
    public Map<String, Enchant> getRegisteredEnchants() {
        return Collections.unmodifiableMap(this.registeredEnchants);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}

