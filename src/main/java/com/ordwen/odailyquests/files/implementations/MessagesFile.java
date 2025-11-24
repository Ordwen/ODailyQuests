package com.ordwen.odailyquests.files.implementations;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.base.MessagesFileBase;
import com.ordwen.odailyquests.tools.PluginLogger;

public class MessagesFile extends MessagesFileBase<ODailyQuests, QuestsMessages> {

    private static MessagesFile instance;

    public MessagesFile(ODailyQuests plugin) {
        super(plugin, "messages.yml", QuestsMessages.values());
        instance = this;
    }

    @Override
    protected void onLoadError(Exception e, boolean fileJustCreated) {
        PluginLogger.error("An error occurred while loading the messages file.");
        PluginLogger.error(e.getMessage());
    }

    @Override
    protected void onMessagesDefaultsAddedAndSaved() {
        PluginLogger.warn("Some messages were missing in the messages file. Default messages have been added and saved.");
    }

    @Override
    protected void onMessagesSaveError(Exception e) {
        PluginLogger.error("An error occurred while saving the messages file.");
        PluginLogger.error(e.getMessage());
    }

    @Override
    protected void onMessagesLoaded(boolean fileJustCreated, boolean hadMissing) {
        if (fileJustCreated) {
            PluginLogger.info("Messages file created.");
        }
        PluginLogger.fine("Messages file successfully loaded.");
    }

    public static MessagesFile getInstance() {
        return instance;
    }
}
