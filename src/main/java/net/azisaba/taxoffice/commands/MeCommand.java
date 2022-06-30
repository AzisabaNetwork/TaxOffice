package net.azisaba.taxoffice.commands;

import net.azisaba.taxoffice.TaxOffice;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MeCommand implements TaxOfficeCommand {
    @Override
    public @NotNull String getName() {
        return "me";
    }

    @Override
    public @NotNull String getDescription() {
        return "Check your points.";
    }

    @Override
    public @NotNull String getPermission() {
        return "taxoffice.command.me";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used in-game.");
            return;
        }
        TaxOffice.getInstance().asyncExecutor().execute(() -> {
            long points = TaxOffice.getInstance().getPointsManager().getPoints(((Player) sender).getUniqueId());
            TaxOffice.getInstance().sendMessage(sender, ChatColor.GREEN + "You have " + ChatColor.GOLD + points + ChatColor.GREEN + " points.");
        });
    }
}
