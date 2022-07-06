package net.aqdas.server.dao;

import java.io.ByteArrayInputStream;
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
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.cloudmersive.client.*;
import com.cloudmersive.client.model.*;

import net.aqdas.server.model.User;
import net.aqdas.server.model.UserFile;

import com.cloudmersive.client.invoker.*;
import com.cloudmersive.client.invoker.auth.*;

public class UpdateDao {
	String url = "jdbc:mysql://localhost:3306/uploaddb";
	String root_username = "root";
	String password = "password123";
	
	public void deleteFile(String uploadHashId, int userId) throws ClassNotFoundException, IOException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, root_username, password);
			String SQLquery = "DELETE FROM uploadedfiles WHERE UploadHashId LIKE ? AND UserId=?;";
			PreparedStatement preparedStatement = con.prepareStatement(SQLquery, Statement.RETURN_GENERATED_KEYS); // added 2nd param
			
			preparedStatement.setString(1, uploadHashId);
			preparedStatement.setInt(2, userId);
			
			System.out.println(preparedStatement);
			int results = preparedStatement.executeUpdate(); 
			if (results > 0) 
				System.out.println("file delete - success");
			else
				System.out.println("file delete - failure");
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// could easily add functionality to remove files from server, but for sake of preservation, leave it here.
	}
	
	
	public void updateFile(UserFile userFile) throws ClassNotFoundException, IOException {
		System.out.println("-- file submitted to updateDao. updating file with updateFile()");
		// file update.
		
		String filename = userFile.getFilename();
		Part file = userFile.getFile();
		int userId = userFile.getUserId();
		String ipAddress = userFile.getIpAddress();
		//String uploadTime = userFile.getUploadTime();
		//int downloadCounter = userFile.getDownloadCounter();
		boolean privateDownload = userFile.isPrivateDownload();
		boolean unlistedDownload = userFile.isUnlistedDownload();
		String downloadTitle = userFile.getDownloadTitle();
		String description  = userFile.getDescription();
		String uploadHashId = userFile.getUploadHashId();
		boolean doHashMD5 = userFile.isHashMD5();
		boolean doHashSHA256 = userFile.isHashSHA256();
		boolean doHashSHA512 = userFile.isHashSHA512();
		boolean virusScan = userFile.isVirusScan();
		long fileSize = userFile.getFileSize(); 
		
		String checksumMD5 = "(none)";
		String checksumSHA256 = "(none)";
		String checksumSHA512 = "(none)";
		String virusResults = "(none)";
		
		// use uploadHashId to get UploadId - to find file directory.
		int uploadId = 0;
		String oldFilename = "";
		long oldFileSize = 0;
		String oldVirusResults = "";
		int uploadUserId = 0;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, root_username, password);
			String SQLquery = "SELECT * FROM uploadedfiles WHERE uploadHashId = ?";
			PreparedStatement preparedStatement = con.prepareStatement(SQLquery, Statement.RETURN_GENERATED_KEYS); // added 2nd param
			preparedStatement.setString(1, uploadHashId);
			System.out.println(preparedStatement);
			ResultSet rs = preparedStatement.executeQuery(); 
			while (rs.next()) {
				uploadId = rs.getInt("UploadId");
				oldFilename = rs.getString("Filename"); // reenter the same file details too.
				oldFileSize = rs.getLong("FileSizeBytes");
				oldVirusResults = rs.getString("VirusScanResults");
				uploadUserId = rs.getInt("UserId");
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (file == null) {
			// if not uploading new file/leaving old file as is, overwrite with same details that were just retrieved.
			filename = oldFilename;
			fileSize = oldFileSize;
		}
		
		// this is important. do a check here to see if the user doing this request actually owns the file. if not, don't do it.
		if (userId == uploadUserId) {
			// use uploadId to find file location, and overwrite the file
			String uploadDirectory = "C:/uploadedFiles/";
			String uploadPath = uploadDirectory + uploadId + "/" + filename; // you can choose to clear this folder if u want
			System.out.println("update path: " + uploadPath);
						
			if (file != null) { // if user updating with a new file, overwrite the old one.
				try {
					FileOutputStream fileOutputStream = new FileOutputStream(uploadPath);
					InputStream inputStream = file.getInputStream(); 
					
					byte[] data = new byte[inputStream.available()]; 
					inputStream.read(data); 
					fileOutputStream.write(data); 
					fileOutputStream.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
			
			// re-do checksums and virus scan (e.g. in case the API is updated later)
			try {
				//String checksum = generateChecksum("MD5", new File(uploadPath));
				if (doHashMD5)
					checksumMD5 = generateChecksum("MD5", new File(uploadPath));
				if (doHashSHA256)
					checksumSHA256 = generateChecksum("SHA-256", new File(uploadPath));
				if (doHashSHA512)
					checksumSHA512 = generateChecksum("SHA-512", new File(uploadPath));
			} catch (NoSuchAlgorithmException | IOException e) {
				e.printStackTrace();
			}
			if (virusScan) {
				VirusScanResult VRS = returnVirusScan(uploadPath); // can return null if bad request.
				if (VRS != null) {
					virusResults = VRS.toString();
					System.out.println("virus scan results = " + VRS);
					System.out.println("TO STRING THO = " + virusResults);
					
					if (VRS.isCleanResult()) {
						virusResults = "The file is clean and contains no malicious content.";
					} else {
						virusResults = "The file has been found to be malicious. \n Here are the list of viruses found: \n";
						for (VirusFound virus : VRS.getFoundViruses()) {
							virusResults += "- " + virus.getVirusName() + "\n";
						}
					}
				} else { // e.g. a bad request.
					virusResults = "The virus scan was unable to be completed - potentially due to a Bad Request.";
				}
			}
			
			// do full SQL statement of updating the table. use uploadHashId for this.
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection con = DriverManager.getConnection(url, root_username, password);
				
				String SQLquery = "UPDATE uploadedfiles SET " +
						"Filename = ?," +
						"IpAddress = ?," +
						"Private = ?," +
						"Unlisted = ?, " +
						"DownloadTitle = ?, " +
						"Description = ?, " +
						"MD5 = ?, " +
						"SHA256 = ?, " +
						"SHA512 = ?, " +
						"VirusScan = ?, " +
						"FileSizeBytes = ?, " + 
						"VirusScanResults = ? " + // remove the last comma here.
						"WHERE UploadHashId = ?;";
				
				PreparedStatement preparedStatement = con.prepareStatement(SQLquery, Statement.RETURN_GENERATED_KEYS); // added 2nd param
				
				preparedStatement.setString(1, filename);
				preparedStatement.setString(2, ipAddress);
				preparedStatement.setBoolean(3, privateDownload);
				preparedStatement.setBoolean(4, unlistedDownload);
				preparedStatement.setString(5, downloadTitle);
				preparedStatement.setString(6, description);
				preparedStatement.setString(7, checksumMD5);
				preparedStatement.setString(8, checksumSHA256);
				preparedStatement.setString(9, checksumSHA512);
				preparedStatement.setBoolean(10, virusScan);
				preparedStatement.setLong(11, fileSize);
				preparedStatement.setString(12, virusResults);
				
				preparedStatement.setString(13, uploadHashId);
				
				System.out.println(preparedStatement);
				int results = preparedStatement.executeUpdate(); 
				if (results > 0)
					System.out.println("update - success");
				else 
					System.out.println("update - failure");
				
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	// for use in generating checksums for downloads
	private String generateChecksum(String hashingAlgo, File file) throws IOException, NoSuchAlgorithmException {
		MessageDigest msgDigest = MessageDigest.getInstance(hashingAlgo);
		// algorithms for use (just enter in quotation marks above ^: MD5, SHA-256, SHA-512
		
		FileInputStream fileInputStream = new FileInputStream(file); // get bytes of file
		byte[] byteArray = new byte[1024]; // to store data chunks
		int byteCount = 0; // 
		while ((byteCount = fileInputStream.read(byteArray)) != -1) {
			msgDigest.update(byteArray, 0, byteCount);
		}
		fileInputStream.close();
		
		byte[] bytes = msgDigest.digest();
		StringBuilder stringBuilder = new StringBuilder();
		for (int i=0; i < bytes.length; i++) {
			stringBuilder.append( Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1) );
		}
		
		return stringBuilder.toString();
	}
	
	public List<UserFile> searchForSpecificFilesOfUserId(String downloadName, int userId) throws ClassNotFoundException, IOException {
		// this can then be reused to search by Filename instead of UploadId
		List<UserFile> filesFound = new ArrayList<UserFile>();
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, root_username, password);
			
			String SQLquery = "SELECT * FROM uploadedfiles WHERE DownloadTitle LIKE ? AND UserId LIKE ?;";
			PreparedStatement preparedStatement = con.prepareStatement(SQLquery);
			
			preparedStatement.setString(1, "%"+downloadName+"%");
			preparedStatement.setInt(2, userId);
			
			System.out.println(preparedStatement);
			ResultSet rs = preparedStatement.executeQuery(); // returns resultSet
			
			while (rs.next()) {
				//if (rs.getString("Filename").equals(name)) {
				UserFile foundFile = new UserFile();
				
				// these will then be usable by the UploadSearchServlet and (more importantly) the view's JSTL
				foundFile.setUploadId( rs.getInt("UploadId") );
				foundFile.setFilename( rs.getString("Filename") );
				foundFile.setUploadHashId( rs.getString("UploadHashId") );
				foundFile.setDownloadTitle( rs.getString("DownloadTitle") );
				foundFile.setUserId( rs.getInt("UserId") );
				foundFile.setDownloadCounter( rs.getInt("DownloadCounter") );
				foundFile.setFilename( rs.getString("Filename") );
				foundFile.setPrivateDownload( rs.getBoolean("Private") );
				foundFile.setUnlistedDownload( rs.getBoolean("Unlisted") );
				//System.out.println("private = " + rs.getBoolean("Private") + ", unlisted = " + rs.getBoolean("Unlisted") );
				System.out.println("private = " + foundFile.isPrivateDownload() + ", unlisted = " + foundFile.isUnlistedDownload() );
				
				// not bothering getting the username and verified status, as you ARE the user!
				
				// since you are the creator, you see the private and unlisted files.
				filesFound.add(foundFile); // finally add the UserFile to the ArrayList.
				//}
			}
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return filesFound;
	}
	
	private VirusScanResult returnVirusScan(String fileLocation) {
		VirusScanResult result = null;
		try
        {
            ApiClient defaultClient = Configuration.getDefaultApiClient();

            // Configure API key authorization: Apikey
            ApiKeyAuth Apikey = (ApiKeyAuth) defaultClient.getAuthentication("Apikey");
            Apikey.setApiKey("e8866d9a-b176-4656-a655-b23a9916ccee"); // my API key. 800 calls/month
            
            ScanApi apiInstance = new ScanApi();
            File inputFile = new File(fileLocation); // File | Input file to perform the operation on.
            
            //ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[100]);
            
            try 
            {
                //VirusScanResult result = apiInstance.scanFile(inputStream); // method for inputStream variant doesnt exist
            	result = apiInstance.scanFile(inputFile);
            	System.out.println(result);
            	
            	//File inputFile, Boolean allowExecutables, Boolean allowInvalidFiles, Boolean allowScripts, Boolean allowPasswordProtectedFiles, String restrictFileTypes
            	//VirusScanAdvancedResult advancedResult = apiInstance.scanFileAdvanced(inputFile, true, true, true, true, "");
                //System.out.println(advancedResult);
            } catch (ApiException e) {
                System.err.println("Exception when calling ScanApi#scanFile");
                e.printStackTrace();
            }
        }
        catch (Exception e)
        {
            System.out.println("Error:" + e.toString() + e.getMessage() );
        }
		
		return result; // for now
	}
	
}
