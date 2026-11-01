
package ru.zhanbyrbay.zhanrewards;

import org.bukkit.plugin.java.JavaPlugin;

public class ZhanRewards extends JavaPlugin {

    private static ZhanRewards instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        getCommand("rewards").setExecutor(new RewardsCommand(this));
        getCommand("zhanrewards").setExecutor(new AdminCommand(this));

        getServer().getPluginManager().registerEvents(
                new RewardsListener(this), this
        );

        getLogger().info("ZhanRewards успешно запущен!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ZhanRewards выключен!");
    }

    public static ZhanRewards getInstance() {
        return instance;
    }
}
