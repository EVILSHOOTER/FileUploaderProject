<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Upload Your File</title>
		
		<style>
			<%@include file="/WEB-INF/css/usualPageStyle.css"%>
			
			body {
				background: rgb(140,140,140);
				background: linear-gradient(270deg, rgba(140,140,140,1) 0%, rgba(255,255,255,1) 100%);
				background-attachment: fixed;			
			}
			
			/*from here on is more specific to the page*/
			
			#content #pageTitle {
				padding-top: 10px;
				text-align: center;
			}
			
			#content .sectionTitle {
				text-decoration: underline;
				text-align: left;
				font-weight: bold;
				margin: 0;
				padding-bottom: 5px;
			}
			
			#content #buttonDiv {
				display: flex;
				justify-content: center;
				align-items: center;
				text-align: center;
			}
			#content #buttonDiv #uploadButton {
				margin: 5px;
				padding: 10px 20px 10px 20px; /* up right down left */
			}
		</style>
		
	</head>
	<body>
		<%@include file="/WEB-INF/views/userheader.jsp"%> <!-- header -->
		
		<div id="content">
			<h2 id="pageTitle">Upload a new file</h2>
			<hr>
			
			<!-- jstl if this area if a fail message was found. -->
			<c:if test="${not empty param['failMessage']}">
				<p style="color: red;"> ${param['failMessage']} </p>
				<hr>
			</c:if>
			
			<form action="uploadFile" method="post" enctype="multipart/form-data">
				<!-- file browser -->
				<p class="sectionTitle">Select your file</p>
				<input type="file" name="uploadedFile" />
				<hr>
				
				<!-- description -->
				<p class="sectionTitle">Name - 50 characters maximum</p>
				<textarea maxlength="50" rows="1" cols="50" placeholder="Optional title" style="resize:none;" name="downloadTitle"></textarea>
				<p class="sectionTitle"> Description - 500 characters maximum </p>
				<textarea maxlength="500" rows="4" cols="50" placeholder="Optional description" name="description"></textarea>
				<hr>
				
				<c:if test="${sessionScope.user != null}">
					<!-- privacy -->
					<p class="sectionTitle">Choose a privacy setting </p>
					<input type="radio" id="public_radio" name="privacy" value="public" checked/>
						<label for="public_radio">Public (will appear in search results)</label>
						<br>
					<input type="radio" id="unlisted_radio" name="privacy" value="unlisted" />
						<label for="unlisted_radio">Unlisted (only accessible via URL)</label>
						<br>
					<input type="radio" id="private_radio" name="privacy" value="private" />
						<label for="private_radio">Private (only you can access)</label>
						<br>
					<hr>
					
					<!-- hash functions. "name" is the attribute name. -->
					<p class="sectionTitle">Generate a checksum (for your file) </p>
					<input type="checkbox" id="md5_checkbox" name="md5" value="1" />
						<label for="md5_checkbox">MD5</label>
						<br>
					<input type="checkbox" id="sha256_checkbox" name="sha256" value="1" />
						<label for="sha256">SHA-256</label>
						<br>
					<input type="checkbox" id="sha512_checkbox" name="sha512" value="1" />
						<label for="sha512">SHA-512</label>
						<br>
					<hr>
					<!-- virus scan option -->
					<p class="sectionTitle">Have a virus scan on the download page? </p>
					<input type="radio" id="yesVirusScan_radio" name="virusScan" value="yes" />
						<label for="yesVirusScan_radio">Yes</label>
					<input type="radio" id="noVirusScan_radio" name="virusScan" value="no" checked/>
						<label for="noVirusScan_radio">No</label>
					<hr>
				</c:if>
				
				<!-- submit button -->
				<div id="buttonDiv">
					<input id="uploadButton" type="submit" value="Upload" />
				</div>
			</form>
		</div>
		
	</body>
</html>