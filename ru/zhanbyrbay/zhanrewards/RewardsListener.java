
package ru.zhanbyrbay.zhanrewards;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

        if (!event.getView().getTitle().equals("§8Награды • Календарь")) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() != Material.CHEST) return;
        if (!item.hasItemMeta()) return;

        String name = item.getItemMeta().getDisplayName();

        if (!name.startsWith("§6✦ §fДень §e")) return;

        int day;

        try {
            day = Integer.parseInt(
                    name.replace("§6✦ §fДень §e", "")
            );
        } catch (NumberFormatException exception) {
            return;
        }

        NamespacedKey key = new NamespacedKey(plugin, "day_" + day);

        if (player.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
            player.sendMessage("§cЭта награда уже получена!");
            return;
        }

        player.getInventory().addItem(new ItemStack(Material.DIAMOND, 2));

        player.getPersistentDataContainer().set(
                key,
                PersistentDataType.BYTE,
                (byte) 1
        );

        player.sendMessage("§a✦ §fТы получил награду за день §e" + day + "§a!");
        player.closeInventory();
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {

        if (event.getView().getTitle().equals("§8Награды • Календарь")) {
            event.setCancelled(true);
        }
    }
}
