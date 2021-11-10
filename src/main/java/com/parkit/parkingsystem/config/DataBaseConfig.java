package com.parkit.parkingsystem.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataBaseConfig {

	private static final Logger logger = LogManager.getLogger("DataBaseConfig");

	public Connection getConnection() throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {
		logger.info("Create DB connection");
		Class.forName("com.mysql.cj.jdbc.Driver");
//		String url0 = ("jdbc:mysql://localhost:3306/test");
		final String url = "jdbc:mysql://localhost:3306/prod";
		final String dblogin = "root";
		final String dbpassword = "rootroot";
		try (Connection con = DriverManager.getConnection(url, dblogin, dbpassword)) {
		}
		return DriverManager.getConnection(url, dblogin, dbpassword);
	}

	public void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
				logger.info("Closing DB connection");
			} catch (final SQLException e) {
				logger.error("Error while closing connection", e);
			}
		}
	}

	public void closePreparedStatement(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
				logger.info("Closing Prepared Statement");
			} catch (final SQLException e) {
				logger.error("Error while closing prepared statement", e);
			}
		}
	}

	public void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
				logger.info("Closing Result Set");
			} catch (final SQLException e) {
				logger.error("Error while closing result set", e);
			}
		}
	}
}
