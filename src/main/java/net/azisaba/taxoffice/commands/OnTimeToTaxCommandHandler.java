package net.azisaba.taxoffice.commands;

import net.azisaba.taxoffice.TaxOffice;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class OnTimeToTaxCommandHandler implements TabExecutor {
    private static final int COOLTIME_DURATION = 5 * 1000;
    private final Map<UUID, Long> coolTime = new HashMap<>();
    private final Set<UUID> confirm = new HashSet<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used in-game.");
            return true;
        }
        Player player = (Player) sender;
        if (checkCoolTime(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "クールタイム中です。数秒程度時間を開けてもう一度試してください。");
            return true;
        }
        Set<Integer> slots = new HashSet<>();
        int amount = 0;
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (isTicketItem(item)) {
                amount += item.getAmount();
                slots.add(i);
            }
        }
        if (amount == 0) {
            sender.sendMessage(ChatColor.RED + "チケットを持っていません。");
            return true;
        }
        if (confirm.contains(player.getUniqueId())) {
            confirm.remove(player.getUniqueId());
            for (int slot : slots) {
                inventory.setItem(slot, null);
            }
            TaxOffice.getInstance().getPointsManager().givePoints(player.getUniqueId(), amount * 10L);
            sender.sendMessage(ChatColor.GREEN + "チケット" + ChatColor.GOLD + amount + ChatColor.GREEN + "枚を奉納しました。");
            sender.sendMessage(ChatColor.GREEN + "現在の所持ポイントは" + ChatColor.GOLD + "/taxoffice me" + ChatColor.GREEN + "で表示できます。");
        } else {
            confirm.add(player.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "チケット" + ChatColor.GOLD + amount + ChatColor.GREEN + "枚を奉納しますか？");
            sender.sendMessage(ChatColor.GREEN + "変換する場合は10秒以内にもう一度同じコマンドを実行してください。");
            // remove confirm 10 seconds later
            Bukkit.getScheduler().runTaskLaterAsynchronously(TaxOffice.getInstance(), () -> confirm.remove(player.getUniqueId()), 20 * 10);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }

    @Contract(value = "null -> false", pure = true)
    private static boolean isTicketItem(@Nullable ItemStack item) {
        if (item == null || item.getType() != Material.PAPER || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        String rawDisplayName = TaxOffice.getInstance().getOttDisplayName();
        if (rawDisplayName == null) {
            return false;
        }
        String displayName = ChatColor.translateAlternateColorCodes('&', rawDisplayName);
        return meta.hasDisplayName() && meta.getDisplayName().equals(displayName);
    }

    /**
     * Checks the cooltime state of the player.
     * @param uuid the uuid to check
     * @return true if cooltime is still not yet elapsed; false otherwise
     */
    private boolean checkCoolTime(UUID uuid){
        if (coolTime.containsKey(uuid)) {
            if (System.currentTimeMillis() - coolTime.get(uuid) > COOLTIME_DURATION) {
                coolTime.remove(uuid);
                return false;
            } else {
                return true;
            }
        }
        coolTime.put(uuid, System.currentTimeMillis());
        return false;
    }
}
