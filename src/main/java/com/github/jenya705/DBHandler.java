package com.github.jenya705;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.logging.Level;

public class DBHandler implements Listener {

    @EventHandler
    public void playerQuit(PlayerQuitEvent event){

        try {
            DBStatsPlugin plugin = DBStatsPlugin.getPlugin();
            DBPlayer player = new DBPlayer(event.getPlayer());
            plugin.getStatsSenderTasks().put(event.getPlayer().getUniqueId(),
                    plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () ->
                    {
                        try {
                            plugin.getManager().sendStats(player, true);
                        } catch (SQLException e) {
                            plugin.getLogger().log(Level.SEVERE, "SQLException while trying to send stats:", e);
                        }
                    }, 400));
            plugin.getManager().changePlayerState(player, false);
        } catch (SQLException e){
            DBStatsPlugin.getPlugin().getLogger().log(Level.SEVERE, "SQLException while trying to change player state:", e);
        }

    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event){

        DBStatsPlugin plugin = DBStatsPlugin.getPlugin();
        if (plugin.getStatsSenderTasks().containsKey(event.getPlayer().getUniqueId())){
            plugin.getStatsSenderTasks().get(event.getPlayer().getUniqueId()).cancel();
        }
        else {
            plugin.getPlayersPlayTimeStart()
                    .put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
        try {
            plugin.getManager().changePlayerState(new DBPlayer(event.getPlayer()), true);
        } catch (SQLException e){
            plugin.getLogger().log(Level.SEVERE, "SQLException while trying to change player state:", e);
        }
    }

    @EventHandler
    public void command(PlayerCommandPreprocessEvent event){
        if (event.getMessage().startsWith("/reload")){
            event.getPlayer().sendMessage(ChatColor.RED + "[DBStats] Reload will break DBStats plugin! ");
        }
    }

}
