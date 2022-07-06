package net.aqdas.server.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.aqdas.server.dao.DownloadDao;
import net.aqdas.server.dao.UserDao;
import net.aqdas.server.model.User;
import net.aqdas.server.model.UserFile;
import net.aqdas.server.other.ReusableModule;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/downloader")
public class DownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private UserDao userDao = new UserDao(); // data access object - for the database. it's like a nice part-2 of the controller
	private ReusableModule reusableModule = new ReusableModule();
	private DownloadDao downloadDao = new DownloadDao();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/downloadfile.jsp"); 
		
		String downloadId = request.getParameter("id"); // check parameters for ID
		
		UserFile downloadDetails = new UserFile(); // initialised, at least.
		try {
			downloadDetails = downloadDao.findFile(downloadId);
			//System.out.println("download title= " + download.getDownloadTitle());
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
		// find the username and insert it into downloadDetails (incase the username is changed)
		int userId = downloadDetails.getUserId();
		String username = "";
		boolean verified = false;
		try {
			username = userDao.returnUsername(userId);
			verified = userDao.returnVerified(userId);
			if (username.equals("")) {
				username = "Unknown";
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		downloadDetails.setUsername(username);
		downloadDetails.setVerified(verified);
		
		// check if download is private and user attribute's username == download's.
				
		boolean UserNotAllowedFile = false;
		if (downloadDetails.isPrivateDownload()) {
			HttpSession session = request.getSession();
			User user = (User) session.getAttribute("user");
			/*
			if (user != null) {
				// if user logged in, check if it's the same user.
				if (!username.equals( user.getUserName() )) {
					dispatcher = request.getRequestDispatcher("/WEB-INF/views/privatefilepage.jsp"); 
				}
			} else {
				dispatcher = request.getRequestDispatcher("/WEB-INF/views/privatefilepage.jsp"); 
			}*/
			
			if (user == null || (user != null && !username.equals(user.getUserName())) ) {
				dispatcher = request.getRequestDispatcher("/WEB-INF/views/privatefilepage.jsp");
				UserNotAllowedFile = true;
			}
		}
		
		request.setAttribute("downloadDetails", downloadDetails); // for display on page
		
		// perform download. check if private too.
		String downloadParameter = request.getParameter("download");
		if (downloadParameter != null && !UserNotAllowedFile) { // if download request, and user allowed
			downloadFile(request, response, downloadDetails); // since this contains a getWriter, it's one or the other.
			
			// increment the download counter by 1.
			try {
				downloadDao.incrementDownloadCounter( downloadDetails.getUploadHashId() );
				downloadDao.recordDownload(request, downloadDetails);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		} else {
			dispatcher.forward(request, response);
		}
		
		// dispatcher forward was here initially.
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
	
	private void downloadFile(HttpServletRequest request, HttpServletResponse response, UserFile downloadDetails) throws ServletException, IOException {
		String uploadPath = "C:/uploadedFiles/";
		int uploadId = downloadDetails.getUploadId();
		String filename = downloadDetails.getFilename();
        String filePath = uploadPath + uploadId + "/" + filename;
        
        File downloadFile = new File(filePath);
        FileInputStream inStream = new FileInputStream(downloadFile);
        
		// gets MIME type of file
        ServletContext context = getServletContext(); // servlet context used to get MIME type
		String mimeType = context.getMimeType(filePath);
		if (mimeType == null) {        
			// set to binary type if MIME mapping not found
			mimeType = "application/octet-stream";
		}
		System.out.println("MIME type: " + mimeType);
		 
		// use MIME type to modify response
		response.setContentType(mimeType);
		response.setContentLength((int) downloadFile.length());
		 
		// download
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
		response.setHeader(headerKey, headerValue);
		 
		// obtains response's output stream
		OutputStream outStream = response.getOutputStream();
		 
		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		while ((bytesRead = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, bytesRead);
		}
		 
		inStream.close();
		outStream.close(); 
	}
	
}
