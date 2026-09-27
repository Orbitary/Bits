package xyz.orbitary.bits.paper.example.command;

import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.spigotmc.SpigotConfig;

import xyz.orbitary.bits.command.BitsCommand;
import xyz.orbitary.bits.command.CommandReturnType;
import xyz.orbitary.bits.paper.PaperBitsConfig;
import xyz.orbitary.bits.paper.example.command.impl.TeleportCommand;
import xyz.orbitary.bits.paper.example.text.decorator.impl.CommandDecorator;
import xyz.orbitary.bits.paper.lib.command.PaperBitsCommandManager;

import java.util.List;

public class CustomBitsCommandManager extends PaperBitsCommandManager {

    private final CustomBitsCommandListener listener = new CustomBitsCommandListener(
      CommandDecorator.of(CommandReturnType.ERROR),
      Component.text(SpigotConfig.unknownCommandMessage)
    );

    @Override
    public void startup() {
        super.startup();
        Bukkit.getPluginManager().registerEvents(listener, (PaperBitsConfig.get()).getPlugin());
    }

    @Override
    public void shutdown() {
        super.shutdown();
        HandlerList.unregisterAll(listener);
    }

    @Override
    public CustomCommandContext createContext(CommandContext<CommandSourceStack> brigadierContext) {
        return new CustomCommandContext(brigadierContext);
    }

    @Override
    protected List<BitsCommand> getAllCommands() {
        return List.of(new TeleportCommand());
    }

}