
package ru.zhanbyrbay.zhanrewards;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;

public class RewardsListener implements Listener {

    private final ZhanRewards plugin;
    private final Economy economy;
    private final PlayerPoints playerPoints;

    public RewardsListener(ZhanRewards plugin, Economy economy, PlayerPoints playerPoints) {
        this.plugin = plugin;
        this.economy = economy;
        this.playerPoints = playerPoints;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        String title = plugin.getConfig().getString("settings.title", "§8Награды • DoomLand");

        if (!event.getView().getTitle().equals(title)) return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() != Material.CHEST) return;
        if (!item.hasItemMeta()) return;

        String name = item.getItemMeta().getDisplayName();

        if (!name.startsWith("§6✦ §fДень §e")) return;

        int day;

        try {
            day = Integer.parseInt(name.replace("§6✦ §fДень §e", ""));
        } catch (NumberFormatException exception) {
            return;
        }

        ConfigurationSection reward = plugin.getConfig().getConfigurationSection("rewards." + day);

        if (reward == null) {
            player.sendMessage("§cНаграда не настроена!");
            return;
        }

        NamespacedKey key = new NamespacedKey(plugin, "day_" + day);

        if (player.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
            player.sendMessage("§cЭта награда уже получена!");
            return;
        }

        // Монеты и аметисты
        for (String command : reward.getStringList("commands")) {
            String finalCommand = command.replace("%player%", player.getName());

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
        }

        // Предметы
        ConfigurationSection items = reward.getConfigurationSection("items");

        if (items != null) {
            for (String materialName : items.getKeys(false)) {

                Material material = Material.matchMaterial(materialName);

                if (material == null) continue;

                int amount = items.getInt(materialName);

                if (amount <= 0) continue;

                ItemStack rewardItem = new ItemStack(material, amount);

                player.getInventory().addItem(rewardItem).values()
                        .forEach(leftover -> player.getWorld().dropItemNaturally(
                                player.getLocation(), leftover
                        ));
            }
        }

        player.getPersistentDataContainer().set(
                key,
                PersistentDataType.BYTE,
                (byte) 1
        );

        player.sendMessage("§a✦ §fНаграда за день §e" + day + " §fполучена!");
        player.closeInventory();
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        String title = plugin.getConfig().getString("settings.title", "§8Награды • DoomLand");

        if (event.getView().getTitle().equals(title)) {
            event.setCancelled(true);
        }
    }
}
