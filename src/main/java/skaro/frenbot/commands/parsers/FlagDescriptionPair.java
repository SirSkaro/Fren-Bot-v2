package skaro.frenbot.commands.parsers;

public class FlagDescriptionPair {

	private String argumentFlag;
	private String description;
	
	public FlagDescriptionPair(String flag, String description) {
		this.argumentFlag = flag;
		this.description = description;
	}

	public String getFlag() {
		return argumentFlag;
	}

	public String getDescription() {
		return description;
	}
	
}
