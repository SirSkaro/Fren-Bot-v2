package skaro.frenbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import skaro.frenbot.commands.FixedRewardCommand;
import skaro.frenbot.commands.PingCommand;
import skaro.frenbot.commands.PointAwardCommand;
import skaro.frenbot.commands.PointListenerCommand;
import skaro.frenbot.commands.ProgressCommand;
import skaro.frenbot.commands.RPSCommand;

@Configuration
public class CommandConfig {
	
	@Autowired
	ReceiverConfig receiverConfig;

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public PingCommand getPingCommand() {
		PingCommand command = new PingCommand();
		command.setReceiver(receiverConfig.getPingReceiver());
		return command;
	}
	
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public PointAwardCommand getPointAwardCommand() {
		PointAwardCommand command = new PointAwardCommand();
		command.setReceiver(receiverConfig.getPointAwardReceiver());
		return command;
	}
	
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public ProgressCommand getProgressCommand() {
		ProgressCommand command = new ProgressCommand();
		command.setReceiver(receiverConfig.getProgressReceiver());
		return command;
	}
	
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public PointListenerCommand getPointRewardCommand() {
		PointListenerCommand command = new FixedRewardCommand();
		command.setReceiver(receiverConfig.getSilentPointAwardReceiver());
		return command;
	}
	
	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public RPSCommand getRPSCommand() {
		RPSCommand command = new RPSCommand();
		command.setReceiver(receiverConfig.getRPSReceiver());
		return command;
	}
	
}
