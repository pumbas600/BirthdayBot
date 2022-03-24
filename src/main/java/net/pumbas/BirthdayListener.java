package net.pumbas;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

@Service
public class BirthdayListener extends ListenerAdapter
{
    @Inject
    private ApplicationContext applicationContext;
    @Inject
    private BirthdayCommands birthdayCommands;

    private final List<String> responses = List.of(
        "Happy birthday, %s!",
        "I can't believe how old you are now %s!",
        "Finally reached 18 %s :tada:",
        "I always limit my budget on buying birthday gifts according to what that person gave me as a gift on my " +
            "birthday. Enjoy your gift of nothing %s!",
        "Happy birthday to one of the few people whose birthday I can remember without a Facebook reminder %s",
        "Forget about the past, you can't change it. Forget about the future, you can't predict it. Forget about the " +
            "present, I didn't get you one %s",
        "May your messages be filled with birthday wishes from people you've never met, haven't seen in years, or " +
            "genuinely couldn't care less about %s",
        "On your birthday %s, don't forget to set goals that are sky high and spend the rest of the year miserably " +
            "trying to build a rocket to get there",
        "You're a really hard individual to shop for %s... so I didn't get you anything. Happy birthday!",
        "Happy birthday to the only person I would rescue in the event of a zombie apocalypse %s",
        "If you were Jesus, today would be Christmas! %s",
        "Smart, good looking, and funny. But enough about me. Happy birthday %s!",
        "It is scientifically proven that people who have more birthdays live longer %s",
        "Right, let's get you so drunk that you end up believing it's my birthday and buying me drinks all night %s!",
        "Happy birthday %s! Can you believe we used to think people our age were adults and had their life in order?",
        "Congratulations on getting slightly older %s!",
        "Well done %s - you've survived another year!",
        "Good birth %s!",
        "Objective, survive... %s",
        "Happy birthday %s, you now have 1 less year to live",
        "Enjoy the next 24 hours %s...",
        "Happy birthday %s! May you live long enough to see Amazon delivering on the moon",
        "Here is a hug! :people_hugging: Happy birthday %s!!!! PS: I'm broke!",
        "Today, I have gathered the courage to stand in front of you %s, look into your eyes, hold your hand and tell" +
            " you... that I am only here for the cake",
        "I didn't get you a gift or a happy birthday card, but please accept this half-assed happy birthday wish sent" +
            " via social media %s",
        "May your birthday be sprinkled with fun and laughter. Have a great day %s!",
        "Happy birthday to the smartest, funniest, and most beautiful person in the world... Wait, did I say 'to'? I " +
            "mean from %s",
        "Only if Discovery showed animals celebrating their birthdays... Yes! You'd be on television %s!",
        "Happy birthday %s! May your day be more beautiful than a unicorn farting rainbows!",
        "%s, I regret to inform you that your childhood has officially EXPIRED",
        "Remember on your birthday - good wishes are far more important than presents. Happy birthday from me %s!",
        "Happy birthday to a brother who I wouldn't trade for all the Nutella in the world %s",
        "%s, I figured today would be as good a day as any to tell you that you were adopted!! Just kidding." +
            " Happy birthday—or is it??",
        "%s, on this day, some people will tell you that you are still a young man. Well, let me tell you" +
            " something, those people are huge liars!",
        "Life is all about forgiving and forgetting. I forgot your birthday, and you can forgive me. How perfect %s"
    );

    private final Map<String, Set<String>> collectedBirthdayMessages = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return; // Prevent it from triggering itself in an infinite loop

        final String guildId = event.getGuild().getId();

        this.getTargetUser(guildId, event.getMessage())
            .present(user -> {

                String response = this.getRandomResponse();

                event.getChannel()
                    .sendMessage(response.formatted(user.getAsMention()))
                    .queue();

                if (notAlreadyCollected(guildId, response)) {
                    MessageEmbed newBirthdayMessageEmbed = this.collectedNewBirthdayMessageEmbed(guildId);
                    event.getChannel()
                        .sendMessageEmbeds(newBirthdayMessageEmbed)
                        .queue();
                }
            });
    }

    private boolean notAlreadyCollected(String guildId, String response) {
        return collectedBirthdayMessages
            .computeIfAbsent(guildId, key -> new HashSet<>())
            .add(response);
    }

    private MessageEmbed collectedNewBirthdayMessageEmbed(String guildId) {
        return new EmbedBuilder()
            .setTitle(":sparkles: New Birthday Message :sparkles:")
            .setColor(Color.ORANGE)
            .setDescription("You've found a new birthday message!\n\n***(%d/%d) collected.***"
                .formatted(collectedBirthdayMessages.get(guildId).size(), this.responses.size()))
            .build();
    }

    private String getRandomResponse() {
        return this.responses.get(this.random.nextInt(this.responses.size()));
    }

    private Exceptional<User> getTargetUser(String guildId, Message message) {
        if (!this.birthdayCommands.getTargets().containsKey(guildId))
            return Exceptional.empty();

        String targetId = this.birthdayCommands.getTargets().get(guildId);

        return Exceptional.of(
            message.getMentionedUsers()
                .stream()
                .filter(user -> user.getId().equals(targetId))
                .findFirst());
    }
}
