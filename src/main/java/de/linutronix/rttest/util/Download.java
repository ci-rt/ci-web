// SPDX-License-Identifier: MIT
// Copyright (c) 2016-2019 Linutronix GmbH

package de.linutronix.rttest.util;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Base servlet implementation to download files.
 */
public class Download extends HttpServlet {

    private static final long serialVersionUID = 0L;

    /**
     * Database configuration settings.
     */
    private DbConf db;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Download() {
        super();
    }

    /**
     * Initialize base download servlet used to deliver files.
     *
     * The base download servlet servlet needs to initialize both:
     * - the servlet itself and
     * - the database access.
     *
     * @param config Database and Servlet configuration data from the
     *               application server.
     * @throws javax.servlet.ServletException on database error.
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        db = new DbConf(config);
    }

    /**
     * SQL query callback function.
     *
     * @param request the CIT-RT test ID
     * @return the SQL query
     * @throws java.sql.SQLException if not defined in child
     */
    protected String getSqlQuery(final HttpServletRequest request)
            throws SQLException {
        throw new SQLException("internal error: No SQL callback defined.");
    }

    /**
     * content callback function.
     *
     * @param rs the ResultSet of the SQL query
     * @return the file content
     * @throws java.sql.SQLException if not defined in child
     */
    protected byte[] getContent(final ResultSet rs) throws SQLException {
        throw new SQLException("internal error: No content defined.");
    }

    /**
     * filename callback function.
     *
     * @param rs the ResultSet of the SQL query
     * @return filename offered to user
     * @throws java.sql.SQLException if not defined in child
     */
    protected String getFilename(final ResultSet rs) throws SQLException {
        throw new SQLException("internal error: No filename defined.");
    }

    /**
     * GET callback to deliver files.
     *
     * @param request the html request from the user
     * @param response GET response delivering kernel configs
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doGet(final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection con = DriverManager.getConnection(db.getURL(),
                db.getDbuser(), db.getDbpassword())) {
            ResultSet rs = null;

            try (Statement stmt = con.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY)) {
                rs = stmt.executeQuery(getSqlQuery(request));

                if (!rs.next()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "no valid data found");
                    return;
                }
                byte[] content = getContent(rs);
                String filename = getFilename(rs);
                // Set response content type
                response.setContentType("application/force-download");
                response.setContentLengthLong(content.length);
                response.setHeader("Content-Transfer-Encoding", "binary");
                response.setHeader("Content-Disposition",
                        "attachment; filename=\"" + filename + "\"");

                try (OutputStream o = response.getOutputStream()) {
                    o.write(content);
                    o.flush();
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
        } catch (IOException | SQLException e) {
            throw new ServletException("Database error: " + e.getMessage());
        }
    }

    /**
     * POST callback to deliver files.
     *
     * @param request the html request from the user
     * @param response POST response delivering files
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doPost(final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
