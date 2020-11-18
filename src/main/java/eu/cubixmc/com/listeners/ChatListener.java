package eu.cubixmc.com.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import eu.cubixmc.com.GameState;
import eu.cubixmc.com.Main;
import eu.cubixmc.com.teams.Teams;

public class ChatListener implements Listener{
	
	private Main main;
	
	public ChatListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if(main.isState(GameState.LOBBY)) {
			return;
		}
		Player p = e.getPlayer();
		Teams team = main.getTeams().get(p.getUniqueId());
		e.setCancelled(true);
		if(team == null) {
			e.setCancelled(true);
			for(Player players : Bukkit.getOnlinePlayers()) {
				if(main.getTeams().get(players.getUniqueId()) == null) players.sendMessage("§8Spectateur §7" + p.getName() + " §8» §7" + e.getMessage());
			}
			return;
		}
		if(e.getMessage().startsWith("!")) {
			Bukkit.broadcastMessage("§bChat Global §8| §3" + p.getName() + " §8» §f " + e.getMessage().substring(1));
			e.setCancelled(true);
		}
		e.setCancelled(true);
		for(UUID uuid : main.getTeamPlayers(team)) {
			Player pteam = Bukkit.getPlayer(uuid);
			if(pteam == null) continue;
			pteam.sendMessage(team.getPrefix() + " " + p.getName() + " §8» §f" + e.getMessage());
		}
	}

}
