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
    <header>
      <div class="title">	    
	<h2>Target overview</h2>
      </div>
    </header>
    <p> Click on the desired target to show and compare all cyclictests executed on the target. </p>
    <div id="target_overview"></div>
    
  </article>
</div>

<%@ include file="sidebar.jspf" %>
</div>
<!-- Scripts -->
<%@ include file="scripts.jspf" %>
<!--<script src="dashbuilder/js-api/dashbuilder-1.0.0.js"></script>-->
<script src="assets/js/targets.js"></script>
<script>embedCharts()</script>


</body>
</html>
