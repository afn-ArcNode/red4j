package cn.afternode.red4j.api.plugin;

import java.util.List;

public record PluginDefinitionFile(
        String name,
        String main,
        String version,
        String description,
        List<String> authors
) {
}
