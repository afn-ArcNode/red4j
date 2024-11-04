package cn.afternode.red4j.test;

import cn.afternode.red4j.api.plugin.Plugin;

public class TestPlugin extends Plugin {
    @Override
    protected void onLoad() {
        getLogger().info("Hello world");
    }
}
