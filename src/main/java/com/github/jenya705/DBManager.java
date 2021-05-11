package com.github.jenya705;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;

@Getter
public class DBManager {

    private final Connection connection;

    public DBManager() throws SQLException, ClassNotFoundException {
        DBStatsPlugin plugin = DBStatsPlugin.getPlugin();
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s&useUnicode=true&characterEncoding=UTF-8" +
                        "&autoReconnect=true&failOverReadOnly=false&maxReconnects=10",
                plugin.getConfiguration().getSqlHost(), plugin.getConfiguration().getSqlPort(),
                plugin.getConfiguration().getSqlDatabaseName(), plugin.getConfiguration().getSqlUser(),
                plugin.getConfiguration().getSqlPassword()));
        update(String.format("create table if not exists %s (\n" +
                "    text_id text,\n" +
                "    played_time bigint,\n" +
                "    online boolean,\n" +
                "    hours boolean,\n" +
                "    id bigint auto_increment primary key\n" +
                ");", plugin.getConfiguration().getTableName()));

    }

    public void update(String sql, Object... objects) throws SQLException{
        if (objects.length == 0) {
            getConnection().createStatement().executeUpdate(sql);
        }
        else {
            PreparedStatement statement = getConnection().prepareStatement(sql);
            for (int i = 0; i < objects.length; ++i){
                statement.setObject(i+1, objects[i]);
            }
            statement.executeUpdate();
        }
    }

    public void sendStats(DBPlayer player, boolean delete) throws SQLException {

        DBStatsPlugin plugin = DBStatsPlugin.getPlugin();
        long startTime = plugin.getPlayersPlayTimeStart().get(player.getUuid());
        long endTime = System.currentTimeMillis();
        long time = (endTime - startTime) / (1000 * 60);
        boolean hours = false;
        if (time >= plugin.getConfiguration().getTimeToHours()) {
            time /= 60;
            hours = true;
        }
        update(String.format("insert into %s (text_id, played_time, hours, online) values (?, ?, ?, false);",
                plugin.getConfiguration().getTableName()),
                Bukkit.getOnlineMode() ? player.getUuid().toString() : player.getName(), time, hours);
        if (delete){
            plugin.getPlayersPlayTimeStart().remove(player.getUuid());
        }
        plugin.getLogger().info("Send stats for " + player.getName());

    }

    public void changePlayerState(DBPlayer player, boolean online) throws SQLException {

        DBStatsPlugin plugin = DBStatsPlugin.getPlugin();
        update(String.format("update %s set online = ? where text_id = ?", plugin.getConfiguration().getTableName()),
                online, Bukkit.getOnlineMode() ? player.getUuid().toString() : player.getName());

    }

}
