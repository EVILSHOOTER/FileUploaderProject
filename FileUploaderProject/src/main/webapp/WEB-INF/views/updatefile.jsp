<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Update Your File</title>
		
		<style>
			<%@include file="/WEB-INF/css/usualPageStyle.css"%>
			
			body {
				background: rgb(173,173,173);
				background: radial-gradient(circle, rgba(173,173,173,1) 0%, rgba(0,0,0,1) 100%);
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
				/*display: inline-block;*/
				float: left;
				
			}
			
			#content #deleteButtonDiv {
				/*display: inline-block;
				text-align: right;*/
				float: right;
			}
			
			#content #buttonDiv #uploadButton, #content #deleteButtonDiv #deleteButton  {
				margin: 5px;
				padding: 10px 20px 10px 20px; /* up right down left */
				border-radius: 7px;
			}
			#content #buttonDiv #uploadButton {
				background-color: #90EE90;
				color: black;
			}
			#content #deleteButtonDiv #deleteButton {
				background-color: #FF0000;
				color: white;
			}
		</style>
		
	</head>
	<body>
		<%@include file="/WEB-INF/views/userheader.jsp"%> <!-- header -->
		
		<%-- ${requestScope.fileDetails.downloadTitle} and ${fileDetails.downloadTitle} are identical--%>
		
		<div id="content">
			<h2 id="pageTitle"> <span style="color: grey;">Updating File:</span>  ${fileDetails.downloadTitle}</h2>
			<hr>
			
			<!-- jstl if this area if a fail message was found. -->
			<c:if test="${not empty param['failMessage']}">
				<p style="color: red;"> ${param['failMessage']} </p>
				<hr>
			</c:if>
			
			<c:if test="${fileDetails.privateDownload}"> <c:set var="privateValue" value="checked" /> </c:if>
			<c:if test="${fileDetails.unlistedDownload}"> <c:set var="unlistedValue" value="checked" /> </c:if>
			<c:if test="${!fileDetails.privateDownload && !fileDetails.unlistedDownload}"> <c:set var="publicValue" value="checked" /> </c:if>
			
			<c:if test="${fileDetails.stringMD5 != '(none)'}"> <c:set var="md5Value" value="checked" /> </c:if>
			<c:if test="${fileDetails.stringSHA256 != '(none)'}"> <c:set var="sha256Value" value="checked" /> </c:if>
			<c:if test="${fileDetails.stringSHA512 != '(none)'}"> <c:set var="sha512Value" value="checked" /> </c:if>
			
			<c:set var="virusScanYesValue" value="" /> 
			<c:set var="virusScanNoValue" value="checked" /> 
			<c:if test="${fileDetails.virusScan}"> 
				<c:set var="virusScanYesValue" value="checked" /> 
				<c:set var="virusScanNoValue" value="" /> 
			</c:if>
			
			<form action="uploadManager" method="post" enctype="multipart/form-data">
				<!-- submit the hashId to update the file data in -->
				<input type="hidden" name="uploadHashId" value="${param['id']}" />
				
				<!-- file browser -->
				<p class="sectionTitle">Select your file</p>
				<input type="file" name="uploadedFile" />
				<hr>
				
				<!-- description -->
				<p class="sectionTitle">Name - 50 characters maximum</p>
				<textarea maxlength="50" rows="1" cols="50" placeholder="Optional title" style="resize:none;" name="downloadTitle">${fileDetails.downloadTitle}</textarea>
				<p class="sectionTitle"> Description - 500 characters maximum </p>
				<textarea maxlength="500" rows="4" cols="50" placeholder="Optional description" name="description">${fileDetails.description}</textarea>
				<hr>
				
				<!-- privacy -->
				<p class="sectionTitle">Choose a privacy setting </p>
				<input type="radio" id="public_radio" name="privacy" value="public" ${publicValue} />
					<label for="public_radio">Public (will appear in search results)</label>
					<br>
				<input type="radio" id="unlisted_radio" name="privacy" value="unlisted" ${unlistedValue} />
					<label for="unlisted_radio">Unlisted (only accessible via URL)</label>
					<br>
				<input type="radio" id="private_radio" name="privacy" value="private" ${privateValue} />
					<label for="private_radio">Private (only you can access)</label>
					<br>
				<hr>
				
				<!-- hash functions. "name" is the attribute name. -->
				<p class="sectionTitle">Generate a checksum (for your file) </p>
				<input type="checkbox" id="md5_checkbox" name="md5" value="1" ${md5Value} />
					<label for="md5_checkbox">MD5</label>
					<br>
				<input type="checkbox" id="sha256_checkbox" name="sha256" value="1" ${sha256Value} />
					<label for="sha256">SHA-256</label>
					<br>
				<input type="checkbox" id="sha512_checkbox" name="sha512" value="1" ${sha512Value} />
					<label for="sha512">SHA-512</label>
					<br>
				<hr>
				<!-- virus scan option -->
				<p class="sectionTitle">Have a virus scan on the download page? </p>
				<input type="radio" id="yesVirusScan_radio" name="virusScan" value="yes" ${virusScanYesValue} />
					<label for="yesVirusScan_radio">Yes</label>
				<input type="radio" id="noVirusScan_radio" name="virusScan" value="no" ${virusScanNoValue} />
					<label for="noVirusScan_radio">No</label>
				<hr>
				
				<!-- submit button -->
				<div id="buttonDiv">
					<input id="uploadButton" type="submit" value="Update" />
				</div>
			</form>
			
			<!-- a separate form for deleting things. -->
			<form action="uploadManager" method="post">
				<input type="hidden" name="uploadHashId" value="${param['id']}" />
				<input type="hidden" id="hiddenDeleteInput" name="deleteFile" value="deletion" />
				<div id="deleteButtonDiv"><input id="deleteButton" type="submit" value="Delete" /></div>
			</form>
			
			<br><br><br>
			
		</div>
		
	</body>
</html>