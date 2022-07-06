package net.azisaba.taxoffice.commands;

import net.azisaba.taxoffice.TaxOffice;
import net.azisaba.taxoffice.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SetCommand implements TaxOfficeCommand {
    @Override
    public @NotNull String getName() {
        return "set";
    }

    @Override
    public @NotNull String getDescription() {
        return "Set points of a player.";
    }

    @Override
    public @NotNull String getPermission() {
        return "taxoffice.command.set";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/taxoffice set <player> <points>");
            return;
        }
        long points;
        try {
            points = Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid number: " + args[1]);
            return;
        }
        PlayerUtil.getPlayerByName(args[0]).thenAcceptAsync(profile -> {
            if (profile == null) {
                sender.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
                return;
            }
            profile.setPoints(points);
            TaxOffice.getPluginLogger().info(sender.getName() + " set points of " + profile.name() + " to " + points + " points");
            TaxOffice.getInstance().sendMessage(sender, ChatColor.GREEN + "Set points of " + profile.name() + " to " + points + " points.");
        }, TaxOffice.getInstance().asyncExecutor());
    }

    @Override
    public @NotNull List<String> getUnfilteredSuggestions(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
