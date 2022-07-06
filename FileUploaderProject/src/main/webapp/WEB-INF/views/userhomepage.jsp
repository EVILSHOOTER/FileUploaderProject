<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="net.aqdas.server.model.User" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>User homepage</title>
		
		<style>
			<%@include file="/WEB-INF/css/usualPageStyle.css"%>
			<%@include file="/WEB-INF/css/searchTableStyle.css"%>
			
			body {
				background: rgb(255,255,255);
				background: linear-gradient(180deg, rgba(255,255,255,1) 0%, rgba(255,247,79,1) 100%);
				background-attachment: fixed;
			}
			
			#content h1 {
				margin:0;
			}
			
			.searchResults {
				width: 75%;
				margin-left: auto;
				margin-right: auto;
			}
			
			#content #tableTitle {
				text-decoration: underline;
				text-align: center;
				font-weight: bold;
			}
			
			
		</style>
	</head>
	<body>
		<%@include file="/WEB-INF/views/userheader.jsp"%>
		<div id="content">
			<h1>Hello ${sessionScope.user.userName},</h1>
			<hr>
			Please use any of the options at the header to perform your tasks.
			
			<hr>
			<p id=tableTitle>Recent downloads of your files</p> 
			<table class="searchResults">
				<tr>
					<th>Time</th>
					<th>Download</th>
					<th>User</th>
					<%-- <th>IP Address</th> --%>
				</tr>
				
				<c:forEach items="${requestScope.downloadRequestResults}" var="downloadRequest">
					<tr>
						<td>${downloadRequest.downloadTime}</td>
						<td>${downloadRequest.uploadName}</td>
						<td>${downloadRequest.username}</td>
						<%-- <td>${downloadRequest.ipAddress}</td> --%>
					</tr>
				</c:forEach>
			</table>
			
			<hr>
			
		</div>
		
	</body>
</html>