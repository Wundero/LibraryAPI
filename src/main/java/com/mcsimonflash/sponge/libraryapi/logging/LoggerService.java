package com.mcsimonflash.sponge.libraryapi.logging;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;

/* TODO:
 * Consider a single LoggerService that catches all logged message and outputs
 * them to a unified collection of log files. A useful 'filter' feature might be
 * included to filter out (or in) logs from specific plugins.
 */
/* TODO:
 * Consider implementing the slf4j Logger instead of wrapping an existing one.
 */
/* TODO:
 * If this class implements the slf4j Logger, is it possible to overwrite the
 * Guice @Inject Logger registered by Sponge to return this instead?
 */
/**
 * A logging service that wraps the {@link org.slf4j.Logger}. This logger can
 * save message to an external log file and enable/disable a debug mode.
 */
public class LoggerService {

    private final Logger logger;
    private final LogFile info, warn, error, debug;
    private boolean debugMode;

    /**
     * Creates a new instance without any log files.
     *
     * @param logger the slf4j logger to wrap
     */
    public LoggerService(Logger logger) {
        this(logger, null, null, null, null);
    }

    /**
     * Creates a new instance where all {@link LogFile}s are stored in the
     * directory of the given path.
     *
     * @param logger the slf4j logger to wrap
     * @param path the path to the directory holding logs
     * @throws IOException if a LogFile could not be initialized
     */
    public LoggerService(Logger logger, Path path) throws IOException {
        this(logger, new LogFile(path.resolve("info.log")), new LogFile(path.resolve("warn.log")), new LogFile(path.resolve("error.log")), new LogFile(path.resolve("debug.log")));
    }

    /**
     * Creates a new instance with the given {@link LogFile}s.
     *
     * This is useful for only logging certain types of messages, different
     * types to the same file, or files in different locations.
     *
     * @param logger the slf4j logger to wrap
     * @param info the file to log info messages to
     * @param warn the file to log warn messages to
     * @param error the file to log error messages to
     * @param debug the file to log debug messages to
     */
    public LoggerService(Logger logger, LogFile info, LogFile warn, LogFile error, LogFile debug) {
        this.logger = logger;
        this.info = info;
        this.warn = warn;
        this.error = error;
        this.debug = debug;
    }

    /**
     * Enables or disables the debug mode. If disabled, debug message will not
     * be sent or logged to the debug file.
     *
     * @param debug whether debug mode should be enabled
     */
    public void setDebugMode(boolean debug) {
        debugMode = debug;
    }

    /**
     * Attempts to log a message to the provided {@link LogFile}. If the file
     * for this type of log is not set, this call does nothing.
     *
     * If {@link LogFile#log(String)} throws an {@link IOException}, it will be
     * resent as an error. If the file is the {@link #error} file, it will call
     * {@link Logger#error(String, Throwable)}.
     *
     * @param file the file to log the message in
     * @param msg the message to be logged
     */
    private void logMessage(LogFile file, String msg) {
        if (file != null) {
            try {
                file.log(msg);
            } catch (IOException e) {
                if (file == error) {
                    logger.error("Unable to save log message to file. Message: " + msg, e);
                } else {
                    error("Unable to save log message to file. Message: " + msg, e);
                }
            }
        }
    }

    /**
     * Attempts to log a {@link Throwable}.
     *
     * This constructs a call to {@link #logMessage(LogFile, String)} where the
     * {@param msg} is the stacktrace of the throwable.
     *
     * @param log the file to log the throwable in
     * @param t the throwable to be logged
     */
    private void logThrowable(LogFile log, Throwable t) {
        if (log != null) {
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw, true));
            logMessage(log, sw.getBuffer().toString());
        }
    }

    /*
     * The following methods are all designed to implement the basic Logger
     * methods and some of their variants.
     *
     * Each method simply calls the necessary log methods above before calling
     * the respective Logger method.
     */

    public void info(String msg) {
        logMessage(info, msg);
        logger.info(msg);
    }

    public void info(String msg, Throwable t) {
        logMessage(info, msg);
        logThrowable(info, t);
        logger.info(msg, t);
    }

    public void info(String format, Object... arguments) {
        logMessage(info, String.format(format, arguments));
        logger.info(format, arguments);
    }

    public void warn(String msg) {
        logMessage(warn, msg);
        logger.warn(msg);
    }

    public void warn(String msg, Throwable t) {
        logMessage(warn, msg);
        logThrowable(warn, t);
        logger.warn(msg, t);
    }

    public void warn(String format, Object... arguments) {
        logMessage(warn, String.format(format, arguments));
        logger.warn(format, arguments);
    }

    public void error(String msg) {
        logMessage(error, msg);
        logger.error(msg);
    }

    public void error(String msg, Throwable t) {
        logMessage(error, msg);
        logThrowable(error, t);
        logger.error(msg, t);
    }

    public void error(String format, Object... arguments) {
        logMessage(error, String.format(format, arguments));
        logger.error(format, arguments);
    }

    /*
     * These methods include a simple check to see if debug mode is enabled. If
     * it is not, the method does nothing.
     */

    public void debug(String msg) {
        if (debugMode) {
            logMessage(debug, msg);
            logger.debug(msg);
        }
    }

    public void debug(String msg, Throwable t) {
        if (debugMode) {
            logMessage(debug, msg);
            logThrowable(debug, t);
            logger.debug(msg, t);
        }
    }

    public void debug(String format, Object... arguments) {
        if (debugMode) {
            logMessage(debug, String.format(format, arguments));
            logger.debug(format, arguments);
        }
    }

}