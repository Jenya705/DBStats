package com.github.jenya705;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

@Getter
public class DBConfiguration {

    private final String sqlUser;
    private final String sqlPassword;
    private final String sqlDatabaseName;
    private final int sqlPort;
    private final String sqlHost;
    private final String tableName;
    private final int timeBetweenSendingStats;
    private final int timeToHours;

    public DBConfiguration(FileConfiguration configuration){
        sqlUser = configuration.getString("sqlUser");
        sqlPassword = configuration.getString("sqlPassword");
        sqlDatabaseName = configuration.getString("sqlDatabaseName");
        sqlPort = configuration.getInt("sqlPort");
        String sqlHost = Objects.requireNonNull(configuration.getString("sqlHost"));
        if (sqlHost.equals("localhost")) this.sqlHost = "127.0.0.1";
        else this.sqlHost = sqlHost;
        tableName = configuration.getString("tableName");
        timeBetweenSendingStats = configuration.getInt("timeBetweenSendingStats");
        timeToHours = configuration.getInt("timeToHours");
    }

}
