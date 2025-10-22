package com.miw.presentation.commands;

import com.miw.model.User;
import com.miw.presentation.controller.AuthorizationManager;
import com.miw.presentation.di.HttpSessionAware;
import com.miw.presentation.di.ServletRequestAware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class LoginCommand implements Command, HttpSessionAware, ServletRequestAware {

	private HttpServletRequest request;
	private HttpSession session;
	private String username;

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public void execute() {
		if (session != null && session.getAttribute("user") != null) {
			User currentUser = (User) session.getAttribute("user");
			request.setAttribute("error", "You are already logged in as '" + currentUser.getUsername()
					+ "'. Log out of the current session to sign in with another account.");
			return;
		}

		AuthorizationManager authManager = AuthorizationManager.getInstance();
		String role = authManager.getUserRole(username);

		if (role != null) {
			User user = new User(username, role);
			session.setAttribute("user", user);
		} else {
			request.setAttribute("error", "Username not found.");
			if (session != null)
				session.invalidate();
		}
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setHttpSession(HttpSession session) {
		this.session = session;
	}
}
