package com.ordwen.odailyquests.quests;

import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;

import static org.junit.Assert.*;

public class QuestTest {

    ItemStack itemStack = new ItemStack(Material.COBBLESTONE);
    Reward reward1 = new Reward(RewardType.COMMAND, Arrays.asList("say Hello World !"));
    Reward reward2 = new Reward(RewardType.MONEY, 500);
    Quest quest1 = new Quest("testQuest", Arrays.asList("testDesc"), QuestType.BREAK, itemStack, 32, reward1);
    Quest quest2 = new Quest("testQuest", Arrays.asList("testDesc", "testDescTwo", ""), QuestType.BREAK, itemStack, 32, reward2);

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
        assert quest1.getQuestDesc().indexOf("testDesc") == 0;
        assert quest1.getQuestDesc().size() == 1;

        assert quest2.getQuestDesc().indexOf("testDesc") == 0;
        assert quest2.getQuestDesc().indexOf("testDescTwo") == 1;
        assert quest2.getQuestDesc().indexOf("") == 2;
        assert quest2.getQuestDesc().size() == 3;
    }

    @Test
    public void getItemRequired() {
        assert quest1.getItemRequired().isSimilar(itemStack);
        assert quest2.getItemRequired().equals(quest1.getItemRequired());
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