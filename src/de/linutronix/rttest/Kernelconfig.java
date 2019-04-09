// SPDX-License-Identifier: MIT
// Copyright (c) 2016-2019 Linutronix GmbH

package de.linutronix.rttest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.sql.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Kernelconfig
 */
@WebServlet("/Kernelconfig")
public class Kernelconfig extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private String URL;
    private String user;
    private String password;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Kernelconfig() {
        super();
    }

    /**
     * @see Servlet#init(ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        InputStream input = getServletContext()
                .getResourceAsStream("/WEB-INF/db.properties");
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

        // get the property value and print it out
        String DbClass = prop.getProperty("class");
        try {
            Class.forName(DbClass);
        } catch (ClassNotFoundException e) {
            throw new ServletException("Could not load database class" + DbClass);
        }

        URL = prop.getProperty("URL");
        user = prop.getProperty("user");
        password = prop.getProperty("password");
    }

    /**
     * @see Servlet#destroy()
     */
    public void destroy() {
        // TODO Auto-generated method stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        try {
            Connection con = DriverManager.getConnection(URL, user, password);
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
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
