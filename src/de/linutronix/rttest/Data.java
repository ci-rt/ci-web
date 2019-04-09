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
 * Servlet implementation class Data
 */
@WebServlet("/Data")
public class Data extends DataSourceServlet {
    private static final long serialVersionUID = 1L;
    private DbConf db;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        db = new DbConf(config);
    }

    /**
     * The SQL predefined capabilities set is a special custom set for SQL
     * databases. This implements most of the data source capabilities more
     * efficiently.
     */
    @Override
    public Capabilities getCapabilities() {
        return Capabilities.SQL;
    }

    @Override
    public DataTable generateDataTable(Query query, HttpServletRequest request)
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
     * NOTE: By default, this function returns true, which means that cross
     * domain requests are rejected.
     * This check is disabled here so examples can be used directly from the
     * address bar of the browser. Bear in mind that this exposes your
     * data source to xsrf attacks.
     * If the only use of the data source url is from your application,
     * that runs on the same domain, it is better to remain in restricted mode.
     */
    @Override
    protected boolean isRestrictedAccessMode() {
        return false;
    }
}
