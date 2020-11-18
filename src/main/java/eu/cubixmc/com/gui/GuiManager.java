package eu.cubixmc.com.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import eu.cubixmc.com.Main;

public class GuiManager implements Listener {
	
	private Main main;
	
	public GuiManager(Main main) {
		this.main = main;
	}

    @EventHandler
    public void onClick(InventoryClickEvent event){

        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        ItemStack current = event.getCurrentItem();

        if(event.getCurrentItem() == null) return;

        main.getRegisteredMenus().values().stream()
                .filter(menu -> inv.getName().equalsIgnoreCase(menu.name()))
                .forEach(menu -> {
                    try {
                        menu.onClick(player, inv, current, event.getSlot());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    event.setCancelled(true);
                });

    }

    public void addMenu(GuiBuilder m){
        main.getRegisteredMenus().put(m.getClass(), m);
    }

    public void open(Player player, Class<? extends GuiBuilder> gClass){

        if(!main.getRegisteredMenus().containsKey(gClass)) return;

        GuiBuilder menu = main.getRegisteredMenus().get(gClass);
        Inventory inv = Bukkit.createInventory(null, menu.getSize(), menu.name());
        menu.contents(player, inv);

        new BukkitRunnable() {

            @Override
            public void run() {
                player.openInventory(inv);
            }

        }.runTaskLater(main, 1);

    }

}