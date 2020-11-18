package eu.cubixmc.com.world;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.BlockPopulator;

import eu.cubixmc.com.Main;
import eu.cubixmc.com.world.populator.LobbyPopulator;
import eu.cubixmc.com.world.populator.NetherPopulator;
import eu.cubixmc.com.world.populator.OrePopulator;

public class WorldListener implements Listener {
	
	private Main main;
	
	public WorldListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onWorldInit(WorldInitEvent e) {
		
		if(e.getWorld().getName().equalsIgnoreCase("world")) {
			LobbyPopulator.createLobby(e.getWorld(), e.getWorld().getChunkAt(0, 0), main.getClass().getClassLoader().getResourceAsStream("lobby.schematic"));
			for(BlockPopulator pop : e.getWorld().getPopulators()) {
				if(pop instanceof NetherPopulator) {
					return;
				}
				if(pop instanceof OrePopulator) {
					return;
				}
			}
			e.getWorld().getPopulators().add(new OrePopulator());
			e.getWorld().getPopulators().add(new NetherPopulator(main));
		}
	}
	
	@EventHandler
	public void onWorldLoaded(WorldLoadEvent e) {	
		World world = e.getWorld();
		world.setGameRuleValue("naturalRegeneration", "false");
		world.setGameRuleValue("doDaylightCycle", "false");
		world.setThundering(false);
		world.setTime(6000);
		world.setSpawnLocation(327, 131, 327);
		world.getWorldBorder().setCenter(0, 0);
		world.getWorldBorder().setSize(1000);
		System.out.println("Monde " + e.getWorld().getName() + " chargé avec succès !");	
	}

}
