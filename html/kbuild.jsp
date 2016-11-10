<!DOCTYPE HTML>
<!--
    Future Imperfect by HTML5 UP
    html5up.net | @ajlkn
    Free for personal and commercial use under the CCA 3.0 license (html5up.net/license)
  -->
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
    <%@ include file="kb_header.jspf" %>
    <div>
	<h3>Overview about Kernel Configurations and Tests</h3>
	<p> Click on the desired kernel configuration to show details about the corresponding tests </p>
	<div id="detail"></div>
    </div>

  </article>
</div>

<%@ include file="sidebar.jspf" %>
</div>

<!-- Scripts -->
<%@ include file="scripts.jspf" %>
<script src="assets/js/kbuild.js"></script>
<script src="assets/js/kb_header.js"></script>
<script>embedCharts(${param.id});</script>

</body>
</html>
