package com.ordwen.odailyquests;

import com.ordwen.odailyquests.commands.interfaces.InterfaceInventory;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.quests.QuestsLoader;
import com.ordwen.odailyquests.quests.categories.Category;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardLoader;
import com.ordwen.odailyquests.tools.Pair;
import com.ordwen.odailyquests.tools.TimerTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class QuestSystem {

    @Getter @Setter
    private String systemName;
    @Getter @Setter
    private String playerTableSQL;
    @Getter @Setter
    private String progressionTableSQL;
    @Getter @Setter
    private String playerTableName;
    @Getter @Setter
    private String progressionTableName;
    @Getter @Setter
    private String MYSQL_PLAYER_QUERY;
    @Getter @Setter
    private String MYSQL_PROGRESS_UPDATE;
    @Getter @Setter
    private String H2_PLAYER_QUERY;
    @Getter @Setter
    private String H2_PROGRESS_UPDATE;
    @Getter @Setter
    private String configPath;
    @Getter @Setter
    private int questsMode;
    @Getter @Setter
    private int timeStampMode;
    @Getter @Setter
    private QuestsMessages QUESTS_RENEWED_MESSAGE;
    @Getter @Setter
    private Category globalCategory;
    @Getter @Setter
    private Category easyCategory;
    @Getter @Setter
    private Category mediumCategory;
    @Getter @Setter
    private Category hardCategory;
    @Getter @Setter
    private FileConfiguration globalQuestsConfig;
    @Getter @Setter
    private FileConfiguration easyQuestsConfig;
    @Getter @Setter
    private FileConfiguration mediumQuestsConfig;
    @Getter @Setter
    private FileConfiguration hardQuestsConfig;
    @Getter @Setter
    private File globalQuestsFile;
    @Getter @Setter
    private File easyQuestsFile;
    @Getter @Setter
    private File mediumQuestsFile;
    @Getter @Setter
    private File hardQuestsFile;
    @Getter @Setter
    private String questsFilePath;
    @Getter @Setter
    private QuestsLoader questsLoader;
    @Getter @Setter
    private int globalQuestsAmount;
    @Getter @Setter
    private int easyQuestsAmount;
    @Getter @Setter
    private int mediumQuestsAmount;
    @Getter @Setter
    private int hardQuestsAmount;
    @Getter @Setter
    private TimerTask timerTask;
    @Getter @Setter
    private int temporalityMode;
    @Getter @Setter
    private int questsAmount;
    @Getter
    private HashMap<String, PlayerQuests> activeQuests = new HashMap<>();
    @Getter @Setter
    private File progressionFile;
    @Getter @Setter
    private String interfaceName;
    @Getter @Setter
    private Inventory playerQuestsInventoryBase;
    @Getter @Setter
    private int size;
    @Getter @Setter
    private String achieved;
    @Getter @Setter
    private String status;
    @Getter @Setter
    private String progression;
    @Getter @Setter
    private String completeGetType;
    @Getter @Setter
    private boolean isPlayerHeadEnabled;
    @Getter @Setter
    private boolean isGlowingEnabled;
    @Getter @Setter
    private boolean isStatusDisabled;
    @Getter @Setter
    private ConfigurationSection interfaceConfig;
    /* item slots */
    @Getter
    private final Set<Integer> slotsPlayerHead = new HashSet<>();
    @Getter
    private final HashMap<Integer, List<Integer>> slotQuests = new HashMap<>();
    /* item lists */
    @Getter
    private final Set<ItemStack> fillItems = new HashSet<>();
    @Getter
    private final Set<ItemStack> closeItems = new HashSet<>();
    @Getter
    private final Map<Integer, List<String>> playerCommandsItems = new HashMap<>();
    @Getter
    private final Map<Integer, List<String>> consoleCommandsItems = new HashMap<>();
    /* items with placeholders */
    @Getter
    private final Map<Integer, ItemStack> papiItems = new HashMap<>();
    @Getter @Setter
    private boolean isEasyRewardEnabled;
    @Getter @Setter
    private boolean isMediumRewardEnabled;
    @Getter @Setter
    private boolean isHardRewardEnabled;
    @Getter @Setter
    private Reward easyReward;
    @Getter @Setter
    private Reward mediumReward;
    @Getter @Setter
    private Reward hardReward;
    @Getter
    private final RewardLoader rewardLoader = new RewardLoader();
    @Getter @Setter
    private Reward globalReward;
    @Getter @Setter
    private boolean isGlobalRewardEnabled;
    @Getter @Setter
    private QuestsMessages ALL_QUESTS_ACHIEVED;
    @Getter @Setter
    private QuestsMessages EASY_QUESTS_ACHIEVED;
    @Getter @Setter
    private QuestsMessages MEDIUM_QUESTS_ACHIEVED;
    @Getter @Setter
    private QuestsMessages HARD_QUESTS_ACHIEVED;
    @Getter @Setter
    private QuestsMessages QUESTS_IN_PROGRESS;
    @Getter @Setter
    private String papiPrefix;
    @Getter @Setter
    private String playerNPCName;
    @Getter @Setter
    private String globalNPCName;
    @Getter @Setter
    private String easyNPCName;
    @Getter @Setter
    private String mediumNPCName;
    @Getter @Setter
    private String hardNPCName;
    @Getter
    private final float questsInvSize = 45;
    @Getter
    private final List<ItemStack> emptyCaseItems = new ArrayList<>();
    @Getter
    private final List<InterfaceInventory> categorizedInterfaces = new ArrayList<>();
    @Getter
    private final List<String> commandAliases = new ArrayList<>();
    @Getter @Setter
    private String commandName;
    @Getter @Setter
    private String adminCommandName;
    @Getter
    private final List<String> adminCommandAliases = new ArrayList<>();
}
