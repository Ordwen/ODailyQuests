package com.ordwen.odailyquests.files.base;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

/**
 * Generic base for a messages.yml-like file that autofill missing keys
 * from an enum of MessageKey.
 */
public abstract class MessagesFileBase<P extends JavaPlugin, E extends Enum<E> & MessageKey>
        extends APluginFile<P> {

    private final E[] values;

    protected MessagesFileBase(P plugin, String fileName, E[] values) {
        super(plugin, fileName);
        this.values = values;
    }

    @Override
    protected void onPostLoad(boolean fileJustCreated) {
        boolean missingMessages = false;

        for (E item : values) {
            if (config.getString(item.getPath()) == null) {
                missingMessages = true;
                config.set(item.getPath(), item.getDefault());
            }
        }

        if (missingMessages) {
            try {
                config.save(file);
                onMessagesDefaultsAddedAndSaved();
            } catch (IOException e) {
                onMessagesSaveError(e);
            }
        }

        onMessagesLoaded(fileJustCreated, missingMessages);
    }

    /**
     * Called after missing messages have been added and the file saved.
     */
    protected void onMessagesDefaultsAddedAndSaved() {
        // default: no-op, subclasses can log
    }

    /**
     * Called if saving the messages file fails after defaults injection.
     */
    protected void onMessagesSaveError(Exception e) {
        // default: no-op, subclasses can log
    }

    /**
     * Called after the messages file has been loaded and defaults handled.
     *
     * @param fileJustCreated true if file just created
     * @param hadMissing      true if some messages were missing and added
     */
    protected void onMessagesLoaded(boolean fileJustCreated, boolean hadMissing) {
        // default: no-op, subclasses can log
    }

    public String get(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }
}
