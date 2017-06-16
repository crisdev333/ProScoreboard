package me.crisdev333.proscoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreHelper {
	
	private Scoreboard scoreboard;
	private Objective sidebar;
	
	public ScoreHelper(String title) {
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		sidebar = scoreboard.registerNewObjective("sidebar", "dummy");
		sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
		setTitle(title);
	}
	
	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}
	
	public void setTitle(String title) {
		title = ChatColor.translateAlternateColorCodes('&', title);
		if(title.length()>32) {
			title = title.substring(0, 32);
		}
		sidebar.setDisplayName(title);
	}
	
	public void removeAllSlots() {
		for(int i=1; i<=15; i++) {
			removeSlot(i);
		}
	}
	
	public void removeSlot(int slot) {
		if(scoreboard.getTeam("SLOT_" + slot) != null) {
			String entry = getEntry(slot);
			scoreboard.resetScores(entry);
			Team team = scoreboard.getTeam("SLOT_" + slot);
			team.removeEntry(entry);
			team.unregister();
		}
	}
	
	public void setSlot(int slot, String text) {
		text = ChatColor.translateAlternateColorCodes('&', text);
		
		if(text.length()>32) {
			text = text.substring(0, 32);
		}
		
		Team team;
		
		if(scoreboard.getTeam("SLOT_" + slot) != null) {
			team = scoreboard.getTeam("SLOT_" + slot);
		} else {
			team = scoreboard.registerNewTeam("SLOT_" + slot);
			String entry = getEntry(slot);
			team.addEntry(entry);
			sidebar.getScore(entry).setScore(slot);
		}
		
		String preffix = getFirstSplit(text);
		String lastColor = ChatColor.getLastColors(preffix);
		String firstSuffix = getSecondSplit(text);
		String secondSuffix = lastColor + firstSuffix;
		String suffix = getFirstSplit(secondSuffix);
		
		team.setPrefix(preffix);
		team.setSuffix(suffix);
	}
	
	private String getEntry(int slot) {
		return ChatColor.values()[slot].toString();
	}
	
	private String getFirstSplit(String s) {
		return s.length()>16 ? s.substring(0, 16) : s;
	}
	
	private String getSecondSplit(String s) {
		if(s.length()>32) {
			s = s.substring(0, 32);
		}
		return s.length()>16 ? s.substring(16, s.length()) : "";
	}
}
