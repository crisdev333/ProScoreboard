package me.crisdev333.proscoreboard;

import java.util.List;

public class ScoreWorld {
	
	private String title;
	private List<String> lines;

	public ScoreWorld(String title, List<String> lines) {
		this.title = title;
		this.lines = lines;
		
		while(this.lines.size() > 15) {
			this.lines.remove(this.lines.size()-1);
		}
	}
	
	public String getTitle() {
		return title;
	}
	
	public List<String> getLines() {
		return lines;
	}

}