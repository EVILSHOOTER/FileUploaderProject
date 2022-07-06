<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="net.aqdas.server.model.User" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Unviewable File</title>
		
		<style>
			h1, h2{ 
				font-family: 'Trebuchet MS', sans-serif;
				text-align: center;
			}
		</style>
		
	</head>
	<body>
		<%@include file="/WEB-INF/views/userheader.jsp"%>
		
		<h1>This file is private and cannot be viewed.</h1>
		<h2 style="color: grey">... unless you are the owner.</h2>
	</body>
</html>