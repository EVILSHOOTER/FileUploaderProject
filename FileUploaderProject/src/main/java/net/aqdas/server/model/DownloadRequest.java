package net.aqdas.server.model;

// bean used solely for transfer of download request information.
public class DownloadRequest {
	private int requestId;
	private int uploadId;
	private String uploadName;
	private int uploaderId;
	private int userId;
	private String username;
	private String ipAddress;
	private String downloadTime;
	
	// getters/setters
	public int getRequestId() {
		return requestId;
	}
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	public int getUploadId() {
		return uploadId;
	}
	public void setUploadId(int uploadId) {
		this.uploadId = uploadId;
	}
	public String getUploadName() {
		return uploadName;
	}
	public void setUploadName(String uploadName) {
		this.uploadName = uploadName;
	}
	public int getUploaderId() {
		return uploaderId;
	}
	public void setUploaderId(int uploaderId) {
		this.uploaderId = uploaderId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getDownloadTime() {
		return downloadTime;
	}
	public void setDownloadTime(String downloadTime) {
		this.downloadTime = downloadTime;
	}
	
}
