package net.azisaba.taxoffice;

import me.armar.plugins.autorank.pathbuilder.builders.RequirementBuilder;
import net.azisaba.taxoffice.autorank.TaxRequirement;
import net.azisaba.taxoffice.commands.OnTimeToTaxCommandHandler;
import net.azisaba.taxoffice.commands.TaxOfficeCommandHandler;
import net.azisaba.taxoffice.manager.CachedPointsManager;
import net.azisaba.taxoffice.manager.PointsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

public class TaxOffice extends JavaPlugin {
    private final Map<ItemStack, Long> itemPoints = new HashMap<>();
    private DatabaseConfig databaseConfig;
    private int maximumPoints;
    private String autorankRequirementDescription;
    private String ottDisplayName;

    private final CachedPointsManager pointsManager = new CachedPointsManager(new PointsManager());

    private final Executor syncExecutor = (runnable) -> getServer().getScheduler().runTask(this, runnable);
    private final Executor asyncExecutor = (runnable) -> getServer().getScheduler().runTaskAsynchronously(this, runnable);

    @Override
    public void onEnable() {
        // load configuration
        saveDefaultConfig();
        this.databaseConfig = new DatabaseConfig(Objects.requireNonNull(getConfig().getConfigurationSection("database"), "database configuration is missing"));
        this.autorankRequirementDescription = getConfig().getString("autorank-requirement-description", "Pay at least %d points to the tax office");
        this.ottDisplayName = getConfig().getString("ott-display-name", null);
        loadAdditionalConfig();

        // load database
        try {
            DBConnector.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // hook to autorank to register requirement
        try {
            if (Bukkit.getPluginManager().isPluginEnabled("Autorank")) {
                // hook to Autorank
                RequirementBuilder.registerRequirement("taxoffice", TaxRequirement.class);
            }
        } catch (Throwable t) {
            getLogger().warning("Failed to hook to Autorank");
            t.printStackTrace();
        }

        // register command
        Objects.requireNonNull(getCommand("taxoffice")).setExecutor(new TaxOfficeCommandHandler());
        if (ottDisplayName != null) {
            Objects.requireNonNull(getCommand("ottt")).setExecutor(new OnTimeToTaxCommandHandler());
        }
    }

    private void loadAdditionalConfig() {
        itemPoints.clear();
        File itemPointsFile = new File(getDataFolder(), "item-points.yml");
        if (!itemPointsFile.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                itemPointsFile.createNewFile();
            } catch (IOException e) {
                getLogger().warning("Failed to create item-points.yml");
                e.printStackTrace();
            }
        }
        YamlConfiguration itemPointsConfig = YamlConfiguration.loadConfiguration(itemPointsFile);
        itemPointsConfig.getMapList("items").forEach(map -> {
            ItemStack stack = deserializeItemStack(map.get("item"));
            if (stack == null) {
                return;
            }
            itemPoints.put(stack, Long.parseLong(String.valueOf(map.get("points"))));
        });
        getLogger().info("Loaded " + itemPoints.size() + " items.");
    }

    private void saveAdditionalConfig() {
        File itemPointsFile = new File(getDataFolder(), "item-points.yml");
        YamlConfiguration itemPointsConfig = YamlConfiguration.loadConfiguration(itemPointsFile);
        List<Map<String, Object>> mapList = new ArrayList<>();
        itemPoints.forEach((item, points) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("item", item.serialize());
            map.put("points", points);
            mapList.add(map);
        });
        getConfig().set("items", mapList);
        getLogger().info("Saved " + mapList.size() + " items.");
        try {
            itemPointsConfig.save(itemPointsFile);
        } catch (IOException e) {
            getLogger().warning("Failed to save item-points.yml");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private @Nullable ItemStack deserializeItemStack(Object o) {
        if (o instanceof ItemStack) {
            return (ItemStack) o;
        } else if (o instanceof Map<?, ?>) {
            return ItemStack.deserialize((Map<String, Object>) o);
        } else {
            getLogger().warning("Unreadable left item: " + o + " (" + o.getClass().getTypeName() + ")");
            return null;
        }
    }

    @Override
    public void onDisable() {
        saveAdditionalConfig();
        DBConnector.close();
    }

    @NotNull
    public static TaxOffice getInstance() {
        return JavaPlugin.getPlugin(TaxOffice.class);
    }

    @Contract(pure = true)
    @NotNull
    public static Logger getPluginLogger() {
        return getInstance().getLogger();
    }

    @NotNull
    public DatabaseConfig getDatabaseConfig() {
        return Objects.requireNonNull(databaseConfig, "database configuration is not loaded");
    }

    public int getMaximumPoints() {
        return maximumPoints;
    }

    @NotNull
    public PointsManager getPointsManager() {
        return pointsManager;
    }

    @NotNull
    public Executor syncExecutor() {
        return syncExecutor;
    }

    @NotNull
    public Executor asyncExecutor() {
        return asyncExecutor;
    }

    /**
     * Sends the message to sender synchronously using the {@link #syncExecutor}.
     * @param sender The sender.
     * @param message The message.
     */
    public void sendMessage(@NotNull CommandSender sender, @NotNull String message) {
        if (Bukkit.isPrimaryThread()) {
            sender.sendMessage(message);
        } else {
            syncExecutor.execute(() -> sender.sendMessage(message));
        }
    }

    @NotNull
    public Map<ItemStack, Long> getItemPoints() {
        return itemPoints;
    }

    @NotNull
    public String getAutorankRequirementDescription() {
        return autorankRequirementDescription;
    }

    @Nullable
    public String getOttDisplayName() {
        return ottDisplayName;
    }
}
