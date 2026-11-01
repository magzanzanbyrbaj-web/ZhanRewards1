
package ru.zhanbyrbay.zhanrewards;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RewardsMenu {

    private final ZhanRewards plugin;

    private static final String TITLE = "§8Награды • Календарь";

    public RewardsMenu(ZhanRewards plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, TITLE);

        // Боковые декоративные панели
        ItemStack glass = createItem(
                Material.CYAN_STAINED_GLASS_PANE,
                "§b✦",
                List.of("§7Календарь наград")
        );

        for (int slot = 0; slot < 36; slot++) {
            if (slot < 9 || slot >= 27 || slot % 9 == 0 || slot % 9 == 8) {
                inv.setItem(slot, glass);
            }
        }

        // 28 дней
        for (int day = 1; day <= 28; day++) {

            int slot = 9 + ((day - 1) / 7) * 9 + 1 + ((day - 1) % 7);

            boolean claimed = player.getPersistentDataContainer().has(
                    new org.bukkit.NamespacedKey(plugin, "day_" + day),
                    org.bukkit.persistence.PersistentDataType.BYTE
            );

            Material material;
            String status;

            if (claimed) {
                material = Material.LIME_STAINED_GLASS_PANE;
                status = "§a✔ Получено";
            } else {
                material = Material.CHEST;
                status = "§eНажми, чтобы получить";
            }

            inv.setItem(slot, createItem(
                    material,
                    "§6✦ §fДень §e" + day,
                    List.of(
                            "",
                            "§7Награда за ежедневный вход",
                            "",
                            status
                    )
            ));
        }

        player.openInventory(inv);
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(new ArrayList<>(lore));
            item.setItemMeta(meta);
        }

        return item;
    }
}
