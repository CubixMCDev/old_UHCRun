package eu.cubixmc.com.scoreboard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import eu.cubixmc.com.GameState;
import eu.cubixmc.com.Main;
import eu.cubixmc.com.tasks.AutoStart;
import eu.cubixmc.com.tasks.BordureCycle;
import eu.cubixmc.com.tasks.GameCycle;
import eu.cubixmc.com.tasks.PvPCycle;
import eu.cubixmc.com.teams.Teams;

public class LobbyScoreboard {
	
	private final Player p;
	private final UUID uuid;
	public final ObjectiveSign objSign;
	private static LobbyScoreboard instance;
	final Date date = new Date();
	private String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(date).replace("-", "/");
	private Main main;
	private World world;
	private WorldBorder wb;
	
	public LobbyScoreboard(Player p, Main main) {
		instance = this;
		this.world = Bukkit.getWorld("world");
		this.wb = world.getWorldBorder();
		this.main = main;
		this.p = p;
		uuid = p.getUniqueId();
		objSign = new ObjectiveSign("sidebar", "UHCRun");
		
		objSign.addReceiver(p);
		setLines("§eplay.cubixmc.fr");
	}
	
	public void reloadData() {}

	public void setLines(String ip) {
		int borduretimer = 300 - GameCycle.getTimer();
		String bordureFormat = new SimpleDateFormat("mm:ss").format(borduretimer * 1000);
		String finalFormat = new SimpleDateFormat("mm:ss").format(BordureCycle.getTimer() * 1000);
		String dateFormat = new SimpleDateFormat("mm:ss").format(GameCycle.getTimer() * 1000);
		String pvpdateFormat = new SimpleDateFormat("mm:ss").format(PvPCycle.getTimer() * 1000);
		objSign.setDisplayName("§8§l― §6UHCRun §8§l―");

		if(GameCycle.getTimer() < 1) {
			objSign.setLine(0, "§8» §7" + currentDate);
			objSign.setLine(1, "§8");
			objSign.setLine(2, "§8» §7Joueurs: §e" + main.getPlayers().size() + "§6/§e24");
			if(main.getTeams().get(p.getUniqueId()) == null) {
				objSign.setLine(3, "§8» §7Mon Équipe: §eAléatoire");
			}else {
				objSign.setLine(3, "§8» §7Mon Équipe: " + main.getTeams().get(p.getUniqueId()).getPrefix());
			}
			objSign.setLine(4, "§3");
			if(main.getPlayers().size() < 6) {
				objSign.setLine(5, "§8» §7Attente de joueurs...");
			}else if (main.getPlayers().size() > 6 && main.getPlayers().size() < 12) {
				objSign.setLine(5, "§8» §7Manque §e" + (12 - main.getPlayers().size()) + " §7joueurs");
			}else {
				objSign.setLine(5, "§8» §7Début dans §e" + AutoStart.getTimer() + "s");
			}
			objSign.setLine(6, "§7");
			objSign.setLine(7, "§8» " + ip);
	
			objSign.updateLines();
		}else {
			objSign.setLine(0, "§8» §7" + currentDate);
			objSign.setLine(1, "§8");
			objSign.setLine(2, "§8» §7Joueurs: §e" + main.getPlayers().size() + "§6/§e24");
			if(main.getTeams().get(p.getUniqueId()) == null) {
				objSign.setLine(3, "§8» §7Mon Équipe: §8Spectateur");
			}else {
				objSign.setLine(3, "§8» §7Mon Équipe: " + main.getTeams().get(p.getUniqueId()).getPrefix());
			}
			objSign.setLine(4, "§8» §7Équipes: §e" + main.getTeamsAlive().size());
			objSign.setLine(5, "§8» §7Kills: §e" + main.getKills().get(p.getUniqueId()));
			objSign.setLine(6, "§3");
			objSign.setLine(7, "§8» §7Durée: §e" + dateFormat.replace(":", "§6:§e"));
			if(main.isState(GameState.PLAYING) || main.isState(GameState.TELEPORTING)) {
				if(wb.getSize() > 100 ) {	
					objSign.setLine(8, "§8» §7Bordure: §eEn mouvement !");
				}else if(BordureCycle.getTimer() < 1500) {
					objSign.setLine(8, "§8» §7Bordure: §e" + finalFormat);
				}else if(BordureCycle.isCancelled()) {
					objSign.setLine(8, "§8» §7Bordure: §eImmobile");
				}else {
					objSign.setLine(8, "§8» §7Bordure: §e" + bordureFormat);
				}
			}else {
				objSign.setLine(8, "§8» §7Téléportation: §e" + pvpdateFormat.replace(":", "§6:§e"));
			}
			objSign.setLine(9, "§4");
			objSign.setLine(10, "§8» §7Carte: §e" + (int) wb.getSize() + " §6/ §e-" + (int) wb.getSize());
			objSign.setLine(11, "§8» §7Centre: §6§l" + getDirection(p.getLocation().getYaw()) + "§e" + centerblock(p.getLocation().getX(), p.getLocation().getZ()));

			objSign.setLine(12, "§7");
			objSign.setLine(13, "§8» " + ip);

			objSign.updateLines();
		}
	}
	
	public void setGameLines(String ip) {
		String dateFormat = new SimpleDateFormat("mm:ss").format(GameCycle.getTimer() * 1000);
		objSign.setDisplayName("§8§l- §6UHCRun §8§l-");

		objSign.setLine(0, "§8» §7" + currentDate);
		objSign.setLine(1, "§8");
		objSign.setLine(2, "§8» §7Joueurs: §e" + main.getPlayers().size() + "§6/§e24");
		objSign.setLine(3, "§8» §7Mon Équipe: " + main.getTeams().get(p.getUniqueId()).getPrefix());
		objSign.setLine(4, "§8» §7Équipes: §e" + Teams.values().length);
		objSign.setLine(5, "§3");
		objSign.setLine(6, "§8» §7Durée: §e" + dateFormat);
		objSign.setLine(7, "§8» §7PvP: §cIn Dev");
		objSign.setLine(8, "§4");
		objSign.setLine(9, "§8» §7Carte: §e500 §6/ §e-500");
		objSign.setLine(10, "§8» §7Centre: §e" + centerblock(p.getLocation().getX(), p.getLocation().getZ()));
		objSign.setLine(11, "§1");
		objSign.setLine(12, "§8» §7Serveur: §eUHCRun");
		objSign.setLine(13, "§7");
		objSign.setLine(14, "§8» " + ip);

		objSign.updateLines();
	}
	
	public void onLogout() {
		objSign.removeReceiver(Bukkit.getServer().getOfflinePlayer(uuid));
	}

	public static LobbyScoreboard getInstance() {
		return instance;
	}
	
	public int centerblock(double x, double z) {
		return (int) ((int) Math.abs(x) + Math.abs(z));
	}
	
	public String getDirection(float yaw) {
		String direction;
		if(yaw > 90 && yaw < 91) {
			direction = "↑";
		}else if(yaw < 90 && yaw < 20) {
			direction = "↗";
		}else if(yaw < 20 && yaw < 360) {
			direction = "→";
		}else if(yaw < 360 && yaw < 280) {
			direction = "↘";
		}else if(yaw >= 270 && yaw < 280) {
			direction = "↓";
		}else if(yaw <= 280 && yaw < 180) {
			direction = "←";
		}else{
			direction = "🡧";
		}
		return direction;
	}

}
