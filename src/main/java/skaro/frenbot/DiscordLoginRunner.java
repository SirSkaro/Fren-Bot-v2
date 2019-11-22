package skaro.frenbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import discord4j.core.DiscordClient;

@Component
@Order(value = Ordered.LOWEST_PRECEDENCE)
public class DiscordLoginRunner implements CommandLineRunner {

	@Autowired
	private DiscordClient discordClient;
	
	@Override
	public void run(String... args) throws Exception {
		discordClient.login().block(); 	
	}

}
