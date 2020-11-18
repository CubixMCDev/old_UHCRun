package eu.cubixmc.com.teams;

public enum Teams {
	
	ROUGE("§cRouge", "§c", 1),
	BLEUE_CLAIR("§bBleue Clair", "§b", 12),
	BLEUE("§9Bleu", "§9", 6),
	VERTE("§2Verte", "§2", 2),
	ORANGE("§6Orange","§6", 14),
	JAUNE("§eJaune", "§e", 11),
	VIOLETTE("§5Violette", "§5", 5),
	NOIRE("§0Noire", "§0", 0),
	ROSE("§dRose", "§d", 9),
	VERTE_CLAIR("§aVerte Clair", "§a", 10);
	
	private String prefix;
	private int b;
	private String tag;
	
	private Teams(String prefix, String tag, int b) {
		this.prefix = prefix;
		this.tag = tag;
		this.b = b;
	}

	public String getPrefix() {
		return prefix;
	}

	public int getByte() {
		return b;
	}

	public String getTag() {
		return tag;
	}
	
}
