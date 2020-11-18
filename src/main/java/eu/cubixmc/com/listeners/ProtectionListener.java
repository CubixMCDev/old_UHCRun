package eu.cubixmc.com.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import eu.cubixmc.com.GameState;
import eu.cubixmc.com.Main;
import eu.cubixmc.com.gui.SpectatorGUI;
import eu.cubixmc.com.gui.TeamSelector;

public class ProtectionListener implements Listener{
	
	private Main main;
	
	public ProtectionListener(Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		ItemStack item = e.getItemDrop().getItemStack();
		if(item.getType() == Material.COMPASS && item.hasItemMeta() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§bNaviguation")) {
			e.setCancelled(true);
		}
		if(item.getType() == Material.NAME_TAG && item.hasItemMeta() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§9Choisir une équipe")) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		ItemStack item = e.getCurrentItem();
		if(item == null) return;
		if(item.getType() == Material.COMPASS && item.hasItemMeta() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§bNaviguation")) {
			if(main.getTeams().get(p.getUniqueId()) != null) return;
			main.getGuiManager().open(p, SpectatorGUI.class);
			e.setCancelled(true);
		}
		if(item.getType() == Material.NAME_TAG && item.hasItemMeta() && item.getItemMeta().getDisplayName().equalsIgnoreCase("§9Choisir une équipe")) {
			if(!main.isState(GameState.LOBBY)) return;
			main.getGuiManager().open(p, TeamSelector.class);
			e.setCancelled(true);
		}
	}

}
