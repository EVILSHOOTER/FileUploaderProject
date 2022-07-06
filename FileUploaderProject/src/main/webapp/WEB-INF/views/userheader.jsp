<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <!-- import JSTL core library -->
<!DOCTYPE html>
<html>
	<head>
		<!-- 
		<meta charset="ISO-8859-1">
		<title>User Header</title>
		 -->
		<style>
			<%@include file="/WEB-INF/css/headerNavBarStyle.css"%>
		</style>
	</head>
	<body>
		<!-- set some JSTL variables. if user, show buttons, if not, hide them. NO ELSE IFs in JSTL. -->
		<!-- previously using visibility: hidden/visible -->
		<c:set var="buttonVisibility1" scope="session" value="display: block" /> <!-- if session found -->
		<c:set var="buttonVisibility2" scope="session" value="display: none" />
		<c:if test="${sessionScope.user == null}">
			<c:set var="buttonVisibility1" scope="session" value="display: none" />
			<c:set var="buttonVisibility2" scope="session" value="display: block" />
		</c:if>
		
		<!-- user header will always appear the top of the page and will contain hyperlinks along with a logout button -->
		<div class="header">
			<p class="name">storageUPLOAD</p>
			<div class="left_buttons">
				<a href="userHomepage">Home</a>
				<a href="uploadFile">Upload File</a>
				<a href="myUploads" style="${buttonVisibility1}" >Manage My Uploads</a>
				<a href="downloadSearch" style="${buttonVisibility1}" >Search All Downloads</a>
			</div>
			<div class="right_buttons">
				<a href="logoutPage" class="logout_button" style="${buttonVisibility1}" >Log out</a>
				<a href="loginPage" class="login_button" style="${buttonVisibility2}" >Log in</a>
				<a href="registerPage" class="register_button" style="${buttonVisibility2}" >Register</a>
			</div>
		</div>
		<br>
		<br>
		
	</body>
</html>