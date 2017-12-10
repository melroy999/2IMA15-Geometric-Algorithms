package geo.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A simple wrapper class for the java.util logger, in which we automatically create a log file.
 */
public class GeoLogger {
    /**
     * Create a logger for the given name space, log to the default log file.
     *
     * @param name The name space of the logger.
     * @return A logger with the given name space, that also logs to the file log/geo.log.
     */
    public static Logger getLogger(String name) {
        return getLogger(name, "main.log");
    }

    /**
     * Create a logger for the given name space.
     *
     * @param name The name space of the logger.
     * @param logFileNames The name of the log file(s).
     * @return A logger with the given name space, that also logs to the file log/geo.log.
     */
    public static Logger getLogger(String name, String... logFileNames) {
        // Get a java logger.
        Logger logger = Logger.getLogger(name);

        // Now, connect a file handler to this logger.
        try {
            // Start with making the log folder.
            new File("log").mkdir();

            // Now for all given log file names, create a file handler and add it.
            for(String logFileName : logFileNames) {
                FileHandler fh = new FileHandler("log/" + logFileName, true);
                logger.addHandler(fh);

                SimpleFormatter formatter = new SimpleFormatter();
                fh.setFormatter(formatter);
            }

            // Make sure we write something to log initially.
            logger.info("Initialized logger for name [" + name + "]");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the logger.
        return logger;
    }
}
