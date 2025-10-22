package com.miw.infrastructure.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.miw.presentation.di.MyLogger;

public class Log4jAdapter implements MyLogger {

	private Logger log4jLogger;

	public Log4jAdapter(Class<?> clazz) {
		this.log4jLogger = LogManager.getLogger(clazz);
	}

	@Override
	public void debug(String message) {
		log4jLogger.debug(message);
	}

	@Override
	public void info(String message) {
		log4jLogger.info(message);
	}

	@Override
	public void warn(String message) {
		log4jLogger.warn(message);
	}

	@Override
	public void error(String message) {
		log4jLogger.error(message);
	}

	@Override
	public void error(String message, Throwable throwable) {
		log4jLogger.error(message, throwable);
	}
}
