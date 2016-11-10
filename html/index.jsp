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
      			<h2>Build Overview</h2>
      		</div>
    	</header>
	    <div id="dash_builds_div">
        	<div id="con_branch_div"> </div><br>
		 	<div id="con_tag_div"> </div><br>
			<div id="con_time_div"> </div>
			<hr>
			<p> Click on the desired build to show the build details.</p>
      		<div id="table_div"> </div>
    	</div>
    </article>
</div>

<%@ include file="sidebar.jspf" %>
</div>
<!-- Scripts -->
<%@ include file="scripts.jspf" %>
<script src="assets/js/overview.js"></script>
<script>embedCharts()</script>

</body>
</html>
