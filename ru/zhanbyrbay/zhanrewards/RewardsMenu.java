
package ru.zhanbyrbay.zhanrewards;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.List;

public class RewardsMenu {

    private final ZhanRewards plugin;

    public RewardsMenu(ZhanRewards plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {

        String title = plugin.getConfig().getString(
                "settings.title", "§8Награды • DoomLand"
        );

        Inventory inv = Bukkit.createInventory(null, 54, title);

        ItemStack glass = createItem(
                Material.CYAN_STAINED_GLASS_PANE,
                "§b✦",
                List.of("§7DoomLand Rewards")
        );

        // Декоративные рамки
        for (int slot = 0; slot < 54; slot++) {
            if (slot < 9 || slot >= 45 || slot % 9 == 0 || slot % 9 == 8) {
                inv.setItem(slot, glass);
            }
        }

        // 28 наград
        for (int day = 1; day <= 28; day++) {

            int slot = 10 + ((day - 1) / 7) * 9 + ((day - 1) % 7);

            ConfigurationSection reward =
                    plugin.getConfig().getConfigurationSection("rewards." + day);

            if (reward == null) continue;

            NamespacedKey key = new NamespacedKey(plugin, "day_" + day);

            boolean claimed = player.getPersistentDataContainer().has(
                    key, PersistentDataType.BYTE
            );

            Material material = claimed
                    ? Material.LIME_STAINED_GLASS_PANE
                    : Material.CHEST;

            String name = reward.getString("name", "§eДень " + day);

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§7Награда за день §e" + day);
            lore.add("");

            for (String command : reward.getStringList("commands")) {
                if (command.startsWith("eco give")) {
                    lore.add("§6Монеты §f— §e" +
                            command.replaceAll("[^0-9]", ""));
                } else if (command.startsWith("points give")) {
                    lore.add("§dАметисты §f— §d" +
                            command.replaceAll("[^0-9]", ""));
                }
            }

            ConfigurationSection items = reward.getConfigurationSection("items");

            if (items != null) {
                for (String item : items.getKeys(false)) {
                    lore.add("§f" + item + " §7x" + items.getInt(item));
                }
            }

            lore.add("");

            if (claimed) {
                lore.add("§a✔ Уже получено");
            } else {
                lore.add("§eНажми, чтобы получить");
            }

            inv.setItem(slot, createItem(material, name, lore));
        }

        // Информация
        inv.setItem(49, createItem(
                Material.NETHER_STAR,
                "§b§lКалендарь наград",
                List.of(
                        "",
                        "§7Всего наград: §f28",
                        "§7Собирай награды каждый день!",
                        "",
                        "§bDoomLand"
                )
        ));

        player.openInventory(inv);
    }

    private ItemStack createItem(
            Material material,
            String name,
            List<String> lore
    ) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }
}
