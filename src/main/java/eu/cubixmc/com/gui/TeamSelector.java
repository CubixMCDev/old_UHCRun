package eu.cubixmc.com.gui;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.cubixmc.com.Main;
import eu.cubixmc.com.teams.Teams;
import fr.cubixmc.api.CubixAPI;
import fr.cubixmc.api.ranks.Ranks;

public class TeamSelector implements GuiBuilder {

	public Main main;
	private String PREFIX;
	private CubixAPI api = (CubixAPI) Bukkit.getServer().getPluginManager().getPlugin("CubixAPI");
	
	public TeamSelector(Main main) {
		this.main = main;
		this.PREFIX = main.getPREFIX();
	}
	
	@Override
	public String name() {
		return "§0Chosir une équipe";
	}

	@Override
	public int getSize() {
		return 36;
	}

	@Override
	public void contents(Player player, Inventory inv) {
		for(int i = 0; i < Teams.values().length; i ++) {
			ArrayList<String> lore = new ArrayList<String>();
			ItemStack it = new ItemStack(Material.BANNER, 1, (byte) Teams.values()[i].getByte());
			ItemMeta itM = it.getItemMeta();
			for(UUID uuid : main.getTeamPlayers(getTeam((byte) Teams.values()[i].getByte()))) {
				Player pteam = Bukkit.getPlayer(uuid);
				if(main.getTeamPlayers(getTeam((byte) Teams.values()[i].getByte())).isEmpty()) {
					continue;
				}else {
					lore.add("§f- " + pteam.getName());
				}
			}
			itM.setDisplayName("§fÉquipe " + Teams.values()[i].getPrefix());
			itM.setLore(lore);
			it.setItemMeta(itM);
			inv.setItem(i, it);
		}
		ItemStack alea = new ItemStack(Material.BANNER, 1, (byte) 15);
		ItemMeta aleaM = alea.getItemMeta();
		aleaM.setDisplayName("§fÉquipe §8Aléatoire");
		alea.setItemMeta(aleaM);
		inv.setItem(Teams.values().length - 1, alea);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(Player p, Inventory inv, ItemStack current, int slot) throws InterruptedException {
		
		if(inv.getName().equalsIgnoreCase(name())) {
			if(current == null) return;
			ArrayList<String> lore = new ArrayList<String>();			
			if(current == null || current.getType() == Material.NAME_TAG) return;
			String itemByteString = current.getData().toString().replaceAll("[^0-9]", "");
			int itemByte = Integer.valueOf(itemByteString);
			if(itemByte == 15) {
				p.sendMessage(PREFIX + "§7Vous avez sélectionné l'équipe §8Aléatoire§7.");
				if(main.getTeams().containsKey(p.getUniqueId())) main.getTeams().remove(p.getUniqueId());
				p.setPlayerListName(api.getRankManager().getRank(p).getTagColor() + api.getRankManager().getRank(p).getNameTag() + "§8❘ " + api.getRankManager().getRank(p).getTagColor() + p.getName());
				addPlayerByRank(p);
				p.closeInventory();
				return;
			}
			if(main.getTeamPlayers(getTeam(itemByte)).size() == 2) {
				p.sendMessage(PREFIX + "§eCette équipe est déjà complète !");
				return;
			}
			p.sendMessage(PREFIX + "§7Vous avez sélectionné l'équipe " + getTeam(itemByte).getPrefix());
			p.setPlayerListName(getTeam(itemByte).getPrefix() + " " + p.getName());
			main.getTeams().put(p.getUniqueId(), getTeam(itemByte));
			Bukkit.getScoreboardManager().getMainScoreboard().getTeam(getTeam(itemByte).getPrefix()).addPlayer(p);
			if(!lore.isEmpty()) current.getItemMeta().setLore(lore);
			p.closeInventory();
		}
	}
	
	public Teams getTeam(int b) {
		Teams team = null;
		for(Teams teams : Teams.values()) {
			if(teams.getByte() == b) {
				return teams;
			}
		}
		return team;
	}
	
	@SuppressWarnings("deprecation")
	private void addPlayerByRank(Player player) {
		Ranks rank = api.getRankManager().getRank(player);
	    if (rank == Ranks.ADMIN) {
	        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("aAdmin").addPlayer(player);
	    } else if (rank == Ranks.RESPMOD) {
	        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("bRespMod").addPlayer(player);
	    } else if (rank == Ranks.DEV) {
	      Bukkit.getScoreboardManager().getMainScoreboard().getTeam("cDev").addPlayer(player);
	    } else if (rank == Ranks.MOD) {
	      Bukkit.getScoreboardManager().getMainScoreboard().getTeam("dMod").addPlayer(player);
	    } else if (rank == Ranks.HELPER) {
	    	Bukkit.getScoreboardManager().getMainScoreboard().getTeam("eAss").addPlayer(player);
	    } else if (rank == Ranks.BUILDER) {
	        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("fBuilder").addPlayer(player);
	    } else if (rank == Ranks.PARTNER) {
	        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("gPartner").addPlayer(player);
	    } else if (rank == Ranks.FRIEND) {
	        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("hFriend").addPlayer(player);
	    } else if (rank == Ranks.YOUTUBE) {
	        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("iYoutube").addPlayer(player);
	    } else if (rank == Ranks.VIPPLUS) {
	        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("jVip+").addPlayer(player);
	    } else if (rank == Ranks.VIP) {
	        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("kVip").addPlayer(player);
	    } else if (rank == Ranks.PLAYER) {
	        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("lPlayer").addPlayer(player);
	    } 
	}
}
