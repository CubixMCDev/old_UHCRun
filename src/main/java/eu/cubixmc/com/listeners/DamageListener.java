package eu.cubixmc.com.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.cubixmc.com.GameState;
import eu.cubixmc.com.Main;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand.EnumClientCommand;

public class DamageListener implements Listener {
	
	private Main main;
	private String PREFIX;
	
	public DamageListener(Main main) {
		this.main = main;
		this.PREFIX = main.getPREFIX();
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		
		if(main.isState(GameState.LOBBY)) {
			e.setCancelled(true);
			return;
		}
		if(main.isState(GameState.TELEPORTING)){
			e.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onPvP(EntityDamageByEntityEvent e) {
		
		if(main.isState(GameState.LOBBY)) {
			e.setCancelled(true);
			return;
		}
		
		Entity damager = e.getDamager();
		Entity victim = e.getEntity();
		if(main.isState(GameState.PVPWAITING)) {
			if(damager instanceof Player && victim instanceof Player) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(PlayerDeathEvent e) {
		
		// Lave, Mob, Dégats de chutes, Creeper, Gravier, Noyade, PvP
		
		Player p = e.getEntity();
		Player killer = p.getKiller();
		EntityDamageEvent dmgevent = p.getLastDamageCause();
		DamageCause dc = dmgevent.getCause();
		
		Bukkit.getScheduler().runTaskLater(main, new Runnable() {
			
			@Override
			public void run() {
				PacketPlayInClientCommand packet = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
				((CraftPlayer) p).getHandle().playerConnection.a(packet);
			}
		}, 5L);
		
		if(dc == DamageCause.LAVA) {
			e.setDeathMessage(PREFIX + "§6" + p.getName() + " §7est mort(e) dans la lave.");
		}else if(dc == DamageCause.FALL || dc == DamageCause.FALLING_BLOCK) {
			e.setDeathMessage(PREFIX + "§6" + p.getName() + " §7est mort(e) de dégats de chute.");
		}else if(dc == DamageCause.ENTITY_EXPLOSION || dc == DamageCause.BLOCK_EXPLOSION) {
			e.setDeathMessage(PREFIX + "§6" + p.getName() + " §7est mort(e) d'une explosion ou d'un Creeper.");
		}else if(dc == DamageCause.SUFFOCATION) {
			e.setDeathMessage(PREFIX + "§6" + p.getName() + " §7est mort(e) suffoqué(e).");
		}else if(e.getDeathMessage().contains("Zombie")){
			e.setDeathMessage(PREFIX + "§6" + p.getName() + " §7est mort(e) d'un Zombie.");
		}else if(e.getDeathMessage().contains("Skeleton")){
			e.setDeathMessage(PREFIX + "§6" + p.getName() + " §7est mort(e) d'un Squelette.");
		}else if(e.getDeathMessage().contains("Witch")){
			e.setDeathMessage(PREFIX + "§6" + p.getName() + " §7est mort(e) d'une Sorcière.");
		}else {
			e.setDeathMessage(PREFIX + "§6" + p.getName() + " §7est mort(e).");
		}
		
		main.eliminate(p);
		
		if(p.getKiller() == null) return;
		
		if(dmgevent.getEntity() instanceof Player || e.getDeathMessage().contains("was shot by") || e.getDeathMessage().contains("was killed by")) {
			main.getKills().put(killer.getUniqueId(), main.getKills().get(killer.getUniqueId()) + 1);
			e.setDeathMessage(PREFIX + "§6" + p.getName() + "§7 a été tué par §e" + killer.getName());
			main.getExp().put(killer.getUniqueId(), main.getExp().get(p.getUniqueId()) + 2);
			main.getCoins().put(killer.getUniqueId(), main.getCoins().get(p.getUniqueId()) + 5);
		}
		main.getPreviousteams().put(p.getUniqueId(), main.getTeams().get(p.getUniqueId()));
		p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.GOLDEN_APPLE));
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		ItemStack spec = new ItemStack(Material.COMPASS, 1);
		ItemMeta specM = spec.getItemMeta();
		specM.setDisplayName("§bNaviguation");
		spec.setItemMeta(specM);
		e.getPlayer().getInventory().setItem(0, spec);
	}
}
