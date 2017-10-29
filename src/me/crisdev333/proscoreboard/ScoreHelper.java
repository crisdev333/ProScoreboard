package me.crisdev333.proscoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.clip.placeholderapi.PlaceholderAPI;

public class ScoreHelper {

	private static HashMap<UUID, ScoreHelper> players = new HashMap<>();

	public static ScoreHelper create(Player player, boolean healthName, boolean healthTab) {
		return new ScoreHelper(player, healthName, healthTab);
	}

	public static ScoreHelper get(Player player) {
		return players.get(player.getUniqueId());
	}

	public static void remove(Player player) {
		players.remove(player.getUniqueId());
	}

	private Player player;
	private Scoreboard scoreboard;
	private Objective sidebar;

	private ScoreHelper(Player player, boolean healthName, boolean healthTab) {
		this.player = player;
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		sidebar = scoreboard.registerNewObjective("sidebar", "dummy");
		sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
		player.setScoreboard(scoreboard);
		// Create Teams
		for(int i=1; i<=15; i++) {
			Team team = scoreboard.registerNewTeam("SLOT_" + i);
			team.addEntry(genEntry(i));
		}

		if(healthName) {
			Objective hName = scoreboard.registerNewObjective("hname", "health");
			hName.setDisplaySlot(DisplaySlot.BELOW_NAME);
			hName.setDisplayName(ChatColor.RED + "❤");
		}

		if(healthTab) {
			Objective hTab = scoreboard.registerNewObjective("htab", "health");
			hTab.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		}

		players.put(player.getUniqueId(), this);
	}

	public void setTitle(String title) {
		title = PlaceholderAPI.setPlaceholders(player, title);
		
		if(title.length() > 32)
			title = title.substring(0, 32);
		
		if(!sidebar.getDisplayName().equals(title))
			sidebar.setDisplayName(title);
	}

	public void setSlot(int slot, String text) {
		Team team = scoreboard.getTeam("SLOT_" + slot);
		String entry = genEntry(slot);
		if(!scoreboard.getEntries().contains(entry)) {
			sidebar.getScore(entry).setScore(slot);
		}

		text = PlaceholderAPI.setPlaceholders(player, text);
		String pre = getFirstSplit(text);
		String suf = getFirstSplit(ChatColor.getLastColors(pre) + getSecondSplit(text));

		if(!team.getPrefix().equals(pre))
			team.setPrefix(pre);
		if(!team.getSuffix().equals(suf))
			team.setSuffix(suf);
	}

	public void removeSlot(int slot) {
		String entry = genEntry(slot);
		if(scoreboard.getEntries().contains(entry)) {
			scoreboard.resetScores(entry);
		}
	}

	public void setSlotsFromList(List<String> list) {
		int slot = list.size();

		if(slot<15) {
			for(int i=(slot +1); i<=15; i++) {
				removeSlot(i);
			}
		}

		for(String line : list) {
			setSlot(slot, line);
			slot--;
		}
	}

	private String genEntry(int slot) {
		return ChatColor.values()[slot].toString();
	}

	private String getFirstSplit(String s) {
		return s.length()>16 ? s.substring(0, 16) : s;
	}

	private String getSecondSplit(String s) {
		if(s.length()>32)
			s = s.substring(0, 32);
		return s.length()>16 ? s.substring(16, s.length()) : "";
	}

}