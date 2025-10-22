package com.miw.presentation.commands;

import com.miw.presentation.di.HttpSessionAware;

import jakarta.servlet.http.HttpSession;

public class LogoutCommand implements Command, HttpSessionAware {

	private HttpSession session;

    @Override
    public void execute() {
        if (session != null)
            session.invalidate();
    }

    @Override
    public void setHttpSession(HttpSession session) {
        this.session = session;
    }
}
