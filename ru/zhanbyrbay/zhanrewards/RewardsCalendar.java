
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

    private NamespacedKey startKey() {
        return new NamespacedKey(plugin, "calendar_start");
    }

    private NamespacedKey lastClaimKey() {
        return new NamespacedKey(plugin, "last_claim_date");
    }

    private NamespacedKey dayKey(int day) {
        return new NamespacedKey(plugin, "day_" + day);
    }

    public int getCurrentDay(Player player) {
        PersistentDataContainer data =
                player.getPersistentDataContainer();

        String start = data.get(startKey(), PersistentDataType.STRING);

        if (start == null) {
            start = LocalDate.now().toString();
            data.set(startKey(), PersistentDataType.STRING, start);
        }

        LocalDate startDate = LocalDate.parse(start);
        long passed = ChronoUnit.DAYS.between(
                startDate, LocalDate.now()
        );

        return (int) Math.min(28, Math.max(1, passed + 1));
    }

    public boolean isClaimed(Player player, int day) {
        return player.getPersistentDataContainer().has(
                dayKey(day), PersistentDataType.BYTE
        );
    }

    public boolean canClaim(Player player, int day) {
        if (day < 1 || day > 28) return false;
        if (day > getCurrentDay(player)) return false;
        if (isClaimed(player, day)) return false;

        String lastClaim = player.getPersistentDataContainer().get(
                lastClaimKey(), PersistentDataType.STRING
        );

        return !LocalDate.now().toString().equals(lastClaim);
    }

    public boolean hasClaimedToday(Player player) {
        String lastClaim = player.getPersistentDataContainer().get(
                lastClaimKey(), PersistentDataType.STRING
        );

        return LocalDate.now().toString().equals(lastClaim);
    }

    public void markClaimed(Player player, int day) {
        PersistentDataContainer data =
                player.getPersistentDataContainer();

        data.set(dayKey(day), PersistentDataType.BYTE, (byte) 1);
        data.set(
                lastClaimKey(),
                PersistentDataType.STRING,
                LocalDate.now().toString()
        );
    }
}
