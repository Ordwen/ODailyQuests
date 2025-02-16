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
import com.ordwen.odailyquests.files.ConfigurationFiles;

import java.util.HashMap;
import java.util.Map;

public class ConfigFactory {

    private ConfigFactory() {}

    private static final Map<Class<? extends IConfigurable>, IConfigurable> configs = new HashMap<>();

    public static void registerConfigs(ConfigurationFiles configurationFiles) {
        // essentials
        configs.put(Modes.class, new Modes(configurationFiles));
        configs.put(Temporality.class, new Temporality(configurationFiles));
        configs.put(QuestsAmount.class, new QuestsAmount(configurationFiles));
        configs.put(CustomFurnaceResults.class, new CustomFurnaceResults(configurationFiles));
        configs.put(Logs.class, new Logs(configurationFiles));
        configs.put(CustomTypes.class, new CustomTypes(configurationFiles));
        configs.put(Antiglitch.class, new Antiglitch(configurationFiles));

        // functionalities
        configs.put(ActionBar.class, new ActionBar(configurationFiles));
        configs.put(Title.class, new Title(configurationFiles));
        configs.put(DisabledWorlds.class, new DisabledWorlds(configurationFiles));
        configs.put(GlobalReward.class, new GlobalReward(configurationFiles));
        configs.put(CategoriesRewards.class, new CategoriesRewards(configurationFiles));
        configs.put(SpawnerProgression.class, new SpawnerProgression(configurationFiles));
        configs.put(TakeItem.class, new TakeItem(configurationFiles));
        configs.put(ProgressionMessage.class, new ProgressionMessage(configurationFiles));
        configs.put(ProgressBar.class, new ProgressBar(configurationFiles));

        // integrations
        configs.put(NPCNames.class, new NPCNames(configurationFiles));
        configs.put(WildStackerEnabled.class, new WildStackerEnabled(configurationFiles));
        configs.put(ItemsAdderEnabled.class, new ItemsAdderEnabled(configurationFiles));
        configs.put(OraxenEnabled.class, new OraxenEnabled(configurationFiles));
        configs.put(SharedMobs.class, new SharedMobs(configurationFiles));

        // utils
        configs.put(Synchronization.class, new Synchronization(configurationFiles));
        configs.put(RerollNotAchieved.class, new RerollNotAchieved(configurationFiles));

        // load all configs
        configs.values().forEach(IConfigurable::load);
    }

    public static <T extends IConfigurable> T getConfig(Class<T> clazz) {
        return clazz.cast(configs.get(clazz));
    }
}
