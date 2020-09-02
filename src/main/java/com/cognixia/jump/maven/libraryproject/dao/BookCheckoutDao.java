package com.cognixia.jump.maven.libraryproject.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cognixia.jump.maven.libraryproject.connection.ConnectionManager;
import com.cognixia.jump.maven.libraryproject.model.BookCheckout;

public class BookCheckoutDao {
	
	private Connection conn = ConnectionManager.getConnection();
	private final String TABLE = "book_checkout";
	private final String INSERT_QUERY = "insert into " + TABLE + " (checkout_id, patron_id, isbn, checkedout, due_date, returned) values (null, ?, ?, ?,?, null)";
	private final String GET_BY_ID_QUERY = "select * from " + TABLE + " where patron_id = ?";
	private final String UPDATE_QUERY = "update " + TABLE + " set checkedout = ?, due_date = ?, returned = ? where patron_id = ? and isbn = ?";
	private final String GET_ALL_CHECKOUTS = "select * from " + TABLE;
	
	public boolean addCheckout(BookCheckout bc) throws SQLException {
		
		PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY);
			
		stmt.setInt(1, bc.getPatronId());
		stmt.setString(2, bc.getIsbn());
		stmt.setString(3, bc.getCheckedOut());
		stmt.setString(4, bc.getDueDate());
//			stmt.setString(5, bc.getReturned());
		
		int rowCount = stmt.executeUpdate();
		

		stmt.close();
		
		if (rowCount > 0) {
			return true;
		} else {
			return false;
		}


		
	}
	
	public List<BookCheckout> getCheckoutsByPatronId(int pId) throws SQLException {
		
		List<BookCheckout> bcList = new ArrayList<>();
		
		PreparedStatement stmt = conn.prepareStatement(GET_BY_ID_QUERY);
		stmt.setInt(1, pId);
			
		ResultSet rs = stmt.executeQuery();
		
		while (rs.next()) {
			int checkoutId = rs.getInt("checkout_id");
			int patronId = rs.getInt("patron_id");
			String isbn = rs.getString("isbn");
			String checkedout = rs.getString("checkedout");
			String dueDate = rs.getString("due_date");
			String returned = rs.getString("returned");
			
			BookCheckout bc = new BookCheckout(checkoutId, patronId, isbn,checkedout, dueDate, returned);
			bcList.add(bc);
		}
		
		rs.close();
		stmt.close();
		
		return bcList;
	}
	
	public List<BookCheckout> getAllCheckouts() throws SQLException {
		List<BookCheckout> bcList = new ArrayList<>();
		PreparedStatement stmt = conn.prepareStatement(GET_ALL_CHECKOUTS);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			int checkoutId = rs.getInt("checkout_id");
			int patronId = rs.getInt("patron_id");
			String isbn = rs.getString("isbn");
			String checkedout = rs.getString("checkedout");
			String dueDate = rs.getString("due_date");
			String returned = rs.getString("returned");
			BookCheckout bc = new BookCheckout(checkoutId, patronId, isbn,checkedout, dueDate, returned);
			bcList.add(bc);
		}

		rs.close();
		stmt.close();
		
		return bcList;
	}
	
	public boolean updateCheckout(BookCheckout bc) throws SQLException{
		
		PreparedStatement stmt = conn.prepareStatement(UPDATE_QUERY);
			
		stmt.setString(1, bc.getCheckedOut());
		stmt.setString(2, bc.getDueDate());
		stmt.setString(3, bc.getReturned());
		stmt.setInt(4, bc.getPatronId());
		stmt.setString(5, bc.getIsbn());
		
		int rowCount = stmt.executeUpdate();
		stmt.close();
		
		if (rowCount > 0) {
			return true;
		} else {
			return false;
		}

	}
	
}
