// SPDX-License-Identifier: MIT
// Copyright (c) 2016-2019 Linutronix GmbH

package de.linutronix.rttest.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.hibernate.HibernateException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

/**
 * HTTP servlet with hibernate support.
 *
 * @author Benedikt Spranger
 */
public class HibernateHttpServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * The session factory.
     */
    private SessionFactory sessionFactory;

    /**
     * The configuration.
     */
    private static final Configuration CONFIGURATION = new Configuration();

    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger("de.linutronix.rttest");

    /**
     *
     */
    private String dbuser;

    /**
     *
     */
    private String dbpass;

    /**
     *
     */
    public HibernateHttpServlet() {
        this.dbpass = null;
        this.dbuser = null;
    }

    /**
     * Initialize HTTP servlet with hibernate support.
     *
     * @param config servlet configuration
     * @throws ServletException on database configuration error
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        DbConf dbconf = new DbConf(config);
        LOGGER.log(Level.SEVERE, dbconf.toString());

        sessionFactory = buildSessionFactory(dbconf);
    }

    /**
     * Build session factory.
     *
     * @param dbconf database configuration
     * @return session factory
     * @throws ServletException on database configuration error
     */
    private SessionFactory buildSessionFactory(final DbConf dbconf)
            throws ServletException {

        try {
            CONFIGURATION.setProperty("hibernate.connection.driver_class",
                    dbconf.getDbclass());
            CONFIGURATION.setProperty("hibernate.connection.url",
                    dbconf.getURL());
            if (dbuser == null) {
                CONFIGURATION.setProperty("hibernate.connection.username",
                        dbconf.getDbuser());
            } else {
                CONFIGURATION.setProperty("hibernate.connection.username",
                        dbuser);
            }

            if (dbpass == null) {
                CONFIGURATION.setProperty("hibernate.connection.password",
                        dbconf.getDbpassword());
            } else {
                CONFIGURATION.setProperty("hibernate.connection.password",
                        dbpass);
            }

            CONFIGURATION.setProperty("hibernate.dialect", dbconf.getDialect());
            CONFIGURATION.setProperty("hibernate.hbm2ddl.auto", "update");
            CONFIGURATION.setProperty("hibernate.show_sql", dbconf.getDebug());
            CONFIGURATION.setProperty("hibernate.connection.pool_size", "10");

            StandardServiceRegistryBuilder builder =
                    new StandardServiceRegistryBuilder()
                            .applySettings(CONFIGURATION.getProperties());

            return CONFIGURATION.buildSessionFactory(builder.build());
        } catch (ServletException | HibernateException ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed: " + ex);
            return null;
        }
    }

    /**
     * Set database password.
     *
     * @param pass
     */
    public void setDbpass(final String pass) {
        this.dbpass = pass;
    }

    /**
     * Set database user.
     *
     * @param user
     */
    public void setDbuser(final String user) {
        this.dbuser = user;
    }

    /**
     * Get configuration metadata from annotations.
     *
     * @param aClass annotated class
     */
    public void addAnnotatedClass(final Class<?> aClass) {
        CONFIGURATION.addAnnotatedClass(aClass);
    }

    /**
     * Get session factory.
     *
     * @return session factory
     * @throws javax.servlet.ServletException
     */
    public SessionFactory getSessionFactory() throws ServletException {
        if (sessionFactory == null)
            throw new ServletException("Database configuration error.");
        return sessionFactory;
    }

    /**
     * Open session.
     *
     * @return session
     * @throws javax.servlet.ServletException
     */
    public Session openSession() throws ServletException {
        return getSessionFactory().openSession();
    }

    /**
     * Shutdown servlet.
     * @throws javax.servlet.ServletException
     */
    public void shutdown() throws ServletException {
        getSessionFactory().close();
    }

}
