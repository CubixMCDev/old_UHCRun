package eu.cubixmc.com.listeners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class CraftListener implements Listener {

	
	@EventHandler
	public void onCraft(PrepareItemCraftEvent e) {
		ItemStack item = e.getRecipe().getResult();
		if(item.getType() == Material.WOOD_PICKAXE) {
			item.setType(Material.STONE_PICKAXE);
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
			item.addEnchantment(Enchantment.DURABILITY, 2);
			e.getInventory().setResult(item);
		}else if(item.getType() == Material.STONE_PICKAXE) {
			item.setType(Material.IRON_PICKAXE);
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
			item.addEnchantment(Enchantment.DURABILITY, 2);
			e.getInventory().setResult(item);
		}else if(item.getType() == Material.IRON_PICKAXE) {
			item.setType(Material.IRON_PICKAXE);
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
			item.addEnchantment(Enchantment.DURABILITY, 2);
			e.getInventory().setResult(item);
		}else if(item.getType() == Material.DIAMOND_PICKAXE) {
			item.setType(Material.DIAMOND_PICKAXE);
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
			item.addEnchantment(Enchantment.DURABILITY, 2);
			e.getInventory().setResult(item);
		}
		
		if(item.getType() == Material.WOOD_AXE) {
			item.setType(Material.STONE_AXE);
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
			item.addEnchantment(Enchantment.DURABILITY, 2);
			e.getInventory().setResult(item);
		}else if(item.getType() == Material.STONE_AXE) {
			item.setType(Material.IRON_AXE);
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
			item.addEnchantment(Enchantment.DURABILITY, 2);
			e.getInventory().setResult(item);
		}else if(item.getType() == Material.IRON_AXE) {
			item.setType(Material.IRON_AXE);
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
			item.addEnchantment(Enchantment.DURABILITY, 2);
			e.getInventory().setResult(item);
		}else if(item.getType() == Material.DIAMOND_AXE) {
			item.setType(Material.DIAMOND_AXE);
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
			item.addEnchantment(Enchantment.DURABILITY, 2);
			e.getInventory().setResult(item);
		}
		
		if(item.getType() == Material.WOOD_SPADE) {
			item.setType(Material.STONE_SPADE);
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
			item.addEnchantment(Enchantment.DURABILITY, 2);
			e.getInventory().setResult(item);
		}else if(item.getType() == Material.STONE_SPADE) {
			item.setType(Material.IRON_SPADE);
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
			item.addEnchantment(Enchantment.DURABILITY, 2);
			e.getInventory().setResult(item);
		}else if(item.getType() == Material.IRON_SPADE) {
			item.setType(Material.IRON_SPADE);
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
			item.addEnchantment(Enchantment.DURABILITY, 2);
			e.getInventory().setResult(item);
		}else if(item.getType() == Material.DIAMOND_SPADE) {
			item.setType(Material.DIAMOND_SPADE);
			item.addEnchantment(Enchantment.DIG_SPEED, 3);
			item.addEnchantment(Enchantment.DURABILITY, 2);
			e.getInventory().setResult(item);
		}
	}
		
}
