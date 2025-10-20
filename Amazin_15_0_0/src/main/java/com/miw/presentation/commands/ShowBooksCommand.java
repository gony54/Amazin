package com.miw.presentation.commands;

import com.miw.presentation.book.BookManagerServiceHelper;
import com.miw.presentation.di.MyLogger;
import com.miw.presentation.di.HttpSessionAware;
import com.miw.presentation.di.LoggerAware;
import com.miw.presentation.di.ServletContextAware;
import com.miw.presentation.di.ServletRequestAware;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class ShowBooksCommand
		implements Command, HttpSessionAware, ServletRequestAware, ServletContextAware, LoggerAware {

	private HttpServletRequest request;
	// private HttpSession session;
	// private ServletContext context;

	private String myParameter = null;
	private MyLogger logger;

	@Override
	public void setLogger(MyLogger logger) {
		this.logger = logger;
	}

	public String getMyParameter() {
		return myParameter;
	}

	public void setMyParameter(String myParameter) {
		if (this.logger != null)
			logger.debug("Setting myParameter to " + myParameter);
		this.myParameter = myParameter;
	}

	public void execute() {
		if (this.logger != null)
			logger.debug("Executing ShowBooksCommand");
		BookManagerServiceHelper helper = new BookManagerServiceHelper();
		try {
			request.setAttribute("books", helper.getBooks());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setServletContext(ServletContext context) {
		// this.context = context;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setHttpSession(HttpSession session) {
		// this.session = session;
	}
}
