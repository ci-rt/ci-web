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
 * Servlet implementation class Testscript
 */
@WebServlet("/Testscript")
public class Testscript extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private String url;
    private String user;
    private String password;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Testscript() {
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
        String dbClass = prop.getProperty("class");
        try {
            Class.forName(dbClass);
        } catch (ClassNotFoundException e) {
            throw new ServletException("Could not load database class"
                    + dbClass);
        }

        url = prop.getProperty("URL");
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
            Connection con = DriverManager.getConnection(url, user, password);
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            ResultSet rs;
            String test = request.getParameter("test");
            String id = request.getParameter("id");
            String table = "null";
            String filename = "null";

            if (test.equals("build")) {
                table = "build";
                filename = "buildtest-script";
            } else if (test.equals("cyclic")) {
                table = "cyclictest_view";
                filename = "cyclictest-script";
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "test type undefined");
                return;
            }

            rs = stmt.executeQuery("SELECT testscript FROM " + table
                    + " WHERE id = " + id + ";");
            if (!rs.next()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "config not found");
                return;
            }
            byte[] script = rs.getBytes("testscript");

            // Set response content type
            response.setContentType("application/force-download");
            response.setContentLengthLong(script.length);
            response.setHeader("Content-Transfer-Encoding", "binary");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + filename + "\"");

            OutputStream o = response.getOutputStream();
            o.write(script);
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
