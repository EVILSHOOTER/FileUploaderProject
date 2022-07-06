package net.aqdas.server.other;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.aqdas.server.model.User;

public class ReusableModule {
	
	// this function is used in a lot of pages so has been placed here.
	// if not logged in, redirect to main homepage.
	public boolean checkUserLoggedInElseLogOut(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		
		boolean userLoggedIn = false; // new, nice to check.
		
		if (user != null) {
			System.out.println("user logged in - stay on page");
			userLoggedIn = true;
			RequestDispatcher dispatcher = request.getRequestDispatcher(page); 
			dispatcher.forward(request, response);
		} else {
			System.out.println("user logged out - leave page");
			response.sendRedirect("logoutPage");
		}
		
		return userLoggedIn;
	}

}
