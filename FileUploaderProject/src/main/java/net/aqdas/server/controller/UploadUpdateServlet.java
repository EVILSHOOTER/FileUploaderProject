package net.aqdas.server.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import net.aqdas.server.dao.DownloadDao;
import net.aqdas.server.dao.UpdateDao;
import net.aqdas.server.dao.UserDao;
import net.aqdas.server.model.User;
import net.aqdas.server.model.UserFile;
import net.aqdas.server.other.ReusableModule;

/**
 * Servlet implementation class UserServlet
 */
@MultipartConfig
@WebServlet("/uploadManager")
public class UploadUpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private UserDao userDao = new UserDao(); // data access object - for the database. it's like a nice part-2 of the controller
	private ReusableModule reusableModule = new ReusableModule();
	private DownloadDao downloadDao = new DownloadDao();
	private UpdateDao updateDao = new UpdateDao();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// firstly, check if the file is yours.
		// get user attribute
		// if user.userId == file.
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		String uploadHashId = request.getParameter("id");
		UserFile fileDetails = null;
		try {
			fileDetails = downloadDao.findFile(uploadHashId);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (user != null && fileDetails != null) {
			if (user.getUserId() == fileDetails.getUserId()) {
				// you are the uploader. fill the details in the site.
				request.setAttribute("fileDetails", fileDetails);
				reusableModule.checkUserLoggedInElseLogOut(request, response, "/WEB-INF/views/updatefile.jsp");
			} else {
				// you are not the uploader. go back to the upload search page.
				response.sendRedirect("myUploads");
			}
		} else {
			response.sendRedirect("myUploads");
		}
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// send details to updateDao to save the details.
		// save the details - BUT check file: if no file, don't bother writing a new one. if file, do so.
		
		String redirectURL = "myUploads";
		
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		// userId
		int userId = 0;
		if (user != null) {
			userId = user.getUserId();
		}
		// uploadHashId
		String uploadHashId = request.getParameter("uploadHashId"); // submitted as a hidden input in the form
		
		// if delete option
		String deletion = request.getParameter("deleteFile");
		if (deletion != null) {
			// delete. SQL statement is like DELETE FROM uploadedfiles WHERE UploadHashId LIKE ? AND UserId=?;
			try {
				updateDao.deleteFile(uploadHashId, userId);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		} else {
			// it's a file upload.
			
			// file.
			Part file = request.getPart("uploadedFile");
			String filename = file.getSubmittedFileName(); // will be empty if no file.
			long fileSize = file.getSize();
			if (fileSize == 0) { // if no file, just update without details.
				file = null;
			}
			
			// upload time ignored - retained as the original.
			// download counter ignored.
			
			// IP address
			String ipAddress = getIPaddress(request);
			
			// private or unlisted download (should be set as parameter by user)
			String privacyParameter = request.getParameter("privacy");
			boolean privateDownload, unlistedDownload;
			privateDownload = unlistedDownload = false;
			if (privacyParameter != null) { // if this is not given.
				privateDownload = (privacyParameter.equals("private")?true:false) ;
				unlistedDownload = (privacyParameter.equals("unlisted")?true:false);
			}
			
			// hash parameters.
			boolean md5Parameter = request.getParameter("md5")==null?false:true; 
			boolean sha256Parameter = request.getParameter("sha256")==null?false:true;
			boolean sha512Parameter = request.getParameter("sha512")==null?false:true;
			
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
			//userFile.setUploadTime(uploadTime);
			userFile.setUserId(userId);
			userFile.setIpAddress(ipAddress);
			//userFile.setDownloadCounter(downloadCounter);
			userFile.setPrivateDownload(privateDownload);
			userFile.setUnlistedDownload(unlistedDownload);
			userFile.setDownloadTitle(title);
			userFile.setDescription(desc);
			userFile.setHashMD5(md5Parameter);
			userFile.setHashSHA256(sha256Parameter);
			userFile.setHashSHA512(sha512Parameter);
			userFile.setVirusScan(virusScan);
			
			userFile.setUploadHashId(uploadHashId);
			userFile.setFileSize(fileSize);
			
			try {
				updateDao.updateFile(userFile); 
			} catch (ClassNotFoundException | IOException e1) {
				e1.printStackTrace();
			} 
			
		}
		
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
