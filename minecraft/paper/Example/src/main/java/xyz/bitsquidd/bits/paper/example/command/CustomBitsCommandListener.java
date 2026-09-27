package xyz.orbitary.bits.paper.example.command;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;

import xyz.orbitary.bits.sendable.text.Text;
import xyz.orbitary.bits.sendable.text.decorator.ITextDecorator;

public class CustomBitsCommandListener implements Listener {
    private final ITextDecorator errorDecorator;
    private final Component unknownCommandMessage;

    public CustomBitsCommandListener(ITextDecorator errorDecorator, Component unknownCommandMessage) {
        this.errorDecorator = errorDecorator;
        this.unknownCommandMessage = unknownCommandMessage;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUnknownCommand(UnknownCommandEvent unknownCommandEvent) {
        Component message = unknownCommandEvent.message();

        Text.of(message == null ? unknownCommandMessage : message)
          .decorate(errorDecorator).send(unknownCommandEvent.getSender());
    }

}