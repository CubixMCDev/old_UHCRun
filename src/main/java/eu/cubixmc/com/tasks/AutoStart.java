package eu.cubixmc.com.tasks;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import eu.cubixmc.com.Main;
import eu.cubixmc.com.Title;
import eu.cubixmc.com.teams.Teams;

public class AutoStart extends BukkitRunnable{
	
	private Main main;
	private static int timer = 120;
	private Title title = new Title();
	private static AutoStart instance;
	public static boolean isRunning;
	
	public AutoStart(Main main) {
		instance = this;
		this.main = main;
	}
	
	@Override
	public void run() {
		
		isRunning = true;
		
		if(timer == 10 || timer == 9 || timer == 8 || timer == 7 || timer == 6 || timer == 5 ||timer == 4 || timer == 3 || timer == 2 || timer == 1) {
			for(UUID uuid : main.getPlayers()) {
				Player player = Bukkit.getPlayer(uuid);
				title.sendTitle(player, "§c" + timer, "§ePréparez-vous", 20);
			}
		}
		
		if(timer == 0) {
			for(Player players : Bukkit.getOnlinePlayers()) {
				if(!main.getPlayers().contains(players.getUniqueId())) main.getPlayers().add(players.getUniqueId());
				if(!main.getKills().containsKey(players.getUniqueId())) main.getKills().put(players.getUniqueId(), 0);
			}
			fillTeams();
			setHealthOnSb();
			for(UUID uuid : main.getPlayers()) {
				Player player = Bukkit.getPlayer(uuid);
				title.sendTitle(player, "§aLancement de la partie !", "", 20);
				player.setGameMode(GameMode.SURVIVAL);
				main.getExp().put(uuid, 0);
				main.getCoins().put(uuid, 0);
			}
			for(Teams team : Teams.values()) {
				if(main.getTeamPlayers(team).size() > 0) {
					main.getTeamsAlive().add(team);
				}
			}
			for(Teams team : main.getTeamsAlive()) {
				teleport(team);
			}
			Bukkit.getWorld("world").getWorldBorder().setSize(1000);
			GameCycle cycle = new GameCycle(main);
			cycle.runTaskTimer(main, 0, 20);
			PvPCycle pvp = new PvPCycle(main);
			pvp.runTaskTimer(main, 0, 20);
			deleteLobby(Bukkit.getWorld("world"));
			this.cancel();
		}
		
		timer--;
	}
	
	private void setHealthOnSb() {
		ScoreboardManager sbm = Bukkit.getScoreboardManager();
		Scoreboard sb = sbm.getMainScoreboard();
		Objective obj;
		if(sb.getObjective("health") == null) {
			obj = sb.registerNewObjective("health", "health");
			obj.setDisplaySlot(DisplaySlot.PLAYER_LIST);	
		}else {
			obj = sb.getObjective("health");
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
	}

	private void deleteLobby(World world) {
	    Location loc1 = new Location(world, 0.0D, 135.0D, 0.0D);
	    Location loc2 = new Location(world, 60.0D, 165.0D, 60.0D);
	    int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
	    int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
	    int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
	    int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
	    int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
	    int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
	    
	    for (int x = minX; x <= maxX; x++) {
	      for (int y = minY; y <= maxY; y++) {
	        for (int z = minZ; z <= maxZ; z++) {
	          
	          Block block = world.getBlockAt(x, y, z);
	          block.setType(Material.AIR);
	          for (Entity entity : world.getEntities()) {
	            if (entity instanceof org.bukkit.entity.Item) entity.remove(); 
	          } 
	        } 
	      }
	    }
	}

	private void teleport(Teams team) {
		Random r = new Random();
		int x = r.nextInt(500);
		int y = 150;
		int z = r.nextInt(500);
		for(UUID uuid : main.getTeamPlayers(team)) {
			Player p = Bukkit.getPlayer(uuid);
			p.teleport(new Location(Bukkit.getWorld("world"), x, y, z));
			p.getInventory().clear();
		}
	}
	
	@SuppressWarnings("deprecation")
	private void fillTeams() {
		if(main.getPlayers().size() == 24) {
			return;
		}
		for(UUID uuid : main.getPlayers()) {
			Player p = Bukkit.getPlayer(uuid);
			if(main.getTeams().get(p.getUniqueId()) == null) {
				for(Teams team : Teams.values()) {
					int size = main.getTeamPlayers(team).size();
					if(size == 2) continue;
					main.getTeams().put(p.getUniqueId(), team);
					Bukkit.getScoreboardManager().getMainScoreboard().getTeam(team.getPrefix()).addPlayer(p);
					p.setPlayerListName(team.getPrefix() + " " + p.getName());
				}
			}
		}
	}
	
	public static int getTimer() {
		return timer;
	}
	
	public static void setTimer(int time) {
		timer = time;
	}

	public static AutoStart getInstance() {
		return instance;
	}

}
