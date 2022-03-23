package net.pumbas;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import org.dockbox.hartshorn.core.annotations.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import nz.pumbas.halpbot.commands.annotations.Command;
import nz.pumbas.halpbot.converters.annotations.parameter.Source;
import nz.pumbas.halpbot.permissions.HalpbotPermissions;
import nz.pumbas.halpbot.permissions.Permissions;

@Service
public class BirthdayCommands
{
    @Getter
    private final Map<String, String> targets = new ConcurrentHashMap<>();

    @Permissions(permissions = HalpbotPermissions.BOT_OWNER)
    @Command(description = "Set the birthday target")
    public String target(@Source Guild guild, User user) {
        targets.put(guild.getId(), user.getId());
        return "Set the birthday target to " + user.getAsMention();
    }


    @Command(description = "Deprecated")
    public String joke() {
        return "This bot no longer tells jokes :clown:";
    }
}
