package net.pumbas;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import nz.pumbas.halpbot.HalpbotBuilder;
import nz.pumbas.halpbot.buttons.UseButtons;
import nz.pumbas.halpbot.commands.annotations.UseCommands;
import nz.pumbas.halpbot.common.Bot;

@Service
@Activator
@UseButtons
@UseCommands
public class Main extends ListenerAdapter implements Bot
{
    @Inject
    private ApplicationContext applicationContext;
    @Inject
    private BirthdayListener birthdayListener;

    public static void main(String[] args) throws ApplicationException {
        HalpbotBuilder.build(Main.class, args);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        this.applicationContext.log().info("Running in %s servers".formatted(event.getGuildTotalCount()));
    }

    @Override
    public JDABuilder initialise(String[] args) {
        String token = args[0]; // The token is the first argument
        return JDABuilder.createDefault(token)
            .addEventListeners(this, birthdayListener)
            .setActivity(Activity.listening("the birthday beat!"));
    }
}
