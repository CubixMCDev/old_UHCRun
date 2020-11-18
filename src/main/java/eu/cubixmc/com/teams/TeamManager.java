package eu.cubixmc.com.teams;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class TeamManager {
	
	public void registerTeams() {
		ScoreboardManager sbm = Bukkit.getScoreboardManager();
		Scoreboard sb = sbm.getMainScoreboard();
		Objective obj;
		if(sb.getObjective("health") != null) {
			obj = sb.getObjective("health");
			obj.unregister();
		}
		for(Teams team : Teams.values()) {
			if(Bukkit.getScoreboardManager().getMainScoreboard().getTeam(team.getPrefix()) != null) {
				Team sbteam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(team.getPrefix());
				sbteam.unregister();
			}
			Team sbteam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(team.getPrefix());
			sbteam.setPrefix(team.getPrefix() + " ");
			sbteam.setAllowFriendlyFire(false);			
		}
	}
}
