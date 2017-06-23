package me.crisdev333.proscoreboard;

import java.util.ArrayList;
import java.util.List;

public class ScoreWorld {
	
	private String world, title;
	private List<String> lines;

	public ScoreWorld(String world, String title, List<String> lines) {
		this.world = world;
		this.title = title;
		if(lines.size()<=15) {
			this.lines = lines;
		} else {
			this.lines = new ArrayList<>();
			for(int i=0; i<15; i++) {
				this.lines.add(lines.get(i));
			}
		}
	}
	
	public String getWorld() {
		return world;
	}
	
	public String getTitle() {
		return title;
	}
	
	public List<String> getLines() {
		return lines;
	}

}