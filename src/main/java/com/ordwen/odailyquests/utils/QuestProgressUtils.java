package com.ordwen.odailyquests.utils;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.api.events.QuestCompletedEvent;
import com.ordwen.odailyquests.api.events.QuestProgressEvent;
import com.ordwen.odailyquests.configuration.functionalities.progression.ProgressionMessage;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.Utils;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class QuestProgressUtils {
  /**
   * Raises the QuestProgressEvent event and determines whether to perform progress based on the event result.
   * @param player
   * @param progression
   * @param quest
   * @param amount
   */
  public static void actionQuest(Player player, Progression progression, AbstractQuest quest, int amount) {
    final QuestProgressEvent event = new QuestProgressEvent(player, progression, quest, amount);
    Bukkit.getPluginManager().callEvent(event);

    if (!event.isCancelled()) {
      runProgress(player, progression, quest, amount);
    }
  }

  /**
   * Increases quest progress.
   * @param player
   * @param progression
   * @param quest
   * @param amount
   */
  private static void runProgress(Player player, Progression progression, AbstractQuest quest, int amount) {
    if (Utils.isTimeToRenew(player, QuestsManager.getActiveQuests())) return;

    if (!quest.getRequiredWorlds().isEmpty() && !quest.getRequiredWorlds().contains(player.getWorld().getName())) {
      final String msg = QuestsMessages.NOT_REQUIRED_WORLD.getMessage(player);
      if (msg != null) player.sendMessage(msg);

      return;
    }

    for (int i = 0; i < amount; i++) {
      progression.increaseProgression();
    }

    if (progression.getProgression() >= quest.getAmountRequired()) {
      final QuestCompletedEvent completedEvent = new QuestCompletedEvent(player, progression, quest);
      ODailyQuests.INSTANCE.getServer().getPluginManager().callEvent(completedEvent);
      return;
    }

    ProgressionMessage.sendProgressionMessage(player, quest.getQuestName(), progression.getProgression(), quest.getAmountRequired());
  }
}
