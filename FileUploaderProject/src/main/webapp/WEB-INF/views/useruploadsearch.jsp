<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <!-- import JSTL core library -->
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Manage My Files</title>
		
		<style>
			<%@include file="/WEB-INF/css/usualPageStyle.css"%>
			<%@include file="/WEB-INF/css/searchTableStyle.css"%>
			
			body {
				background: rgb(132,205,255);
				background: radial-gradient(circle, rgba(132,205,255,1) 0%, rgba(37,129,255,1) 100%);
				background-attachment: fixed;
			}
			
			
			.downloadButtonColumn, .manageButtonColumn {
				text-align: center;
			}
			#privacyColumn {
				text-align: center;
			}
		</style>
	</head>
	<body>
		<%@include file="/WEB-INF/views/userheader.jsp"%> <!-- header -->
		
		<div id="content">
			<br>
			<div>
				<form action="myUploads" method="post">
					Search for your file:
					<input type="text" name="searchUploadName" placeholder="*leave blank to search all files...*" style="width:200px;" />
					<input type="submit" value="Show Available Files" />
				</form>
			</div>
			<br>
			<div>
				<table class="searchResults">
					<tr>
						<th>File ID</th>
						<th>File</th>
						<th>Filename</th>
						<th>Privacy</th>
						<th>No. of Downloads</th>
						<th>Download</th>
						<th>Manage</th>
					</tr>
					
					<c:forEach items="${requestScope.uploadSearchResults}" var="selectedFile">
						<!-- JSTL, variable for pub/unl/priv -->
						<c:choose>
							<c:when test="${selectedFile.unlistedDownload}">
								<c:set var="privacySetting" value="Unlisted" />
							</c:when>
							<c:when test="${selectedFile.privateDownload}">
								<c:set var="privacySetting" value="Private" />
							</c:when>
							<c:otherwise>
								<c:set var="privacySetting" value="Public" />
							</c:otherwise>
						</c:choose>
						<tr>
							<td>${selectedFile.uploadId}</td>
							<td>${selectedFile.downloadTitle}</td>
							<td>${selectedFile.filename}</td>
							<td id="privacyColumn">${privacySetting}</td>
							<td>${selectedFile.downloadCounter}</td>
							<td class="downloadButtonColumn"> <a href="downloader?id=${selectedFile.uploadHashId}" class="downloadButton">Download</a> </td>
							<td class="manageButtonColumn"> <a href="uploadManager?id=${selectedFile.uploadHashId}" class="manageButton">Manage</a> </td>
						</tr>
					</c:forEach>
				</table>
			</div>
			<hr>
		</div>
		
		
	</body>
</html>