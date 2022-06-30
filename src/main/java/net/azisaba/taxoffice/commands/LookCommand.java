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

public class LookCommand implements TaxOfficeCommand {
    @Override
    public @NotNull String getName() {
        return "look";
    }

    @Override
    public @NotNull String getDescription() {
        return "Check the points of a player.";
    }

    @Override
    public @NotNull String getPermission() {
        return "taxoffice.command.look";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/taxoffice look <player>");
            return;
        }
        PlayerUtil.getPlayerByName(args[0]).thenAcceptAsync(profile -> {
            if (profile == null) {
                sender.sendMessage(ChatColor.RED + "Player not found: " + args[0]);
                return;
            }
            long points = profile.getPoints();
            TaxOffice.getInstance().sendMessage(sender, ChatColor.GREEN + profile.name() + " has " + ChatColor.GOLD + points + ChatColor.GREEN + " points.");
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
