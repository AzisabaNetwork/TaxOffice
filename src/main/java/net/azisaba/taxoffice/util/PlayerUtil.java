package net.azisaba.taxoffice.util;

import net.azisaba.taxoffice.DBConnector;
import net.azisaba.taxoffice.TaxOffice;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerUtil {
    @Contract(pure = true)
    @NotNull
    public static PlayerProfile getPlayerByNameFast(@NotNull String name) {
        Player player = Bukkit.getPlayerExact(name);
        if (player != null) {
            return new PlayerProfile(player.getName(), player.getUniqueId());
        }
        try {
            UUID uuid = UUID.fromString(name);
            return new PlayerProfile(null, uuid);
        } catch (IllegalArgumentException ignore) {
        }
        return PlayerProfile.empty();
    }

    @Contract("_ -> new")
    @NotNull
    public static CompletableFuture<PlayerProfile> getPlayerByName(@NotNull String name) {
        PlayerProfile profile = getPlayerByNameFast(name);
        if (profile != PlayerProfile.empty()) {
            return CompletableFuture.completedFuture(profile);
        }
        return CompletableFuture.supplyAsync(() -> {
            try {
                return DBConnector.getPrepareStatement("SELECT `uuid` FROM `players` WHERE `name` = ?", ps -> {
                    ps.setString(1, name);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        return PlayerProfile.empty();
                    }
                    String uuid = rs.getString("uuid");
                    return new PlayerProfile(name, UUID.fromString(uuid));
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, TaxOffice.getInstance().asyncExecutor());
    }
}
