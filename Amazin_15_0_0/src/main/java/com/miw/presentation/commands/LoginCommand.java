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
		AuthorizationManager authManager = AuthorizationManager.getInstance();
		String role = authManager.getUserRole(username);

		if (role != null) {
			User user = new User(username, role);
			session.setAttribute("user", user);
			// No redirigimos desde aquí, ControllerServlet lo hará a la vista "success"
		} else {
			// Usuario no encontrado, añadir mensaje de error al request
			request.setAttribute("error", "Username not found.");
			// Invalidar la sesión por si acaso había una antigua
			session.invalidate();
			// No redirigimos, ControllerServlet irá a la vista "error" o "input"
			// (necesitamos definir una vista de error/input en controller.properties)
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
