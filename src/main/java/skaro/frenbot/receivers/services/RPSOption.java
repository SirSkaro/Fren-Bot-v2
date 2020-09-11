package skaro.frenbot.receivers.services;

import java.util.Arrays;
import java.util.List;

public enum RPSOption {
	ROCK(1, 5, "https://cdn.bulbagarden.net/upload/thumb/f/f2/076Golem.png/250px-076Golem.png", "Rock", "%s wins %d points!"),
	PAPER(1, 5, "https://cdn.bulbagarden.net/upload/f/fe/798Kartana.png", "Paper", "%s wins %d points!"),
	SCISSORS(1, 5, "https://cdn.bulbagarden.net/upload/thumb/5/55/212Scizor.png/900px-212Scizor.png", "Scissors", "%s wins %d points!"),
	GUN(0.001, 1000, "https://66.media.tumblr.com/044c5436d1efd65bb24f6ee406bd0793/tumblr_p9pe1qVv8W1vdcjzno2_400.jpg", "a Gun?!", "%s LOOK OUT! THEY'VE GOT A GUN! (%d points for winning)"),
	L(0.001, 1000, "https://archives.bulbagarden.net/media/upload/b/b3/201Unown_L_Dream.png", "an L", "%s gave you an L to hold! (%d points for winning)");
	
	private List<RPSOption> losesTo;
	private String imageURL;
	private String name;
	private String victoryMessage;
	private double weight;
	private int pointsForWinning;
	
	private RPSOption(double weight, int pointsForWinning, String imageURL, String name, String victoryMessage) {
		this.imageURL = imageURL;
		this.name = name;
		this.victoryMessage = victoryMessage;
		this.weight = weight;
		this.pointsForWinning = pointsForWinning;
	}
	
	static {
		ROCK.losesTo = Arrays.asList(RPSOption.PAPER, RPSOption.GUN, RPSOption.L);
		PAPER.losesTo = Arrays.asList(RPSOption.SCISSORS, RPSOption.GUN, RPSOption.L);
		SCISSORS.losesTo = Arrays.asList(RPSOption.ROCK, RPSOption.GUN, RPSOption.L);
		GUN.losesTo = Arrays.asList(RPSOption.L);
		L.losesTo = Arrays.asList();
	}

	public boolean losesTo(RPSOption option) {
		return losesTo.contains(option);
	}

	public String getImageURL() {
		return imageURL;
	}

	public String getName() {
		return name;
	}
	
	public String getVictoryMessage(String mention) {
		return String.format(victoryMessage, mention, pointsForWinning);
	}
	
	public double getWeight() {
		return weight;
	}
	
	public int getPointsForWinning() {
		return pointsForWinning;
	}
}
