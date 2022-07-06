package net.aqdas.server.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import net.aqdas.server.dao.UploadDao;
import net.aqdas.server.dao.UserDao;
import net.aqdas.server.model.User;
import net.aqdas.server.model.UserFile;

/**
 * Servlet implementation class UserServlet
 */
@MultipartConfig /*( // java annotation allows servlet to handle files in chunked format (cuz it's sent in parts)
		fileSizeThreshold = 1024*1024*1, // 1mb
		maxFileSize = 1024*1024*10, 	// 10mb
		maxRequestSize = 1024*1024*100 // 100mb
)*/
@WebServlet("/uploadFile")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private UserDao userDao = new UserDao(); // data access object - for the database. it's like a nice part-2 of the controller
	private UploadDao uploadDao = new UploadDao();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// allow non-users to upload, with restrictions.
		
		// anyone can upload, users get more functionality
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/uploadfile.jsp"); 
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("-- UploadServlet: doPost()");
		
		String redirectURL = "uploadFile"; // default URL incase the upload is incorrectly done. ("uploadFile?failMessage=Message here")
		
		// first get details, save in a UserFile bean, then send to uploadDao to save into DB and storage.
		// the file itself
		Part file = request.getPart("uploadedFile"); // Part objects represents a part/form item sent via multipart/form-data requests
		
		// error message printing for: - non-users uploading 100mb+ files, - no file uploads
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		if (file.getSize() == 0) { // check if file is even uploaded.
			redirectURL += "?failMessage=No file was chosen for upload";
		} 
		else if (file.getSize() > 1048576 && user == null) { // if filesize > 1048576 (100mb) and not user, reject.
			redirectURL += "?failMessage=As a non-user, you can only upload files up to 100MB in size";
		}
		else { 
			// download name
			String filename = file.getSubmittedFileName(); // consider multiple files as part of upload?
			//System.out.println("filename: [" + filename + "]");
			
			// current datetime (upload time)
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String uploadTime = dateFormat.format(date); // ready to be inserted as a string
			
			// the userId
			//HttpSession session = request.getSession();
			//User user = (User) session.getAttribute("user");
			int userId = 0;
			if (user != null) {
				userId = user.getUserId();
				//System.out.println("zomg a user: " + userId);
			}
			
			// IP address
			String ipAddress = getIPaddress(request);
			
			// download counter (default = 0)
			int downloadCounter = 0;
			
			// private or unlisted download (should be set as parameter by user)
			String privacyParameter = request.getParameter("privacy");
			boolean privateDownload, unlistedDownload;
			privateDownload = unlistedDownload = false;
			if (privacyParameter != null) { // if this is not given.
				privateDownload = (privacyParameter.equals("private")?true:false) ;
				unlistedDownload = (privacyParameter.equals("unlisted")?true:false);
			}
			
			//System.out.println("privateDownload = " + privateDownload + ", unlistedDownload = " + unlistedDownload);
			
			// booleans for if the user wants hashes or not. parameters for true/false = null/"1", respectively
			boolean md5Parameter = request.getParameter("md5")==null?false:true; 
			boolean sha256Parameter = request.getParameter("sha256")==null?false:true;
			boolean sha512Parameter = request.getParameter("sha512")==null?false:true;
			//System.out.println("MD5, SHA256, SHA512 = " + md5Parameter + ", " + sha256Parameter + ", " + sha512Parameter);
			
			// description of download. capped to 500 characters. if more cut it off, server-wise
			String titleParameter = (String) request.getParameter("downloadTitle");
			String descParameter = (String) request.getParameter("description");
			
			String title = filename; // default
			String desc = "(none)";
			
			if (!titleParameter.isEmpty()) {
				title = titleParameter;
			}
			if (!descParameter.isEmpty()) {
				desc = descParameter;
			}
			title = title.substring( 0, Math.min(50, title.length()) ); // <=50chars
			desc = desc.substring( 0, Math.min(500, desc.length()) ); // <=500chars
			
			/*System.out.println("title = " + title + ", desc = " + desc);
			System.out.println("title chars = " + title.length() );
			System.out.println("desc chars = " + desc.length() );*/
			
			// the choice to do a virus scan
			String virusScanParameter = request.getParameter("virusScan");
			boolean virusScan = false;
			if (virusScanParameter != null) { // because this can be set to false.
				virusScan = (virusScanParameter.equals("yes")?true:false) ;
			}
			
			// inserting all values into the UserFile bean
			UserFile userFile = new UserFile();
			userFile.setFile(file);
			userFile.setFilename(filename);
			userFile.setUploadTime(uploadTime);
			userFile.setUserId(userId);
			userFile.setIpAddress(ipAddress);
			userFile.setDownloadCounter(downloadCounter);
			userFile.setPrivateDownload(privateDownload);
			userFile.setUnlistedDownload(unlistedDownload);
			userFile.setDownloadTitle(title);
			userFile.setDescription(desc);
			userFile.setHashMD5(md5Parameter);
			userFile.setHashSHA256(sha256Parameter);
			userFile.setHashSHA512(sha512Parameter);
			userFile.setVirusScan(virusScan);
			
			// send bean details to UploadDao: it saves on DB + save the file too.
			String downloadID = "";
			try {
				downloadID = uploadDao.uploadFile(userFile); 
			} catch (ClassNotFoundException | IOException e1) {
				e1.printStackTrace();
			} 
			
			// redirect user to the download page, settign the parameter to the downloadID (downloader?id=downloadID)
			redirectURL = "downloader?id=" + downloadID;
		} // end of IF statement for file check.
		
		response.sendRedirect(redirectURL);
	}
	
	private String getIPaddress(HttpServletRequest request) {
		String IP = request.getHeader("X-FORWARDED-FOR");
		if (IP == null) {
			IP = request.getRemoteAddr();
		}
		return IP;
	}
	
	

}
