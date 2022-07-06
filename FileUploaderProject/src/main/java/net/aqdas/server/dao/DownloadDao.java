package net.aqdas.server.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import net.aqdas.server.model.DownloadRequest;
import net.aqdas.server.model.User;
import net.aqdas.server.model.UserFile;

public class DownloadDao {
	String url = "jdbc:mysql://localhost:3306/uploaddb";
	String root_username = "root";
	String password = "password123";
	
	// uses hashId!
	public UserFile findFile(String id) throws ClassNotFoundException, IOException {
		System.out.println("-- ID submitted to downloadDao. doing findFile()");
		// file download - get details.
		
		UserFile foundFile = new UserFile();
		foundFile.setUploadId( 0 ); // default value to indicate that no download was recieved.
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, root_username, password);
			
			String SQLquery = "SELECT * FROM uploadedfiles WHERE UploadHashId LIKE ?";
			PreparedStatement preparedStatement = con.prepareStatement(SQLquery, Statement.RETURN_GENERATED_KEYS); 
			
			preparedStatement.setString(1, id);
			
			System.out.println(preparedStatement);
			ResultSet rs = preparedStatement.executeQuery(); // returns resultSet
			
			while (rs.next()) {
				// filling in details, in order of columns in table too
				foundFile.setUploadId( rs.getInt("UploadId") );
				foundFile.setFilename( rs.getString("Filename") );
				foundFile.setUserId( rs.getInt("UserId") );
				foundFile.setIpAddress( rs.getString("IpAddress") );
				foundFile.setUploadTime( rs.getString("UploadTime") );
				foundFile.setDownloadCounter( rs.getInt("DownloadCounter") );
				foundFile.setPrivateDownload( rs.getBoolean("Private") );
				foundFile.setUnlistedDownload( rs.getBoolean("Unlisted") );
				foundFile.setDownloadTitle( rs.getString("DownloadTitle") );
				foundFile.setDescription( rs.getString("Description") );
				foundFile.setUploadHashId( rs.getString("UploadHashId") );
				foundFile.setStringMD5( rs.getString("MD5") );
				foundFile.setStringSHA256( rs.getString("SHA256") );
				foundFile.setStringSHA512( rs.getString("SHA512") );
				foundFile.setVirusScan( rs.getBoolean("VirusScan") );
				foundFile.setFileSize( rs.getLong("FileSizeBytes") );
				foundFile.setVirusResults( rs.getString("VirusScanResults") );
			}
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return foundFile;
	}
	
	public void incrementDownloadCounter(String uploadHashId) throws ClassNotFoundException, IOException {
		System.out.println("-- incrementing download counter by 1...");
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, root_username, password);
			
			String SQLquery = "UPDATE uploadedfiles SET DownloadCounter=DownloadCounter+1 WHERE UploadHashId LIKE ?";
			PreparedStatement preparedStatement = con.prepareStatement(SQLquery, Statement.RETURN_GENERATED_KEYS); 
			
			preparedStatement.setString(1, uploadHashId);
			
			System.out.println(preparedStatement);
			int results = preparedStatement.executeUpdate();
			
			if (results > 0) {
				System.out.println("download counter incremented.");
			} else {
				System.out.println("download counter increment failed.");
			}
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void recordDownload(HttpServletRequest request, UserFile downloadDetails) throws ClassNotFoundException, IOException {
		System.out.println("-- recording the download into the download requests table");
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		// get details from the user themself.
		int userId = 0;
		String username = "Non-user";
		if (user != null) {
			userId = user.getUserId();
			username = user.getUserName();
		}
		String ipAddress = getIPaddress(request);
		String downloadTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, root_username, password);
			
			String SQLquery = "INSERT INTO downloadrequests (UploadId, UploadTitle, UploaderId, UserId, Username, IpAddress, DownloadTime) VALUE (?,?,?,?,?,?,?)";
			PreparedStatement preparedStatement = con.prepareStatement(SQLquery, Statement.RETURN_GENERATED_KEYS); 
			
			preparedStatement.setInt(1, downloadDetails.getUploadId() );
			preparedStatement.setString(2, downloadDetails.getDownloadTitle() );
			preparedStatement.setInt(3, downloadDetails.getUserId());
			preparedStatement.setInt(4, userId);
			preparedStatement.setString(5, username); // inserting username because nice to know the name at the TIME.
			preparedStatement.setString(6, ipAddress);
			preparedStatement.setString(7, downloadTime);
			
			System.out.println(preparedStatement);
			int results = preparedStatement.executeUpdate();
			
			if (results > 0) {
				System.out.println("download counter incremented.");
			} else {
				System.out.println("download counter increment failed.");
			}
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getIPaddress(HttpServletRequest request) {
		String IP = request.getHeader("X-FORWARDED-FOR");
		if (IP == null) {
			IP = request.getRemoteAddr();
		}
		return IP;
	}
	
	// this is used for showing download requests on the homepage.
	public List<DownloadRequest> getDownLoadRequestsOfUser(int userId) throws ClassNotFoundException, IOException {
		List<DownloadRequest> requestsFound = new ArrayList<DownloadRequest>();
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, root_username, password);
			
			String SQLquery = "SELECT * FROM downloadrequests WHERE UploaderId LIKE ?;";
			PreparedStatement preparedStatement = con.prepareStatement(SQLquery);
			
			preparedStatement.setInt(1, userId); // search via uploaderId. 
			
			System.out.println(preparedStatement);
			ResultSet rs = preparedStatement.executeQuery(); // returns resultSet
			
			while (rs.next()) {
				DownloadRequest foundRequest = new DownloadRequest();
				
				// these will then be usable by the UserHomepage and (more importantly) the view's JSTL
				foundRequest.setRequestId( rs.getInt("RequestId") );
				foundRequest.setUploadId( rs.getInt("UploadId") );
				foundRequest.setUploadName( rs.getString("UploadTitle") );
				foundRequest.setUploaderId( rs.getInt("UploaderId") );
				foundRequest.setUserId( rs.getInt("UserId") );
				foundRequest.setUsername( rs.getString("Username") );
				foundRequest.setIpAddress( rs.getString("IpAddress") );
				foundRequest.setDownloadTime( rs.getString("DownloadTime") );
				
				requestsFound.add(foundRequest); // finally add the request to the ArrayList.
			}
			
			// rearrange list in time order? just reverse list so newest entries appear at top
			Collections.reverse(requestsFound);
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return requestsFound;
	}
	
	
}
