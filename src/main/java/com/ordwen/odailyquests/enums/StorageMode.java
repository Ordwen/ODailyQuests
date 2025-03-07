package com.ordwen.odailyquests.enums;

import com.ordwen.odailyquests.tools.PluginLogger;

public enum StorageMode {

    YAML(true),
    SQLITE(true),
    MYSQL(false),
    ;

    private final boolean isLocal;

    StorageMode(boolean isLocal) {
        this.isLocal = isLocal;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public static StorageMode getStorageMode(String mode) {
        for (StorageMode storageMode : values()) {
            if (storageMode.name().equalsIgnoreCase(mode)) {
                return storageMode;
            }
        }

        PluginLogger.error("Unknown storage mode: " + mode);
        throw new IllegalArgumentException("Unknown storage mode: " + mode);
    }
}
