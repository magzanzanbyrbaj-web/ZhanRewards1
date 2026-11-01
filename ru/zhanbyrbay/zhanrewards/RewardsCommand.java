
package ru.zhanbyrbay.zhanrewards;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RewardsCommand implements CommandExecutor {

    private final ZhanRewards plugin;

    public RewardsCommand(ZhanRewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Эта команда доступна только игроку.");
            return true;
        }

        new RewardsMenu(plugin).open(player);
        return true;
    }
}
