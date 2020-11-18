package eu.cubixmc.com;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

public class Title {
	
	public void sendTitle(Player p, String title, String subTitle, int ticks) {
		IChatBaseComponent baseTitle = ChatSerializer.a("{\"text\":\"" + title + "\"}");
		IChatBaseComponent baseSubTitle = ChatSerializer.a("{\"text\":\"" + subTitle + "\"}");
		
		PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, baseTitle);
		PacketPlayOutTitle subTitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, baseSubTitle);
		
		CraftPlayer craftPlayer = ((CraftPlayer) p);
		craftPlayer.getHandle().playerConnection.sendPacket(titlePacket);
		craftPlayer.getHandle().playerConnection.sendPacket(subTitlePacket);
		sendTime(p, ticks);
	}
	
	public void sendTime(Player p, int ticks) {
		PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, 20, ticks, 20);
		CraftPlayer craftPlayer = ((CraftPlayer) p);
		craftPlayer.getHandle().playerConnection.sendPacket(titlePacket);
	}
	
	public void sendActionBar(Player p, String msg) {
		IChatBaseComponent baseTitle = ChatSerializer.a("{\"text\": \"" + msg + "\"}");
		PacketPlayOutChat packet = new PacketPlayOutChat(baseTitle, (byte) 2);
		CraftPlayer craftPlayer = ((CraftPlayer) p);
		craftPlayer.getHandle().playerConnection.sendPacket(packet);
	}

}
