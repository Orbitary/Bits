package xyz.orbitary.bits.paper.example;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import xyz.orbitary.bits.paper.PaperBitsConfig;
import xyz.orbitary.bits.paper.example.command.CustomBitsCommandManager;

public class ExampleBitsPlugin extends JavaPlugin {
    private static @Nullable ExampleBitsPlugin instance;

    private static CustomBitsCommandManager exampleCommandManager;

    public ExampleBitsPlugin() {
        if (instance != null) throw new IllegalStateException("ExampleBitsPlugin instance already exists!");
        instance = this;
    }

    public static ExampleBitsPlugin getInstance() {
        if (instance == null) throw new IllegalStateException("ExampleBitsPlugin instance is not initialized yet!");
        return instance;
    }

    @Override
    public void onEnable() {
        new PaperBitsConfig(this); // We must initialise the BitsConfig with our plugin for Bits to work!

        exampleCommandManager = new CustomBitsCommandManager();
        exampleCommandManager.startup();

        getLogger().info("Enabled " + getName());
    }

    @Override
    public void onDisable() {
        exampleCommandManager.shutdown();

        getLogger().info("Disabled " + getName());
    }

}
