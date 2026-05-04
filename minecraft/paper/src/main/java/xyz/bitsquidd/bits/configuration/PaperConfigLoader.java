/*
 * This file is part of a Bit libraries package.
 * Licensed under the GNU Lesser General Public License v3.0.
 *
 * Copyright (c) 2023-2026 ImBit
 */

package xyz.bitsquidd.bits.configuration;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import xyz.bitsquidd.bits.configuration.loader.ConfigLoader;
import xyz.bitsquidd.bits.configuration.loader.YamlConfigLoader;
import xyz.bitsquidd.bits.configuration.node.ConfigSection;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * A {@link ConfigLoader} decorator that adds Paper-specific convenience to any
 * underlying loader:
 * <ul>
 *   <li><b>Plugin data folder resolution</b> - relative paths are resolved
 *       inside the plugin's data folder automatically.</li>
 *   <li><b>Default resource extraction</b> - if the target file is missing and
 *       a resource with the same name exists in the plugin JAR, it is extracted
 *       before the first load so the server owner gets a documented default config.</li>
 * </ul>
 * <p>
 * Typical usage:
 * <pre>{@code
 * BitsConfig config = ConfigBuilder.create()
 *     .loader(PaperConfigLoader.yaml(myPlugin, "config.yml"))
 *     .currentVersion(2)
 *     .migration(new V1ToV2Migration())
 *     .build();
 * config.reload();
 * }</pre>
 *
 * @since 0.1.0
 */
public final class PaperConfigLoader implements ConfigLoader {

    private final ConfigLoader delegate;
    private final JavaPlugin plugin;
    private final String resourcePath;
    private final File resolvedFile;

    private PaperConfigLoader(
      @NotNull ConfigLoader delegate,
      @NotNull JavaPlugin plugin,
      @NotNull String resourcePath,
      @NotNull File resolvedFile
    ) {
        this.delegate = delegate;
        this.plugin = plugin;
        this.resourcePath = resourcePath;
        this.resolvedFile = resolvedFile;
    }

    /**
     * Creates a YAML-backed {@link PaperConfigLoader} for the given plugin and
     * resource path.
     * <p>
     * The file is resolved relative to the plugin's data folder. If a resource
     * named {@code resourcePath} exists in the plugin JAR, it is extracted on
     * first load.
     *
     * @param plugin       the owning plugin
     * @param resourcePath path relative to the plugin's data folder, e.g. {@code "config.yml"}
     *
     * @since 0.1.0
     */
    public static @NotNull PaperConfigLoader yaml(@NotNull JavaPlugin plugin, @NotNull String resourcePath) {
        File file = new File(plugin.getDataFolder(), resourcePath);
        return new PaperConfigLoader(new YamlConfigLoader(file), plugin, resourcePath, file);
    }

    /**
     * Creates a {@link PaperConfigLoader} wrapping an arbitrary {@link ConfigLoader}.
     * Adds default resource extraction on top of the delegate.
     *
     * @param delegate     the underlying loader
     * @param plugin       the owning plugin
     * @param resourcePath the JAR resource to extract if the file is missing
     * @param targetFile   the file the delegate reads from
     *
     * @since 0.1.0
     */
    public static @NotNull PaperConfigLoader wrapping(
      @NotNull ConfigLoader delegate,
      @NotNull JavaPlugin plugin,
      @NotNull String resourcePath,
      @NotNull File targetFile
    ) {
        return new PaperConfigLoader(delegate, plugin, resourcePath, targetFile);
    }


    @Override
    public @NotNull ConfigSection load() throws ConfigException {
        extractDefaultIfAbsent();
        return delegate.load();
    }

    @Override
    public void save(@NotNull ConfigSection section) throws ConfigException {
        delegate.save(section);
    }

    @Override
    public @NotNull String formatName() {
        return delegate.formatName() + " (Paper)";
    }


    /**
     * If the target file does not exist and a matching resource exists in the
     * plugin JAR, extracts it so the server owner has a documented default config.
     */
    private void extractDefaultIfAbsent() throws ConfigException {
        if (resolvedFile.exists()) return;

        InputStream resource = plugin.getResource(resourcePath);
        if (resource == null) return; // No default, that's fine

        try {
            File parent = resolvedFile.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            Files.copy(resource, resolvedFile.toPath());
        } catch (Exception e) {
            throw ConfigException.loadFailed(resolvedFile.getPath(), e);
        }
    }

    /**
     * Returns the resolved file this loader targets.
     */
    public @NotNull File file() {
        return resolvedFile;
    }

}