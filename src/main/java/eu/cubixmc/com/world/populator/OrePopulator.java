package eu.cubixmc.com.world.populator;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class OrePopulator extends BlockPopulator {

	private static final int[] iterations = { 3, 4, 4, 3, 4, 4 };
	private static final int[] amount = { 3, 8, 6, 6, 8, 12 };
	private static final Material[] type = { Material.REDSTONE_ORE, Material.IRON_ORE, Material.LAPIS_ORE,
			Material.GOLD_ORE, Material.DIAMOND_ORE, Material.OBSIDIAN };

	private static final int[] minHeight = { 5, 5, 5, 5, 5, 5 };
	private static final int[] maxHeight = { 32, 32, 32, 32, 32, 18 };

	public void populate(World world, Random random, Chunk chunk) {
		for (int i = 0; i < type.length; i++) {
			for (int j = 0; j < iterations[i]; j++) {

				int x = random.nextInt(16);
				int y = minHeight[i] + random.nextInt(maxHeight[i] - minHeight[i]);
				int z = random.nextInt(16);

				makeOres(chunk, random, x, y, z, minHeight[i], maxHeight[i], amount[i], type[i]);
			}
		}
	}

	private static void makeOres(Chunk chunk, Random random, int x, int y, int z, int minHeight, int maxHeight,
			int amount, Material type) {
		for (int i = 0; i <= amount; i++) {

			if (x >= 0 && x <= 15 && y >= minHeight && y <= maxHeight && z >= 0 && z <= 15
					&& chunk.getBlock(x, y, z).getType() == Material.STONE) {
				chunk.getBlock(x, y, z).setType(type, false);
			}
			x += random.nextInt(3) - 1;
			y += random.nextInt(3) - 1;
			z += random.nextInt(3) - 1;
		}
	}

}
