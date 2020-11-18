package eu.cubixmc.com.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import eu.cubixmc.com.GameState;
import eu.cubixmc.com.Main;
import eu.cubixmc.com.teams.Teams;

public class PvPCycle extends BukkitRunnable{
	
	private static int timer = 1500;
	private Main main;
	private String PREFIX;
	private World world;
	private WorldBorder wb;
	private static PvPCycle instance;
	private List<UUID> eliminated = new ArrayList<UUID>();
	private List<Teams> removedteams = new ArrayList<Teams>();
	
	public PvPCycle(Main main) {
		this.main = main;
		this.PREFIX = main.getPREFIX();
		world = Bukkit.getWorld("world");
		wb = world.getWorldBorder();
		instance = this;
		
	}
	
	@Override
	public void run() {
		
		if(timer == 0) {
			Bukkit.broadcastMessage(PREFIX + "§aLe PvP est désormais activé ! Les joueurs peuvent s'entre tuer.");
			Bukkit.broadcastMessage(main.getPREFIX() + "§eLa bordure est désormais en mouvement !");
			wb.setSize(100, 300);
			for(UUID uuid : main.getPlayers()) {
				Player p = Bukkit.getPlayer(uuid);
				if(p == null) {
					eliminated.add(uuid);
				}else {
					p.setHealth(20);
				}
			}
			if(!main.isState(GameState.LOBBY)) {
				for(Teams team : main.getTeamsAlive()) {
					int count = 0;
					for(UUID teamuuid : main.getTeamPlayers(team)) {
						if(Bukkit.getPlayer(teamuuid) == null) count++;
						if(count == main.getTeamPlayers(team).size()) {
							Bukkit.broadcastMessage(PREFIX + "§7L'équipe " + main.getTeams().get(teamuuid).getPrefix() + " §7a été éliminé !");
							removedteams.add(team);
						}
					}
				}
			}			
			
			for(UUID uuid : eliminated) {
				if(main.getPlayers().contains(uuid)) {
					main.getPlayers().remove(uuid);
					main.getTeams().remove(uuid);
				}
				if(main.getTeams().containsKey(uuid)) main.getTeams().remove(uuid);
			}
			for(Teams team : removedteams) main.getTeamsAlive().remove(team);
			for(Teams team : main.getTeamsAlive()) {
				teleport(team);
			}
			if(!main.isState(GameState.LOBBY)) {
				main.checkWin();
			}
			main.setState(GameState.TELEPORTING);
			BordureCycle bordure = new BordureCycle(main);
			bordure.runTaskTimer(main, 0, 20);
			this.cancel();
		}
		
		timer--;
	}
	
	private void teleport(Teams team) {
		Random r = new Random();
		int x = r.nextInt(250);
		int y = 150;
		int z = r.nextInt(250);
		for(UUID uuid : main.getTeamPlayers(team)) {
			Player p = Bukkit.getPlayer(uuid);
			p.teleport(new Location(Bukkit.getWorld("world"), x, y, z));
		}
	}
	
	public static int getTimer() {
		return timer;
	}

	public static void setTimer(int newtime) {
		timer = newtime;
	}

	public static PvPCycle getInstance() {
		return instance;
	}
	
}
