package com.ordwen.odailyquests.configuration;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.*;
import com.ordwen.odailyquests.configuration.functionalities.CommandAliases;
import com.ordwen.odailyquests.configuration.functionalities.CompleteOnlyOnClick;
import com.ordwen.odailyquests.configuration.functionalities.DisabledWorlds;
import com.ordwen.odailyquests.configuration.functionalities.SpawnerProgression;
import com.ordwen.odailyquests.configuration.functionalities.TakeItem;
import com.ordwen.odailyquests.configuration.functionalities.progression.*;
import com.ordwen.odailyquests.configuration.functionalities.rewards.CategoriesRewards;
import com.ordwen.odailyquests.configuration.functionalities.rewards.GlobalReward;
import com.ordwen.odailyquests.configuration.functionalities.rewards.TotalRewards;
import com.ordwen.odailyquests.configuration.integrations.*;
import com.ordwen.odailyquests.files.FilesManager;
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;
import com.ordwen.odailyquests.files.implementations.TotalRewardsFile;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigFactory {

    private ConfigFactory() {}

    private static final Map<Class<? extends IConfigurable>, IConfigurable> configs = new LinkedHashMap<>();

    public static void registerConfigs(FilesManager filesManager) {
        final ConfigurationFile configurationFile = filesManager.getConfigurationFile();
        final TotalRewardsFile totalRewardsFile = filesManager.getTotalRewardsFile();

        // essentials
        configs.put(Prefix.class, new Prefix(configurationFile));
        configs.put(Antiglitch.class, new Antiglitch(configurationFile));
        configs.put(CustomFurnaceResults.class, new CustomFurnaceResults(configurationFile));
        configs.put(CustomTypes.class, new CustomTypes(configurationFile));
        configs.put(Database.class, new Database(configurationFile));
        configs.put(PlayerDataLoadDelay.class, new PlayerDataLoadDelay(configurationFile));
        configs.put(Debugger.class, new Debugger(configurationFile));
        configs.put(JoinMessageDelay.class, new JoinMessageDelay(configurationFile));
        configs.put(Logs.class, new Logs(configurationFile));
        configs.put(TimestampMode.class, new TimestampMode(configurationFile));
        configs.put(SafetyMode.class, new SafetyMode(configurationFile));
        configs.put(QuestsPerCategory.class, new QuestsPerCategory(configurationFile));
        configs.put(RerollNotAchieved.class, new RerollNotAchieved(configurationFile));
        configs.put(Synchronization.class, new Synchronization(configurationFile));
        configs.put(RenewInterval.class, new RenewInterval(configurationFile));
        configs.put(RenewTime.class, new RenewTime(configurationFile));
        configs.put(CheckForUpdate.class, new CheckForUpdate(configurationFile));

        // functionalities
        configs.put(ActionBar.class, new ActionBar(configurationFile));
        configs.put(ProgressBar.class, new ProgressBar(configurationFile));

        // if there was a previous ProgressionMessage config, clean it up first
        final IConfigurable prev = configs.get(ProgressionMessage.class);
        if (prev instanceof ProgressionMessage pm) {
            pm.cleanup(); // remove existing boss bars
        }
        configs.put(ProgressionMessage.class, new ProgressionMessage(configurationFile));

        configs.put(Title.class, new Title(configurationFile));
        configs.put(ToastNotification.class, new ToastNotification(configurationFile));
        configs.put(DisabledWorlds.class, new DisabledWorlds(configurationFile));
        configs.put(SpawnerProgression.class, new SpawnerProgression(configurationFile));
        configs.put(TakeItem.class, new TakeItem(configurationFile));
        configs.put(CompleteOnlyOnClick.class, new CompleteOnlyOnClick(configurationFile));
        configs.put(CommandAliases.class, new CommandAliases(configurationFile));

        // rewards
        configs.put(CategoriesRewards.class, new CategoriesRewards(configurationFile));
        configs.put(GlobalReward.class, new GlobalReward(configurationFile));
        configs.put(TotalRewards.class, new TotalRewards(totalRewardsFile));

        // integrations
        configs.put(ItemsAdderEnabled.class, new ItemsAdderEnabled(configurationFile));
        configs.put(NPCNames.class, new NPCNames(configurationFile));
        configs.put(OraxenEnabled.class, new OraxenEnabled(configurationFile));
        configs.put(NexoEnabled.class, new NexoEnabled(configurationFile));
        configs.put(SharedMobs.class, new SharedMobs(configurationFile));
        configs.put(WildStackerEnabled.class, new WildStackerEnabled(configurationFile));
        configs.put(RoseStackerEnabled.class, new RoseStackerEnabled(configurationFile));

        // load all configs
        configs.values().forEach(IConfigurable::load);

        // reload the timer task
        if (ODailyQuests.INSTANCE.timerTask != null) {
            ODailyQuests.INSTANCE.timerTask.reload();
        }
    }

    public static <T extends IConfigurable> T getConfig(Class<T> clazz) {
        return clazz.cast(configs.get(clazz));
    }
}
