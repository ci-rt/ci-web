// SPDX-License-Identifier: MIT
// Copyright (c) 2016-2019 Linutronix GmbH

package de.linutronix.rttest.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * class DBConf for Database Configuration.
 *
 * @author Benedikt Spranger
 */
public class DbConf implements Serializable {

    private static final long serialVersionUID = 0L;

    /**
     * The database connection URL.
     */
    private final String url;

    /**
     * The database user.
     */
    private final String dbuser;

    /**
     * The database password.
     */
    private final String dbpassword;

    /**
     * The database driver class.
     */
    private final String dbclass;

    /**
     * The debug status.
     */
    private String debug;

    /**
     * The database SQL dialect.
     */
    private final String dialect;

    /**
     * Initialize database connection class member.
     *
     * @param context application server context
     * @param prop properties file
     * @param property property key name
     * @return value of property key
     */
    private String initialize(final ServletContext context,
            final Properties prop, final String property) {
        String ovr, env, val;

        try {
            Context context1 = new InitialContext();
            Context envContext = (Context) context1.lookup("java:/comp/env");
            env = (String) envContext.lookup(property);
        } catch (NamingException ex) {
            env = null;
        }
        val = prop.getProperty(property);
        ovr = context.getInitParameter(property);
        if (env == null) {
            if (ovr == null) {
                return val;
            }
            return ovr;
        }
        return env;
    }

    /**
     * Database configuration class.
     *
     * @param config database configuration
     * @throws ServletException on database errors
     */
    public DbConf(final ServletConfig config) throws ServletException {
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

        String dbClass = prop.getProperty("class");
        try {
            Class.forName(dbClass);
        } catch (ClassNotFoundException e) {
            throw new ServletException("Could not load database class" + dbClass);
        }

        url = initialize(context, prop, "URL");
        dbclass = initialize(context, prop, "class");
        dbuser = initialize(context, prop, "user");
        dbpassword = initialize(context, prop, "password");
        debug = initialize(context, prop, "debug");
        dialect = initialize(context, prop, "dialect");
    }

    /**
     * represent object as String.
     *
     * @return string representation of object
     */
    public String toString() {
        return "URL    : " + url
                + "\ndbuser : " + dbuser
                + "\ndbpass : " + dbpassword
                + "\ndebug  : " + debug
                + "\ndialect: " + dialect;
    }

    /**
     * Get database driver class.
     *
     * @return database driver class
     * @throws ServletException if database driver class is not set
     */
    public String getDbclass() throws ServletException {
        if (dbclass == null) {
            throw new ServletException("db.property: class not set");
        }
        return dbclass;
    }

    /**
     * Get database connection URL.
     *
     * @return database connection URL
     * @throws ServletException if database connection URL is not set
     */
    public String getURL() throws ServletException {
        if (dbclass == null) {
            throw new ServletException("db.property: class not set");
        }
        return url;
    }

    /**
     * Get database user.
     *
     * @return database user
     * @throws ServletException if database user is not set
     */
    public String getDbuser() throws ServletException {
        if (dbuser == null) {
            throw new ServletException("db.property: user not set");
        }
        return dbuser;
    }

    /**
     * Get database password.
     *
     * @return database password
     * @throws ServletException if database password is not set
     */
    public String getDbpassword() throws ServletException {
        if (dbpassword == null) {
            throw new ServletException("db.property: password not set");
        }
        return dbpassword;
    }

    /**
     * Get debug status.
     *
     * @return debug status
     */
    public String getDebug() {
        if (debug == null) {
            debug = "false";
        }
        return debug;
    }

    /**
     * Get database SQL dialect.
     *
     * @return database SQL dialect
     * @throws ServletException if database SQL dialect is not set
     */
    public String getDialect() throws ServletException {
        if (dialect == null) {
            throw new ServletException("db.property: dialect not set");
        }
        return dialect;
    }
}
