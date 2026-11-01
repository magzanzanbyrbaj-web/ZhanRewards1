
package ru.zhanbyrbay.zhanrewards;

import org.bukkit.plugin.java.JavaPlugin;

public class ZhanRewards extends JavaPlugin {

    private static ZhanRewards instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        getLogger().info("ZhanRewards успешно включён!");

        if (getCommand("rewards") != null) {
            getCommand("rewards").setExecutor(new RewardsCommand(this));
        }

        if (getCommand("zhanrewards") != null) {
            getCommand("zhanrewards").setExecutor(new AdminCommand(this));
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("ZhanRewards выключен!");
    }

    public static ZhanRewards getInstance() {
        return instance;
    }
}
