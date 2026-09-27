package xyz.orbitary.bits.paper.example.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

import xyz.orbitary.bits.command.CommandReturnType;
import xyz.orbitary.bits.sendable.text.Text;
import xyz.orbitary.bits.paper.example.text.decorator.impl.CommandDecorator;
import xyz.orbitary.bits.paper.lib.command.PaperBitsCommandContext;

public class CustomCommandContext extends PaperBitsCommandContext {
    public CustomCommandContext(CommandContext<CommandSourceStack> brigadierContext) {
        super(brigadierContext);
    }

    public void respond(Text message, CommandReturnType type) {
        respond(message.decorate(CommandDecorator.of(type)));
    }

}
