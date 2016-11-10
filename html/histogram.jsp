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
    <ul class="icons">
      <li>
	<img id="hist_status" width="18"/>
      </li>
      <li>
	<h3>Cyclictest</h3>
      </li>
      <li>
        <button type="button" id="download_script" onclick="downloadScript(${param.cyclictest})">Get Cyclictest Script</button>
      </li>
    </ul>
    
    <p id="hist_descr"/>
    <p id="hist_arch"/>
    
	<hr>
    <div id="histogram"></div>
    <div id="toolbar"></div>
    
  </article>
</div>

<%@ include file="sidebar.jspf" %>
</div>

<!-- Scripts -->
<%@ include file="scripts.jspf" %>
<script src="assets/js/histogram.js"></script>
<script src="assets/js/kb_header.js"></script>
<script>embedCharts(${param.cyclictest}, ${param.id})</script>

</body>
</html>
