
package ru.zhanbyrbay.zhanrewards;

import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ZhanRewards extends JavaPlugin {

    private Economy economy;
    private PlayerPoints playerPoints;
    private RewardsCalendar calendar;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        calendar = new RewardsCalendar(this);

        if (!setupEconomy()) {
            getLogger().severe("Vault Economy не найден!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!(getServer().getPluginManager()
                .getPlugin("PlayerPoints") instanceof PlayerPoints pp)) {
            getLogger().severe("PlayerPoints не найден!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        playerPoints = pp;

        getCommand("rewards").setExecutor(new RewardsCommand(this));
        getCommand("zhanrewards").setExecutor(new AdminCommand(this));

        getServer().getPluginManager().registerEvents(
                new RewardsListener(this), this
        );

        getLogger().info("ZhanRewards успешно запущен!");
    }

    private boolean setupEconomy() {

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> provider =
                getServer().getServicesManager()
                        .getRegistration(Economy.class);

        if (provider == null) return false;

        economy = provider.getProvider();
        return economy != null;
    }

    public Economy getEconomy() {
        return economy;
    }

    public PlayerPoints getPlayerPoints() {
        return playerPoints;
    }

    public RewardsCalendar getCalendar() {
        return calendar;
    }

    @Override
    public void onDisable() {
        getLogger().info("ZhanRewards выключен!");
    }
}
