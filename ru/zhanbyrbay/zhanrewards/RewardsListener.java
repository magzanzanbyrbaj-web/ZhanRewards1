
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

public class RewardsListener implements Listener {

    private final ZhanRewards plugin;

    public RewardsListener(ZhanRewards plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        String title = plugin.getConfig().getString(
                "settings.title", "§8Награды • DoomLand"
        );

        if (!event.getView().getTitle().equals(title)) return;

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory() != event.getView().getTopInventory()) return;

        ItemStack item = event.getCurrentItem();

        if (item == null || !item.hasItemMeta()) return;

        String name = item.getItemMeta().getDisplayName();

        for (int day = 1; day <= 28; day++) {

            ConfigurationSection reward =
                    plugin.getConfig().getConfigurationSection("rewards." + day);

            if (reward == null) continue;
            if (!name.equals(reward.getString("name"))) continue;

            NamespacedKey key = new NamespacedKey(plugin, "day_" + day);

            if (player.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                player.sendMessage("§cЭта награда уже получена!");
                return;
            }

            // Выполнение денежных команд
            for (String command : reward.getStringList("commands")) {
                Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        command.replace("%player%", player.getName())
                );
            }

            // Выдача предметов
            ConfigurationSection items =
                    reward.getConfigurationSection("items");

            if (items != null) {
                for (String materialName : items.getKeys(false)) {

                    Material material = Material.matchMaterial(materialName);
                    if (material == null) continue;

                    int amount = items.getInt(materialName);
                    if (amount <= 0) continue;

                    ItemStack stack = new ItemStack(material, amount);

                    player.getInventory().addItem(stack).values().forEach(leftover ->
                            player.getWorld().dropItemNaturally(
                                    player.getLocation(), leftover
                            )
                    );
                }
            }

            player.getPersistentDataContainer().set(
                    key,
                    PersistentDataType.BYTE,
                    (byte) 1
            );

            player.sendMessage("§a✔ Награда за день §e" + day + " §aполучена!");
            player.closeInventory();
            return;
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {

        String title = plugin.getConfig().getString(
                "settings.title", "§8Награды • DoomLand"
        );

        if (event.getView().getTitle().equals(title)) {
            event.setCancelled(true);
        }
    }
}
