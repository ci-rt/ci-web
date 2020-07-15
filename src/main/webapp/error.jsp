<!DOCTYPE HTML>
<!-- SPDX-License-Identifier: MIT -->
<!-- Copyright (c) 2016-2019 Linutronix GmbH -->
<%@ page isErrorPage="true" %>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>

<html>
    <%@ include file="head.jspf" %>
    <body>
        <!-- Wrapper -->
        <div id="wrapper">
            <%@ include file="header.jspf" %>

            <!-- Main -->
            <div id="main">
                <!-- Post -->
                <article class="post">
                    <header>
                        <div class="title">
                            <h2>Error page</h2>
                            Something's wrong here.
                        </div>
                    </header>
                    <div id="error_div">
                        <h3>Error ${pageContext.errorData.statusCode} </h3>
                        <div id="error_status_div" class="fas fa-frown">
                            The component
                            &raquo;${pageContext.errorData.servletName}&laquo;
                            reported an error while handling your request.
                        </div>
                        <hr>
                        <a href=
                           "mailto:ci@linutronix.de?subject=CI-RT%20web%20failure&body=An%20failure%20occured%20in%20the%20component%20%22${pageContext.errorData.servletName}%22%20while%20handling%20%22${pageContext.errorData.getRequestURI()}%22%20at%20${Date(System.currentTimeMillis())}.">
                            Send an error report to the CI-Team</a>
                    </div>
                </article>
            </div>

            <%@ include file="sidebar.jspf" %>
        </div>

        <!-- Scripts -->
        <%@ include file="scripts.jspf" %>
        <script src="<%=request.getContextPath()%>/assets/js/overview.js"></script>
    </body>
</html>
