package skaro.frenbot.receivers.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.state.StateView;
import discord4j.discordjson.json.RoleData;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.frenbot.DiscordConfig;
import skaro.frenbot.api.RoleQuery;
import skaro.frenbot.api.resources.DiscordRole;
import skaro.pokeaimpi.sdk.resource.Badge;

@Service
public class RoleStoreServiceImpl implements RoleStoreService {
	@Autowired
	private GatewayDiscordClient discordClient;
	@Autowired
	private PokeAimPIService apiService;
	@Autowired
	private DiscordConfig discordConfig;
	@Autowired
	private ObjectMapper mapper;
	
	@Override
	public Flux<DiscordRole> getAllRoles(RoleQuery query) {
		return getStore().getRoleStore().values()
			.filter(role -> !role.managed())
			.filter(role -> !role.name().equalsIgnoreCase("@everyone"))
			.collectList()
			.flatMapMany(roles -> gatherRoleData(roles))
			.filter(role -> ( query.shouldFilterReservedRoles() ? !role.isReserved() : true) );
	}
	
	@Override
	public Mono<DiscordRole> getRoleById(String id) {
		return getStore().getRoleStore()
				.find(Long.parseLong(id))
				.flatMap(role -> apiService.getBadge(Long.parseLong(id))
						.map(badge ->combineMetadata(role, badge))
						.switchIfEmpty(Mono.just(combineMetadata(role))));
	}
	
	private StateView getStore() {
		return discordClient.getGatewayResources().getStateView();
	}
	
	private Flux<DiscordRole> gatherRoleData(List<RoleData> roles) {
		return apiService.getBadges()
			.collectMap(badge -> badge.getDiscordRoleId())
			.map(badges -> combineMetadata(roles, badges))
			.flatMapMany(populatedRoles -> Flux.fromIterable(populatedRoles));
	}
	
	private List<DiscordRole> combineMetadata(List<RoleData> roleBeans, Map<String, Badge> badges) {
		return roleBeans.stream()
				.map(role -> badges.containsKey(role.id()) ? combineMetadata(role, badges.get(role.id())) : combineMetadata(role))
				.collect(Collectors.toList());
	}
	
	private DiscordRole combineMetadata(RoleData role, Badge badge) {
		DiscordRole result = mapper.convertValue(role, DiscordRole.class);
		result.setAssignedBadgeId(badge.getId());
		result.setIsReserved(true);
		return result;
	}
	
	private DiscordRole combineMetadata(RoleData role) {
		DiscordRole result = mapper.convertValue(role, DiscordRole.class);
		result.setIsReserved(isReservedRole(result));
		
		return result;
	}
	
	private boolean isReservedRole(DiscordRole role) {
		List<String> configuredReservedRoles = Stream.of(discordConfig.getEarnedBadgeDivider(), discordConfig.getBadgeDivider())
				.flatMap(dividers -> Stream.of(dividers.getBottomDivider(), dividers.getTopDivider()))
				.map(dividerId -> dividerId.asString())
				.collect(Collectors.toList());
		
		return configuredReservedRoles.contains(role.getId());
	}

}
