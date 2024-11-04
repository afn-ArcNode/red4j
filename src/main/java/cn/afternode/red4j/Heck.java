package cn.afternode.red4j;

import cn.afternode.red4j.api.RED4JApi;
import org.slf4j.Logger;

public class Heck {
    public static boolean load(Logger logger) {
        ApiImpl impl = new ApiImpl();
        RED4JApi.setApi(impl);

        logger.info("Loading plugins");
        try {
            PluginManagerImpl pluginManager = new PluginManagerImpl(impl);
            impl.setPluginManager(pluginManager);
            pluginManager.loadPlugins();
            logger.info("Loaded {} plugins", impl.getPluginManager().getPlugins().length);
        } catch (Throwable t) {
            logger.error("Error loading plugins", t);
            return false;
        }

        logger.info("Initialization completed");
        return true;
    }
}
