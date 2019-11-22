package skaro.frenbot.receivers.services;

import java.util.List;
import java.util.function.Consumer;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.util.Permission;
import discord4j.core.spec.MessageCreateSpec;
import reactor.core.publisher.Mono;
import skaro.frenbot.receivers.dtos.BadgeDTO;

public interface DiscordService {

	public Mono<Member> getUserById(String id);
	public Mono<Member> getAuthor(Message message);
	public Mono<Message> replyToMessage(Message message, Consumer<MessageCreateSpec> reply);
	public Mono<Void> assignBadgeRoles(Member user, List<BadgeDTO> badges);
	public Mono<Role> getRoleForBadge(BadgeDTO badge);
	public Mono<Role> getRoleByName(String name);
	public Mono<Void> notifyRequestRecieved(Message message);
	public Mono<Boolean> authorHasPermission(Message message, Permission permission);
	
}
