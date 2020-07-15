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
                            The page you are looking for is not available.
                        </div>
                        <hr>
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
