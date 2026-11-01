
package ru.zhanbyrbay.zhanrewards;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {

    private final ZhanRewards plugin;

    public AdminCommand(ZhanRewards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {

        if (!sender.hasPermission("zhanrewards.admin")) {
            sender.sendMessage("§cУ тебя нет прав!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§6§lZhanRewards");
            sender.sendMessage("§e/zhanrewards reload §7- Перезагрузить конфиг");
            sender.sendMessage("§e/zhanrewards reset <ник> §7- Сбросить награды");
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "reload" -> {
                plugin.reloadConfig();
                sender.sendMessage("§aКонфигурация ZhanRewards перезагружена!");
            }

            case "reset" -> {
                if (args.length < 2) {
                    sender.sendMessage("§cИспользуй: /zhanrewards reset <ник>");
                    return true;
                }

                Player target = plugin.getServer().getPlayerExact(args[1]);

                if (target == null) {
                    sender.sendMessage("§cИгрок должен быть онлайн!");
                    return true;
                }

                for (int day = 1; day <= 28; day++) {
                    target.getPersistentDataContainer().remove(
                            new org.bukkit.NamespacedKey(plugin, "day_" + day)
                    );
                }

                sender.sendMessage("§aНаграды игрока " + target.getName() + " сброшены!");
                target.sendMessage("§eТвой календарь наград был сброшен!");
            }

            default -> sender.sendMessage("§cНеизвестная команда!");
        }

        return true;
    }
}
