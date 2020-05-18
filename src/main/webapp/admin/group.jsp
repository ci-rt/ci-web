<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html>
  <%@ include file="../head.jspf" %>

  <body>
    <!-- Wrapper -->
    <div id="wrapper">

      <%@ include file="../header.jspf" %>
      <!-- Main -->
      <div id="main">
	<article class="post">
	  <header>
	    <div class="title">
	      <h2>Group Administration</h2>
	    </div>
	  </header>
	  <div class="table">
		<div class="row">
		  <div class="1u">ID</div>
		  <div class="5u">Groupname</div>
		  <div class="3u">enabled</div>
		</div>
	      <c:forEach items="${grouplist}" var="item">
		  <div class="row">
		    <div class="1u">${item.getId()}</div>
		    <div class="5u">${item.getGroup()}</div>
		    <div class="1u">${item.getGroupStatus()}</div>
		    <div class="2u">
		    <form action="Group" method="post">
		        <input type="hidden" name="cmd" value="toggle">
		        <input type="hidden" name="j_groupname" value="${item.getGroup()}">
		        <input type="submit" value="${item.getGroupStatus()?'disable':'enable'}">
		      </form>
		    </div>
		    <div class="3u">
		      <form action="Group" method="post">
		        <input type="hidden" name="cmd" value="del">
		        <input type="hidden" name="j_groupname" value="${item.getGroup()}">
		        <input type="submit" value="Remove">
		      </form>
		    </div>
		  </div>
	      </c:forEach>
	    <form action="Group" method="post">
	      <input type="hidden" name="cmd" value="new">
	      <div class="row">
	      <div class="6u"><input type="text" name="j_groupname"></div>
	      <div class="3u">-</div>
	      <div class="3u"><input type="submit" value="Create"></div>
	      </div>
	    </form>
	  </div>
	</article>
      </div>

      <%@ include file="../sidebar.jspf" %>
    </div>

    <%@ include file="../scripts.jspf" %>

<script>embedCharts()</script>
  </body>
</html>
