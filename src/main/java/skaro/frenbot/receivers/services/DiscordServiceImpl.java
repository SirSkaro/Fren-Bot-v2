package skaro.frenbot.receivers.services;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.frenbot.DiscordConfig;
import skaro.pokeaimpi.sdk.resource.Badge;

@Service
public class DiscordServiceImpl implements DiscordService {

	@Autowired
	private GatewayDiscordClient discordClient;
	@Autowired
	private DiscordConfig discordConfig;
	
	@Override
	public Mono<Member> getUserById(String id) {
		return fetchServer()
				.flatMap(server -> server.getMemberById(Snowflake.of(id)));
	}
	
	@Override
	public Mono<Member> getAuthor(Message message) {
		return message.getAuthor()
				.map(author -> author.getId().asString())
				.map(userId -> getUserById(userId))
				.orElse(Mono.empty());
	}

	@Override
	public Mono<Message> replyToMessage(Message message, Consumer<MessageCreateSpec> reply) {
		return message.getChannel()
				.flatMap(channel -> channel.createMessage(reply));
	}

	@Override
	public Mono<Void> assignBadgeRoles(Member user, List<Badge> badges) {
		return Flux.fromIterable(badges)
				.map(badge -> Snowflake.of(badge.getDiscordRoleId()))
				.flatMap(roleId -> user.addRole(roleId))
				.then();
	}

	@Override
	public Mono<Role> getRoleById(String id) {
		return fetchServer()
				.flatMap(server -> server.getRoleById(Snowflake.of(id)));
	}
	
	@Override
	public Mono<Role> getRoleForBadge(Badge badge) {
		return getRoleById(badge.getDiscordRoleId());
	}

	@Override
	public Mono<Void> notifyRequestRecieved(Message message) {
		return message.getChannel()
				.flatMap(channel -> channel.type());
	}

	@Override
	public Mono<Boolean> authorHasPermission(Message message, Permission permission) {
		return message.getAuthorAsMember()
			.flatMap(member -> member.getBasePermissions())
			.map(permissions -> permissions.containsAll(PermissionSet.of(permission)));
	}

	@Override
	public Mono<Role> getRoleByName(String name) {
		return fetchServer()
				.flatMapMany(server -> server.getRoles())
				.filter(role -> role.getName().equalsIgnoreCase(name))
				.singleOrEmpty();
	}
	
	@Override
	public Flux<Member> getAllMembers() {
		return fetchServer()
				.flatMapMany(server -> server.getMembers());
	}
	
	@Override
	public Mono<Void> assignDividerRoles(Member user) {
		return Flux.just(discordConfig.getEarnedBadgeDivider(), discordConfig.getBadgeDivider())
				.flatMap(divider -> Flux.just(divider.getTopDivider(), divider.getBottomDivider()))
				.filter(roleId -> !user.getRoleIds().contains(roleId))
				.flatMap(roleId -> user.addRole(roleId))
				.then();	
	}
	
	private Mono<Guild> fetchServer() {
		return discordClient.getGuildById(discordConfig.getServerSnowflake());
	}

}
