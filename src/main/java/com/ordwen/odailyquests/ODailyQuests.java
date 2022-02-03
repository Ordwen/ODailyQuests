package com.ordwen.odailyquests;

import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.logging.Logger;

public final class ODailyQuests extends JavaPlugin {

    Logger logger = getLogger();

    /* test */
    Reward reward = new Reward("msg console Hello");
    Quest quest = new Quest("testQuest", "testDesc", QuestType.BREAK, Material.COBBLESTONE, 32, RewardType.COMMAND, reward);

    @Override
    public void onEnable() {
        logger.info(ChatColor.GOLD + "Plugin is starting...");
        logger.info(ChatColor.GREEN + "Plugin is started !");

        /* test */
        logger.info("Name of quest is : " + quest.getQuestName());
        logger.info("Description of quest is : " + quest.getQuestDesc());
        logger.info("Type of quest is : " + quest.getType().getTypeName());
        logger.info("Item required of quest is : " + quest.getItemRequired());
        logger.info("Amount required of quest is : " + quest.getAmountRequired());
        logger.info("Reward type of quest is : " + quest.getRewardType());
        logger.info("Reward of quest is : " + quest.getReward().getRewardCommand());
        this.getServer().dispatchCommand(Bukkit.getConsoleSender(), quest.getReward().getRewardCommand());
    }

    @Override
    public void onDisable() {
        logger.info(ChatColor.RED + "Plugin is shutting down...");
    }
}
