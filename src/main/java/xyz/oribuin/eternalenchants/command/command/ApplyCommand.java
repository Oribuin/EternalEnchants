package xyz.oribuin.eternalenchants.command.command;

import dev.rosewood.rosegarden.RosePlugin;
import dev.rosewood.rosegarden.command.framework.CommandContext;
import dev.rosewood.rosegarden.command.framework.RoseCommand;
import dev.rosewood.rosegarden.command.framework.RoseCommandWrapper;
import dev.rosewood.rosegarden.command.framework.annotation.RoseExecutable;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.oribuin.eternalenchants.enchant.Enchant;
import xyz.oribuin.eternalenchants.manager.EnchantManager;

public class ApplyCommand extends RoseCommand {

    public ApplyCommand(RosePlugin rosePlugin, RoseCommandWrapper parent) {
        super(rosePlugin, parent);
    }

    @RoseExecutable
    public void execute(CommandContext context, Enchant enchant) {
        Player player = (Player) context.getSender();
        ItemStack item = player.getInventory().getItemInMainHand();
        EnchantManager manager = this.rosePlugin.getManager(EnchantManager.class);

        if (manager.apply(item, enchant)) {
            player.sendMessage(Component.text("Successfully applied enchantment."));
            return;
        }

        player.sendMessage(Component.text("Failed to apply enchantment."));
    }

    @Override
    protected String getDefaultName() {
        return "apply";
    }

    @Override
    public String getDescriptionKey() {
        return "command-apply-description";
    }

    @Override
    public String getRequiredPermission() {
        return "eternalenchants.apply";
    }

}
