package cn.afternode.red4j.api.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Plugin {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private PluginState state = PluginState.NONE;

    protected void onLoad() {}
    protected void onEnable() {}
    protected void onDisable() {}

    public void load() {
        if (this.state == PluginState.NONE) {
            onLoad();
            this.state = PluginState.LOADED;
        }
    }

    public void enable() {
        if (state == PluginState.LOADED || state == PluginState.DISABLED) {
            onEnable();
            this.state = PluginState.ENABLED;
        }
    }

    public void disable() {
        if (state == PluginState.ENABLED) {
            onDisable();
            this.state = PluginState.DISABLED;
        }
    }

    public PluginState getState() {
        return state;
    }

    public Logger getLogger() {
        return logger;
    }
}
