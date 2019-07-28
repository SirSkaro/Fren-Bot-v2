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

@Configuration
public class CommandConfig {
	
	@Autowired
	ReceiverConfig receiverConfig;

	@Bean
	public PingCommand getPingCommand() {
		PingCommand command = new PingCommand();
		command.setReceiver(receiverConfig.getPingReceiver());
		return command;
	}
	
	@Bean
	public PointAwardCommand getPointAwardCommand() {
		PointAwardCommand command = new PointAwardCommand();
		command.setReceiver(receiverConfig.getPointAwardReceiver());
		return command;
	}
	
	@Bean
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
	
}
