package net.aqdas.server.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.aqdas.server.dao.UploadDao;
import net.aqdas.server.dao.UserDao;
import net.aqdas.server.model.User;
import net.aqdas.server.model.UserFile;
import net.aqdas.server.other.ReusableModule;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/downloadSearch")
public class DownloadSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private UserDao userDao = new UserDao(); // data access object - for the database. it's like a nice part-2 of the controller
	private ReusableModule reusableModule = new ReusableModule();
	private UploadDao uploadDao = new UploadDao();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadSearchServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// if logged in, show the page. if not logged in, redirect to main homepage.
		reusableModule.checkUserLoggedInElseLogOut(request, response, "/WEB-INF/views/downloadsearch.jsp");
		
		// get attribute for fileResults. scan through this
		// but how will you update the page? might have to be done via view
			// start off by printing the results of the fileResults attribute - see if it has even been received.
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("-- download search: received upload name for search.");
		
		// return to this page regardless.
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/WEB-INF/views/downloadsearch.jsp");
		
		// maybe this can just be done within the doGet() instead
		String fileId = request.getParameter("searchUploadName"); // can be null. errors if convering a number input to string.
		
		if (fileId != null) { 
			//int int_fileId = Integer.parseInt(fileId); // this will cause an error if null
			List<UserFile> fileResults = new ArrayList<UserFile>();
			
			// to be put into a searchDao or any related Dao. DAO: search for file
			try {
				//fileResults = uploadDao.searchFilesWithId(int_fileId); // arrayList
				fileResults = uploadDao.searchFilesWithName(fileId); // arrayList
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			
			// test print through array list
			for (UserFile uf : fileResults) {
				System.out.println("uploadId: " + uf.getUploadId() + ", Filename: " + uf.getFilename());
			}
			
			request.setAttribute("downloadSearchResults", fileResults); // set attributes for this list in the request
		}
		
		// then use requestDispatcher to send us back to this page again
		requestDispatcher.forward(request, response);
	}

}
