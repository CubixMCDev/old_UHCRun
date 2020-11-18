package eu.cubixmc.com;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import eu.cubixmc.com.commands.UhcCmd;
import eu.cubixmc.com.gui.GuiBuilder;
import eu.cubixmc.com.gui.GuiManager;
import eu.cubixmc.com.gui.SpectatorGUI;
import eu.cubixmc.com.gui.TeamSelector;
import eu.cubixmc.com.listeners.AutoLeafDelay;
import eu.cubixmc.com.listeners.ChatListener;
import eu.cubixmc.com.listeners.CraftListener;
import eu.cubixmc.com.listeners.CutClean;
import eu.cubixmc.com.listeners.DamageListener;
import eu.cubixmc.com.listeners.PlayerListener;
import eu.cubixmc.com.listeners.ProtectionListener;
import eu.cubixmc.com.scoreboard.ScoreboardManager;
import eu.cubixmc.com.tasks.RedemTask;
import eu.cubixmc.com.teams.Teams;
import eu.cubixmc.com.world.WorldListener;

public class Main extends JavaPlugin {
	
	private String PREFIX = "§6UHCRun §8❘ ";
	private GameState state;
	private ArrayList<UUID> players = new ArrayList<UUID>();
	private ArrayList<Player> adminMode = new ArrayList<Player>();
	private GuiManager guiM;
	private Map<Class<? extends GuiBuilder>, GuiBuilder> registeredMenus;
	private HashMap<UUID, Teams> teams = new HashMap<UUID, Teams>();
	private HashMap<UUID, Teams> previousteams = new HashMap<UUID, Teams>();
	private ArrayList<Teams> teamsAlive = new ArrayList<Teams>();
	private static Main instance;
	private ScoreboardManager scoreboardManager;
	private ScheduledExecutorService executorMonoThread;
	private ScheduledExecutorService scheduledExecutorService;
	private Title title = new Title();
	private HashMap<UUID, Integer> kills = new HashMap<UUID, Integer>();
	private HashMap<UUID, ItemStack[]> playerInv = new HashMap<UUID, ItemStack[]>();
	private HashMap<UUID, ItemStack[]> playerArmor = new HashMap<UUID, ItemStack[]>();
	private HashMap<UUID, Location> playerLoc = new HashMap<UUID, Location>();
	private HashMap<UUID, Integer> exp = new HashMap<UUID, Integer>();
	private HashMap<UUID, Integer> coins = new HashMap<UUID, Integer>();
	private boolean isRegistered = false;
	
	@Override
	public void onEnable() {
		instance = this;
		setState(GameState.LOBBY);
		loadGui();
		scheduledExecutorService = Executors.newScheduledThreadPool(16);
		executorMonoThread = Executors.newScheduledThreadPool(1);
		
		scoreboardManager = new ScoreboardManager(this);
		
		if(Bukkit.getOnlinePlayers().size() > 0) {
			for(Player players : Bukkit.getOnlinePlayers()) {
				getScoreboardManager().update(players);
			}
		}
		registerListeners();
		loadChannel();
		getCommand("uhc").setExecutor(new UhcCmd(this));
	}

	@Override
	public void onDisable() {
		File file = new File("world");
		deleteWorld(file);
	}

	private boolean deleteWorld(File path) {
		if(path.exists()) {
			File files[] = path.listFiles();
			for(int i = 0; i < files.length; i++) {
				files[i].delete();		
			}
		}
		return (path.delete());
	}

	public ArrayList<UUID> getPlayers() {
		return players;
	}

	public boolean isState(GameState state) {
		return this.state == state;
	}
	
	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public String getPREFIX() {
		return PREFIX;
	}
	
	private void registerListeners() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerListener(this), this);
		pm.registerEvents(new DamageListener(this), this);
		pm.registerEvents(new AutoLeafDelay(this), this);
		pm.registerEvents(new ProtectionListener(this), this);
		pm.registerEvents(new WorldListener(this), this);
		pm.registerEvents(new CutClean(), this);
		pm.registerEvents(new CraftListener(), this);
		pm.registerEvents(new ChatListener(this), this);
	}

	public GuiManager getGuiManager() {
		return guiM;
	}

	public Map<Class<? extends GuiBuilder>, GuiBuilder> getRegisteredMenus() {
		return registeredMenus;
	}
	
	private void loadGui() {
		guiM = new GuiManager(this);
		getServer().getPluginManager().registerEvents(guiM, this);
		registeredMenus = new HashMap<Class<? extends GuiBuilder>, GuiBuilder>();
		guiM.addMenu(new TeamSelector(this));
		guiM.addMenu(new SpectatorGUI(this));
	}

	public HashMap<UUID, Teams> getTeams() {
		return teams;
	}

	public static Main getInstance() {
		return instance;
	}

	public ScoreboardManager getScoreboardManager() {
		return scoreboardManager;
	}

	public ScheduledExecutorService getExecutorMonoThread() {
		return executorMonoThread;
	}

	public ScheduledExecutorService getScheduledExecutorService() {
		return scheduledExecutorService;
	}
	
	public void destroyPlayer(Player p) {
		if(teams.containsKey(p.getUniqueId())) teams.remove(p.getUniqueId());
		if(players.contains(p.getUniqueId())) players.remove(p.getUniqueId());
		if(!isState(GameState.LOBBY)) {
			checkWin();
		}
	}
	
	public ArrayList<UUID> getTeamPlayers(Teams team){
		ArrayList<UUID> players = new ArrayList<UUID>();
		for(UUID uuid : getPlayers()) {
			if(teams.get(uuid) == team) {
				players.add(uuid);
			}
		}
		return players;
	}

	public void eliminate(Player p) {
		if(getTeamPlayers(teams.get(p.getUniqueId())).size() == 1) {
			teamsAlive.remove(teams.get(p.getUniqueId()));
			Bukkit.broadcastMessage(PREFIX + "§7L'équipe " + teams.get(p.getUniqueId()).getPrefix() + " §7a été éliminé !");
		}
		previousteams.put(p.getUniqueId(), teams.get(p.getUniqueId()));
		p.setGameMode(GameMode.SPECTATOR);
		title.sendTitle(p, "§cVous êtes mort(e)", "§7Vous passez en mode §eSpectateur§7.", 20);
		for(Player players : Bukkit.getOnlinePlayers()) {
			players.playSound(players.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
		}
		p.sendMessage(PREFIX + "§6Ouvrez votre inventaire et cliquez sur la boussole pour accèder au menu des spéctateurs.");
		Bukkit.getOnlinePlayers().forEach(players -> players.hidePlayer(p));
		new InventoryManager(this).saveAndClearInv(p);
		new InventoryManager(this).saveLocation(p);
		destroyPlayer(p);
	}

	public void checkWin() {
		
		if(teamsAlive.size() == 0) {
			Bukkit.broadcastMessage(PREFIX + "§6Il n'ya plus d'équipes en jeu mais personne ne semble avoir gagné la partie.. Redémarrage du serveur.");
			setState(GameState.FINISH);
			
			RedemTask redem = new RedemTask(this);
			redem.runTaskTimer(this, 0, 20);
		}
		
		if(teamsAlive.size() == 1) {
			Teams winner = teamsAlive.get(0);
			
			Bukkit.broadcastMessage(PREFIX + "§6L'équipe " + winner.getPrefix() + " §6a gagné la partie !");
			setState(GameState.FINISH);
			
			RedemTask redem = new RedemTask(this);
			redem.runTaskTimer(this, 0, 20);
		}
	}

	public HashMap<UUID, Integer> getKills() {
		return kills;
	}

	public boolean isRegistered() {
		return isRegistered;
	}

	public void setRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}

	public ArrayList<Teams> getTeamsAlive() {
		return teamsAlive;
	}

	public HashMap<UUID, ItemStack[]> getPlayerInv() {
		return playerInv;
	}

	public HashMap<UUID, ItemStack[]> getPlayerArmor() {
		return playerArmor;
	}

	public HashMap<UUID, Location> getPlayerLoc() {
		return playerLoc;
	}
	
	public void teleport(Player p, String server){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server);
		p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
	}
	
	private void loadChannel(){
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	}

	public HashMap<UUID, Teams> getPreviousteams() {
		return previousteams;
	}

	public ArrayList<Player> getAdminMode() {
		return adminMode;
	}

	public HashMap<UUID, Integer> getExp() {
		return exp;
	}

	public HashMap<UUID, Integer> getCoins() {
		return coins;
	}
	
}
