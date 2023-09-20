package xyz.oribuin.eternalenchants.enchant.impl;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffectType;
import xyz.oribuin.eternalenchants.EternalEnchants;
import xyz.oribuin.eternalenchants.enchant.ContextHandler;
import xyz.oribuin.eternalenchants.enchant.Enchant;
import xyz.oribuin.eternalenchants.enchant.EnchantTarget;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TurboBreakerEnchant extends Enchant {

    private final Map<UUID, TurboBreakerData> userData; // Cooldown data
    private long cooldown; // Cooldown in ticks (20 = 1 second)
    private long duration; // Duration in ticks (20 = 1 second)
    private double triggerChance; // Chance to trigger in percentage (10 = 10%)
    private Map<PotionEffectType, Integer> effects; // Map of effects to apply

    public TurboBreakerEnchant() {
        super("turbo_breaker", EnchantTarget.PICKAXE, EnchantTarget.SHOVEL, EnchantTarget.AXE);

        this.description = "Chance to apply effects when breaking blocks.";
        this.userData = new HashMap<>();
        this.cooldown = 60;
        this.duration = 5;
        this.triggerChance = 10.0;
        this.effects = new HashMap<>();
    }

    @Override
    public void run(ContextHandler context) {
        final BlockBreakEvent event = context.as(BlockBreakEvent.class);
        if (event == null)
            return;

        // Check if the enchantment is enabled
        final TurboBreakerData data = this.userData.computeIfAbsent(event.getPlayer().getUniqueId(), x -> new TurboBreakerData());
        if (data.isEnabled())
            return;

        // Check if the cooldown has passed
        if (System.currentTimeMillis() - data.getLastTriggered() < (this.cooldown / 20 * 1000))
            return;

        // Chance to trigger the effect
        if (Math.random() * 100 > this.triggerChance)
            return;

        data.setEnabled(true);
        data.setLastTriggered(System.currentTimeMillis());
        this.userData.put(event.getPlayer().getUniqueId(), data);

        // TODO: Make spigot compatible
        event.getPlayer().sendMessage(Component.text("You have triggered Turbo Breaker!"));

        // Apply effects
        for (final Map.Entry<PotionEffectType, Integer> entry : this.effects.entrySet()) {
            event.getPlayer().addPotionEffect(entry.getKey().createEffect((int) (this.duration * 20), entry.getValue()));
        }

        // Disable the effect after the duration
        Bukkit.getScheduler().runTaskLater(EternalEnchants.getInstance(), () -> {
            data.setEnabled(false);
            this.userData.put(event.getPlayer().getUniqueId(), data);
        }, this.duration * 20);
    }


    @Override
    public void load() {
        this.cooldown = this.config.getLong("cooldown", 60);
        this.duration = this.config.getLong("duration", 5);
        this.triggerChance = this.config.getDouble("trigger-chance", 10.0);
        this.effects = new HashMap<>();

        // Load effects as a string
        final ConfigurationSection effectsSection = this.config.getConfigurationSection("effects");
        if (effectsSection == null)
            return;

        for (final String effect : effectsSection.getKeys(false)) {
            final PotionEffectType type = PotionEffectType.getByName(effect);
            if (type == null)
                continue;

            this.effects.put(type, effectsSection.getInt(effect, 0));
        }
    }

    @Override
    public void set() {
        this.config.set("cooldown", this.cooldown);
        this.config.set("duration", this.duration);
        this.config.set("trigger-chance", this.triggerChance);

        // Add haste 2
        if (this.effects.isEmpty()) {
            this.effects.put(PotionEffectType.FAST_DIGGING, 1);
            this.effects.put(PotionEffectType.SPEED, 1);
        }

        for (Map.Entry<PotionEffectType, Integer> entry : this.effects.entrySet()) {
            this.config.set("effects." + entry.getKey().getName(), entry.getValue());
        }

    }

    private static class TurboBreakerData {

        private boolean enabled;
        private long lastTriggered;

        public TurboBreakerData() {
            this.enabled = false;
            this.lastTriggered = 0;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public long getLastTriggered() {
            return lastTriggered;
        }

        public void setLastTriggered(long lastTriggered) {
            this.lastTriggered = lastTriggered;
        }

    }

}
