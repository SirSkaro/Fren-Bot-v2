package skaro.frenbot;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.MemberUpdateEvent;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.services.DiscordService;
import skaro.frenbot.receivers.services.PokeAimPIService;
import skaro.pokeaimpi.sdk.resource.Badge;

@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class RoleRemoveEventRunner implements CommandLineRunner {
	private static final Logger LOG = LogManager.getLogger(MethodHandles.lookup().lookupClass());
	
	@Autowired
	private GatewayDiscordClient discordClient;
	@Autowired
	private DiscordService discordService;
	@Autowired
	private PokeAimPIService apiService;
	
	@Override
	public void run(String... args) throws Exception {
		discordClient.getEventDispatcher().on(MemberUpdateEvent.class)
			.filter(event -> roleWasRemoved(event))
			.flatMap(event -> event.getMember()
					.flatMap(member -> getBadgesToReassign(event.getCurrentRoles(), member)
							.flatMap(badges -> discordService.assignBadgeRoles(member, badges))
							.then(discordService.assignDividerRoles(member))))
			.onErrorResume(throwable -> {throwable.printStackTrace(); return Mono.empty();})
			.subscribe(event -> LOG.info("member edit handled"));
	}
	
	private boolean roleWasRemoved(MemberUpdateEvent event) {
		return event.getOld()
				.map(oldMember -> oldMember.getRoleIds().size() > event.getCurrentRoles().size())
				.orElse(true);
	}
	
	private Mono<List<Badge>> getBadgesToReassign(Set<Snowflake> currentRoles, Member member) {
		return apiService.getUserBadges(member)
			.map(award -> award.getBadge())
			.filter(badge -> !currentRoles.contains(Snowflake.of(badge.getId())))
			.collectList();
	}
	
}
