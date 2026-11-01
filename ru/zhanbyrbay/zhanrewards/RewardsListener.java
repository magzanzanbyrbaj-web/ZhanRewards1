
package ru.zhanbyrbay.zhanrewards;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

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

        int clickedSlot = event.getSlot();
        int day = -1;

        for (int i = 1; i <= 28; i++) {
            int slot = 10 + ((i - 1) / 7) * 9 + ((i - 1) % 7);

            if (slot == clickedSlot) {
                day = i;
                break;
            }
        }

        if (day == -1) return;

        ConfigurationSection reward =
                plugin.getConfig().getConfigurationSection("rewards." + day);

        if (reward == null) return;

        RewardsCalendar calendar = plugin.getCalendar();

        if (calendar.isClaimed(player, day)) {
            player.sendMessage("§cЭта награда уже получена!");
            return;
        }

        if (day > calendar.getCurrentDay(player)) {
            player.sendMessage("§cЭта награда пока закрыта!");
            return;
        }

        if (calendar.hasClaimedToday(player)) {
            player.sendMessage("§cСегодня ты уже забрал награду!");
            return;
        }

        if (!calendar.canClaim(player, day)) return;

        for (String command : reward.getStringList("commands")) {
            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    command.replace("%player%", player.getName())
            );
        }

        ConfigurationSection items =
                reward.getConfigurationSection("items");

        if (items != null) {
            for (String materialName : items.getKeys(false)) {
                Material material = Material.matchMaterial(materialName);

                if (material == null) {
                    plugin.getLogger().warning(
                            "Неизвестный предмет: " + materialName
                    );
                    continue;
                }

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

        calendar.markClaimed(player, day);

        player.sendMessage(
                "§a✔ Ты получил награду за день §e" + day + "§a!"
        );

        player.closeInventory();
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
