package skaro.frenbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.services.DiscordService;
import skaro.frenbot.receivers.services.PokeAimPIService;

@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class MemberJoinEventRunner implements CommandLineRunner {

	@Autowired
	private DiscordClient discordClient;
	@Autowired
	private DiscordService discordService;
	@Autowired
	private PokeAimPIService apiService;
	
	@Override
	public void run(String... args) throws Exception {
		discordClient.getEventDispatcher().on(MemberJoinEvent.class)
			.map(event -> event.getMember())
			.flatMap(member -> apiService.getUserBadges(member)
					.map(award -> award.getBadge())
					.collectList()
					.flatMap(badges -> discordService.assignBadgeRoles(member, badges)))
			.onErrorResume(throwable -> Mono.empty())
			.subscribe(arg -> System.out.println("member join handled"));
	}

}
