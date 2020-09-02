package com.cognixia.jump.maven.libraryproject.web;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cognixia.jump.maven.libraryproject.dao.LibrarianDao;
import com.cognixia.jump.maven.libraryproject.dao.PatronDao;
import com.cognixia.jump.maven.libraryproject.model.Librarian;
import com.cognixia.jump.maven.libraryproject.model.Patron;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	PatronDao patronDao;
	LibrarianDao librarianDao;
	
	@Override
	public void init() throws ServletException {
		patronDao = new PatronDao();
		librarianDao = new LibrarianDao();
		
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
		dispatcher.forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = null;
		String radioButton = request.getParameter("user");
		
		boolean isValid=false;
		
		try {
			switch(radioButton) {
			case "librarian":
				if ((isValid = verifyLibrarianLogin(request, response))) {
					dispatcher=request.getRequestDispatcher("jsp/librarian.jsp");
					isValid=true;
				}			
				break;
			case "patron":
				if ((isValid = verifyPatronLogin(request, response))) {
					dispatcher=request.getRequestDispatcher("jsp/patron.jsp");
					isValid=true;
				}
				break;
			default:	
				
			}
			
			if (!isValid) {
				request.setAttribute("isValid", "Login Failed. Please try again.");
				dispatcher=request.getRequestDispatcher("/");
			} 
			
		} catch (SQLException e) {
			response.setStatus(500);
			dispatcher=request.getRequestDispatcher("jsp/error.jsp");
		}

		dispatcher.forward(request, response);
	}
	
	private boolean verifyLibrarianLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		HttpSession session = request.getSession(true);
		Librarian librarian = librarianDao.getByUsername(request.getParameter("uname"));
		if (librarian == null) {
			return false;
		}
		if(librarian.getUsername().equals(request.getParameter("uname")) && 
				(librarian.getPassword().contentEquals(request.getParameter("psw")))) {
			
			session.setAttribute("username", request.getParameter("uname"));
			session.setAttribute("password", request.getParameter("psw"));
			
			return true;
		} else {	
		return false;
	}
}
	
	private boolean verifyPatronLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException{
		HttpSession session = request.getSession(true);
		Patron patron = patronDao.getPatronByUsername(request.getParameter("uname"));
		if (patron == null) {
			return false;
		}
		if(patron.getUserName().equals(request.getParameter("uname")) && 
				(patron.getPassword().contentEquals(request.getParameter("psw")))) {
			session.setAttribute("username", request.getParameter("uname"));
			session.setAttribute("password", request.getParameter("psw"));
			return true;
		} else {	
		return false;
	}
}
}
