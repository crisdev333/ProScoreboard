package me.crisdev333.proscoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.clip.placeholderapi.PlaceholderAPI;

public class ProScoreboard extends JavaPlugin implements Listener {
	
	private HashMap<UUID, ScoreHelper> scores;
	private HashMap<String, ScoreWorld> scoreWorlds;
	private HashMap<UUID, String> lastWorlds;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		scores = new HashMap<>();
		lastWorlds = new HashMap<>();
		loadScoreWorlds();
		loadOnlinePlayers();
		Bukkit.getPluginCommand("proscoreboard").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(this, this);
		long ticks = getConfig().getLong("Options.update-ticks");
		new BukkitRunnable() {

			@Override
			public void run() {
				for(World world : Bukkit.getWorlds()) {
					for(Player player : world.getPlayers()) {
						setScoreBoardForWorld(player, world);
					}
				}
			}

		}.runTaskTimer(this, ticks, ticks);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(args.length==0) {
			sender.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.WHITE +  "/" + label + " reload");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("reload")) {
			reloadConfig();
			loadScoreWorlds();
			for(World world : Bukkit.getWorlds()) {
				for(Player player : world.getPlayers()) {
					scores.get(player.getUniqueId()).removeAllSlots();
					setScoreBoardForWorld(player, world);
				}
			}
			sender.sendMessage(ChatColor.GREEN + "The configuration has been reloaded!");
			return true;
		}
		
		return true;
	}

	private void loadScoreWorlds() {
		scoreWorlds = new HashMap<>();
		for(String world : getConfig().getConfigurationSection("Worlds").getKeys(false)) {
			String title = getConfig().getString("Worlds." + world + ".title");
			List<String> lines = getConfig().getStringList("Worlds." + world + ".lines");
			ScoreWorld score = new ScoreWorld(world, title, lines);
			scoreWorlds.put(world, score);
		}
	}

	private void loadOnlinePlayers() {
		for(World world : Bukkit.getWorlds()) {
			for(Player player : world.getPlayers()) {
				createScoreBoard(player);
				setScoreBoardForWorld(player, world);
			}
		}
	}

	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		createScoreBoard(player);
		setScoreBoardForWorld(player, player.getWorld());
	}

	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		scores.remove(uuid);
		lastWorlds.remove(uuid);
	}

	@EventHandler
	private void onChangeWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		setScoreBoardForWorld(player, player.getWorld());
	}

	private void createScoreBoard(Player player) {
		if(!scores.containsKey(player.getUniqueId())) {
			lastWorlds.put(player.getUniqueId(), player.getWorld().getName());
			ScoreHelper score = new ScoreHelper(player.getName());
			scores.put(player.getUniqueId(), score);
		}
	}

	private void setScoreBoardForWorld(Player player, World world) {
		if(scoreWorlds.containsKey(world.getName())) {
			ScoreWorld scoreWorld = scoreWorlds.get(world.getName());
			ScoreHelper score = scores.get(player.getUniqueId());

			if(!lastWorlds.get(player.getUniqueId()).equals(world.getName())) {
				lastWorlds.put(player.getUniqueId(), world.getName());
				score.removeAllSlots();
			}

			String title = PlaceholderAPI.setPlaceholders(player, scoreWorld.getTitle());
			score.setTitle(title);

			List<String> lines = scoreWorld.getLines();
			int slot = lines.size();
			for(String line : lines) {
				line = PlaceholderAPI.setPlaceholders(player, line);
				score.setSlot(slot, line);
				slot--;
			}

			if(!player.getScoreboard().equals(score.getScoreboard())) {
				player.setScoreboard(score.getScoreboard());
			}

		} else {
			if(!player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
				player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
			}
		}
	}

}
