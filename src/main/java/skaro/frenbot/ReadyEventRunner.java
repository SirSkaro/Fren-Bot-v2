package skaro.frenbot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.dtos.BadgeDTO;
import skaro.frenbot.receivers.services.DiscordService;
import skaro.frenbot.receivers.services.PokeAimPIService;

@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class ReadyEventRunner implements CommandLineRunner {

	@Autowired
	private DiscordClient discordClient;
	@Autowired
	private DiscordService discordService;
	@Autowired
	private PokeAimPIService apiService;
	
	@Override
	public void run(String... args) throws Exception {
		discordClient.getEventDispatcher().on(ReadyEvent.class)
			.flatMap(event -> discordService.getAllMembers())
			.flatMap(member -> getBadgesToReassign(member)
					.flatMap(badges -> discordService.assignBadgeRoles(member, badges)))
			.onErrorResume(throwable -> Mono.empty())
			.subscribe(arg -> System.out.println("all roles restored"));
	}
	
	private Mono<List<BadgeDTO>> getBadgesToReassign(Member member) {
		return apiService.getUserBadges(member)
			.map(award -> award.getBadge())
			.filter(badge -> !member.getRoleIds().contains(badge.getId()))
			.collectList();
	}

}
