package de.linutronix.rttest.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class DbConf {
	private static Logger logger = Logger.getLogger("de.linutronix.rttest");

	private String URL;
	private String dbuser;
	private String dbpassword;
	private String dbclass;
	private String debug;
	private String dialect;

	private String initialize (ServletContext context, Properties prop,
							   String property) {
		String ovr, val;

		val = prop.getProperty(property);
		ovr = context.getInitParameter(property);

		return (ovr == null) ? val : ovr;
	}

	public DbConf(ServletConfig config) throws ServletException {
		ServletContext context = config.getServletContext();

		InputStream input = context.getResourceAsStream("/WEB-INF/db.properties");
		Properties prop = new Properties();

		if (input != null) {
			try {
				prop.load(input);
			} catch (IOException e) {
				throw new ServletException("Failed to read DB properties.");
			}
		} else {
			throw new ServletException("Database not configured.");
		}

		String DbClass = prop.getProperty("class");
		try {
			Class.forName(DbClass);
		} catch (ClassNotFoundException e) {
			throw new ServletException("Could not load database class" + DbClass);
		}

		URL = initialize (context, prop, "URL");
		dbclass = initialize (context, prop, "class");
		dbuser = initialize (context, prop, "user");
		dbpassword = initialize (context, prop, "password");
		debug = initialize (context, prop, "debug");
		dialect = initialize (context, prop, "dialect");
	}

	public String toString() {
		return	"URL    : " + URL +
			"\ndbuser : " + dbuser +
			"\ndbpass : " + dbpassword +
			"\ndebug  : " + debug +
			"\ndialect: " + dialect;
	}

	public String getDbclass() throws ServletException {
		if (dbclass == null) {
			throw new ServletException("db.property: class not set");
		}
		return dbclass;
	}

	public String getURL() throws ServletException {
		if (dbclass == null) {
			throw new ServletException("db.property: class not set");
		}
		return URL;
	}

	public String getDbuser() throws ServletException {
		if (dbuser == null) {
			throw new ServletException("db.property: user not set");
		}
		return dbuser;
	}

	public String getDbpassword() throws ServletException {
		if (dbpassword == null) {
			throw new ServletException("db.property: password not set");
		}
		return dbpassword;
	}

	public String getDebug() {
		if (debug == null)
			debug = "false";
		return debug;
	}

	public String getDialect() throws ServletException {
		if (dialect == null) {
			throw new ServletException("db.property: dialect not set");
		}
		return dialect;
	}
}
