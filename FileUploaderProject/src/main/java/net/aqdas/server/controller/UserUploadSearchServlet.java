package net.aqdas.server.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.aqdas.server.dao.UpdateDao;
import net.aqdas.server.dao.UploadDao;
import net.aqdas.server.dao.UserDao;
import net.aqdas.server.model.User;
import net.aqdas.server.model.UserFile;
import net.aqdas.server.other.ReusableModule;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/myUploads")
public class UserUploadSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private UserDao userDao = new UserDao(); // data access object - for the database. it's like a nice part-2 of the controller
	private ReusableModule reusableModule = new ReusableModule();
	private UploadDao uploadDao = new UploadDao();
	private UpdateDao updateDao = new UpdateDao();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserUploadSearchServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// if logged in, show the page. if not logged in, redirect to main homepage.
		reusableModule.checkUserLoggedInElseLogOut(request, response, "/WEB-INF/views/useruploadsearch.jsp");
		
		// perform check for all results?
		//performSearch(request, response, "");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("-- upload manager - search page");
		
		String searchQuery = request.getParameter("searchUploadName");
		performSearch(request, response, searchQuery);
	}
	
	
	private void performSearch(HttpServletRequest request, HttpServletResponse response, String searchQuery) throws ServletException, IOException {
		// return to this page regardless.
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/WEB-INF/views/useruploadsearch.jsp");
		
		// maybe this can just be done within the doGet() instead
		//String fileId = request.getParameter("searchUploadName"); // can be null. errors if convering a number input to string.
		
		// retrieve userId from session attribute
		int userId = 0;
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user != null) { // should be true, otherwise you'd be logged out.
			userId = user.getUserId();
		}
		
		List<UserFile> fileResults = new ArrayList<UserFile>();
		
		try {
			fileResults = updateDao.searchForSpecificFilesOfUserId(searchQuery, userId);  // returns an array list
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
		// test print through array list
		for (UserFile uf : fileResults) {
			System.out.println("uploadId: " + uf.getUploadId() + ", Filename: " + uf.getFilename());
		}
		
		request.setAttribute("uploadSearchResults", fileResults);
		
		// then use requestDispatcher to send us back to this page again
		requestDispatcher.forward(request, response);
	}

}
