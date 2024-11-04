package cn.afternode.red4j.api.plugin;

import java.io.File;

public interface PluginManager {
    PluginContainer loadPlugin(File file);

    PluginContainer getPlugin(String name);

    PluginContainer[] getPlugins();
}
