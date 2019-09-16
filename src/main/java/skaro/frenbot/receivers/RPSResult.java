package skaro.frenbot.receivers;

import discord4j.core.object.entity.Member;
import skaro.frenbot.receivers.dtos.NewAwardsDTO;
import skaro.frenbot.receivers.services.RPSOption;

public class RPSResult {

	private Member winner;
	private RPSOption winningOption;
	private NewAwardsDTO apiAwards;
	
	public RPSResult(Member winner, RPSOption winningOption, NewAwardsDTO awards) {
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
	public NewAwardsDTO getApiAwards() {
		return apiAwards;
	}
	public void setApiAwards(NewAwardsDTO apiAwards) {
		this.apiAwards = apiAwards;
	}
	
}
