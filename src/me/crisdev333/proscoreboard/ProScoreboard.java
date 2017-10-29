package me.crisdev333.proscoreboard;

import java.util.Collections;
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
		// Load Objects
		loadObjects();
		// Load online players
		createAll();
		// Register command and events
		Bukkit.getPluginCommand("proscoreboard").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(this, this);
		// Create task for update the scoreboards
		long ticks = getConfig().getLong("Options.update-ticks");
		new BukkitRunnable() {

			@Override
			public void run() {
				updateAll();
			}

		}.runTaskTimer(this, ticks, ticks);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if(args.length==1 && args[0].equalsIgnoreCase("reload")) {
			// Reload config.yml file
			reloadConfig();
			// Reload ScoreWorlds Object's from config.yml 
			loadObjects();
			// Update the scoreboards of online players
			createAll();
			// Send message
			sender.sendMessage(ChatColor.GREEN + "The configuration has been reloaded!");
		} else {
			sender.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.WHITE +  "/" + label + " reload");
		}

		return true;
	}

	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event) {
		create(event.getPlayer());
	}

	@EventHandler
	private void onPlayerQuit(PlayerQuitEvent event) {
		ScoreHelper.remove(event.getPlayer());
	}

	@EventHandler
	private void onChangeWorld(PlayerChangedWorldEvent event) {
		update(event.getPlayer());
	}

	private void loadObjects() {
		// ScoreWorlds
		scoreWorlds = new HashMap<>();
		for(String world : getConfig().getConfigurationSection("Worlds").getKeys(false)) {
			String title = getConfig().getString("Worlds." + world + ".title");
			List<String> lines = getConfig().getStringList("Worlds." + world + ".lines");
			scoreWorlds.put(world, new ScoreWorld(title, lines));
		}
		// Booleans
		healthName = getConfig().getBoolean("Options.health-name");
		healthTab = getConfig().getBoolean("Options.health-tab");
	}

	private void create(Player player) {
		ScoreHelper.create(player, healthName, healthTab);
		update(player);
	}
	
	private void createAll() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			create(player);
		}
	}
	
	private void update(Player player) {
		ScoreHelper helper = ScoreHelper.get(player);

		if(scoreWorlds.containsKey(player.getWorld().getName())) {
			ScoreWorld sw = scoreWorlds.get(player.getWorld().getName());
			helper.setTitle(sw.getTitle());
			helper.setSlotsFromList(sw.getLines());
		} else {
			helper.setSlotsFromList(Collections.emptyList());
		}
	}
	
	private void updateAll() {
		for(Player player : Bukkit.getOnlinePlayers()) {
			update(player);
		}
	}

}