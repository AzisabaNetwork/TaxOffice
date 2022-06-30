package net.azisaba.taxoffice.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class HelpCommand implements TaxOfficeCommand {
    @Override
    public @NotNull String getName() {
        return "help";
    }

    @Override
    public @NotNull String getDescription() {
        return "You are here.";
    }

    @Override
    public @NotNull String getPermission() {
        return "taxoffice.command.help";
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        TaxOfficeCommandHandler.COMMANDS.forEach(command ->
                sender.sendMessage(ChatColor.AQUA + " /taxoffice " + command.getName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + command.getDescription())
        );
    }
}
