package com.miw.presentation.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.miw.infrastructure.log.Log4jAdapter;
import com.miw.model.User;
import com.miw.presentation.commands.Command;
import com.miw.presentation.di.HttpSessionAware;
import com.miw.presentation.di.LoggerAware;
import com.miw.presentation.di.MyLogger;
import com.miw.presentation.di.ServletContextAware;
import com.miw.presentation.di.ServletRequestAware;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(value = "/Controller")
public class ControllerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private MyLogger logger = new Log4jAdapter(ControllerServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");

		if (action == null) {
			req.getRequestDispatcher("index.jsp").forward(req, resp);
			return;
		}
		logger.debug("Executing action" + action);

		String forwardView = checkAuthorization(req, action);
		if (forwardView != null) {
			req.getRequestDispatcher(forwardView).forward(req, resp);
			return;
		}

		Command command = ControllerConfigurationManager.getInstance().getCommand(action);

		if (command != null) {
			injectDependencies(command, req, action);
			populateParameters(command, req, resp);
			command.execute();
			forwardView = determineForwardView(action, req);
		} else {
			logger.error("Command not found for action: " + action);
			forwardView = "index.jsp";
		}

		req.getRequestDispatcher(forwardView).forward(req, resp);
	}

	private String checkAuthorization(HttpServletRequest req, String action) {
		if (action.equals("LoginAction") || action.equals("LogoutAction"))
			return null;

		HttpSession session = req.getSession(false);
		User user = (session != null) ? (User) session.getAttribute("user") : null;

		if (user == null) {
			logger.info("Unauthorized access attempt to: " + action);
			req.setAttribute("error", "Please login to access this page.");
			return "login.jsp";
		}

		AuthorizationManager authManager = AuthorizationManager.getInstance();
		if (!authManager.isRoleAllowed(user.getRole(), action)) {
			logger.warn("Access denied for user '" + user.getUsername() + "' to action: " + action);
			return "accessDenied.jsp";
		}

		return null;
	}

	private void injectDependencies(Command command, HttpServletRequest req, String action) {
		if (command instanceof ServletRequestAware) {
			logger.debug("Injecting request in command " + action);
			((ServletRequestAware) command).setServletRequest(req);
		}
		if (command instanceof HttpSessionAware) {
			logger.debug("Injecting session in command " + action);
			((HttpSessionAware) command).setHttpSession(req.getSession());
		}
		if (command instanceof ServletContextAware) {
			logger.debug("Injecting session in command " + action);
			((ServletContextAware) command).setServletContext(req.getServletContext());
		}
		if (command instanceof LoggerAware) {
			logger.debug("Injecting logger in command " + action);
			((LoggerAware) command).setLogger(new Log4jAdapter(command.getClass()));
		}
		injectUserIfRequested(command, req);
	}

	private void injectUserIfRequested(Command command, HttpServletRequest req) {
		try {
			Method setUserMethod = command.getClass().getMethod("setUser", User.class);
			
			HttpSession session = req.getSession(false);
			User user = (session != null) ? (User) session.getAttribute("user") : null;
			
			if (user != null)
				setUserMethod.invoke(command, user);
		} catch (NoSuchMethodException e) {
			// e.printStackTrace();
		} catch (IllegalAccessException | InvocationTargetException e) {
			logger.error("Error injecting user into command", e);
		}
	}
	
	private String determineForwardView(String action, HttpServletRequest req) {
		if (action.equals("LoginAction") && req.getAttribute("error") != null) {
			String errorView = ControllerConfigurationManager.getInstance().getForward(action + ".error");
			return (errorView != null) ? errorView : "login.jsp";
		}
		return ControllerConfigurationManager.getInstance().getForward(action);
	}

	private void populateParameters(Command command, HttpServletRequest req, HttpServletResponse resp) {
		for (String s : req.getParameterMap().keySet()) {
			try {
				Method m = command.getClass().getMethod("set" + s.substring(0, 1).toUpperCase() + s.substring(1),
						String.class);
				if (m != null) {
					logger.debug("Found expected parameter " + "set" + s.substring(0, 1).toUpperCase() + s.substring(1)
							+ ", populating the value");
					try {
						m.invoke(command, req.getParameter(s));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			} catch (NoSuchMethodException e) {
				// e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}
}