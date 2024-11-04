package cn.afternode.red4j;

import cn.afternode.red4j.util.ClasspathAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class RED4J {
    private static Logger logger;

    public static boolean initialize() throws IOException {
        // Inject dependencies
        try {
            ClassLoader loader = RED4J.class.getClassLoader();
            for (File file : new File(RED4J.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().listFiles()) {
                if (file.getName().endsWith(".jar"))
                    ClasspathAppender.app(file.toURI().toURL(), loader);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        logger = LoggerFactory.getLogger("RED4J");
        logger.info("Classpath injection completed");

        return Heck.load(logger);
    }
}
