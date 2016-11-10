package de.linutronix.rttest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.linutronix.rttest.util.CiRTLCred;
import de.linutronix.rttest.util.DbConf;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

@WebServlet("/login/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String URL;
	private String dbuser;
	private String dbpassword;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		DbConf conf = new DbConf(config);
		
		URL = conf.getURL();
		dbuser = conf.getDbuser();
		dbpassword = conf.getDbpassword();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// get request parameters for userID and password
		String user = request.getParameter("j_username");
		String pwd = request.getParameter("j_password");

		try {
			Connection con = DriverManager.getConnection(URL,dbuser,dbpassword);
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
							     ResultSet.CONCUR_READ_ONLY);
			ResultSet rs;

			rs = stmt.executeQuery("SELECT salt FROM passwd WHERE \"user\" = '" + user + "';");
			if (!rs.next()) {
				response.sendRedirect(request.getContextPath() + "/login/login-failed.html");
				return;
			}

			CiRTLCred UserCred;
			byte[] salt = rs.getBytes("salt");

			UserCred = new CiRTLCred(user, pwd, salt);
			Cookie RTUser = new Cookie("CI-RTL", UserCred.ToCookie());
			// setting cookie to expiry in 60 mins
			RTUser.setMaxAge(60 * 60);
			RTUser.setHttpOnly(true);
			RTUser.setPath("/");
			response.addCookie(RTUser);
			response.sendRedirect(request.getContextPath() + "/ShowHeader");
		}
		catch (Exception e)
		{
			throw new ServletException("Database error: " + e.getMessage() + "\n" + e.getStackTrace());
		}
	}
}
