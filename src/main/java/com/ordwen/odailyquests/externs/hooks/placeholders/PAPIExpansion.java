package com.ordwen.odailyquests.externs.hooks.placeholders;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.configuration.functionalities.progression.ProgressBar;
import com.ordwen.odailyquests.tools.TimeRemain;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

public class PAPIExpansion extends PlaceholderExpansion {

    public PAPIExpansion() {
        PAPIHook.setPlaceholderAPIHooked(true);
    }

    @Override
    public String getIdentifier() {
        return "odailyquests";
    }

    @Override
    public String getAuthor() {
        return "Ordwen";
    }

    @Override
    public String getVersion() {
        return "1.0.4";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        for (Map.Entry<String, QuestSystem> entry : ODailyQuests.questSystemMap.entrySet()) {
            String key = entry.getKey();
            QuestSystem questSystem = entry.getValue();
            if (!questSystem.getActiveQuests().containsKey(player.getName())) return null;
            if (QuestLoaderUtils.isTimeToRenew((Player) player, questSystem.getActiveQuests(), questSystem)) return null;
            final PlayerQuests playerQuests = questSystem.getActiveQuests().get(player.getName());

            // player placeholders
            if (params.equalsIgnoreCase(questSystem.getPapiPrefix() + "total")) {
                return String.valueOf(playerQuests.getTotalAchievedQuests());
            }
            if (params.equalsIgnoreCase(questSystem.getPapiPrefix() + "achieved")) {
                return String.valueOf(playerQuests.getAchievedQuests());
            }
            if (params.equalsIgnoreCase(questSystem.getPapiPrefix() + "drawin")) {
                return TimeRemain.timeRemain(player.getName(), questSystem);
            }
            if (params.startsWith(questSystem.getPapiPrefix() + "interface")) {
                return getInterfaceMessage(questSystem, params, player, playerQuests);
            }
            if (params.startsWith(questSystem.getPapiPrefix() + "progressbar")) {
                return getProgressBar(params, playerQuests);
            }
            if (params.startsWith(questSystem.getPapiPrefix() + "progress")) {
                return String.valueOf(getPlayerQuestProgression(params, playerQuests));
            }
            if (params.startsWith(questSystem.getPapiPrefix() + "name")) {
                return getPlayerQuestName(params, playerQuests);
            }
            if (params.startsWith(questSystem.getPapiPrefix() + "desc")) {
                return getPlayerQuestDescription(params, playerQuests);
            }
            if (params.startsWith(questSystem.getPapiPrefix() + "iscompleted")) {
                return isPlayerQuestCompleted(params, playerQuests);
            }
            if (params.startsWith(questSystem.getPapiPrefix() + "requiredamount")) {
                return getPlayerQuestRequiredAmount(params, playerQuests);
            }

            // quests placeholders
            if (params.startsWith(questSystem.getPapiPrefix() + "global")) {
                return getQuestName(params, questSystem.getGlobalCategory());
            }
            if (params.startsWith(questSystem.getPapiPrefix() + "easy")) {
                return getQuestName(params, questSystem.getEasyCategory());
            }
            if (params.startsWith(questSystem.getPapiPrefix() + "medium")) {
                return getQuestName(params, questSystem.getMediumCategory());
            }
            if (params.startsWith(questSystem.getPapiPrefix() + "hard")) {
                return getQuestName(params, questSystem.getHardCategory());
            }
        }

        return null;
    }

    /**
     * Get interface placeholders.
     *
     * @param params       the placeholder.
     * @param player       the player.
     * @param playerQuests the player quests.
     * @return the result.
     */
    private String getInterfaceMessage(QuestSystem questSystem, String params, OfflinePlayer player, PlayerQuests playerQuests) {
        if (params.equals("interface_complete_get_type")) {
            return ColorConvert.convertColorCode(PlaceholderAPI.setPlaceholders(player, questSystem.getCompleteGetType()));
        } else if (params.startsWith("interface_status_")) {
            final String supposedIndex = params.substring("interface_status_".length());
            int index;

            try {
                index = Integer.parseInt(supposedIndex) - 1;
            } catch (Exception e) {
                return ChatColor.RED + "Invalid index.";
            }

            return ColorConvert.convertColorCode(PlaceholderAPI.setPlaceholders(player, getQuestStatus(questSystem, index, playerQuests)));
        }

        return ChatColor.RED + "Invalid placeholder.";
    }

    /**
     * Check if the quest is completed.
     *
     * @param params       the placeholder.
     * @param playerQuests the player quests.
     * @return the result.
     */
    private String isPlayerQuestCompleted(String params, PlayerQuests playerQuests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf('_') + 1)) - 1;
        } catch (Exception e) {
            return ChatColor.RED + "Invalid index.";
        }

        int i = 0;
        for (AbstractQuest quest : playerQuests.getPlayerQuests().keySet()) {
            if (i == index) {
                return String.valueOf(playerQuests.getPlayerQuests().get(quest).isAchieved());
            }
            i++;
        }

        return ChatColor.RED + "Invalid index.";
    }

    /**
     * Get the corresponding text for the quest status.
     *
     * @param index        the quest index.
     * @param playerQuests the player quests.
     * @return the achieved message or the progress message.
     */
    private String getQuestStatus(QuestSystem questSystem, int index, PlayerQuests playerQuests) {

        int i = 0;
        for (AbstractQuest quest : playerQuests.getPlayerQuests().keySet()) {
            if (i == index) {
                return (playerQuests.getPlayerQuests().get(quest).isAchieved() ? questSystem.getAchieved() : questSystem.getProgression())
                        .replace("%progress%", String.valueOf(playerQuests.getPlayerQuests().get(quest).getProgression()))
                        .replace("%required%", String.valueOf(quest.getAmountRequired()))
                        .replace("%progressBar%", ProgressBar.getProgressBar(playerQuests.getPlayerQuests().get(quest).getProgression(), quest.getAmountRequired()));
            }
            i++;
        }

        return ChatColor.RED + "Invalid index.";
    }

    /**
     * Get the name of the current player quest.
     *
     * @param params       the placeholder.
     * @param playerQuests the player quests.
     * @return the name of the quest.
     */
    private String getPlayerQuestName(String params, PlayerQuests playerQuests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf("_") + 1)) - 1;
        } catch (Exception e) {
            return ChatColor.RED + "Invalid index.";
        }

        int i = 0;
        for (AbstractQuest quest : playerQuests.getPlayerQuests().keySet()) {
            if (i == index) {
                return quest.getQuestName();
            }
            i++;
        }

        return ChatColor.RED + "Invalid index.";
    }

    /**
     * Get the required amount of a quest.
     *
     * @param params       the placeholder.
     * @param playerQuests the player quests.
     * @return the required amount.
     */
    private String getPlayerQuestRequiredAmount(String params, PlayerQuests playerQuests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf('_') + 1)) - 1;
        } catch (Exception e) {
            return ChatColor.RED + "Invalid index.";
        }

        int i = 0;
        for (AbstractQuest quest : playerQuests.getPlayerQuests().keySet()) {
            if (i == index) {
                return String.valueOf(quest.getAmountRequired());
            }
            i++;
        }

        return ChatColor.RED + "Invalid index.";
    }

    /**
     * Get the line of a specified quest description
     *
     * @param params       the placeholder
     * @param playerQuests the player quests
     * @return the line of the description
     */
    private String getPlayerQuestDescription(String params, PlayerQuests playerQuests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf("_") + 1, params.lastIndexOf("_"))) - 1;
        } catch (Exception e) {
            return ChatColor.RED + "Invalid index.";
        }

        int line;
        try {
            line = Integer.parseInt(params.substring(params.lastIndexOf("_") + 1)) - 1;
        } catch (Exception e) {
            return ChatColor.RED + "Invalid line.";
        }

        int i = 0;
        for (AbstractQuest quest : playerQuests.getPlayerQuests().keySet()) {
            if (i == index) {
                if (line <= quest.getQuestDesc().size()) return quest.getQuestDesc().get(line);
                else return ChatColor.RED + "Invalid line.";
            }
            i++;
        }

        return ChatColor.RED + "Invalid index.";
    }

    /**
     * Get player quest progression.
     *
     * @param params       the placeholder
     * @param playerQuests the player quests
     * @return current progression
     */
    public int getPlayerQuestProgression(String params, PlayerQuests playerQuests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf("_") + 1)) - 1;
        } catch (Exception e) {
            return -1;
        }

        int i = 0;
        for (AbstractQuest quest : playerQuests.getPlayerQuests().keySet()) {
            if (i == index) {
                return playerQuests.getPlayerQuests().get(quest).getProgression();
            }
            i++;
        }
        return -1;
    }

    /**
     * Get the progress bar of a quest.
     *
     * @param params       the placeholder
     * @param playerQuests the player quests
     * @return the progress bar
     */
    private String getProgressBar(String params, PlayerQuests playerQuests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf("_") + 1)) - 1;
        } catch (Exception e) {
            return ChatColor.RED + "Invalid index.";
        }

        int i = 0;
        for (AbstractQuest quest : playerQuests.getPlayerQuests().keySet()) {
            if (i == index) {
                return ProgressBar.getProgressBar(playerQuests.getPlayerQuests().get(quest).getProgression(), quest.getAmountRequired());
            }
            i++;
        }

        return ChatColor.RED + "Invalid index.";
    }

    /**
     * Get quest name by index & list.
     *
     * @param params placeholder
     * @param quests list where find the quest
     * @return the name of the quest
     */
    private String getQuestName(String params, ArrayList<AbstractQuest> quests) {
        int index;
        try {
            index = Integer.parseInt(params.substring(params.indexOf("_") + 1)) - 1;
        } catch (Exception e) {
            return ChatColor.RED + "Invalid index.";
        }
        if (quests.size() - 1 >= index) {
            return quests.get(index).getQuestName();
        } else return ChatColor.RED + "Invalid index.";
    }
}
