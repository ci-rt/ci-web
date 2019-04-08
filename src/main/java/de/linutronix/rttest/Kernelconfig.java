// SPDX-License-Identifier: MIT
// Copyright (c) 2016-2019 Linutronix GmbH

package de.linutronix.rttest;

import de.linutronix.rttest.util.Download;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * Servlet implementation class Kernelconfig.
 */
@WebServlet("/Kernelconfig")
public class Kernelconfig extends Download {

    private static final long serialVersionUID = 1L;

    /**
     * Provide a Linux Kernel Configuration by CI-RT Test ID.
     */
    public Kernelconfig() {
        super();
    }

    /**
     * Build SQL query string to get a kernel config by CI-RT test ID.
     *
     * @param request user request
     * @return SQL query string
     */
    @Override
    protected String getSqlQuery(final HttpServletRequest request) {
        String id = request.getParameter("id");

        return "SELECT defconfig, config, overlay FROM build WHERE id = "
                    + id + ";";
    }

    /**
     * Get kernel config.
     *
     * @param rs SQL query result
     * @return the kernel config
     * @throws SQLException on database errors
     */
    @Override
    protected byte[] getContent(final ResultSet rs) throws SQLException {
            return rs.getBytes("defconfig");
    }

    /**
     * Create kernel config file name offered to user.
     *
     * @param rs SQL query result
     * @return kernel config file name
     * @throws SQLException on database errors
     */
    @Override
    protected String getFilename(final ResultSet rs) throws SQLException {
        String name = rs.getString("config");
        String overlay = rs.getString("overlay");

        return name.concat(overlay);
    }
}
