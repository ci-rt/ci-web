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
    <%@ include file="kb_header.jspf" %>
    <div>
      <ul class="icons">
	<li>
	  <img id="b_status" width="18"/>
	<li>
	<li>
	  <h3>Buildtest</h3>
	</li>
      </ul>
      <ul class="icons">
	<li id="build" />
	<li>
	  <button type="button" id="download" onclick="downloadConfig(${param.b_id})">Get Kernel Configuration</button>
	</li>	
    <li>
	  <button type="button" id="download_script" onclick="downloadScript(${param.b_id})">Get Build Script</button>
	</li>
      </ul>
    </div>
    <div id="boot_div" style="visibility: hidden;">
      <hr>
      <ul class="icons">
	<li>
	  <img id="bt_status" width="18"/>
        </li>
	<li>
	  <h3>Boottest</h3>
	</li>
      </ul>
      <p id="boot"></p>
    </div>
    <div id="cyclic_div"  style="visibility: hidden;">
      <hr>
      <ul class="icons">
	<li>
	  <img id="cyc_status" width="18"/>
        </li>
	<li>
	<h3>Cyclictest</h3>
	</li>
      </ul>
      <p id="cyc_descr"> Click on the cyclictest run to show the
      corresponding histogram.</p>
    </div>
    <div id="cyclic_table"></div>
    
  </article>
</div>

<%@ include file="sidebar.jspf" %>
</div>

<!-- Scripts -->
<%@ include file="scripts.jspf" %>
<script src="assets/js/kb_details.js"></script>
<script src="assets/js/kb_header.js"></script>
<script>embedCharts(${param.id}, ${param.b_id}, ${param.bt_id})</script>


</body>
</html>
