package eu.cubixmc.com.tasks;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import eu.cubixmc.com.GameState;
import eu.cubixmc.com.Main;

public class GameCycle extends BukkitRunnable{
	
	private static int timer = 0;
	private Main main;
	private String PREFIX;
	private static GameCycle instance;
	
	public GameCycle(Main main) {
		instance = this;
		this.main = main;
		this.PREFIX = main.getPREFIX();
	}
	
	@Override
	public void run() {
		
		if(timer == 10) {
			Bukkit.broadcastMessage(PREFIX + "§eVous n'êtes désormais plus invincible !");
			main.setState(GameState.PVPWAITING);
		}
		
		if(timer % 600 == 0) {
			for(UUID uuid : main.getPlayers()) {
				main.getExp().put(uuid, main.getExp().get(uuid) + 1);
				main.getCoins().put(uuid, main.getCoins().get(uuid) + 1);
			}
		}
		
		if(main.isState(GameState.FINISH)) this.cancel();
		
		timer++;
	}

	public static int getTimer() {
		return timer;
	}
	
	public static void setTimer(int n) {
		timer = n;
	}

	public static GameCycle getInstance() {
		return instance;
	}

}
