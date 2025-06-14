package com.ordwen.odailyquests.quests.player.progression.storage.yaml;

import com.ordwen.odailyquests.files.implementations.ProgressionFile;

public class YamlManager {

    private final LoadProgressionYAML loadProgressionYAML;
    private final SaveProgressionYAML saveProgressionYAML;

    public YamlManager(ProgressionFile progressionFile) {
        this.loadProgressionYAML = new LoadProgressionYAML(progressionFile);
        this.saveProgressionYAML = new SaveProgressionYAML(progressionFile);
    }

    /**
     * Get LoadProgressionYAML instance.
     * @return LoadProgressionYAML instance.
     */
    public LoadProgressionYAML getLoadProgressionYAML() {
        return loadProgressionYAML;
    }

    /**
     * Get SaveProgressionYAML instance.
     * @return SaveProgressionYAML instance.
     */
    public SaveProgressionYAML getSaveProgressionYAML() {
        return saveProgressionYAML;
    }
}
