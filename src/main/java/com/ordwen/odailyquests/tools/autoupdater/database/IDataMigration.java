package com.ordwen.odailyquests.tools.autoupdater.database;

public interface IDataMigration {

    String getTargetVersion();

    void apply() throws UpdateException;
}