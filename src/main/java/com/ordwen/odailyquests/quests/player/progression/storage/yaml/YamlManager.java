package com.ordwen.odailyquests.quests.player.progression.storage.yaml;

public class YamlManager {

    private final LoadProgressionYAML loadProgressionYAML;
    private final SaveProgressionYAML saveProgressionYAML;

    public YamlManager() {
        this.loadProgressionYAML = new LoadProgressionYAML();
        this.saveProgressionYAML = new SaveProgressionYAML();
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
