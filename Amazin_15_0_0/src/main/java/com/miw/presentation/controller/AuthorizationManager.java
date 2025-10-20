package com.miw.presentation.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.*;

public class AuthorizationManager {

	private static final Logger logger = LogManager.getLogger(AuthorizationManager.class);
	private static AuthorizationManager instance = null;
	private Properties usersData;
	private Properties rolesData;

	private AuthorizationManager() {
		usersData = loadProperties("users.properties");
		rolesData = loadProperties("roles.properties");
	}

	private Properties loadProperties(String fileName) {
		Properties props = new Properties();
		try {
			InputStream file = AuthorizationManager.class.getClassLoader().getResourceAsStream(fileName);
			if (file != null) {
				logger.debug("Loading authorization file: " + fileName);
				props.load(file);
				file.close();
			} else {
				logger.error("Authorization file not found: " + fileName);
			}
		} catch (IOException e) {
			logger.error("Error loading authorization file: " + fileName, e);
		}
		return props;
	}

	public static synchronized AuthorizationManager getInstance() {
		if (instance == null)
			instance = new AuthorizationManager();
		return instance;
	}

	public String getUserRole(String username) {
		if (username == null)
			return null;
		return usersData.getProperty(username);
	}

	public boolean isRoleAllowed(String role, String action) {
		if (role == null || action == null) {
			logger.warn("Checking permission with null role or action. Role: " + role + ", Action: " + action);
			return false; // No role or no action means no permission
		}

		String allowedActions = rolesData.getProperty(role);
		if (allowedActions == null) {
			logger.warn("Role not found in configuration: " + role);
			return false; // Role doesn't exist in config
		}

		// Check if the action is in the comma-separated list for the role
		List<String> actionsList = Arrays.asList(allowedActions.split("\\s*,\\s*")); // Split by comma, trimming
																						// whitespace
		boolean allowed = actionsList.contains(action);
		logger.debug("Permission check for Role '" + role + "' on Action '" + action + "': "
				+ (allowed ? "ALLOWED" : "DENIED"));
		return allowed;
	}
}
