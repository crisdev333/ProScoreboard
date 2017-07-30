package me.crisdev333.proscoreboard;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class ProScoreboard extends JavaPlugin implements Listener {

	private HashMap<String, ScoreWorld> scoreWorlds;
	private boolean healthName, healthTab;

	@Override
	public void onEnable() {
		// Copy config.yml file
		saveDefaultConfig();
		// Load ScoreWorlds Object's from config.yml
		loadScoreWorlds();
		// Init variables
		long ticks = getConfig().getLong("Options.update-ticks");
		healthName = getConfig().getBoolean("Options.health-name");
		healthTab = getConfig().getBoolean("Options.health-tab");
		// Load online players
		loadOnlinePlayers();
		// Register command and events
		Bukkit.getPluginCommand("proscoreboard").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(this, this);
		// Create task for update the scoreboards
		new BukkitRunnable() {

			@Override
			public void run() {
				for(Player player : Utils.getOnlinePlayers()) {
					updatePlayer(player);
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
			// Reload config.yml file
			reloadConfig();
			// Reload ScoreWorlds Object's from config.yml 
			loadScoreWorlds();
			// Update the scoreboards of online players
			for(Player player : Utils.getOnlinePlayers()) {
				updatePlayer(player);
			}
			// Send message
			sender.sendMessage(ChatColor.GREEN + "The configuration has been reloaded!");
			return true;
		}

		return true;
	}

	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event) {
		registerPlayer(event.getPlayer());
	}

	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event) {
		ScoreHelper.removePlayer(event.getPlayer());
	}

	@EventHandler
	private void onChangeWorld(PlayerChangedWorldEvent event) {
		updatePlayer(event.getPlayer());
	}

	private void loadScoreWorlds() {
		scoreWorlds = new HashMap<>();
		for(String world : getConfig().getConfigurationSection("Worlds").getKeys(false)) {
			String title = getConfig().getString("Worlds." + world + ".title");
			List<String> lines = getConfig().getStringList("Worlds." + world + ".lines");
			scoreWorlds.put(world, new ScoreWorld(world, title, lines));
		}
	}

	private void loadOnlinePlayers() {
		for(Player player : Utils.getOnlinePlayers()) {
			registerPlayer(player);
		}
	}

	private void registerPlayer(Player player) {
		new ScoreHelper(player, healthName, healthTab);
		updatePlayer(player);
	}

	private void updatePlayer(Player player) {
		ScoreHelper helper = ScoreHelper.getByPlayer(player);

		if(scoreWorlds.containsKey(player.getWorld().getName())) {
			ScoreWorld score = scoreWorlds.get(player.getWorld().getName());
			helper.setTitle(score.getTitle());
			helper.setSlotsFromList(score.getLines());
		} else {
			helper.setSlotsFromList(Utils.EMPTY_LIST);
		}
	}

}