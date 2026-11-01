
package ru.zhanbyrbay.zhanrewards;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RewardsCalendar {

    private final ZhanRewards plugin;

    public RewardsCalendar(ZhanRewards plugin) {
        this.plugin = plugin;
    }

    private NamespacedKey getStartDateKey() {
        return new NamespacedKey(plugin, "calendar_start");
    }

    private NamespacedKey getClaimedDayKey() {
        return new NamespacedKey(plugin, "calendar_claimed");
    }

    public int getCurrentDay(Player player) {

        PersistentDataContainer data =
                player.getPersistentDataContainer();

        LocalDate today = LocalDate.now();

        if (!data.has(getStartDateKey(), PersistentDataType.STRING)) {
            data.set(
                    getStartDateKey(),
                    PersistentDataType.STRING,
                    today.toString()
            );

            return 1;
        }

        LocalDate start = LocalDate.parse(
                data.get(getStartDateKey(), PersistentDataType.STRING)
        );

        long days = ChronoUnit.DAYS.between(start, today);

        return (int) Math.min(28, days + 1);
    }

    public boolean canClaim(Player player, int day) {

        int currentDay = getCurrentDay(player);

        if (day != currentDay) {
            return false;
        }

        PersistentDataContainer data =
                player.getPersistentDataContainer();

        Integer claimed = data.get(
                getClaimedDayKey(),
                PersistentDataType.INTEGER
        );

        return claimed == null || claimed < day;
    }

    public void markClaimed(Player player, int day) {

        player.getPersistentDataContainer().set(
                getClaimedDayKey(),
                PersistentDataType.INTEGER,
                day
        );
    }

    public boolean isClaimed(Player player, int day) {

        Integer claimed = player.getPersistentDataContainer().get(
                getClaimedDayKey(),
                PersistentDataType.INTEGER
        );

        return claimed != null && claimed >= day;
    }
}
