package me.crisdev333.proscoreboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Utils {
	
	public static List<Player> getOnlinePlayers() {
		ArrayList<Player> list = new ArrayList<>();
		for(World world : Bukkit.getWorlds()) {
			for(Player player: world.getPlayers()) {
				list.add(player);
			}
		}
		return list;
	}

}