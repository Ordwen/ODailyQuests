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
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;
import com.ordwen.odailyquests.files.implementations.FilesManager;
import com.ordwen.odailyquests.files.implementations.TotalRewardsFile;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigFactory {

    private ConfigFactory() {}

    private static final Map<Class<? extends IConfigurable>, IConfigurable> configs = new LinkedHashMap<>();

    private static <T extends IConfigurable> void reg(Class<T> type, T instance) {
        configs.put(type, instance);
    }

    public static void registerConfigs(FilesManager filesManager) {
        // cleanup stateful configs before re-registering (e.g. boss bars)
        IConfigurable prev = configs.get(ProgressionMessage.class);
        if (prev instanceof ProgressionMessage pm) {
            pm.cleanup(); // remove existing boss bars
        }

        configs.clear();

        final ConfigurationFile configurationFile = filesManager.getConfigurationFile();
        final TotalRewardsFile totalRewardsFile = filesManager.getTotalRewardsFile();

        // essentials
        reg(Prefix.class, new Prefix(configurationFile));
        reg(Antiglitch.class, new Antiglitch(configurationFile));
        reg(CustomFurnaceResults.class, new CustomFurnaceResults(configurationFile));
        reg(CustomTypes.class, new CustomTypes(configurationFile));
        reg(Database.class, new Database(configurationFile));
        reg(PlayerDataLoadDelay.class, new PlayerDataLoadDelay(configurationFile));
        reg(Debugger.class, new Debugger(configurationFile));
        reg(JoinMessageDelay.class, new JoinMessageDelay(configurationFile));
        reg(ReloadMessage.class, new ReloadMessage(configurationFile));
        reg(Logs.class, new Logs(configurationFile));
        reg(TimestampMode.class, new TimestampMode(configurationFile));
        reg(SafetyMode.class, new SafetyMode(configurationFile));
        reg(QuestsPerCategory.class, new QuestsPerCategory(configurationFile));
        reg(RerollNotAchieved.class, new RerollNotAchieved(configurationFile));
        reg(RerollMaximum.class, new RerollMaximum(configurationFile));
        reg(Synchronization.class, new Synchronization(configurationFile));
        reg(RenewInterval.class, new RenewInterval(configurationFile));
        reg(RenewTime.class, new RenewTime(configurationFile));
        reg(CheckForUpdate.class, new CheckForUpdate(configurationFile));

        // functionalities
        reg(ActionBar.class, new ActionBar(configurationFile));
        reg(ProgressBar.class, new ProgressBar(configurationFile));
        reg(ProgressionMessage.class, new ProgressionMessage(configurationFile));
        reg(Title.class, new Title(configurationFile));
        reg(ToastNotification.class, new ToastNotification(configurationFile));
        reg(DisabledWorlds.class, new DisabledWorlds(configurationFile));
        reg(SpawnerProgression.class, new SpawnerProgression(configurationFile));
        reg(TakeItem.class, new TakeItem(configurationFile));
        reg(CompleteOnlyOnClick.class, new CompleteOnlyOnClick(configurationFile));
        reg(CommandAliases.class, new CommandAliases(configurationFile));

        // rewards
        reg(CategoriesRewards.class, new CategoriesRewards(configurationFile));
        reg(GlobalReward.class, new GlobalReward(configurationFile));
        reg(TotalRewards.class, new TotalRewards(totalRewardsFile));

        // integrations
        reg(ItemsAdderEnabled.class, new ItemsAdderEnabled(configurationFile));
        reg(NPCNames.class, new NPCNames(configurationFile));
        reg(OraxenEnabled.class, new OraxenEnabled(configurationFile));
        reg(NexoEnabled.class, new NexoEnabled(configurationFile));
        reg(SharedMobs.class, new SharedMobs(configurationFile));
        reg(WildStackerEnabled.class, new WildStackerEnabled(configurationFile));
        reg(RoseStackerEnabled.class, new RoseStackerEnabled(configurationFile));

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
