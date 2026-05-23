package xyz.bitsquidd.bits.discord.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import xyz.bitsquidd.bits.Bits;
import xyz.bitsquidd.bits.BitsDiscord;
import xyz.bitsquidd.bits.discord.command.annotation.Async;
import xyz.bitsquidd.bits.discord.command.annotation.Command;
import xyz.bitsquidd.bits.lifecycle.manager.BitsModule;
import xyz.bitsquidd.bits.log.Logger;
import xyz.bitsquidd.bits.util.reflection.ReflectionUtils;
import xyz.bitsquidd.bits.util.reflection.ScannerFlags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JdaCommandManager implements BitsModule {
    private final Map<String, JdaCommand> commands = new HashMap<>();

    @Override
    public void startup() {
        Set<JdaCommand> found = ReflectionUtils.General.createClassesInDir("*", JdaCommand.class, ScannerFlags.DEFAULT);
        List<SlashCommandData> data = new ArrayList<>();

        for (JdaCommand cmd : found) {
            Command meta = cmd.getClass().getAnnotation(Command.class);
            commands.put(meta.name(), cmd);
            data.add(cmd.build());
            Logger.info("Registered command: /" + meta.name());
        }

        BitsDiscord.jda().updateCommands().addCommands(data).queue(
            ok -> Logger.success("Registered " + commands.size() + " slash commands."),
            err -> Logger.exception("Failed to register slash commands", err)
        );
        BitsDiscord.jda().addEventListener(new CommandListener());
    }

    private class CommandListener extends ListenerAdapter {
        @Override
        public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
            JdaCommand cmd = commands.get(event.getName());
            if (cmd == null) return;

            if (cmd.getClass().isAnnotationPresent(Async.class)) {
                Bits.get().runLaterAsync(() -> execute(cmd, event), 0);
            } else {
                execute(cmd, event);
            }
        }

        private void execute(JdaCommand cmd, SlashCommandInteractionEvent event) {
            try {
                cmd.execute(event);
            } catch (Exception e) {
                Logger.exception("Unhandled exception in command /" + event.getName(), e);
            }
        }
    }
}
