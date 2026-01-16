package com.ordwen.odailyquests.quests.player.progression.storage.sql.mysql;

import com.ordwen.odailyquests.configuration.essentials.Database;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.LoadProgressionSQL;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SaveProgressionSQL;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * SQL manager implementation for MySQL databases.
 * <p>
 * This class configures and manages a HikariCP connection pool
 * dedicated to MySQL and wires the SQL-based progression loaders
 * and savers used by the quest system.
 * <p>
 * Database connections, table initialization, and connection testing
 * are performed during construction.
 */
public class MySQLManager extends SQLManager {

    /**
     * Creates a new MySQL manager and initializes the database layer.
     * <p>
     * This constructor:
     * - instantiates SQL progression loaders and savers
     * - initializes the HikariCP connection pool
     * - tests the database connection
     * - ensures required tables exist
     */
    public MySQLManager() {
        super.loadProgressionSQL = new LoadProgressionSQL(this);
        super.saveProgressionSQL = new SaveProgressionSQL(this);

        setupDatabase();
    }

    /**
     * Initializes the HikariCP connection pool for MySQL.
     * <p>
     * Pool settings such as maximum pool size, connection timeout,
     * lifetime, and leak detection are configured here.
     * The resulting data source is stored in the parent SQL manager.
     */
    public void initHikariCP() {
        final HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setJdbcUrl(this.toUri());
        hikariConfig.setUsername(Database.getUser());
        hikariConfig.setPassword(Database.getPassword());
        hikariConfig.setMaxLifetime(300000L);
        hikariConfig.setLeakDetectionThreshold(10000L);
        hikariConfig.setConnectionTimeout(10000L);

        super.hikariDataSource = new HikariDataSource(hikariConfig);
    }

    /**
     * Initializes the database layer.
     * <p>
     * This method:
     * - creates the HikariCP data source
     * - validates the database connection
     * - creates or updates required tables
     */
    public void setupDatabase() {
        close();
        initHikariCP();

        testConnection();
        setupTables();
    }

    /**
     * Builds the JDBC connection URL for the MySQL database.
     *
     * @return the JDBC URL used by the connection pool
     */
    private String toUri() {
        return "jdbc:mysql://" + Database.getHost() + ":" + Database.getPort() + "/" + Database.getName();
    }

}
