package skaro.frenbot.receivers;

import discord4j.core.object.entity.Member;
import skaro.frenbot.receivers.services.RPSOption;
import skaro.pokeaimpi.sdk.resource.NewAwardList;

public class RPSResult {

	private Member winner;
	private RPSOption winningOption;
	private NewAwardList apiAwards;
	
	public RPSResult(Member winner, RPSOption winningOption, NewAwardList awards) {
		this.winner = winner;
		this.winningOption = winningOption;
		this.apiAwards = awards;
	}
	
	public Member getWinner() {
		return winner;
	}
	public void setWinner(Member winner) {
		this.winner = winner;
	}
	public RPSOption getWinningOption() {
		return winningOption;
	}
	public void setWinningOption(RPSOption winningOption) {
		this.winningOption = winningOption;
	}
	public NewAwardList getApiAwards() {
		return apiAwards;
	}
	public void setApiAwards(NewAwardList apiAwards) {
		this.apiAwards = apiAwards;
	}
	
}
