package skaro.frenbot.commands.arguments;

import java.util.List;

import skaro.frenbot.commands.parsers.FlagDescriptionPair;

public class UsageHelp {

	private List<FlagDescriptionPair> flags;
	
	public UsageHelp(List<FlagDescriptionPair> flags) {
		this.flags = flags;
	}
	
	public List<FlagDescriptionPair> getFlags() {
		return flags;
	}
	
}
