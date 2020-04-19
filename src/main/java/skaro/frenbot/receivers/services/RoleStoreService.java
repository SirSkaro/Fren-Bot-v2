package skaro.frenbot.receivers.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.frenbot.api.RoleQuery;
import skaro.frenbot.api.resources.DiscordRole;

public interface RoleStoreService {
	Flux<DiscordRole> getAllRoles(RoleQuery query);
	Mono<DiscordRole> getRoleById(String id);
}
