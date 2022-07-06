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

public class UploadDao {
	String url = "jdbc:mysql://localhost:3306/uploaddb";
	String root_username = "root";
	String password = "password123";
	
	public String uploadFile(UserFile userFile) throws ClassNotFoundException, IOException {
		System.out.println("-- file submitted to uploadDao. doing uploadFile()");
		// file upload.
		
		String filename = userFile.getFilename();
		Part file = userFile.getFile();
		int userId = userFile.getUserId();
		String ipAddress = userFile.getIpAddress();
		String uploadTime = userFile.getUploadTime();
		int downloadCounter = userFile.getDownloadCounter();
		boolean privateDownload = userFile.isPrivateDownload();
		boolean unlistedDownload = userFile.isUnlistedDownload();
		String downloadTitle = userFile.getDownloadTitle();
		String description  = userFile.getDescription();
		//String uploadHashId; // this will be generated
		boolean doHashMD5 = userFile.isHashMD5();
		boolean doHashSHA256 = userFile.isHashSHA256();
		boolean doHashSHA512 = userFile.isHashSHA512();
		boolean virusScan = userFile.isVirusScan();
		
		// save details into DB.
		long uploadId = 0; // this is used to create a directory for the file to be saved in
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, root_username, password);
			
			String SQLquery = "INSERT INTO uploadedfiles (Filename, UserId, IpAddress, UploadTime, DownloadCounter, Private) VALUES (?,?,?,?,?,?);";
			PreparedStatement preparedStatement = con.prepareStatement(SQLquery, Statement.RETURN_GENERATED_KEYS); // added 2nd param
			
			// uploadId auto-incremented.
			preparedStatement.setString(1, filename);
			preparedStatement.setInt(2, userId);
			preparedStatement.setString(3, ipAddress);
			preparedStatement.setString(4, uploadTime);
			preparedStatement.setInt(5, downloadCounter);
			preparedStatement.setBoolean(6, privateDownload);
			
			System.out.println(preparedStatement);
			int result = preparedStatement.executeUpdate(); // returns no. of rows affected. so if 1, it worked.
			if (result > 0) {
				System.out.println("successful upload");
			} else {
				System.out.println("upload failure");
			}
			
			// do executeQuery and get the uploadId, and use that for the file saving
			ResultSet genKeys = preparedStatement.getGeneratedKeys();
			if (genKeys.next()) {
				uploadId = genKeys.getLong(1);
				System.out.println("uploadId for file is: " + uploadId);
			}
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// with the uploadId retrieved..
		String uploadDirectory = "C:/uploadedFiles/";
		// then create a folder with name as the uploadId
		new File(uploadDirectory + uploadId).mkdirs();
		// use this to save the file inside
		String uploadPath = uploadDirectory + uploadId + "/" + filename; // String uploadPath = uploadDirectory + filename;
		System.out.println("upload path: " + uploadPath);
		
		// file handling - each file is saved in a folder (named with its uploadId, so no collision)
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(uploadPath); // used to write the data down into a file
			InputStream inputStream = file.getInputStream(); // get file's data
			
			byte[] data = new byte[inputStream.available()]; // size of file
			inputStream.read(data); // read bytes and store in data variable
			fileOutputStream.write(data); // writes it into a file
			fileOutputStream.close(); // close the output stream
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// given that we now know the UploadId, we can update the row we want with new details.
		// hash.
		String checksumMD5 = "(none)";
		String checksumSHA256 = "(none)";
		String checksumSHA512 = "(none)";
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
		System.out.println("MD5 of file = " + checksumMD5 );
		System.out.println("SHA-256 of file = " + checksumSHA256 );
		System.out.println("SHA-512 of file = " + checksumSHA512 );
		
		long fileSizeBytes = new File(uploadPath).length();
		System.out.println("SIZE of file = " + fileSizeBytes + " bytes" ); // test file size
		
		// hashing the ID for use in URLs. (will be called uploadHashId)
		String uploadHashId = generateUploadHashId(Long.toString(uploadId));
		
		// perform a virus scan using Cloudmersive API.
		// if virusScan true, do this. save into table. and bean.
		String virusResults = "(none)";
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
		
		// UPDATE the rows in uploadId row of table with extra details (Unlisted -> VirusScan).
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, root_username, password);
			
			// using uploadId, we can update the recently inserted entry.
			
			String SQLquery = "UPDATE uploadedfiles SET " +
					"Unlisted = ?, " +
					"DownloadTitle = ?, " +
					"Description = ?, " +
					"UploadHashId = ?, " +
					"MD5 = ?, " +
					"SHA256 = ?, " +
					"SHA512 = ?, " +
					"VirusScan = ?, " +
					"FileSizeBytes = ?, " + 
					"VirusScanResults = ? " + // remove the last comma here.
					"WHERE UploadId = ?;";
			
			PreparedStatement preparedStatement = con.prepareStatement(SQLquery, Statement.RETURN_GENERATED_KEYS); // added 2nd param
			
			// new values after Private.
			preparedStatement.setBoolean(1, unlistedDownload);
			preparedStatement.setString(2, downloadTitle);
			preparedStatement.setString(3, description);
			preparedStatement.setString(4, uploadHashId);
			preparedStatement.setString(5, checksumMD5);
			preparedStatement.setString(6, checksumSHA256);
			preparedStatement.setString(7, checksumSHA512);
			preparedStatement.setBoolean(8, virusScan);
			preparedStatement.setLong(9, fileSizeBytes);
			preparedStatement.setString(10, virusResults);
			preparedStatement.setInt(11, Math.toIntExact(uploadId));
			
			System.out.println(preparedStatement);
			int result = preparedStatement.executeUpdate(); // returns no. of rows affected. so if 1, it worked.
			if (result > 0) {
				System.out.println("successful edit of upload");
			} else {
				System.out.println("upload edit failure");
			}
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return uploadHashId; // finally return the ID so the file can be accessed
	}
	
	// for use in creating IDs
	private String generateUploadHashId(String uploadId) {
		// get ID, concatenate a random string at end, then hash. harder to bruteforce
		String string = uploadId + "-" + Double.toString(Math.random()); // uploadId + a Math.random() double concatenated to it.
		String hash = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] msgArray = md.digest(string.getBytes());
			BigInteger bi = new BigInteger(1, msgArray);
			hash = bi.toString(16);
			while (hash.length() < 32) {
				hash = "0" + hash;
			} 
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("HASH OF uploadId: original ID = " + string + ", hash = " + hash);
		
		return hash;
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
	
	public List<UserFile> searchFilesWithName(String name) throws ClassNotFoundException, IOException {
		// this can then be reused to search by Filename instead of UploadId
		List<UserFile> filesFound = new ArrayList<UserFile>();
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection(url, root_username, password);
			
			String SQLquery = "SELECT * FROM uploadedfiles WHERE DownloadTitle LIKE ?;";
			PreparedStatement preparedStatement = con.prepareStatement(SQLquery);
			
			preparedStatement.setString(1, "%"+name+"%"); // search via uploadId. make it by name instead later
			
			System.out.println(preparedStatement);
			ResultSet rs = preparedStatement.executeQuery(); // returns resultSet
			
			while (rs.next()) {
				//if (rs.getString("Filename").equals(name)) {
				UserFile foundFile = new UserFile();
				
				// these will then be usable by the DownloadSearchServlet and (more importantly) the view's JSTL
				foundFile.setUploadId( rs.getInt("UploadId") );
				foundFile.setFilename( rs.getString("Filename") );
				foundFile.setUploadHashId( rs.getString("UploadHashId") );
				foundFile.setDownloadTitle( rs.getString("DownloadTitle") );
				foundFile.setUserId( rs.getInt("UserId") );
				foundFile.setDownloadCounter( rs.getInt("DownloadCounter") );
				foundFile.setVirusScan( rs.getBoolean("VirusScan") );
				
				// extra properties that I think that are important for the search
				// get the username and verified status because that is not part of the file itself.
				String username = "";
				boolean verified = false;
				try {
					UserDao userDao = new UserDao();
					username = userDao.returnUsername( rs.getInt("UserId") );
					verified = userDao.returnVerified( rs.getInt("UserId") );
					if (username.equals("")) {
						username = "Unknown";
					}
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
				
				foundFile.setUsername( username );
				foundFile.setVerified( verified );
				
				// if private, don't add to the list.
				if (!rs.getBoolean("Private") && !rs.getBoolean("Unlisted")) {
					filesFound.add(foundFile); // finally add the UserFile to the ArrayList.
				}
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
            
            // timeout might be the issue - doesn't seem to work either: still Bad Request.
            defaultClient.setReadTimeout(0); // 0 means no timeout. default = "10_000
            defaultClient.setWriteTimeout(0);
            defaultClient.setConnectTimeout(0);

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
