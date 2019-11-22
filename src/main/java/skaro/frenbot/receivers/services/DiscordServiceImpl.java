package skaro.frenbot.receivers.services;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.util.Permission;
import discord4j.core.object.util.PermissionSet;
import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.dtos.BadgeDTO;

@Service
public class DiscordServiceImpl implements DiscordService {

	@Autowired
	Guild server;
	
	@Override
	public Mono<Member> getUserById(String id) {
		Snowflake discordId = Snowflake.of(id);
		return server.getMemberById(discordId);
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
	public Mono<Void> assignBadgeRoles(Member user, List<BadgeDTO> badges) {
		return Flux.fromIterable(badges)
				.map(badge -> Snowflake.of(badge.getDiscordRoleId()))
				.flatMap(roleId -> user.addRole(roleId))
				.then();
	}

	@Override
	public Mono<Role> getRoleForBadge(BadgeDTO badge) {
		Snowflake discordId = Snowflake.of(badge.getDiscordRoleId());
		return server.getRoleById(discordId);
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
		return server.getRoles()
				.filter(role -> role.getName().equalsIgnoreCase(name))
				.singleOrEmpty();
	}

}
