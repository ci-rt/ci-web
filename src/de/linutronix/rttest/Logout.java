package de.linutronix.rttest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie c = cookies[i];
				String name = c.getName();
				
				if (name.equals("CI-RTL")) {
					c.setValue("");
					c.setPath("/");
					c.setMaxAge(0);
					response.addCookie(c);
				}
			}
		}
		
		response.sendRedirect(request.getContextPath() + "/ShowHeader");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
