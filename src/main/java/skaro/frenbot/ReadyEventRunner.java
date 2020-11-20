package skaro.frenbot;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.services.DiscordService;
import skaro.frenbot.receivers.services.PokeAimPIService;
import skaro.pokeaimpi.sdk.resource.Badge;
import skaro.pokeaimpi.sdk.resource.BadgeAwardRecord;

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
			.flatMap(event -> apiService.getAllAwards()
					.flatMapMany(awards -> discordService.getAllMembers()
							.flatMap(member -> reassignMissingBadges(awards, member)
									.then(discordService.assignDividerRoles(member)))))
			.onErrorResume(throwable -> Mono.empty())
			.subscribe(arg -> System.out.println("all roles restored"));
	}
	
	private Mono<Void> reassignMissingBadges(List<BadgeAwardRecord> allAwards, Member member) {
		List<Badge> badgesToReassignToMember = getBadgesToReassign(allAwards, member);
		return discordService.assignBadgeRoles(member, badgesToReassignToMember);
	}
	
	private List<Badge> getBadgesToReassign(List<BadgeAwardRecord> allAwards, Member member) {
		return allAwards.stream()
				.filter(award -> awardBelongsToMember(award, member))
				.map(award -> award.getBadge())
				.filter(badge -> !member.getRoleIds().contains(Snowflake.of(badge.getId())))
				.collect(Collectors.toList());
	}
	
	private boolean awardBelongsToMember(BadgeAwardRecord award, Member member) {
		return award.getUser().getSocialProfile().getDiscordConnection().getDiscordId().equals(member.getId().asString());
	}

}
