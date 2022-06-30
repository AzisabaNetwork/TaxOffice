package net.azisaba.taxoffice.commands;

import net.azisaba.taxoffice.TaxOffice;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TaxOfficeCommandHandler implements TabExecutor {
    public static final List<TaxOfficeCommand> COMMANDS = Arrays.asList(
            new HelpCommand(),
            new LookCommand(),
            new GiveCommand(),
            new TakeCommand(),
            new SetCommand()
    );
    public static final Set<String> COMMAND_NAMES;

    static {
        Set<String> commandNames = new HashSet<>();
        for (TaxOfficeCommand command : COMMANDS) {
            for (String name : commandNames) {
                if (name.equalsIgnoreCase(command.getName())) {
                    throw new IllegalArgumentException("Duplicate command name: " + name + " (" + command.getClass().getTypeName() + ")");
                }
            }
            if (command.getName().contains(" ")) {
                throw new IllegalArgumentException("Command name cannot contain space: " + command.getName());
            }
            commandNames.add(command.getName());
        }
        COMMAND_NAMES = commandNames;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "Running TaxOffice v" + TaxOffice.getInstance().getDescription().getVersion() + ".");
            sender.sendMessage(ChatColor.YELLOW + "Type /taxoffice help to see available commands.");
            return true;
        }
        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        for (TaxOfficeCommand cmd : COMMANDS) {
            if (cmd.getName().equalsIgnoreCase(args[0])) {
                if (!cmd.hasPermission(sender)) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                cmd.execute(sender, newArgs);
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "Unknown command. Type /taxoffice help to see available commands");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 0) return Collections.emptyList();
        if (args.length == 1) {
            return COMMAND_NAMES.stream().filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        for (TaxOfficeCommand cmd : COMMANDS) {
            if (cmd.getName().equalsIgnoreCase(args[0])) {
                return cmd.getSuggestions(sender, newArgs);
            }
        }
        return Collections.emptyList();
    }
}
