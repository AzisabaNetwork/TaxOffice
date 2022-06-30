package net.azisaba.taxoffice.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface TaxOfficeCommand {
    @NotNull
    String getName();

    @NotNull
    String getDescription();

    @NotNull
    String getPermission();

    void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args);

    @NotNull
    default List<String> getSuggestions(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        return getUnfilteredSuggestions(sender, args).stream().filter(s -> s.startsWith(args[args.length - 1])).collect(Collectors.toList());
    }

    @NotNull
    default List<String> getUnfilteredSuggestions(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        return Collections.emptyList();
    }

    /**
     * Checks if the command can be executed by the sender. Subclasses can override this method to implement additional
     * checks.
     * @param sender The sender.
     * @return {@code true} if the command can be executed; {@code false} otherwise.
     */
    default boolean hasPermission(@NotNull CommandSender sender) {
        return sender.hasPermission(getPermission());
    }
}
