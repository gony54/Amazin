package com.miw.presentation.di;

public interface MyLogger {
	void debug(String message);
	void info(String message);
	void warn(String message);
	void error(String message);
	void error(String message, Throwable throwable);
}
