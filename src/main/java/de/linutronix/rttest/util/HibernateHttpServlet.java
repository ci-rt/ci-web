// SPDX-License-Identifier: MIT
// Copyright (c) 2016-2019 Linutronix GmbH

package de.linutronix.rttest.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

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
    private static Configuration configuration = new Configuration();

    /**
     * The logger.
     */
    private static Logger logger = Logger.getLogger("de.linutronix.rttest");

    /**
     * Initialize HTTP servlet with hibernate support.
     *
     * @param config servlet configuration
     * @throws ServletException on database configuration error
     */
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        DbConf dbconf = new DbConf(config);
        logger.log(Level.SEVERE, dbconf.toString());

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
            configuration.setProperty("hibernate.connection.driver_class",
                    dbconf.getDbclass());
            configuration.setProperty("hibernate.connection.url",
                    dbconf.getURL());
            configuration.setProperty("hibernate.connection.username",
                    dbconf.getDbuser());
            configuration.setProperty("hibernate.connection.password",
                    dbconf.getDbpassword());
            configuration.setProperty("hibernate.dialect",
                    dbconf.getDialect());
            configuration.setProperty("hibernate.hbm2ddl.auto", "update");
            configuration.setProperty("hibernate.show_sql", dbconf.getDebug());
            configuration.setProperty("hibernate.connection.pool_size", "10");

            StandardServiceRegistryBuilder builder =
                    new StandardServiceRegistryBuilder()
                            .applySettings(configuration.getProperties());

            return configuration.buildSessionFactory(builder.build());
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Get configuration metadata from annotations.
     *
     * @param aClass annotated class
     */
    public void addAnnotatedClass(final Class<?> aClass) {
        configuration.addAnnotatedClass(aClass);
    }

    /**
     * Get session factory.
     *
     * @return session factory
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Open session.
     *
     * @return session
     */
    public Session openSession() {
        return getSessionFactory().openSession();
    }

    /**
     * Shutdown servlet.
     */
    public void shutdown() {
        getSessionFactory().close();
    }

}
