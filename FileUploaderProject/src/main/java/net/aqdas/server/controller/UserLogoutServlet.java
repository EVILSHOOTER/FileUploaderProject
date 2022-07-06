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
@WebServlet("/logoutPage")
public class UserLogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("logging you out via: ").append(request.getContextPath());
		
		// clear attribute
		request.setAttribute("user", null);
		request.getSession().setAttribute("user", null);
		
		//RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/mainhomepage.jsp");
		//dispatcher.forward(request, response);
		response.sendRedirect("mainhomepage.jsp");
		System.out.println("clear user sessions - logged out");
	}

}
