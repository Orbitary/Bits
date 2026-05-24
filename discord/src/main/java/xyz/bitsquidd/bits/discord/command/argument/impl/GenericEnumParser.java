package xyz.bitsquidd.bits.discord.command.argument.impl;

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import xyz.bitsquidd.bits.discord.command.JdaCommandContext;
import xyz.bitsquidd.bits.discord.command.argument.JdaArgumentParser;

import java.util.Arrays;
import java.util.List;

public class GenericEnumParser<E extends Enum<E>> extends JdaArgumentParser<E> {
    private final Class<E> enumClass;

    public GenericEnumParser(Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    public Class<E> type() { return enumClass; }
    public OptionType optionType() { return OptionType.STRING; }

    public E resolve(OptionMapping m, JdaCommandContext ctx) {
        return Enum.valueOf(enumClass, m.getAsString().toUpperCase());
    }

    public boolean supportsAutocomplete() { return true; }

    public List<Command.Choice> autocomplete(String input, JdaCommandContext ctx) {
        return Arrays.stream(enumClass.getEnumConstants())
            .filter(e -> e.name().toLowerCase().startsWith(input.toLowerCase()))
            .limit(25)
            .map(e -> new Command.Choice(e.name().toLowerCase(), e.name()))
            .toList();
    }
}
