package net.azisaba.taxoffice.autorank;

import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;
import net.azisaba.taxoffice.TaxOffice;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TaxRequirement extends AbstractRequirement {
    private long points;

    @Override
    @NotNull
    public String getDescription() {
        return String.format(TaxOffice.getInstance().getAutorankRequirementDescription(), points);
    }

    @Override
    public boolean initRequirement(String[] options) {
        if (options.length > 0) {
            try {
                points = Long.parseLong(options[0]);
            } catch (NumberFormatException e) {
                registerWarningMessage("Failed to parse number: " + options[0]);
                return false;
            }
        }
        // no real reason to limit the points
        /*
        if (points < 0) {
            registerWarningMessage("Points must be greater than 0");
            return false;
        }
        */
        return true;
    }

    @Override
    public String toString() {
        return "TaxRequirement[points=" + points + "]";
    }

    @Override
    protected boolean meetsRequirement(@NotNull Player player) {
        return meetsRequirement(player.getUniqueId());
    }

    @Override
    protected boolean meetsRequirement(@NotNull UUID uuid) {
        return TaxOffice.getInstance().getPointsManager().getPoints(uuid) >= points;
    }

    @Override
    public double getProgressPercentage(@NotNull UUID uuid) {
        return TaxOffice.getInstance().getPointsManager().getPoints(uuid) / (double) points;
    }

    @Override
    @NotNull
    public String getProgressString(@NotNull UUID uuid) {
        return TaxOffice.getInstance().getPointsManager().getPoints(uuid) + "/" + points;
    }

    @Override
    @NotNull
    public String getProgressString(@NotNull Player player) {
        return getProgressString(player.getUniqueId());
    }

    @Override
    public boolean needsOnlinePlayer() {
        return false;
    }
}
