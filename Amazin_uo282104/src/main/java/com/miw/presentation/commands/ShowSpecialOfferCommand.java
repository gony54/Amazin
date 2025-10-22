package com.miw.presentation.commands;

import com.miw.model.User;
import com.miw.presentation.book.BookManagerServiceHelper;
import com.miw.presentation.di.MyLogger;
import com.miw.presentation.di.LoggerAware;
import com.miw.presentation.di.ServletRequestAware;

import jakarta.servlet.http.HttpServletRequest;

public class ShowSpecialOfferCommand implements Command, ServletRequestAware, LoggerAware {
	private HttpServletRequest request;
	private MyLogger logger;
	private User user;
	
	public ShowSpecialOfferCommand() {
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public void execute() {
		if (this.logger != null) {
			logger.debug("Executing " + this.getClass().getName());
			if (user != null)
				logger.debug("Special offer requested by user: " + user.getUsername());
		}
		BookManagerServiceHelper helper = new BookManagerServiceHelper();
		try {
			request.setAttribute("book", helper.getSpecialOffer());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	@Override
	public void setLogger(MyLogger logger) {
		this.logger = logger;
	}

}
