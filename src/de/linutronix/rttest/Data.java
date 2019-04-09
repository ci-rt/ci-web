// SPDX-License-Identifier: MIT
// Copyright (c) 2016-2019 Linutronix GmbH

package de.linutronix.rttest;

import com.google.visualization.datasource.Capabilities;
import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.ReasonType;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.query.Query;
import com.google.visualization.datasource.util.SqlDataSourceHelper;
import com.google.visualization.datasource.util.SqlDatabaseDescription;

import de.linutronix.rttest.util.DbConf;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * Servlet implementation of class Data.
 */
@WebServlet("/Data")
public class Data extends DataSourceServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Database configuration settings.
     */
    private DbConf db;

    /**
     * Initialize servlet used to display datasets.
     *
     * The datasource servlet needs to initialize both:
     * - the servlet itself and
     * - the database access.
     *
     * @param config Database and Servlet configuration data from the
     *               application server.
     * @throws ServletException if initialization fails.
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        db = new DbConf(config);
    }

    /**
     * Provide capabilities about the datasource.
     *
     * The SQL predefined capabilities set is a special custom set for SQL
     * databases. This implements most of the data source capabilities more
     * efficiently.
     *
     * @return The datasource is capable to use SQL.
     */
    @Override
    public Capabilities getCapabilities() {
        return Capabilities.SQL;
    }

    /**
     * Handle datasource queries.
     *
     * @param query datasource query to handle
     * @param request the html request from the user
     * @return query result
     * @throws DataSourceException on database configuration errors.
     */
    @Override
    public DataTable generateDataTable(Query query,
            HttpServletRequest request)
            throws DataSourceException {

        SqlDatabaseDescription dbDescription = null;

        try {
            dbDescription = new SqlDatabaseDescription(
                    db.getURL(), db.getDbuser(), db.getDbpassword(),
                    request.getParameter("table"));
        } catch (ServletException e) {
            e.printStackTrace();
            throw new DataSourceException(ReasonType.INTERNAL_ERROR,
                    "Database configuration error: ");
        }

        return SqlDataSourceHelper.executeQuery(query, dbDescription);
    }

    /**
     * Provide servlet access restriction mode.
     *
     * NOTE: By default, this function returns true, which means that cross
     * domain requests are rejected. This check is disabled here to ease
     * development.
     * Bear in mind that this exposes your data source to xsrf attacks. If the
     * only use of the data source url is from your application, that runs on
     * the same domain, it is better to remain in restricted mode.
     *
     * @return disable restriction mode
     */
    @Override
    protected boolean isRestrictedAccessMode() {
        return false;
    }
}
