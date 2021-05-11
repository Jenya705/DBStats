package com.github.jenya705;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

@Getter
public class DBStatsPlugin extends JavaPlugin {

    @Getter
    private static DBStatsPlugin plugin;

    private Map<UUID, Long> playersPlayTimeStart;
    private Map<UUID, BukkitTask> statsSenderTasks;
    private DBConfiguration configuration;
    private DBManager manager;

    @Override
    @SneakyThrows
    public void onEnable() {
        plugin = this;

        getDataFolder().mkdir();
        if (!new File(getDataFolder(), "config.yml").exists()) {
            getLogger().warning("Check config.yml to set values!");
            saveDefaultConfig();
            setEnabled(false);
            return;
        }

        playersPlayTimeStart = new HashMap<>();
        statsSenderTasks = new HashMap<>();
        configuration = new DBConfiguration(getConfig());
        manager = new DBManager();
        getServer().getPluginManager().registerEvents(new DBHandler(), this);

        for (Player player : Bukkit.getOnlinePlayers()){
            getManager().changePlayerState(new DBPlayer(player), true);
            getPlayersPlayTimeStart().put(player.getUniqueId(), System.currentTimeMillis());
        }

    }

    @SneakyThrows
    @Override
    public void onDisable() {
        for (Player player: Bukkit.getOnlinePlayers()) {
            try {
                getManager().sendStats(new DBPlayer(player), true);
            } catch (SQLException e){
                getLogger().log(Level.SEVERE, "SQLException while sending stats on disable:", e);
            }
        }
        if (getManager() != null && getManager().getConnection() != null) {
            getManager().getConnection().close();
        }
        if (getStatsSenderTasks() != null) {
            for (Map.Entry<UUID, BukkitTask> entryTasks : getStatsSenderTasks().entrySet()) {
                entryTasks.getValue().cancel();
            }
        }
    }
}
