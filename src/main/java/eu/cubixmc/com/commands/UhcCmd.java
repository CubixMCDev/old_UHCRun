package eu.cubixmc.com.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.cubixmc.com.GameState;
import eu.cubixmc.com.InventoryManager;
import eu.cubixmc.com.Main;
import eu.cubixmc.com.gui.SpectatorGUI;
import eu.cubixmc.com.tasks.AutoStart;
import eu.cubixmc.com.tasks.PvPCycle;
import fr.cubixmc.api.CubixAPI;

public class UhcCmd implements CommandExecutor{
	
	private Main main;
	private String PREFIX;
	private InventoryManager inventoryM;
	private CubixAPI api = (CubixAPI) Bukkit.getServer().getPluginManager().getPlugin("CubixAPI");
	
	public UhcCmd(Main main) {
		this.main = main;
		this.PREFIX = main.getPREFIX();
		inventoryM = new InventoryManager(main);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("Commande accessible qu'aux joueurs !");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("uhc")) {
			if(api.getRankManager().getRank(p).getPower() < 100) {
				p.sendMessage("§fCommande inconnue.");
				return true;
			}
			if(args.length == 0) {
				displayHelp(p);
				return true;
			}
			if(args.length == 1 && args[0].equalsIgnoreCase("start")) {
				if(!main.isState(GameState.LOBBY) || AutoStart.isRunning) {
					p.sendMessage(PREFIX + "§cErreur. La partie est déjà en cours.");
					return true;
				}
				AutoStart start = new AutoStart(main);
				start.runTaskTimer(main, 0, 20);
				AutoStart.setTimer(10);
				p.sendMessage(PREFIX + "§eDémarrage forcé de la partie !");
			}
			if(args.length == 2 && args[0].equalsIgnoreCase("sb")) {
				if(args[1].equalsIgnoreCase("on")) {
					main.getScoreboardManager().onLogin(p);
					p.sendMessage("§cDébug §8| §aScoreboard activé.");
				}else if(args[1].equalsIgnoreCase("off")) {
					main.getScoreboardManager().onLogout(p);
					p.sendMessage("§cDébug §8| §cScoreboard désactivé.");
				}
			}
			if(args.length == 1 && args[0].equalsIgnoreCase("spec")) {
				if(main.getPlayers().isEmpty()) {
					p.sendMessage("§8■ §cErreur. Il n'y a pas de joueurs en jeu.");
					return true;
				}
				main.getGuiManager().open(p, SpectatorGUI.class);
				p.sendMessage(PREFIX + "§7Ouverture du §6menu §7des §8spectateurs§7.");
				return true;
			}
			
			if(args.length == 1 && args[0].equalsIgnoreCase("test")) {
				p.sendMessage(PREFIX + "§6Liste : §c" + main.getPlayers() + " §6Es tu dans la liste ? §c" + main.getPlayers().contains(p.getUniqueId()));
				return true;
			}
			if(args.length == 2 && args[0].equalsIgnoreCase("revive")) {
				if(p.isOp()) {
					Player target = Bukkit.getPlayer(args[1]);
					if(target == null) {
						p.sendMessage(PREFIX + "§cCe joueur n'est pas en ligne !");
						return true;
					}else if(main.getPlayers().contains(target.getUniqueId())) {
						p.sendMessage(PREFIX + "§cCe joueuer est encore en vie !");
						return true;
					}
					
					main.getPlayers().add(target.getUniqueId());
					target.setGameMode(GameMode.SURVIVAL);
					Bukkit.getOnlinePlayers().forEach(players -> players.showPlayer(target));
					main.getTeams().put(target.getUniqueId(), main.getPreviousteams().get(target.getUniqueId()));
					main.getKills().put(target.getUniqueId(), 0);
					p.setPlayerListName(main.getTeams().get(p.getUniqueId()).getPrefix() + " " + p.getName());
					Bukkit.getScoreboardManager().getMainScoreboard().getTeam(main.getTeams().get(target.getUniqueId()).getPrefix()).addPlayer(target);
					target.getInventory().clear();
					inventoryM.restoreInventory(p);
					inventoryM.restoreLocation(p);
					Bukkit.broadcastMessage(PREFIX + "§a" + target.getName() + " §7est revenue à la vie !");
				}
			}
			if(args.length == 3 && args[0].equalsIgnoreCase("teleportation")) {
				if(args[1].equalsIgnoreCase("set")) {
					PvPCycle.setTimer(Integer.valueOf(args[2]));
					p.sendMessage(PREFIX + "§6Téléportation fixé à §e" + args[2] + " §6secondes !");
				}
			}
			if(args.length == 1 && args[0].equalsIgnoreCase("adminmod")) {
				if(!main.getAdminMode().contains(p)) {
					main.getAdminMode().add(p);
					inventoryM.saveAndClearInv(p);
					inventoryM.saveLocation(p);
					p.setGameMode(GameMode.CREATIVE);
					ItemStack adminmode = new ItemStack(Material.BOOK_AND_QUILL);
					ItemMeta adminmodeM = adminmode.getItemMeta();
					adminmodeM.setDisplayName("§cGérer la partie");
					adminmode.setItemMeta(adminmodeM);
					p.getInventory().setItem(0, adminmode);
					p.sendMessage(PREFIX + "§7Passage en mode §cAdministrateur§7.");
				}else {
					main.getAdminMode().remove(p);
					inventoryM.restoreInventory(p);
					inventoryM.restoreLocation(p);
					p.sendMessage(PREFIX + "§7Passage en mode §aJoueur§7.");
					p.sendMessage(PREFIX + "§eRestauration de votre inventaire et location précédents.");
				}
			}
		}
		return false;
	}

	private void displayHelp(Player p) {
		p.sendMessage("§e§lAide §f| §6En développement !");
	}
	
}
