package com.miw.presentation.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.miw.infrastructure.log.Log4jAdapter;
import com.miw.model.User;
import com.miw.presentation.commands.Command;
import com.miw.presentation.di.MyLogger;
import com.miw.presentation.di.HttpSessionAware;
import com.miw.presentation.di.LoggerAware;
import com.miw.presentation.di.ServletContextAware;
import com.miw.presentation.di.ServletRequestAware;
import com.miw.presentation.di.UserAware;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(value = "/Controller")
public class ControllerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	// protected Logger logger = LogManager.getLogger(getClass());
	private MyLogger logger = new Log4jAdapter(ControllerServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		String forwardView = "index.html";
		Command command = null;

		if (action == null) {
			logger.info("No action parameter received. Forwarding to index.");
			forwardView = "index.html";
			RequestDispatcher dispatcher = req.getRequestDispatcher(forwardView);
			dispatcher.forward(req, resp);
			return;
		}

		logger.debug("Processing action: " + action);

		// --- Inicio: Control de Autorización ---
		HttpSession session = req.getSession(false);
		User user = (session != null) ? (User) session.getAttribute("user") : null;
		String userRole = (user != null) ? user.getRole() : null;

		AuthorizationManager authManager = AuthorizationManager.getInstance();

		boolean requiresAuthorization = !(action.equals("LoginAction") || action.equals("LogoutAction"));

		if (requiresAuthorization) {
			if (user == null) {
				logger.debug("User not logged in, attempting to access restricted action: " + action);
				forwardView = "login.jsp";
				req.setAttribute("error", "Please login to access this page.");
				RequestDispatcher dispatcher = req.getRequestDispatcher(forwardView);
				dispatcher.forward(req, resp);
				return;
			} else if (!authManager.isRoleAllowed(userRole, action)) {
				logger.debug("Authorization DENIED for user '" + user.getUsername() + "' (Role: " + userRole
						+ ") on action: " + action);
				forwardView = "accessDenied.jsp"; // Vista de acceso denegado
				RequestDispatcher dispatcher = req.getRequestDispatcher(forwardView);
				dispatcher.forward(req, resp);
				return;
			} else {
				logger.debug("Authorization GRANTED for user '" + user.getUsername() + "' (Role: " + userRole
						+ ") on action: " + action);
			}
		} else {
			logger.debug("Action '" + action + "' does not require authorization check.");
		}
		// --- Fin: Control de Autorización ---

		try {
			command = ControllerConfigurationManager.getInstance().getCommand(action);

			if (command != null) {
				injectDependencies(command, req, session, user);

				populateParameters(command, req, resp);

				command.execute();

				if (action.equals("LoginAction") && req.getAttribute("error") != null) {
					forwardView = ControllerConfigurationManager.getInstance().getForward(action + ".error");
					if (forwardView == null)
						forwardView = "login.jsp";
				} else {
					forwardView = ControllerConfigurationManager.getInstance().getForward(action);
				}

			} else {
				logger.error("Command not found for action: " + action);
			}

		} catch (Exception e) {
			logger.error("Error processing action " + action, e);
		}

		logger.debug("Forwarding to: " + forwardView);
		RequestDispatcher dispatcher = req.getRequestDispatcher(forwardView);
		dispatcher.forward(req, resp);
	}

	private void injectDependencies(Command command, HttpServletRequest req, HttpSession session, User user) {
		String actionName = command.getClass().getSimpleName();
		
		if (command instanceof ServletRequestAware) {
			logger.debug("Injecting request into command " + actionName);
			((ServletRequestAware) command).setServletRequest(req);
		}
		if (command instanceof HttpSessionAware) {
			HttpSession currentSession = (session != null) ? session : req.getSession();
			logger.debug("Injecting session into command " + actionName);
			((HttpSessionAware) command).setHttpSession(currentSession);
		}
		if (command instanceof ServletContextAware) {
			logger.debug("Injecting servlet context into command " + actionName);
			((ServletContextAware) command).setServletContext(req.getServletContext());
		}
		if (command instanceof LoggerAware) {
			logger.debug("Injecting logger into command " + actionName);
			((LoggerAware) command).setLogger(new Log4jAdapter(command.getClass()));
		}
		if (command instanceof UserAware && user != null) {
			logger.debug("Injecting user (" + user.getUsername() + ") into command " + actionName);
			((UserAware) command).setUser(user);
		}
	}

	private void populateParameters(Command command, HttpServletRequest req, HttpServletResponse resp) {
		if (command == null) return;
		
		for (String s : req.getParameterMap().keySet()) {
			if ("action".equals(s)) continue;
			
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
				// TODO Auto-generated catch block
				// e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}
}
