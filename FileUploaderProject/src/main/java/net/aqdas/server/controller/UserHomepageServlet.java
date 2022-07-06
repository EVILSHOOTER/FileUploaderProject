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

import net.aqdas.server.dao.DownloadDao;
import net.aqdas.server.dao.UserDao;
import net.aqdas.server.model.DownloadRequest;
import net.aqdas.server.model.User;
import net.aqdas.server.other.ReusableModule;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/userHomepage")
public class UserHomepageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private UserDao userDao = new UserDao(); // data access object - for the database. it's like a nice part-2 of the controller
	private ReusableModule reusableModule = new ReusableModule();
	private DownloadDao downloadDao = new DownloadDao();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserHomepageServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		int userId = 0;
		if (user != null)
			userId = user.getUserId();
		
		List<DownloadRequest> requestsFound = new ArrayList<DownloadRequest>(); // initialised.
		try {
			requestsFound = downloadDao.getDownLoadRequestsOfUser(userId);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
		request.setAttribute("downloadRequestResults", requestsFound);
		
		// if logged in, show the page. if not logged in, redirect to main homepage.
		reusableModule.checkUserLoggedInElseLogOut(request, response, "/WEB-INF/views/userhomepage.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// this will not be used for the homepage.
	}

}
