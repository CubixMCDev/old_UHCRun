package eu.cubixmc.com.world.populator;

import java.io.InputStream;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

import eu.cubixmc.com.world.SchematicsManager;

public class LobbyPopulator {

	public String filename = "lobby.schematic";

	@SuppressWarnings("deprecation")
	public static void createLobby(World world, Chunk chunk, InputStream is) {
		try {
			SchematicsManager manager = new SchematicsManager();
			manager.loadGzipedSchematic(is);

			int width = manager.getWidth();
			int height = manager.getHeight();
			int length = manager.getLength();

			int endY = 139 + height;

			for (int x = 0; x < width; x++) {
				for (int z = 0; z < length; z++) {

					int realX = x + chunk.getX() * 16;
					int realZ = z + chunk.getZ() * 16;
					for (int y = 139; y <= endY && y < 255; y++) {

						int realY = y - 139;
						int id = manager.getBlockIdAt(x, realY, z);
						byte data = manager.getMetadataAt(x, realY, z);
						if (id == -122)
							id = 134;
						if (id == -68)
							id = 188;
						if (id > -1) {

							Material blockMaterial = Material.getMaterial(id);
							world.getBlockAt(realX, y, realZ).setType(blockMaterial);
							BlockState blockState = world.getBlockAt(realX, y, realZ).getState();
							MaterialData materialData = blockState.getData();
							materialData.setData(data);
							blockState.setData(materialData);
							blockState.update();
						}
					}
				}
			}
			is.close();
		} catch (Exception e) {

			System.out.println("UHCRun | Impossible de lire la schematic du lobby !");
			e.printStackTrace();
		}
	}

}
