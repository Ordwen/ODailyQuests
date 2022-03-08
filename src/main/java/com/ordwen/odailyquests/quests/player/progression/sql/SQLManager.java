package com.ordwen.odailyquests.quests.player.progression.sql;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLManager {

    /* init variables */
    private String host;
    private String password;
    private String user;
    private String port;

    private String database;
    private int poolSize;

    private final ConfigurationFiles configurationFiles;
    private HikariDataSource hikariDataSource;

    /**
     * Constructor.
     * @param configurationFiles class.
     */
    public SQLManager(ConfigurationFiles configurationFiles, String database, int poolSize) {
        this.configurationFiles = configurationFiles;
        this.database = database;
        this.poolSize = poolSize;
    }

    public void initSQLConnection() {

        ConfigurationSection sqlSection= configurationFiles.getConfigFile().getConfigurationSection("database");

        host = sqlSection.getString("host");
        password = sqlSection.getString("password");
        user = sqlSection.getString("user");
        port = sqlSection.getString("port");
    }

    public void connect(){
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setMaximumPoolSize(this.poolSize);
        hikariConfig.setJdbcUrl(this.toUri());
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.setMaxLifetime(300000);
        hikariConfig.setLeakDetectionThreshold(3000);
        hikariConfig.setConnectionTimeout(10000);

        this.hikariDataSource = new HikariDataSource(hikariConfig);
    }

    public void close(){
        this.hikariDataSource.close();
    }

    public Connection getConnection() {
        if(this.hikariDataSource != null && !this.hikariDataSource.isClosed()){
            try {
                return this.hikariDataSource.getConnection();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
        return null;
    }

    private String toUri(){
        return "jdbc:mysql://" + host + ":" + port + "/" + this.database + "";
    }
}
