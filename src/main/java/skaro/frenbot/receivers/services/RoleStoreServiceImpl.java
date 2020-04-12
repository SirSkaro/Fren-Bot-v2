package skaro.frenbot.receivers.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import discord4j.core.DiscordClient;
import discord4j.core.StateHolder;
import discord4j.core.object.data.stored.RoleBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.frenbot.DiscordConfig;
import skaro.frenbot.api.RoleQuery;
import skaro.frenbot.api.resources.DiscordRole;
import skaro.frenbot.receivers.dtos.BadgeDTO;

@Service
public class RoleStoreServiceImpl implements RoleStoreService {

	@Autowired
	private DiscordClient discordClient;
	@Autowired
	private PokeAimPIService apiService;
	@Autowired
	private DiscordConfig discordConfig;
	@Autowired
	private ObjectMapper mapper;
	
	@Override
	public Flux<DiscordRole> getAllRoles(RoleQuery query) {
		return getStore().getRoleStore().values()
			.filter(role -> !role.isManaged())
			.filter(role -> !role.getName().equalsIgnoreCase("@everyone"))
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
	
	private StateHolder getStore() {
		return discordClient.getServiceMediator().getStateHolder();
	}
	
	private Flux<DiscordRole> gatherRoleData(List<RoleBean> roles) {
		return apiService.getBadges()
			.collectMap(badge -> Long.parseLong(badge.getDiscordRoleId()))
			.map(badges -> combineMetadata(roles, badges))
			.flatMapMany(populatedRoles -> Flux.fromIterable(populatedRoles));
	}
	
	private List<DiscordRole> combineMetadata(List<RoleBean> roleBeans, Map<Long, BadgeDTO> badges) {
		return roleBeans.stream()
				.map(role -> badges.containsKey(role.getId()) ? combineMetadata(role, badges.get(role.getId())) : combineMetadata(role))
				.collect(Collectors.toList());
	}
	
	private DiscordRole combineMetadata(RoleBean role, BadgeDTO badge) {
		DiscordRole result = mapper.convertValue(role, DiscordRole.class);
		result.setAssignedBadgeId(badge.getId());
		result.setIsReserved(true);
		return result;
	}
	
	private DiscordRole combineMetadata(RoleBean role) {
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
