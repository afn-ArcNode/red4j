package cn.afternode.red4j;

import cn.afternode.red4j.api.plugin.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class PluginManagerImpl implements PluginManager {
    private final Path root;
    private final Gson gson = new Gson();
    private final Logger logger = LoggerFactory.getLogger("PluginManager");

    private final Map<String, PluginContainer> plugins = new HashMap<>();

    public PluginManagerImpl(ApiImpl api) {
        this.root = api.getRoot().resolve("plugins");
    }

    @Override
    public PluginContainer loadPlugin(File file) throws InvalidPluginException {
        try {
            // Read plugin definition file
            PluginDefinitionFile def;
            try (JarFile jf = new JarFile(file)) {
                ZipEntry ent = jf.getEntry("red4j.plugin.json");
                if (ent == null) {
                    throw new IOException("No red4j.plugin.json found in the plugin file");
                }
                def = gson.fromJson(new InputStreamReader(jf.getInputStream(ent), StandardCharsets.UTF_8), PluginDefinitionFile.class);
            }
            if (def == null || def.name() == null || def.main() == null)
                throw new IOException("Invalid red4j.plugin.json format");
            logger.info("Loading plugin {} v{}", def.name(), def.version());

            // Load plugin
            URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()}, getClass().getClassLoader());
            Class<?> cl = loader.loadClass(def.main());
            if (!Plugin.class.isAssignableFrom(cl)) {
                throw new InvalidPluginException("Invalid plugin class: " + cl.getName());
            }
            Constructor<?> constructor;
            try {
                 constructor = cl.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("No constructor available in class: " + cl.getName());
            }
            Plugin instance = (Plugin) constructor.newInstance();
            instance.load();

            PluginContainer container = new PluginContainer(file, loader, def, instance);
            this.plugins.put(def.name(), container);
            return container;
        } catch (Throwable t) {
            throw new InvalidPluginException(t);
        }
    }

    @Override
    public PluginContainer getPlugin(String name) {
        return this.plugins.get(name);
    }

    @Override
    public PluginContainer[] getPlugins() {
        return this.plugins.values().toArray(new PluginContainer[0]);
    }

    void loadPlugins() throws IOException {
        if (!Files.exists(this.root))
            Files.createDirectories(this.root);

        for (File file : root.toFile().listFiles()) {
            try {
                loadPlugin(file);
            } catch (Throwable t) {
                logger.error("Error loading plugin: {}", file.getName(), t);
            }
        }
    }

    void enablePlugins() {
        for (PluginContainer container : plugins.values()) {
            logger.info("Enabling plugin {}", container);
            container.instance().enable();
        }
    }

    void disablePlugins() {
        for (PluginContainer container : plugins.values()) {
            logger.info("Disabling plugin {}", container);
            container.instance().disable();
        }
    }
}
