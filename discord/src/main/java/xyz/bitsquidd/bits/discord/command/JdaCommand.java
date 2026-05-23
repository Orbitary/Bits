package xyz.bitsquidd.bits.discord.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class JdaCommand {

    public abstract SlashCommandData build();

    public abstract void execute(SlashCommandInteractionEvent event);
}
