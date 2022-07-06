<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <!-- import JSTL core library -->
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Search Files</title>
		
		<style>
			<%@include file="/WEB-INF/css/usualPageStyle.css"%>
			<%@include file="/WEB-INF/css/searchTableStyle.css"%>
			
			body {
				background: rgb(255,255,255);
				background: linear-gradient(270deg, rgba(255,255,255,1) 0%, rgba(255,191,122,1) 100%);
				background-attachment: fixed;
			}
			
			.searchResults tr:hover {
				background-color: #dbdbdb;
			}
			.downloadButtonColumn {
				text-align: center;
			}
		</style>
	</head>
	<body>
		<%@include file="/WEB-INF/views/userheader.jsp"%> <!-- header -->
		
		<div id="content">
			<br>
			<div>
				<form action="downloadSearch" method="post">
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
						<th>No. of Downloads</th>
						<th>Virus scanned?</th>
						<th>Uploader</th>
						<th>Download Page</th>
					</tr>
					<!-- essentially, request.getAttribute("downloadSearchResults"), looping through the ArrayList. -->
					<c:forEach items="${requestScope.downloadSearchResults}" var="selectedFile">
					
						<!-- virus scan: Unscanned, Scanned -->
						<c:choose>
							<c:when test="${selectedFile.virusScan}">
								<c:set var="virusScanSetting" value="Scanned" />
								<c:set var="virusScanSettingStyle" value="green" />
							</c:when>
							<c:otherwise>
								<c:set var="virusScanSetting" value="Unscanned" />
								<c:set var="virusScanSettingStyle" value="red" />
							</c:otherwise>
						</c:choose>
					
						<tr>
							<td>${selectedFile.uploadId}</td>
							<td>${selectedFile.downloadTitle}</td>
							<td>${selectedFile.downloadCounter}</td>
							<td style="text-align: center;"> <span style="color: ${virusScanSettingStyle}"> ${selectedFile.virusScan} </span> </td>
							<td>${selectedFile.username}
							<c:if test="${selectedFile.verified}"> <span style="color: green">(VERIFIED)</span> </c:if>
							</td>
							
							<td class="downloadButtonColumn"> <a href="downloader?id=${selectedFile.uploadHashId}" class="downloadButton">Download</a> </td>
							<!-- ^ should be a link to page. like "downloader?uploadId=1" -->
						</tr>
					</c:forEach>
				</table>
			</div>
			<hr>
		</div>
		
		
	</body>
</html>