package cn.afternode.red4j.api.plugin;

import java.io.File;
import java.net.URLClassLoader;

public record PluginContainer(File file, URLClassLoader classLoader, PluginDefinitionFile definition, Plugin instance) {
    @Override
    public String toString() {
        return definition.name() + " v" + definition.version();
    }
}
