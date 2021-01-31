package skaro.frenbot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.services.DiscordService;
import skaro.frenbot.receivers.services.PokeAimPIService;
import skaro.pokeaimpi.sdk.resource.Badge;

@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class MemberJoinEventRunner implements CommandLineRunner {

	@Autowired
	private GatewayDiscordClient discordClient;
	@Autowired
	private DiscordService discordService;
	@Autowired
	private PokeAimPIService apiService;
	
	@Override
	public void run(String... args) throws Exception {
		discordClient.getEventDispatcher().on(MemberJoinEvent.class)
			.map(event -> event.getMember())
			.flatMap(member -> restoreRolesForAwardedBadges(member)
					.then(discordService.assignDividerRoles(member)))
			.onErrorResume(throwable -> {throwable.printStackTrace(); return Mono.empty();})
			.subscribe(arg -> System.out.println("member join handled"));
	}
	
	private Mono<Void> restoreRolesForAwardedBadges(Member member) {
		return getAwardedBadgesForUser(member)
			.flatMap(badges -> discordService.assignBadgeRoles(member, badges));
	}
	
	private Mono<List<Badge>> getAwardedBadgesForUser(Member member) {
		return apiService.getUserBadges(member)
				.map(award -> award.getBadge())
				.collectList();
	}

}
