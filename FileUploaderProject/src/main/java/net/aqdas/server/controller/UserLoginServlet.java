package net.aqdas.server.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.aqdas.server.dao.UserDao;
import net.aqdas.server.model.User;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/loginPage")
public class UserLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private UserDao userDao = new UserDao(); // data access object - for the database. it's like a nice part-2 of the controller
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserLoginServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		// when the user goes to this servlet's URL, they are redirected to this page (the view).
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/userlogin.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// when the form is submitted, this is received.
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/userlogin.jsp"); // default.
		
		String userName = request.getParameter("userName");
		String password = request.getParameter("password");
		// verified will be made only on database.
		
		// do a check here if the username and password are legit
		
		User user = new User();
		user.setUserName(userName);
		user.setPassword(password);
		
		try {
			boolean userExists = userDao.doesUserExist(user);
			boolean passCorrect = userDao.isPasswordCorrect(user);
			System.out.println("user exists? " + userExists + ".. password correct? " + passCorrect);
			
			if (userExists) {
				if (passCorrect) {
					// sign in
					user.setLoggedIn(true); // and this will be checked by each page.
					user.setUserId(userDao.returnUserId(user)); // useful for saving UserId data in uploads
					
					request.getSession().setAttribute("user", user); // save user into session
					dispatcher = request.getRequestDispatcher("/WEB-INF/views/userhomepage.jsp"); // maybe redirect instead?
				} else {
					request.setAttribute("errorMessage", "password is invalid");
				}
			} else {
				request.setAttribute("errorMessage", "username is invalid");
			}
			//System.out.println(request.getAttribute("errorMessage"));
			
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("user", user);
		
		if (user.isLoggedIn()) { // if logged in, redirect to the user homepage instead
			response.sendRedirect("userHomepage");
		} else {
			dispatcher.forward(request, response);
		}
		
	}

}
