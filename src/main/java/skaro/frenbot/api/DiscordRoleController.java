package skaro.frenbot.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.frenbot.api.resources.DiscordRole;
import skaro.frenbot.receivers.services.RoleStoreService;

@RestController
@RequestMapping("/role")
public class DiscordRoleController {

	@Autowired
	RoleStoreService roleService;
	
	@GetMapping
	public Flux<DiscordRole> getAll(RoleQuery query) {
		return roleService.getAllRoles(query);
	}
	
	@GetMapping("/{id}")
	public Mono<DiscordRole> getById(@PathVariable("id") String id) {
		return roleService.getRoleById(id);
	}
	
}
