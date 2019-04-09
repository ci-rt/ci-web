// SPDX-License-Identifier: MIT
// Copyright (c) 2016-2019 Linutronix GmbH

package de.linutronix.rttest;

import de.linutronix.rttest.util.DbConf;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Kernelconfig.
 */
@WebServlet("/Kernelconfig")
public class Kernelconfig extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Database configuration settings.
     */
    private DbConf db;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Kernelconfig() {
        super();
    }

    /**
     * Initialize Kernelconfig servlet used to deliver kernel configs.
     *
     * The Kernelconfig servlet needs to initialize both:
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
     * GET callback to deliver kernel configs.
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
        try {
            Connection con = DriverManager.getConnection(db.getURL(),
                    db.getDbuser(), db.getDbpassword());
            Statement stmt = con.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet rs;
            String id = request.getParameter("id");

            rs = stmt.executeQuery(
                    "SELECT defconfig, config, overlay FROM build WHERE id = "
                    + id + ";");
            if (!rs.next()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "config not found");
                return;
            }
            byte[] defconf = rs.getBytes("defconfig");

            String name = rs.getString("config");
            String overlay = rs.getString("overlay");

            String filename = name.concat(overlay);
            // Set response content type
            response.setContentType("application/force-download");
            response.setContentLengthLong(defconf.length);
            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + filename + "\"");

            OutputStream o = response.getOutputStream();
            o.write(defconf);
            o.flush();
            o.close();
            con.close();
        } catch (Exception e) {
            throw new ServletException("Database error: " + e.getMessage()
                    + "\n" + e.getStackTrace());
        }
    }

    /**
     * POST callback to deliver kernel configs.
     *
     * @param request the html request from the user
     * @param response POST response delivering kernel configs
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
