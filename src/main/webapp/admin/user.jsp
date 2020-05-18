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
	      <h2>User Administration</h2>
	    </div>
	  </header>
	  <div class="table">
		<div class="row">
		  <div class="1u">ID</div>
                  <div class="3u">Realname</div>
		  <div class="3u">Username</div>
		  <div class="1u">enabled</div>
		</div>
	      <c:forEach items="${userlist}" var="item">
		  <div class="row">
		    <div class="1u">${item.getId()}</div>
                    <div class="3u">${item.getRealname()}</div>
		    <div class="3u">${item.getUser()}</div>
		    <div class="1u">${item.getUserStatus()}</div>
		    <div class="2u">
		    <form action="User" method="post">
		        <input type="hidden" name="cmd" value="toggle">
		        <input type="hidden" name="j_username" value="${item.getUser()}">
		        <input type="submit" value="${item.getUserStatus()?'disable':'enable'}">
		      </form>
		    </div>
		    <div class="2u">
		      <form action="User" method="post">
		        <input type="hidden" name="cmd" value="del">
		        <input type="hidden" name="j_username" value="${item.getUser()}">
		        <input type="submit" value="Remove">
		      </form>
		    </div>
		  </div>
	      </c:forEach>
	    <form action="User" method="post">
	      <input type="hidden" name="cmd" value="new">
	      <div class="row">
              <div class="4u"><input type="text" name="j_realname"></div>
	      <div class="3u"><input type="text" name="j_username"></div>
	      <div class="3u"><input type="password" name="j_password"></div>
	      <div class="2u"><input type="submit" value="Create"></div>
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
