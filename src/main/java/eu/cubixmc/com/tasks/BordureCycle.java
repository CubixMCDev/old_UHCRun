package eu.cubixmc.com.tasks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.scheduler.BukkitRunnable;

import eu.cubixmc.com.GameState;
import eu.cubixmc.com.Main;

public class BordureCycle extends BukkitRunnable{
	
	private static int timer = 900;
	private static boolean isCancelled = false;
	private World world = Bukkit.getWorld("world");
	private WorldBorder wb = world.getWorldBorder();
	private Main main;
	private static BordureCycle instance;
	
	public BordureCycle(Main main) {
		instance = this;
		this.main = main;
	}
	
	@Override
	public void run() {
		
		if(timer == 895) {
			main.setState(GameState.PLAYING);
		}
		
		if(timer == 0) {
			wb.setSize(50, 40);
			Bukkit.broadcastMessage(main.getPREFIX() + "§cAccélération du mouvement de la bordure !");
			isCancelled = true;
			this.cancel();
		}
		timer--;
	}

	public static int getTimer() {
		return timer;
	}

	public static boolean isCancelled() {
		return isCancelled;
	}

	public static BordureCycle getInstance() {
		return instance;
	}
	
}
