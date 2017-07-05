package me.crisdev333.proscoreboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Utils {

	public static final List<String> EMPTY_LIST = new ArrayList<>();

	public static List<Player> getOnlinePlayers() {
		ArrayList<Player> list = new ArrayList<>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			list.add(player);
		}
		return list;
	}

}