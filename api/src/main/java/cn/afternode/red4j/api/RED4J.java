package cn.afternode.red4j.api;

import cn.afternode.red4j.api.plugin.PluginManager;

import java.nio.file.Path;

public interface RED4J {
    PluginManager getPluginManager();

    Path getRoot();
}
