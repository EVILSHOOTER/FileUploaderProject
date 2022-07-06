package net.aqdas.server.model;

import javax.servlet.http.Part;

// bean/object model
public class UserFile {
	private int uploadId;
	private String uploadHashId;
	
	// file details
	private Part file;
	private String filename;
	
	// auto-generated details
	private int userId;
	private String ipAddress;
	private String uploadTime;
	private int downloadCounter;
	
	// a little extra for the page.
	private String username;
	private boolean verified;
	private long fileSize;
	
	// user-specified details
	private String downloadTitle;
	private String description;
	
	// privacy settings
	private boolean privateDownload;
	private boolean unlistedDownload;
	
	// hash algorithms
	private boolean hashMD5;
	private boolean hashSHA256;
	private boolean hashSHA512;
	
	private String stringMD5;
	private String stringSHA256;
	private String stringSHA512;
	
	// virus scan option
	private boolean virusScan;
	private String virusResults;
	
	// getters and setters
	public int getUploadId() {
		return uploadId;
	}
	public void setUploadId(int uploadId) {
		this.uploadId = uploadId;
	}
	
	// get/set for the uploadHashId
	public String getUploadHashId() {
		return uploadHashId;
	}
	public void setUploadHashId(String uploadHashId) {
		this.uploadHashId = uploadHashId;
	}
	
	public Part getFile() {
		return file;
	}
	public void setFile(Part file) {
		this.file = file;
	}
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}
	public int getDownloadCounter() {
		return downloadCounter;
	}
	public void setDownloadCounter(int downloadCounter) {
		this.downloadCounter = downloadCounter;
	}
	
	// a little extra for the download page
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	
	// user-specified get/setters
	public String getDownloadTitle() {
		return downloadTitle;
	}
	public void setDownloadTitle(String downloadTitle) {
		this.downloadTitle = downloadTitle;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	// private / unlisted
	public boolean isPrivateDownload() {
		return privateDownload;
	}
	public void setPrivateDownload(boolean privateDownload) {
		this.privateDownload = privateDownload;
	}
	public boolean isUnlistedDownload() {
		return unlistedDownload;
	}
	public void setUnlistedDownload(boolean unlistedDownload) {
		this.unlistedDownload = unlistedDownload;
	}
	
	// hash options
	public boolean isHashMD5() {
		return hashMD5;
	}
	public void setHashMD5(boolean hashMD5) {
		this.hashMD5 = hashMD5;
	}
	public boolean isHashSHA256() {
		return hashSHA256;
	}
	public void setHashSHA256(boolean hashSHA256) {
		this.hashSHA256 = hashSHA256;
	}
	public boolean isHashSHA512() {
		return hashSHA512;
	}
	public void setHashSHA512(boolean hashSHA512) {
		this.hashSHA512 = hashSHA512;
	}
	
	public String getStringMD5() {
		return stringMD5;
	}
	public void setStringMD5(String stringMD5) {
		this.stringMD5 = stringMD5;
	}
	public String getStringSHA256() {
		return stringSHA256;
	}
	public void setStringSHA256(String stringSHA256) {
		this.stringSHA256 = stringSHA256;
	}
	public String getStringSHA512() {
		return stringSHA512;
	}
	public void setStringSHA512(String stringSHA512) {
		this.stringSHA512 = stringSHA512;
	}
	
	// virus scan option - get/setter
	public boolean isVirusScan() {
		return virusScan;
	}
	public void setVirusScan(boolean virusScan) {
		this.virusScan = virusScan;
	}
	public String getVirusResults() {
		return virusResults;
	}
	public void setVirusResults(String virusResults) {
		this.virusResults = virusResults;
	}
}
