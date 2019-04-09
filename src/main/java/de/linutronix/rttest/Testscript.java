// SPDX-License-Identifier: MIT
// Copyright (c) 2016-2019 Linutronix GmbH

package de.linutronix.rttest;

import de.linutronix.rttest.util.Download;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * Servlet implementation class Testscript.
 */
@WebServlet("/Testscript")
public class Testscript extends Download {

    private static final long serialVersionUID = 0L;

    /**
     * Provide a test script by CI-RT test ID ant test.
     */
    public Testscript() {
        super();
    }

    /**
     * Build SQL query string to get a test script by CI-RT test ID and test.
     *
     * @param request user request
     * @return SQL query string
     * @throws SQLException on unknown test
     */
    @Override
    protected String getSqlQuery(final HttpServletRequest request)
            throws SQLException {
        String test = request.getParameter("test");
        String id = request.getParameter("id");
        String column = "testscript";
        String filename = "null";
        String table = "null";

        switch (test) {
            case "build":
                filename = "buildtest-script";
                table = "build";
                break;
            case "cyclic":
                filename = "cyclictest-script";
                table = "cyclictest_view";
                break;
            case "generic":
                filename = "generictest-script";
                table = "generictest_view";
                break;
            case "genericlog":
                column = "testlog";
                filename = "generictest-log";
                table = "generictest_view";
                break;
            default:
                throw new SQLException("Unknown test");
        }

        return "SELECT '" + filename + "', " + column + " AS content FROM "
                + table + " WHERE id = " + id + ";";
    }

    /**
     * Get test script.
     *
     * @param rs SQL query result
     * @return the test script
     * @throws SQLException on database error
     */
    @Override
    protected byte[] getContent(final ResultSet rs) throws SQLException {
            return rs.getBytes("content");
    }

    /**
     * Create test script file name offered to user.
     *
     * @param rs SQL query result
     * @return test script file name
     * @throws SQLException on database error
     */
    @Override
    protected String getFilename(final ResultSet rs) throws SQLException {
        return rs.getString(1);
    }
}
