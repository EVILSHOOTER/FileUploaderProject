<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Main homepage</title>
		
		<style>
			<%@include file="/WEB-INF/css/usualPageStyle.css"%>
			
			body {
				background: rgb(59,59,59);
				background: linear-gradient(180deg, rgba(59,59,59,1) 0%, rgba(0,107,172,1) 100%);
				background-attachment: fixed;
			}
			
			#content #pageTitle {
				text-align: center;
				padding-top: 10px;
			}
			
			#content #menu_buttons {
				width: 100%;
				table-layout: fixed; /*equal width columns*/
			}
			
			#content #menu_buttons, #content #menu_buttons td {
			  /*border: 2px solid;*/
			}
			
			#content #menu_buttons th {
				text-decoration: underline;
			}
			
			#content #menu_buttons td {
				text-align: center;
			}
			
			#content .button button {
				-webkit-appearance: button;
				-moz-appearance: button;
				appearance: button;
				
				width: 100px;
				height: 50px;
				border-radius: 5px;
				border: 2px solid black;
				font-size: 20px;
				font-weight: bold;
				
				transition: transform .2s; /*for animations*/
			}
			
			#content .button button:hover {
				box-shadow: inset 0 0 100px 100px rgba(255, 255, 255, 0.5);
				transform: scale(1.1);
			}
			
			#content .button button:active {
				box-shadow: inset 0 0 100px 100px rgba(0, 0, 0, 0.25);
				transform: scale(0.9);
			}
			
			#content .button #b1 {
				background-color: green;
				color: white;
			}
			#content .button #b2 {
				background-color: yellow;
			}
			#content .button #b3 {
				background-color: gray;
				color: white;
			}
			
		</style>
	</head>
	<body>
		<%@include file="/WEB-INF/views/userheader.jsp"%> <!-- header -->
	
		<div id="content">
			<h1 id="pageTitle">File Upload/Storage App</h1>
			<table id="menu_buttons">
				<tr>
					<th>Login to an account</th>
					<th>Register an account</th>
					<th>Upload a file</th>
				</tr>
				<tr>
					<td><a href="loginPage" class="button"><button id="b1">Login</button></a></td>
					<td><a href="registerPage" class="button"><button id="b2">Register</button></a></td>
					<td><a href="uploadFile" class="button"><button id="b3">Upload</button></a></td>
				</tr>
			</table>
		</div>
	</body>
</html>