package com.javaweb.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectJDBCUtil {
	static final String DB_URL = "jdbc:mysql://localhost:3306/estatebasic";
	static final String USER = "root";
	static final String PASS = "Minhtuan123@";

	public static Connection getConnetion() {
		Connection conn  = null;
		try {
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			return conn;
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
}
