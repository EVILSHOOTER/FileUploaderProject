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
@WebServlet("/registerPage")
public class UserRegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private UserDao userDao = new UserDao(); // data access object - for the database. it's like a nice part-2 of the controller
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserRegisterServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		// when the user goes to this servlet's URL, they are redirected to this page (the view).
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/userregister.jsp"); // userregister.jsp
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// when the form is submitted, this is received.
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/userregister.jsp"); // default.
		
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
			
			
			if (user.getUserName().length() >= 6 && user.getPassword().length() <= 32) { // if username 6-32 characters
				if (!userExists) { // if username does not exist
					if (user.getPassword().length() >= 6 && user.getPassword().length() <= 32) { // if password 6-32 characters
						userDao.registerUser(user);
						dispatcher = request.getRequestDispatcher("/WEB-INF/views/userlogin.jsp");
					} else {
						request.setAttribute("errorMessage", "password should be 6-32 chars"); // if password not 6-32 chars
					}
				} else {
					request.setAttribute("errorMessage", "username exists"); // if username exists
				}
			} else {
				request.setAttribute("errorMessage", "username should be 6-32 chars"); // if username not 6-32 chars
			}
			//System.out.println(request.getAttribute("errorMessage"));
			
			
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("user", user);
		dispatcher.forward(request, response);
	}

}
