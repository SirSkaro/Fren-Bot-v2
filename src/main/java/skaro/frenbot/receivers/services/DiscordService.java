package skaro.frenbot.receivers.services;

import java.util.List;
import java.util.function.Consumer;

import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Permission;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import skaro.pokeaimpi.sdk.resource.Badge;

public interface DiscordService {
	Mono<Member> getUserById(String id);
	Mono<Member> getAuthor(Message message);
	Flux<Member> getAllMembers();
	Mono<Message> replyToMessage(Message message, Consumer<MessageCreateSpec> reply);
	Mono<Void> assignBadgeRoles(Member user, List<Badge> badges);
	Mono<Void> assignDividerRoles(Member user);
	Mono<Role> getRoleForBadge(Badge badge);
	Mono<Role> getRoleByName(String name);
	Mono<Void> notifyRequestRecieved(Message message);
	Mono<Boolean> authorHasPermission(Message message, Permission permission);
	Mono<Role> getRoleById(String id);
}
