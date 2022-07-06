<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Download File</title>
		
		<style>
			body {
				background: rgb(0,49,255);
				background: linear-gradient(190deg, rgba(0,49,255,1) 0%, rgba(255,255,255,1) 100%);
				background-attachment: fixed;
			}
			
			#content {
				background-color: white;
				width: 50%;
				margin-left: auto;
				margin-right: auto;
				border-radius: 5px;
				outline: none;
				box-shadow: 0 0 0 2pt #ddd;
			}

			#content { 
				font-family: 'Trebuchet MS', sans-serif;
				padding: 0px 20px;
			}
			
			#content #title * {
				margin: auto;
			}
			
			#content #title #bigtitle, #content #title #smalltitle {
				text-align: center;
			}
			
			#content #title #bigtitle {
				padding: 10px 0px 2px 0px;
				text-shadow: 2px 2px 0px lightgray;
			}
			#content #title #smalltitle {
				padding: 0px 0px 5px 0px;
				color: #9e9e9e;
			}
			
			#content #creatorANDtime #creator {
				float: left;
			}
			#content #creatorANDtime #time {
				float: right;
				text-align: right;
			}
			
			#content hr {
				clear: both;
			}
			
			#content #creatorANDtime #creator span, #content #creatorANDtime #time span {
				font-weight: bold;
			}
			
			#content #creatorANDtime #creator #verified {
				color: green;
			}
			
			#content #creatorANDtime {
				text-shadow: 1px 1px 0 #ffffff, 1px -1px 0 #ffffff, -1px 1px 0 #ffffff, -1px -1px 0 #ffffff, 1px 0px 0 #ffffff, 0px 1px 0 #ffffff, -1px 0px 0 #ffffff, 0px -1px 0 #ffffff;
			}
			
			#content #downloadArea #fileSize {
				display: block;
				margin: 2px;
				padding: 2px;
			}
			#content #downloadArea #downloadCount {
				display: block;
				margin: 2px;
				padding: 2px;
			}
			#content #downloadArea #downloadButton {
				float: right;
				margin: 5px;
				padding: 10px;
			}
			
			#content #downloadArea span {
				font-weight: bold;
			}
			
			#content #descriptionText, #content #virusScanText {
				height: 100px;
				width: 80%;
				overflow: auto;
				box-shadow: 0 0 0 2pt #ddd;
				margin-left: auto;
				margin-right: auto;
				
				white-space: pre-line;
			}
			
			#content #descriptionTitle, #content #checksumsTitle, #content #virusScanTitle {
				text-decoration: underline;
				text-align: center;
				font-weight: bold;
			}
			
			#content #checksumsTable, #checksumsTable td {
			  border: 2px solid;
			}
			
			#content #checksumsTable {
				border-collapse: collapse;
			    width: 100%;
			}
			
			#content #checksumsTable .checksumHeader {
				font-weight: bold;
				width: 75px;
			}
			
			#content #checksumsTable .checksumHash {
				overflow-wrap: anywhere;
			}
		</style>
	</head>
	<body>
		<%@include file="/WEB-INF/views/userheader.jsp"%> <!-- header -->
		
		<c:set var="fileDetails" scope="request" value="${requestScope.downloadDetails}"/>
		
		<script>
			// a little script that when you click the span, it scrolls through the units of size
			var type = 0; // 0 = bytes, 1=kb, 2=mb, 3=gb
			var sizeBytes = ${fileDetails.fileSize};
			function changeUnit() {
				type = (++type)%4
				
				var newSize = sizeBytes / 1024**type;
				var newUnit = type==0?"bytes": type==1?"KB": type==2?"MB": type==3?"GB":"";
				
				console.log("File Size = " + newSize + " " + newUnit);
				document.getElementById('fileSize_span').innerHTML = (newSize).toFixed(2) + " " + newUnit;
			}
		</script>
		
		<div id="content">
			<div id="title_background">
		      <div id="title">
		        <h1 id="bigtitle">${fileDetails.downloadTitle}</h1>
		        <h3 id="smalltitle">${fileDetails.filename}</h3>
		        <div id="creatorANDtime">
		          <p id="creator">by <span>${fileDetails.username}</span> 
		          	<c:if test="${fileDetails.verified}"> <!-- check for verification -->
		          		<span id="verified">(VERIFIED)</span> 
		          	</c:if>
		          <p>
		          <p id="time">on <span>${fileDetails.uploadTime}</span><p>
		        </div>
		        <hr>
		      </div>
		    </div>
		    <div id="downloadArea">
		    	<form>
		    		<input type="hidden" name="id" value="${fileDetails.uploadHashId}">
		    		<input type="hidden" name="download" value="true">
		    		<input id="downloadButton" type="submit" value="Download">
		    	</form>
		    	
		    	<p id="fileSize">File size: <span id="fileSize_span" onclick="changeUnit()" style="cursor: pointer">${fileDetails.fileSize} bytes</span> </p>
		    	<p id="downloadCount">Downloads: <span>${fileDetails.downloadCounter}</span> </p>
		    	<hr>
		    </div>
		    
		    <div id="descriptionArea">
		    	<p id=descriptionTitle>Description</p> 
		    	<div id="descriptionText">${fileDetails.description}</div>
		    	<hr>
		    </div>
		    <div id="checksumsArea">
		    	<p id="checksumsTitle">Checksums<p>
		    	<table id="checksumsTable">
		    		<tr>
		    			<td class="checksumHeader">MD5</td>
		    			<td class="checksumHash">${fileDetails.stringMD5}</td>
		    		</tr>
		    		<tr>
		    			<td class="checksumHeader">SHA-256</td>
		    			<td class="checksumHash">${fileDetails.stringSHA256}</td>
		    		</tr>
                    <tr>
		    			<td class="checksumHeader">SHA-512</td>
		    			<td class="checksumHash">${fileDetails.stringSHA512}</td>
		    		</tr>
		    	</table>
		    	<hr>
		    </div>
		    <c:if test="${fileDetails.virusScan}">
			    <div id="virusScanArea">
			    	<p id="virusScanTitle">Virus Scan (via Cloudmersive)<p>
		    		<pre id="virusScanText">${fileDetails.virusResults}</pre>
			    	<hr>
			    </div>
		    </c:if>
		</div> 
		
		
	</body>
</html>