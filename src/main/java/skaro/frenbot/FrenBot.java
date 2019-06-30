package skaro.frenbot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import discord4j.core.DiscordClient;

@SpringBootApplication
public class FrenBot {

	public static void main(String[] args) {
		SpringApplication.run(FrenBot.class, args);
	}

	@Bean
	public CommandLineRunner run(DiscordClient discordClient) throws Exception {
		return args -> discordClient.login().block();
	}
	
}
