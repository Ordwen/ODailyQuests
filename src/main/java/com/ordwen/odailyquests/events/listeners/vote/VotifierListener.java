package com.ordwen.odailyquests.events.listeners.vote;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VotifierListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onVotifierEvent(VotifierEvent event) {
        System.out.println("HELLO FROM VotifierListener");

        final Vote vote = event.getVote();
        final String username = vote.getUsername();
        System.out.println("Username: " + username);

        final Player player = Bukkit.getPlayer(username);

        if (player == null) {
            System.out.println("Player is null");
            return;
        }
        setPlayerQuestProgression(event, player, 1, "NU_VOTIFIER");
    }
}
