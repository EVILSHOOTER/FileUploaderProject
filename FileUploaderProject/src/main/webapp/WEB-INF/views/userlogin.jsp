<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <!-- import JSTL core library -->
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>User Login</title>
		
		<style>
			<%@include file="/WEB-INF/css/starterPageStyle.css"%>
		</style>
	</head>
	<body>
		<%
			String usernameNotice = "";
			String passwordNotice = "";
			String errorMessage = (String) request.getAttribute("errorMessage");
			
			if (errorMessage != null) {
				if (errorMessage.equals("username is invalid")) {
					usernameNotice = "Username is invalid";
				} else if (errorMessage.equals("password is invalid")) {
					passwordNotice = "Password is invalid";
				} // else, no errors.
			}
			//out.println("<br>" + errorMessage);
		%>
		
		<div class="content">
			<h2 id="page_title">Log in to an account</h2> 
			<br>
			<form action="loginPage" method="post" id="formSection"> <!-- "<percent=request.getContextPath()/UserServlet" aka. /FileUploaderProject/UserServlet -->
				<table class="form_table">
					<tr>
						<td class="column1"> User name: </td>
						<td class="column2"> <input type="text" name="userName" class="inputBox" placeholder="6-32 characters only" /> </td>
						<td class="column3"> <%=usernameNotice%> </td>
					</tr>
					<tr>
						<td class="column1"> Password: </td>
						<td class="column2"> <input type="password" name="password" class="inputBox" placeholder="6-32 characters only" /> </td>
						<td class="column3"> <%=passwordNotice%> </td>
					</tr>
				</table>
				<input type="submit" value="Log In" class="submitButton"/>
			</form>
		</div>
		
	</body>
</html>