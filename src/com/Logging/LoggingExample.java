package com.Logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.db.jdbc.JdbcAppender;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * Created by Anders on 2016-04-19.
 */
public class LoggingExample {

    private LoggerContext loggerContext;
    private LoggerConfig loggerConfig;
    private JdbcAppender appender;

    public static void main( String[] args ) {
        Logger logger = LogManager.getRootLogger();
        logger.trace("Configuration File Defined To Be :: "+System.getProperty("log4j.configurationFile"));
    }

}
