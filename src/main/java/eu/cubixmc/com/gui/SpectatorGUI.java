package eu.cubixmc.com.gui;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import eu.cubixmc.com.Main;

public class SpectatorGUI implements GuiBuilder {

	private Main main;
	
	public SpectatorGUI(Main main) {
		this.main = main;
	}
	
	@Override
	public String name() {
		return "§0Mode Spéctateur";
	}

	@Override
	public int getSize() {
		return 36;
	}

	@Override
	public void contents(Player player, Inventory inv) {
		for(int i = 0; i < main.getPlayers().size(); i++) {
			UUID uuid = main.getPlayers().get(i);
			Player p = Bukkit.getPlayer(uuid);
			ArrayList<String> lore = new ArrayList<String>();
			if(main.getTeams().get(p.getUniqueId()) == null) {
				lore.add("§8■ §7Équipe: §eAléatoire");
			}else {
				lore.add("§8■ §7Équipe: " + main.getTeams().get(p.getUniqueId()).getPrefix());
			}
			lore.add("§8■ §7Points de vie: §a" + (int) p.getHealth() + "§c♥");
			lore.add("§8■ §7Nourriture: §6" + (int) p.getFoodLevel() / 2 + "§e/§610");
			lore.add("§8■ §bCliquez pour vous téléporter !");
			ItemStack skull = new ItemStack(Material.SKULL_ITEM);
			skull.setDurability((short) 3);
			SkullMeta skullM = (SkullMeta) skull.getItemMeta();
			skullM.setOwner(p.getName());
			if(main.getTeams().get(p.getUniqueId()) == null) {
				skullM.setDisplayName("§8» §f" + p.getName());
			}else{
				skullM.setDisplayName("§8» " + main.getTeams().get(p.getUniqueId()).getTag() + p.getName());
			}
			skullM.setLore(lore);
			skull.setItemMeta(skullM);
			inv.setItem(i, skull);
		}
	}

	@Override
	public void onClick(Player p, Inventory inv, ItemStack current, int slot) throws InterruptedException {
		
		if(inv.getName().equalsIgnoreCase("§0Mode Spéctateur")) {
			
			if(current == null) return;
			
			if(current.getType() == Material.SKULL_ITEM) {
				SkullMeta skullM = (SkullMeta) current.getItemMeta();
				Player target = Bukkit.getPlayer(skullM.getOwner().toString());
				if(target == null) return;
				p.teleport(target.getLocation());
				p.sendMessage("§8» §7Téléportation à §b" + target.getName()); 
			}
		}
	}

}
