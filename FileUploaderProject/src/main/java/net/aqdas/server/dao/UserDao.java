package net.aqdas.server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.aqdas.server.model.User;

// this is used to connect to the DB and store the data from there into the bean
public class UserDao {
	String url = "jdbc:mysql://localhost:3306/uploaddb";
	String root_username = "root";
	String password = "password123";
	
	public int registerUser(User user) throws ClassNotFoundException, SQLException {
		String SQLquery = "INSERT INTO users (Username, Password) VALUES (?,?);";
		// "(UserId, Username, Password, Verified)" // ignore userId as it's A_I
		
		int result = 0;
		
		//Class.forName("com.mysql.jdbc.Driver"); // older version which doesn't work in my case
		Class.forName("com.mysql.cj.jdbc.Driver");
		
		// Connection connection = DriverManager.getConnection("","root","password123")
		try {
			Connection con = DriverManager.getConnection(url, root_username, password);
			
			PreparedStatement preparedStatement = con.prepareStatement(SQLquery); // this helps prevent SQL injection
			
			// ignoring userId as that's auto-incremented
			preparedStatement.setString(1, user.getUserName());
			preparedStatement.setString(2, user.getPassword());
			//preparedStatement.setBoolean(3, user.isVerified());
			
			System.out.println(preparedStatement);
			result = preparedStatement.executeUpdate();
			
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// the connection needs to be closed (con.close()) - java's garbage collector can do it for you but that's poor practice
		
		return result;
	}
	
	public boolean doesUserExist(User user) throws ClassNotFoundException, SQLException {
		boolean result = false; // true = exists
		
		String SQLquery = "SELECT * FROM users WHERE Username LIKE ?;";
		Class.forName("com.mysql.cj.jdbc.Driver");
		try {
			Connection con = DriverManager.getConnection(url, root_username, password);
			PreparedStatement preparedStatement = con.prepareStatement(SQLquery);
			
			preparedStatement.setString(1, user.getUserName());
			
			System.out.println(preparedStatement);
			ResultSet rs = preparedStatement.executeQuery();
			System.out.println("reading SQL ResultSet:");
			String foundUsername = "";
			while (rs.next()) { // going through each row of results. so 2 same usernames will just print two of them. 
				foundUsername = rs.getString("Username");
				System.out.println(foundUsername);
			}
			if (!foundUsername.equals("")) {result = true;}
			
			// also perform a check for invalid characters/names to prevent confusions
			String[] invalidStrings = {"unknown", "non-user"};
			for (String substring : invalidStrings) {
				if (user.getUserName().toLowerCase().contains(substring)) {
					result = true;
				}
			}
			
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean isPasswordCorrect(User user) throws ClassNotFoundException, SQLException {
		boolean result = false; // true = exists
		
		String SQLquery = "SELECT * FROM users WHERE Username LIKE ?;";
		Class.forName("com.mysql.cj.jdbc.Driver");
		try {
			Connection con = DriverManager.getConnection(url, root_username, password);
			PreparedStatement preparedStatement = con.prepareStatement(SQLquery);
			
			preparedStatement.setString(1, user.getUserName());
			
			System.out.println(preparedStatement);
			ResultSet rs = preparedStatement.executeQuery();
			System.out.println("reading SQL ResultSet:");
			String foundUsername = "";
			String foundPassword = "";
			while (rs.next()) { 
				foundUsername = rs.getString("Username");
				foundPassword = rs.getString("Password");
				System.out.println(foundUsername + ", " + foundPassword);
			}
			if (foundUsername != "") {
				if (foundPassword.equals(user.getPassword())) {
					result = true;
				}
			}
			
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int returnUserId(User user) throws ClassNotFoundException, SQLException {
		int id = 0;
		
		String SQLquery = "SELECT * FROM users WHERE Username LIKE ?;";
		Class.forName("com.mysql.cj.jdbc.Driver");
		try {
			Connection con = DriverManager.getConnection(url, root_username, password);
			PreparedStatement preparedStatement = con.prepareStatement(SQLquery);
			
			preparedStatement.setString(1, user.getUserName());
			
			System.out.println(preparedStatement);
			ResultSet rs = preparedStatement.executeQuery();
			System.out.println("reading SQL ResultSet:");
			int foundUserId = 0;
			while (rs.next()) { 
				foundUserId = rs.getInt("UserId");
				System.out.println("found userId: " + foundUserId);
			}
			if (foundUserId != 0) {
				id = foundUserId;
			}
			
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return id;
	}
	
	public String returnUsername(int userId) throws ClassNotFoundException, SQLException {
		String username = "";
		
		String SQLquery = "SELECT * FROM users WHERE UserId LIKE ?;";
		Class.forName("com.mysql.cj.jdbc.Driver");
		try {
			Connection con = DriverManager.getConnection(url, root_username, password);
			PreparedStatement preparedStatement = con.prepareStatement(SQLquery);
			
			preparedStatement.setInt(1, userId);
			
			System.out.println(preparedStatement);
			ResultSet rs = preparedStatement.executeQuery();
			System.out.println("reading SQL ResultSet:");
			String foundUsername = "";
			while (rs.next()) { 
				foundUsername = rs.getString("Username");
				System.out.println("found username: " + foundUsername);
			}
			username = foundUsername;
			
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return username;
	}
	
	public boolean returnVerified(int userId) throws ClassNotFoundException, SQLException {
		boolean verified = false;
		
		String SQLquery = "SELECT * FROM users WHERE UserId LIKE ?;";
		Class.forName("com.mysql.cj.jdbc.Driver");
		try {
			Connection con = DriverManager.getConnection(url, root_username, password);
			PreparedStatement preparedStatement = con.prepareStatement(SQLquery);
			
			preparedStatement.setInt(1, userId);
			
			System.out.println(preparedStatement);
			ResultSet rs = preparedStatement.executeQuery();
			System.out.println("reading SQL ResultSet:");
			boolean foundVerified = false;
			while (rs.next()) { 
				foundVerified = rs.getBoolean("Verified");
				System.out.println("found verified: " + foundVerified);
			}
			verified = foundVerified;
			
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return verified;
	}
	
}
