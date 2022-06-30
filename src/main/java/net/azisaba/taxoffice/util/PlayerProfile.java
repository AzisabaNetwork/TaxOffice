package net.azisaba.taxoffice.util;

import net.azisaba.taxoffice.TaxOffice;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PlayerProfile {
    private static final PlayerProfile EMPTY = new PlayerProfile(null, new UUID(0, 0));
    private final @Nullable String name;
    private final UUID uuid;

    public PlayerProfile(@Nullable String name, @NotNull UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    @Nullable
    public String name() {
        return name;
    }

    @NotNull
    public UUID uuid() {
        return uuid;
    }

    /**
     * Gets the current points of the player.
     * @return The current points of the player.
     */
    public long getPoints() {
        return TaxOffice.getInstance().getPointsManager().getPoints(uuid);
    }

    /**
     * Give the points to the player.
     * @param points The points to add.
     * @return points before the addition.
     */
    public long givePoints(long points) {
        return TaxOffice.getInstance().getPointsManager().givePoints(uuid, points);
    }

    /**
     * Take the points from the player.
     * @param points The points to remove.
     * @return points before the removal.
     */
    public long takePoints(long points) {
        return TaxOffice.getInstance().getPointsManager().takePoints(uuid, points);
    }

    /**
     * Sets the points of the player.
     * @param points The points to set.
     * @return old points.
     */
    public long setPoints(long points) {
        return TaxOffice.getInstance().getPointsManager().setPoints(uuid, points);
    }

    @Contract(pure = true)
    @NotNull
    public static PlayerProfile empty() {
        return EMPTY;
    }
}
