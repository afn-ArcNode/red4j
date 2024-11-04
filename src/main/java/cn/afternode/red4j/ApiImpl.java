package cn.afternode.red4j;

import cn.afternode.red4j.api.RED4J;
import cn.afternode.red4j.api.plugin.PluginManager;

import java.nio.file.Path;

public class ApiImpl implements RED4J {
    private final Path root = Path.of("red4j");
    private PluginManager pluginManager;

    void setPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    @Override
    public Path getRoot() {
        return root;
    }
}
