package net.azisaba.taxoffice.manager;

import net.azisaba.taxoffice.DBConnector;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PointsManager {
    /**
     * Gets the current points of the player.
     * @param uuid The UUID of the player.
     * @return The current points of the player.
     */
    public long getPoints(@NotNull UUID uuid) {
        try {
            return DBConnector.getPrepareStatement("SELECT `points` FROM `players` WHERE `uuid` = ?", ps -> {
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    return 0L;
                }
                return rs.getLong("points");
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Give the points to the player.
     * @param uuid The UUID of the player.
     * @param points The points to add.
     * @return points before the addition.
     */
    public long givePoints(@NotNull UUID uuid, long points) {
        try {
            return DBConnector.getPrepareStatement("INSERT INTO `players` (`uuid`, `name`, `points`) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `points` = `points` + VALUES(`points`)", ps -> {
                ps.setString(1, uuid.toString());
                ps.setString(2, uuid.toString());
                ps.setLong(3, points);
                long old = getPoints(uuid);
                ps.executeUpdate();
                return old;
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Take the points from the player.
     * @param uuid The UUID of the player.
     * @param points The points to remove.
     * @return points before the removal.
     */
    public long takePoints(@NotNull UUID uuid, long points) {
        try {
            return DBConnector.getPrepareStatement("INSERT INTO `players` (`uuid`, `name`, `points`) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `points` = `points` - VALUES(`points`)", ps -> {
                ps.setString(1, uuid.toString());
                ps.setString(2, uuid.toString());
                ps.setLong(3, points);
                long old = getPoints(uuid);
                ps.executeUpdate();
                return old;
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the points of the player.
     * @param uuid The UUID of the player.
     * @param points The points to set.
     * @return old points.
     */
    public long setPoints(@NotNull UUID uuid, long points) {
        try {
            return DBConnector.getPrepareStatement("INSERT INTO `players` (`uuid`, `name`, `points`) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `points` = VALUES(`points`)", ps -> {
                ps.setString(1, uuid.toString());
                ps.setString(2, uuid.toString());
                ps.setLong(3, points);
                long old = getPoints(uuid);
                ps.executeUpdate();
                return old;
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
