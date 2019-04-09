<!DOCTYPE HTML>
<!-- SPDX-License-Identifier: MIT -->
<!-- Copyright (c) 2016-2019 Linutronix GmbH -->
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
    <%@ include file="t_header.jspf" %>
    <div id="t_tests"></div>
    <hr>
    <div>
      <ul class="icons">
	<li>
	  <button type="button" id="comp_max" onclick="compMax()">Compare Cyclictest Results</button>
	</li>
	<li>
	  <p id="comp_hint"></p>
	</li>
      </ul>
    </div>
    <div id="max"></div>

  </article>
</div>

<%@ include file="sidebar.jspf" %>
</div>

<!-- Scripts -->
<%@ include file="scripts.jspf" %>
<script src="assets/js/t_tests.js"></script>
<script src="assets/js/t_header.js"></script>
<script>embedCharts(${param.id})</script>

</body>
</html>
