/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.orbitary.bits.velocity.module.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import xyz.orbitary.bits.log.BasicLogger;
import xyz.orbitary.bits.log.Logger;
import xyz.orbitary.bits.util.reflection.ReflectionUtils;
import xyz.orbitary.bits.mc.module.command.BrigadierArguments;
import xyz.orbitary.bits.module.command.Guard;
import xyz.orbitary.bits.module.command.Parameter;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VelocityCommandManagerTest {

    private static final List<String> calls = new ArrayList<>();
    private static final List<Component> messages = new ArrayList<>();
    private static final Map<String, Command> registered = new HashMap<>();
    private static final Object PLUGIN = new Object();

    @BeforeAll
    static void logger() {
        new BasicLogger(LoggerFactory.getLogger("test"), Logger.LogFlags.defaultFlags());
        ReflectionUtils.setClassloader(VelocityCommandManagerTest.class.getClassLoader());
    }

    @BeforeEach
    void clear() {
        calls.clear();
        messages.clear();
        registered.clear();
    }

    @xyz.orbitary.bits.module.command.Command("greet")
    @SuppressWarnings("unused")
    public static final class Greet {
        @xyz.orbitary.bits.module.command.Command("hello")
        public void hello(@Parameter("who") String who) {
            calls.add("hello " + who);
        }
    }

    public static final class Locked {
        @xyz.orbitary.bits.module.command.Command("secret")
        @Guard(VelocityConditions.PlayerSender.class)
        public void secret() {
            calls.add("secret");
        }
    }

    // A stand-in for the proxy's command manager that records what the manager registers.
    private static CommandManager commandManager() {
        Object[] metaName = new Object[1];
        Object metaBuilder = Proxy.newProxyInstance(
          VelocityCommandManagerTest.class.getClassLoader(),
          new Class<?>[]{CommandMeta.Builder.class},
          (proxy, method, args) -> switch (method.getName()) {
              case "plugin" -> proxy;
              case "build" -> Proxy.newProxyInstance(
                VelocityCommandManagerTest.class.getClassLoader(),
                new Class<?>[]{CommandMeta.class},
                (meta, m, a) -> null
              );
              default -> null;
          }
        );
        return (CommandManager) Proxy.newProxyInstance(
          VelocityCommandManagerTest.class.getClassLoader(),
          new Class<?>[]{CommandManager.class},
          (proxy, method, args) -> {
              switch (method.getName()) {
                  case "metaBuilder" -> {
                      metaName[0] = args[0];
                      return metaBuilder;
                  }
                  case "register" -> {
                      registered.put((String) metaName[0], (Command) args[1]);
                      return null;
                  }
                  default -> {
                      return null;
                  }
              }
          }
        );
    }

    private static CommandSource source(Class<?> type) {
        return (CommandSource) Proxy.newProxyInstance(
          VelocityCommandManagerTest.class.getClassLoader(),
          new Class<?>[]{type},
          (proxy, method, args) -> {
              if (method.getName().equals("sendMessage")) messages.add((Component) args[0]);
              return null;
          }
        );
    }

    private static CommandDispatcher<CommandSource> dispatch(String name) {
        Command command = registered.get(name);
        assertInstanceOf(BrigadierCommand.class, command);
        LiteralCommandNode<CommandSource> node = ((BrigadierCommand) command).getNode();
        CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
        dispatcher.getRoot().addChild(node);
        return dispatcher;
    }

    private static VelocityCommandManager manager(Object plugin, List<Object> customised) {
        return new VelocityCommandManager(commandManager(), plugin, arguments -> customised.add(arguments));
    }

    @Test
    void registersEachCommandRootWithTheProxy() {
        manager(PLUGIN, new ArrayList<>()).startup();

        assertTrue(registered.containsKey("greet"));
        assertTrue(registered.containsKey("secret"));
    }

    @Test
    void registeredCommandRunsItsMethod() throws Exception {
        manager(PLUGIN, new ArrayList<>()).startup();

        assertEquals(1, dispatch("greet").execute("greet hello bob", source(CommandSource.class)));
        assertEquals(List.of("hello bob"), calls);
    }

    @Test
    void customArgumentsHookReceivesTheRegistry() {
        List<Object> customised = new ArrayList<>();

        manager(PLUGIN, customised).startup();

        assertEquals(1, customised.size());
        assertInstanceOf(BrigadierArguments.class, customised.getFirst());
    }

    @Test
    void conditionFailureKeepsTheCommandFromRunning() throws Exception {
        manager(PLUGIN, new ArrayList<>()).startup();

        assertEquals(0, dispatch("secret").execute("secret", source(ConsoleCommandSource.class)));
        assertTrue(calls.isEmpty());
        assertEquals(1, messages.size());
        assertEquals("You cannot use this command.", ((TextComponent) messages.getFirst()).content());
    }

    @Test
    void playerSenderConditionPassesForPlayers() throws Exception {
        manager(PLUGIN, new ArrayList<>()).startup();

        assertEquals(1, dispatch("secret").execute("secret", source(Player.class)));
        assertEquals(List.of("secret"), calls);
    }

    @Test
    void consoleSenderConditionPassesOnlyForTheConsole() {
        VelocityConditions.ConsoleSender condition = new VelocityConditions.ConsoleSender();

        assertTrue(condition.test(source(ConsoleCommandSource.class)));
        assertFalse(condition.test(source(Player.class)));
    }

    @Test
    void playerSenderConditionRejectsTheConsole() {
        VelocityConditions.PlayerSender condition = new VelocityConditions.PlayerSender();

        assertFalse(condition.test(source(ConsoleCommandSource.class)));
    }

    @Test
    void contextRequiresAPlayerSender() {
        VelocityCommandContext context = new VelocityCommandContext(source(Player.class));

        assertInstanceOf(Player.class, context.requirePlayer());
        assertThrows(IllegalStateException.class, () -> new VelocityCommandContext(source(ConsoleCommandSource.class)).requirePlayer());
    }

}
