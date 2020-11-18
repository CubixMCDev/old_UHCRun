package eu.cubixmc.com.listeners;

import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class CutClean implements Listener{
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		LivingEntity entity = e.getEntity();
		if(entity.getType() == EntityType.CHICKEN) {
			entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.LEATHER, new Random().nextInt(4)));
			for(ItemStack drops : e.getDrops()) {
				if(drops.getType() == Material.RAW_CHICKEN) {
					drops.setType(Material.COOKED_CHICKEN);
				}
			}
		}
		if(entity.getType() == EntityType.SHEEP) {
			Random r = new Random();
			int choice = r.nextInt(49) + 1;
			if(choice < 50) {
				entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.LEATHER, r.nextInt(3)));
			}
			for(ItemStack drops : e.getDrops()) {
				if(drops.getType() == Material.MUTTON) {
					drops.setType(Material.COOKED_MUTTON);
				}
			}
		}
		if(entity.getType() == EntityType.RABBIT) {
			for(ItemStack drops : e.getDrops()) {
				if(drops.getType() == Material.RABBIT) {
					drops.setType(Material.COOKED_RABBIT);
				}
			}
		}
		if(entity.getType() == EntityType.COW || entity.getType() == EntityType.MUSHROOM_COW) {
			for(ItemStack drops : e.getDrops()) {
				if(drops.getType() == Material.RAW_BEEF) {
					drops.setType(Material.COOKED_BEEF);
				}
			}
		}
		if(entity.getType() == EntityType.PIG) {
			for(ItemStack drops : e.getDrops()) {
				if(drops.getType() == Material.PORK) {
					drops.setType(Material.GRILLED_PORK);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(e.isCancelled()) return;
		if(e.getPlayer().getGameMode() != GameMode.SURVIVAL) return;
		Block block = e.getBlock();
		Location clone = new Location(block.getWorld(), block.getLocation().getX() + 0.5D, block.getLocation().getY(), block.getLocation().getZ() + 0.5D);
	
		if(block.getType() == Material.IRON_ORE) {
			block.setType(Material.AIR);
			block.getState().update();
			block.getWorld().dropItemNaturally(clone, new ItemStack(Material.IRON_INGOT, 2));
			((ExperienceOrb) block.getWorld().spawn(clone, ExperienceOrb.class)).setExperience(3);;
		}else if(block.getType() == Material.COBBLESTONE && block.getData() > 0) {
			block.setType(Material.AIR);
			block.getState().update();
			block.getWorld().dropItemNaturally(clone, (e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) ? new ItemStack(Material.STONE) : new ItemStack(Material.COBBLESTONE));
		}else if(block.getType() == Material.GOLD_ORE) {
			block.setType(Material.AIR);
			block.getState().update();
			block.getWorld().dropItemNaturally(clone, new ItemStack(Material.GOLD_INGOT, 2));
			((ExperienceOrb) block.getWorld().spawn(clone, ExperienceOrb.class)).setExperience(3);;
		}else if(block.getType() == Material.DIAMOND_ORE) {
			block.setType(Material.AIR);
			block.getState().update();
			block.getWorld().dropItemNaturally(clone, new ItemStack(Material.DIAMOND, 2));
			((ExperienceOrb) block.getWorld().spawn(clone, ExperienceOrb.class)).setExperience(3);;
		}
		else if(block.getType() == Material.GRAVEL) {
			block.setType(Material.AIR);
			block.getState().update();
			block.getWorld().dropItemNaturally(clone, new ItemStack(Material.ARROW, 4));
		}else if(block.getType() == Material.COAL_ORE) {
			block.setType(Material.AIR);
			block.getState().update();
			block.getWorld().dropItemNaturally(clone, new ItemStack(Material.TORCH));
			((ExperienceOrb) block.getWorld().spawn(clone, ExperienceOrb.class)).setExperience(3);;
		}else if(block.getType() == Material.SAND) {
			block.setType(Material.AIR);
			block.getState().update();
			block.getWorld().dropItemNaturally(clone, new ItemStack(Material.GLASS));
			((ExperienceOrb) block.getWorld().spawn(clone, ExperienceOrb.class)).setExperience(3);;
		}
	}

}
