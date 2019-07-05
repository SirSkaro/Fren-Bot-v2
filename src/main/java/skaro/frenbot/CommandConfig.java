package skaro.frenbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import skaro.frenbot.commands.PointRewardCommand;
import skaro.frenbot.commands.impl.FixedRewardCommand;
import skaro.frenbot.commands.impl.PingCommand;

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
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public PointRewardCommand getPointRewardCommand() {
		PointRewardCommand command = new FixedRewardCommand();
		command.setReceiver(receiverConfig.getPointAwardReceiver());
		return command;
	}
	
}
