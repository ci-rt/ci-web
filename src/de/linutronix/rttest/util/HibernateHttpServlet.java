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

public class HibernateHttpServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private SessionFactory sessionFactory;
    private static Configuration configuration = new Configuration();
    private static Logger logger = Logger.getLogger("de.linutronix.rttest");

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        DbConf dbconf = new DbConf(config);
        logger.log(Level.SEVERE, dbconf.toString());

        sessionFactory = buildSessionFactory(dbconf);
    }

    private SessionFactory buildSessionFactory(DbConf dbconf)
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

    public void addAnnotatedClass(Class<?> aClass)
    {
        configuration.addAnnotatedClass(aClass);
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public Session openSession() {
        return getSessionFactory().openSession();
    }

    public void shutdown() {
        getSessionFactory().close();
    }

}
