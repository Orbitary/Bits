package xyz.orbitary.bits.paper.example.text.decorator.impl;

import net.kyori.adventure.text.Component;

import xyz.orbitary.bits.command.CommandReturnType;
import xyz.orbitary.bits.sendable.text.decorator.formatter.BasicColorFormatter;
import xyz.orbitary.bits.sendable.text.decorator.impl.ColorLightenerFormatter;
import xyz.orbitary.bits.sendable.text.decorator.impl.StyleDecorator;

import java.util.Locale;

public class CommandDecorator extends StyleDecorator {
    public final CommandReturnType commandReturnType;

    public CommandDecorator(CommandReturnType commandReturnType) {
        this.commandReturnType = commandReturnType;
        this.globalFormatters.add(new BasicColorFormatter(toMessageColor(commandReturnType)));
        this.formatters.put("b", new ColorLightenerFormatter(0.4f));
    }

    public static CommandDecorator of(CommandReturnType commandReturnType) {
        return new CommandDecorator(commandReturnType);
    }

    @Override
    public Component format(Component component, Locale locale) {
        return Component.empty()
          .append(Component.text(toMessageIcon(commandReturnType)))
          .append(super.format(component, locale));
    }

    private static int toMessageColor(CommandReturnType commandReturnType) {
        return switch (commandReturnType) {
            case SUCCESS -> 0x77FF00;
            case INFO -> 0xBBBBFF;
            case ERROR -> 0xFF2244;
            default -> 0x6622DD;
        };
    }

    private static String toMessageIcon(CommandReturnType commandReturnType) {
        return switch (commandReturnType) {
            case SUCCESS -> "✔";
            case INFO -> "ℹ";
            case ERROR -> "❌";
            default -> ">";
        };
    }

}
