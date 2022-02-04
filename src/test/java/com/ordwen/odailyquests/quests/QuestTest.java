package com.ordwen.odailyquests.quests;

import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardType;
import org.bukkit.Material;
import org.junit.Test;

import static org.junit.Assert.*;

public class QuestTest {

    Reward reward1 = new Reward(RewardType.COMMAND, "say Hello World !");
    Reward reward2 = new Reward(RewardType.MONEY, 500);
    Quest quest1 = new Quest("testQuest", "testDesc", QuestType.BREAK, Material.COBBLESTONE, 32, reward1);
    Quest quest2 = new Quest("testQuest", "testDesc", QuestType.BREAK, Material.COBBLESTONE, 32, reward2);

    @Test
    public void getType() {
        assert quest1.getType().equals(QuestType.BREAK);
    }

    @Test
    public void getQuestName() {
        assert quest1.getQuestName().equalsIgnoreCase("testQuest");
    }

    @Test
    public void getQuestDesc() {
        assert quest1.getQuestDesc().equalsIgnoreCase("testDesc");
    }

    @Test
    public void getItemRequired() {
        assert quest1.getItemRequired() == Material.COBBLESTONE;
    }

    @Test
    public void getAmountRequired() {
        assert quest1.getAmountRequired() == 32;
    }

    @Test
    public void getReward() {
        assert quest1.getReward().equals(reward1);
        assert quest2.getReward().equals(reward2);
    }

    @Test
    public void getNumberOfQuests() {
    }
}