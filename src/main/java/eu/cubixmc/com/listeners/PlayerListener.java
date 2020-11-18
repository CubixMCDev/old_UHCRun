package eu.cubixmc.com.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import eu.cubixmc.com.GameState;
import eu.cubixmc.com.InventoryManager;
import eu.cubixmc.com.Main;
import eu.cubixmc.com.Title;
import eu.cubixmc.com.gui.SpectatorGUI;
import eu.cubixmc.com.gui.TeamSelector;
import eu.cubixmc.com.scoreboard.ScoreboardManager;
import eu.cubixmc.com.tasks.AutoStart;
import eu.cubixmc.com.teams.TeamManager;

public class PlayerListener implements Listener{
	
	private Main main;
	private Title title = new Title();
	private ScoreboardManager sbM;
	private TeamManager teamM = new TeamManager();
	
	public PlayerListener(Main main) {
		this.main = main;
		this.sbM = new ScoreboardManager(main);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		
		Player p = e.getPlayer();
		sbM.onLogin(p);
		e.setJoinMessage(null);
		if(main.isRegistered() == false) {
			teamM.registerTeams();
			main.setRegistered(true);
		}
		
		if(main.isState(GameState.PVPWAITING)) {
			if(main.getPlayers().contains(p.getUniqueId())) {
				new InventoryManager(main).restoreInventory(p);
				new InventoryManager(main).restoreLocation(p);
				main.getTeams().put(p.getUniqueId(), main.getPreviousteams().get(p.getUniqueId()));
				main.getKills().put(p.getUniqueId(), 0);
				Bukkit.getScoreboardManager().getMainScoreboard().getTeam(main.getTeams().get(p.getUniqueId()).getPrefix()).addPlayer(p);
				p.setPlayerListName(main.getTeams().get(p.getUniqueId()).getPrefix() + " " + p.getName());
				Bukkit.broadcastMessage(main.getPREFIX() + "§6" + p.getName() + " §7vient de se §areconnecter §7 !");
				p.setGameMode(GameMode.SURVIVAL);
				return;
			}else {
				ItemStack spec = new ItemStack(Material.COMPASS, 1);
				ItemMeta specM = spec.getItemMeta();
				specM.setDisplayName("§bNaviguation");
				spec.setItemMeta(specM);
				e.getPlayer().getInventory().setItem(0, spec);
				main.destroyPlayer(p);
				title.sendTitle(p, "§eLa partie a déjà commencé !", "§7Passage en mode §8spectéateur", 20);
				Bukkit.getOnlinePlayers().forEach(players -> players.hidePlayer(p));
				p.setGameMode(GameMode.SPECTATOR);
				return;
			}
		}else if(main.isState(GameState.PLAYING)) {
			ItemStack spec = new ItemStack(Material.COMPASS, 1);
			ItemMeta specM = spec.getItemMeta();
			specM.setDisplayName("§bNaviguation");
			spec.setItemMeta(specM);
			e.getPlayer().getInventory().setItem(0, spec);
			main.destroyPlayer(p);
			title.sendTitle(p, "§eLa partie a déjà commencé !", "§7Passage en mode §8spectéateur", 20);
			Bukkit.getOnlinePlayers().forEach(players -> players.hidePlayer(p));
			p.setGameMode(GameMode.SPECTATOR);
			return;
		}
		
		p.setGameMode(GameMode.ADVENTURE);
		p.setMaxHealth(20);
		p.setHealth(20);
		p.setFoodLevel(20);
		p.getInventory().clear();
		removeArmor(p);
		
		p.teleport(new Location(Bukkit.getWorld("world"), 28, 150, 27));
		if(!main.getPlayers().contains(p.getUniqueId())) main.getPlayers().add(p.getUniqueId());
		title.sendActionBar(p, "§6" + p.getName() + " §7a rejoint la partie ! §e(" + Bukkit.getOnlinePlayers().size() + "/24)");
		main.getKills().put(p.getUniqueId(), 0);
		
		ItemStack team = new ItemStack(Material.NAME_TAG);
		ItemMeta teamM = team.getItemMeta();
		teamM.setDisplayName("§9Choisir une équipe");
		team.setItemMeta(teamM);
		
		p.getInventory().setItem(0, team);
		
		if(main.getPlayers().size() == 24) {
			AutoStart.setTimer(10);
		}else if(main.getPlayers().size() == 18) AutoStart.setTimer(60);
		
		if(main.getPlayers().size() == 12) {
			AutoStart start = new AutoStart(main);
			start.runTaskTimer(main, 0, 20);
		}
		
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		
		Player p = e.getPlayer();
		sbM.onLogout(p);
		if(main.isState(GameState.LOBBY)) {
			e.setQuitMessage(null);
			for(Player players : Bukkit.getOnlinePlayers()) title.sendActionBar(players, "§c" + p.getName() + " §7a quitté la partie ! §e(" + Bukkit.getOnlinePlayers().size() + "/24)");
			main.destroyPlayer(p);
			if(main.getPlayers().size() < 12 && main.getPlayers().size() != 0) {
				if(AutoStart.isRunning) AutoStart.setTimer(120); AutoStart.getInstance().cancel();
			}
			return;
		}
		
		if(main.isState(GameState.PVPWAITING)) {
			if(main.getPlayers().contains(p.getUniqueId())) {
				if(main.getTeamsAlive().size() > 1) {
					main.getPreviousteams().put(p.getUniqueId(), main.getTeams().get(p.getUniqueId()));
					new InventoryManager(main).saveAndClearInv(p);
					new InventoryManager(main).saveLocation(p);
					e.setQuitMessage(main.getPREFIX() + "§c" + p.getName() + " §7a quitté la partie. Il peut encore se reconnecter.");
					return;
				}else {
					main.destroyPlayer(p);
				}
			}
		}else {
			if(main.getPlayers().contains(p.getUniqueId())) {
				main.destroyPlayer(p);
				e.setQuitMessage(main.getPREFIX() + "§6" + p.getName() + "§7est éliminé pour cause de déconnexion.");
				return;
			}else {
				e.setQuitMessage(null);
			}
		}
	}
		
	
	private void removeArmor(Player p) {
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
	}
	
	@EventHandler
	public void onInterract(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		ItemStack it = e.getItem();
		Action action = e.getAction();
		
		if(it == null) return;
		
		if(it.getType() == Material.NAME_TAG && it.hasItemMeta() && it.getItemMeta().getDisplayName().equalsIgnoreCase("§9Choisir une équipe")) {
			if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
				e.setCancelled(true);
				main.getGuiManager().open(p, TeamSelector.class);
			}
		}
		
		if(it.getType() == Material.COMPASS && it.hasItemMeta() && it.getItemMeta().getDisplayName().equalsIgnoreCase("§bNaviguation")) {
			if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
				if(main.getTeams().get(p.getUniqueId()) != null) return;
				e.setCancelled(true);
				main.getGuiManager().open(p, SpectatorGUI.class);
			}
		}
	}
	
	@EventHandler
	public void onHealth(EntityRegainHealthEvent e) {
		if(main.isState(GameState.LOBBY)) {
			return;
		}
		if(e.getEntity() instanceof Player) {
			if(e.getRegainReason() == RegainReason.EATING) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onApple(PlayerItemConsumeEvent e) {
		if(e.getItem().getType() == Material.GOLDEN_APPLE) {
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 150, 1));
		}
	}
	
	@EventHandler
	public void onPortal(PortalCreateEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onFood(FoodLevelChangeEvent e) {
		if(main.isState(GameState.LOBBY)) {
			e.setCancelled(true);
		}
	}

}
