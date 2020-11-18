package eu.cubixmc.com.world.populator;

import java.io.InputStream;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import eu.cubixmc.com.Main;
import eu.cubixmc.com.world.SchematicsManager;

public class NetherPopulator extends BlockPopulator {

	public String filename = "nether.schematic";
	private Main main;
	public Enchantment enchantment = null;

	public NetherPopulator(Main main) {
		this.main = main;
	}

	public void populate(World world, Random rand, Chunk chunk) {
		SimplexNoiseGenerator noise = new SimplexNoiseGenerator(world);
		double density = noise.noise(chunk.getX(), chunk.getZ());
		if (density > 0.8D) {

			int chance = rand.nextInt(101);
			if (chance <= 25) {
				makeNether(world, chunk);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void makeNether(World world, Chunk chunk) {
		try {
			InputStream is = main.getClass().getClassLoader().getResourceAsStream(filename);
			SchematicsManager man = new SchematicsManager();
			man.loadGzipedSchematic(is);

			int width = man.getWidth();
			int height = man.getHeight();
			int length = man.getLength();

			// int starty = 10;
			int endy = 10 + height;

			boolean chest1Filled = false, chest2Filled = false, chest3Filled = false, chest4Filled = false,
					chest5Filled = false, chest6Filled = false;

			for (int x = 0; x < width; x++) {
				for (int z = 0; z < length; z++) {

					int realX = x + chunk.getX() * 16;
					int realZ = z + chunk.getZ() * 16;
					for (int y = 10; y <= endy && y < 255; y++) {

						if (world.getBlockAt(realX, y, realZ).getType() == Material.WATER
								|| world.getBlockAt(realX, y, realZ).getType() == Material.getMaterial(9)
								|| world.getBlockAt(realX, y, realZ).getType() == Material.LAVA
								|| world.getBlockAt(realX, y, realZ).getType() == Material.getMaterial(11))
							world.getBlockAt(realX, y, realZ).setType(Material.AIR);
					}
				}
			}
			for (int x = 0; x < width; x++) {
				for (int z = 0; z < length; z++) {

					int realX = x + chunk.getX() * 16;
					int realZ = z + chunk.getZ() * 16;
					for (int y = 10; y <= endy && y < 255; y++) {

						int rely = y - 10;
						int id = man.getBlockIdAt(x, rely, z);
						byte data = man.getMetadataAt(x, rely, z);

						if (id == -103) {
							id = 153;
						}
						if (id > -1 && world.getBlockAt(realX, y, realZ) != null) {

							Material blockMaterial = Material.getMaterial(id);
							world.getBlockAt(realX, y, realZ).setType(blockMaterial);
							BlockState blockState = world.getBlockAt(realX, y, realZ).getState();
							MaterialData materialData = blockState.getData();
							materialData.setData(data);
							blockState.setData(materialData);
							blockState.update();
						}

						if (world.getBlockAt(realX, y, realZ).getType() == Material.getMaterial(52)) {

							BlockState state = world.getBlockAt(realX, y, realZ).getState();
							if (state instanceof CreatureSpawner) {

								((CreatureSpawner) state).setSpawnedType(EntityType.BLAZE);
								state.update();
							}
						} else if (world.getBlockAt(realX, y, realZ).getType() == Material.CHEST) {

							BlockState state = world.getBlockAt(realX, y, realZ).getState();
							if (state instanceof Chest) {

								Inventory chest = ((Chest) state).getInventory();
								if (!chest1Filled) {

									chest.setItem(7, new ItemStack(Material.DIAMOND, 2));
									chest.setItem(10, new ItemStack(Material.SUGAR_CANE, 6));
									ItemStack eBook = new ItemStack(Material.ENCHANTED_BOOK);
									EnchantmentStorageMeta meta = (EnchantmentStorageMeta) eBook.getItemMeta();
									meta.addStoredEnchant(getRandomEnchant(), getEnchantRandomLevel(enchantment),
											chest6Filled);
									eBook.setItemMeta(meta);
									chest.setItem(24, eBook);
									this.enchantment = null;
									chest1Filled = true;
								} else if (chest1Filled && !chest2Filled) {

									chest.setItem(1, new ItemStack(Material.GOLD_INGOT, 3));

									chest.setItem(14, new ItemStack(Material.getMaterial(417)));

									chest.setItem(20, new ItemStack(Material.getMaterial(372), 6));

									chest2Filled = true;
								} else if (chest1Filled && chest2Filled && !chest3Filled) {

									chest.setItem(15, new ItemStack(Material.SADDLE));

									chest.setItem(20, new ItemStack(Material.getMaterial(419)));
									chest3Filled = true;
								} else if (chest1Filled && chest2Filled && chest3Filled && !chest4Filled) {

									chest.setItem(1, new ItemStack(Material.OBSIDIAN));

									chest.setItem(21, new ItemStack(Material.getMaterial(418)));
									chest.setItem(26, new ItemStack(Material.DIAMOND));
									chest4Filled = true;
								} else if (chest1Filled && chest2Filled && chest3Filled && chest4Filled
										&& !chest5Filled) {

									chest.setItem(16, new ItemStack(Material.IRON_INGOT, 8));
									chest.setItem(20, new ItemStack(Material.GOLD_INGOT, 5));
									chest5Filled = true;
								} else if (chest1Filled && chest2Filled && chest3Filled && chest4Filled && chest5Filled
										&& !chest6Filled) {

									chest.setItem(13, new ItemStack(Material.ENDER_PEARL));
									chest6Filled = true;
								}
							}
						}
					}
				}
			}
			is.close();
		} catch (Exception e) {

			System.out.println("UHCRun | Impossible de lire la schematic du Nether !");
			e.printStackTrace();
		}
	}

	public Enchantment getRandomEnchant() {
		Enchantment[] eList = Enchantment.values();
		return this.enchantment = eList[(new Random()).nextInt(eList.length)];
	}

	public int getEnchantRandomLevel(Enchantment enchantement) {
		Random rand = new Random();
		return rand.nextInt(this.enchantment.getMaxLevel()) + 1;
	}
}