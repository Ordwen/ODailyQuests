package com.ordwen.odailyquests.configuration;

import com.ordwen.odailyquests.configuration.essentials.*;
import com.ordwen.odailyquests.configuration.functionalities.DisabledWorlds;
import com.ordwen.odailyquests.configuration.functionalities.SpawnerProgression;
import com.ordwen.odailyquests.configuration.functionalities.TakeItem;
import com.ordwen.odailyquests.configuration.functionalities.progression.ActionBar;
import com.ordwen.odailyquests.configuration.functionalities.progression.ProgressBar;
import com.ordwen.odailyquests.configuration.functionalities.progression.ProgressionMessage;
import com.ordwen.odailyquests.configuration.functionalities.progression.Title;
import com.ordwen.odailyquests.configuration.functionalities.rewards.CategoriesRewards;
import com.ordwen.odailyquests.configuration.functionalities.rewards.GlobalReward;
import com.ordwen.odailyquests.configuration.integrations.*;
import com.ordwen.odailyquests.files.ConfigurationFile;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigFactory {

    private ConfigFactory() {}

    private static final Map<Class<? extends IConfigurable>, IConfigurable> configs = new LinkedHashMap<>();

    public static void registerConfigs(ConfigurationFile configurationFile) {
        // essentials
        configs.put(Antiglitch.class, new Antiglitch(configurationFile));
        configs.put(CustomFurnaceResults.class, new CustomFurnaceResults(configurationFile));
        configs.put(CustomTypes.class, new CustomTypes(configurationFile));
        configs.put(Database.class, new Database(configurationFile));
        configs.put(Debugger.class, new Debugger(configurationFile));
        configs.put(Logs.class, new Logs(configurationFile));
        configs.put(Modes.class, new Modes(configurationFile));
        configs.put(QuestsAmount.class, new QuestsAmount(configurationFile));
        configs.put(RerollNotAchieved.class, new RerollNotAchieved(configurationFile));
        configs.put(Synchronization.class, new Synchronization(configurationFile));
        configs.put(Temporality.class, new Temporality(configurationFile));

        // functionalities
        configs.put(ActionBar.class, new ActionBar(configurationFile));
        configs.put(ProgressBar.class, new ProgressBar(configurationFile));
        configs.put(ProgressionMessage.class, new ProgressionMessage(configurationFile));
        configs.put(Title.class, new Title(configurationFile));
        configs.put(CategoriesRewards.class, new CategoriesRewards(configurationFile));
        configs.put(GlobalReward.class, new GlobalReward(configurationFile));
        configs.put(DisabledWorlds.class, new DisabledWorlds(configurationFile));
        configs.put(SpawnerProgression.class, new SpawnerProgression(configurationFile));
        configs.put(TakeItem.class, new TakeItem(configurationFile));

        // integrations
        configs.put(ItemsAdderEnabled.class, new ItemsAdderEnabled(configurationFile));
        configs.put(NPCNames.class, new NPCNames(configurationFile));
        configs.put(OraxenEnabled.class, new OraxenEnabled(configurationFile));
        configs.put(SharedMobs.class, new SharedMobs(configurationFile));
        configs.put(WildStackerEnabled.class, new WildStackerEnabled(configurationFile));

        // load all configs
        configs.values().forEach(IConfigurable::load);
    }

    public static <T extends IConfigurable> T getConfig(Class<T> clazz) {
        return clazz.cast(configs.get(clazz));
    }
}
