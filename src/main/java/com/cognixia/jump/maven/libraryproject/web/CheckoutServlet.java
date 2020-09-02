package com.cognixia.jump.maven.libraryproject.web;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cognixia.jump.maven.libraryproject.connection.ConnectionManager;
import com.cognixia.jump.maven.libraryproject.dao.BookCheckoutDao;
import com.cognixia.jump.maven.libraryproject.dao.BookDao;
import com.cognixia.jump.maven.libraryproject.dao.PatronDao;
import com.cognixia.jump.maven.libraryproject.model.Book;
import com.cognixia.jump.maven.libraryproject.model.BookCheckout;
import com.cognixia.jump.maven.libraryproject.model.Patron;

/**
 * Servlet implementation class CheckoutServlet
 */

@WebServlet(urlPatterns = {"/patron"})
public class CheckoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private BookCheckoutDao checkOutDao;
	private PatronDao patronDao;
	private BookDao bookDao;
	
	@Override
	public void init() throws ServletException {
		checkOutDao = new BookCheckoutDao();
		patronDao = new PatronDao();
	}

	public void destroy() {
		try {
			ConnectionManager.getConnection().close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to close");
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher = null;
		String username = (String) request.getSession(true).getAttribute("username");
		List<BookCheckout> checkoutHistory = null;
		try {
			Patron patron = patronDao.getPatronByUsername(username);
			checkoutHistory = checkOutDao.getCheckoutsByPatronId(patron.getPatronId());
			request.setAttribute("allCheckouts", checkoutHistory);
			dispatcher = request.getRequestDispatcher("jsp/patron.jsp");
		} catch (SQLException e) {
			response.setStatus(500);
			dispatcher = request.getRequestDispatcher("jsp/error.jsp");
			e.printStackTrace();
		}
		
		dispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String bookAction = request.getParameter("bookAction");
		RequestDispatcher dispatcher = null;
		
		try {
			switch (bookAction) {
			case "checkout":
				checkoutBook(request, response);
				dispatcher = request.getRequestDispatcher("jsp/patron.jsp");
				break;
			case "return":
				returnBook(request, response);
				dispatcher = request.getRequestDispatcher("jsp/patron.jsp");
				break;
			default:
				
			}
		} catch (ServletException | IOException | SQLException e) {
			response.setStatus(500);
			dispatcher = request.getRequestDispatcher("jsp/error.jsp");
			e.printStackTrace();
		}
		
		dispatcher.forward(request, response);
	}
	
	private void checkoutBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {

		RequestDispatcher dispatcher = null;
		HttpSession session = request.getSession(true);
		Patron patron = patronDao.getPatronByUsername((String)session.getAttribute("username"));
		int patronId = patron.getPatronId();

		BookCheckout bco = new BookCheckout(1,patronId,request.getParameter("isbn"),getTodaysDateString(),getDueDateString(),"");
		System.out.println(bco);

		if (checkOutDao.addCheckout(bco)) {
			request.setAttribute("message", "Checkout book successful.");
		} else {
			request.setAttribute("message", "Checkout book failed.");
		}
	}
	
	private void returnBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {

		String isbn = (String) request.getParameter("isbn");
		
		String username = (String) request.getSession(true).getAttribute("username");
		Patron patron = patronDao.getPatronByUsername(username);
		BookCheckout bookToReturn = null;
		boolean isReturned = false;

		List<BookCheckout> checkoutHistory = checkOutDao.getCheckoutsByPatronId(patron.getPatronId());

		for (int i = 0; i < checkoutHistory.size(); i++) {
			if (checkoutHistory.get(i).getIsbn().equals(isbn)) {
				
				bookToReturn = checkoutHistory.get(i);
				isReturned = true;
				break;
			}
		}
		
		if (isReturned) {
			
			BookCheckout newCheckout = new BookCheckout(
					bookToReturn.getCheckoutId(),
					bookToReturn.getPatronId(),
					bookToReturn.getIsbn(), 
					bookToReturn.getCheckedOut(), 
					getDueDateString(),
					getTodaysDateString()
					);
		
			if (checkOutDao.updateCheckout(newCheckout)) {
				request.setAttribute("message", "Book returned successfully.");				
			} else {
				request.setAttribute("message", "Book Return Failed.");
			}

		} else {
			request.setAttribute("message", "Book Return Failed.");
		}
		
	}
		
	private String getTodaysDateString() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		String date = dtf.format(now).toString();  

		return date;
	}	
	
	private String getDueDateString() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		String date = dtf.format(now.plusMonths(1)).toString();  // Due 1 month later
		System.out.println(date);
		return date;
	}	

}
