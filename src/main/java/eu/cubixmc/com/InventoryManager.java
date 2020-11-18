package eu.cubixmc.com;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryManager {
	
	private Main main;
	
	public InventoryManager(Main main) {
		this.main = main;
	}

	public void restoreLocation(Player p) {
		if(main.getPlayerLoc().get(p.getUniqueId()) != null) p.teleport(main.getPlayerLoc().get(p.getUniqueId()));
	}
	
	public void saveLocation(Player p) {
		main.getPlayerLoc().put(p.getUniqueId(), p.getLocation());
	}
	
	public void saveAndClearInv(Player p) {
		ItemStack[] cont = p.getInventory().getContents();
		ItemStack[] armor = p.getInventory().getArmorContents();
		
		main.getPlayerInv().put(p.getUniqueId(), cont);
		main.getPlayerArmor().put(p.getUniqueId(), armor);
		
		p.getInventory().clear();
		removeArmor(p);
	}
	
	public void restoreInventory(Player p) {
		ItemStack[] cont = main.getPlayerInv().get(p.getUniqueId());
		ItemStack[] armor = main.getPlayerArmor().get(p.getUniqueId());
		
		if(cont != null) {
			p.getInventory().setContents(cont);
		}else {
			p.getInventory().clear();
		}
		
		if(armor != null) {
			p.getInventory().setArmorContents(armor);
		}else {
			removeArmor(p);
		}
	}
	
	public void removeArmor(Player p) {
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
	}
	
}
