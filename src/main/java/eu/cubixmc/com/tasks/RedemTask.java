package eu.cubixmc.com.tasks;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import eu.cubixmc.com.Main;
import fr.cubixmc.api.CubixAPI;

public class RedemTask extends BukkitRunnable{

	private static int timer = 10;
	private Main main;
	private static RedemTask instance;
	private CubixAPI api = (CubixAPI) Bukkit.getServer().getPluginManager().getPlugin("CubixAPI");
	
	public RedemTask(Main main) {
		instance = this;
		this.main = main;
	}
	
	@Override
	public void run() {
		
		if(timer == 10 || timer == 9 || timer == 8 || timer == 7 || timer == 6 || timer == 5 ||timer == 4 || timer == 3 || timer == 2) {
			Bukkit.broadcastMessage(main.getPREFIX() + "§cRedémarrage du serveur dans §4" + timer + " §csecondes.");
		}
		
		if(timer == 1) {
			Bukkit.broadcastMessage(main.getPREFIX() + "§cRedémarrage du serveur dans §4" + timer + " §csecondes.");
			for(Player p : Bukkit.getOnlinePlayers()) main.teleport(p, "Hub");
			for(Player player : Bukkit.getOnlinePlayers()) {
				UUID uuid = player.getUniqueId();
				if(main.getExp().get(uuid) == null || main.getExp().get(uuid) == 0) continue;
				if(main.getCoins().get(uuid) == null || main.getCoins().get(uuid) == 0) continue;
				api.getExpManager().addExp(main.getExp().get(uuid), uuid);
				api.getEcoManager().addCoins(player, main.getCoins().get(uuid));
				player.sendMessage("§6[§eCubixMC§6] §eTu as gagné §6" + main.getCoins().get(uuid) + " §ecoins et §d" + main.getExp().get(uuid) + "d'expérience §e!");
			}
			
		}
		
		if(timer == 0) {
			Bukkit.spigot().restart();
			this.cancel();
		}
		timer--;
	}

	public static RedemTask getInstance() {
		return instance;
	}
	
	public static int getTimer() {
		return timer;
	}
	
}
