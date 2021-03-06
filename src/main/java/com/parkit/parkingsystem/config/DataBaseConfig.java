package com.parkit.parkingsystem.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DataBaseConfig {

	private static final Logger logger = LogManager.getLogger("DataBaseConfig");

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		InputStream input = DataBaseConfig.class.getClassLoader().getResourceAsStream("config.properties");
		Properties prop = new Properties();

		if (input == null) {
			System.out.println("Sorry, unable to find config.properties");
		}
		//load a properties file from class path, inside static method
		try {
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//get the property value
		String username = prop.getProperty("username");
		String password = prop.getProperty("password");

		return DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/prod?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Paris",
				username, password);
	}

	public void closeConnection(Connection con){
		if(con!=null){
			try {
				con.close();
				logger.info("Closing DB connection");
			} catch (SQLException e) {
				logger.error("Error while closing connection",e);
			}
		}
	}

	public void closePreparedStatement(PreparedStatement ps) {
		if(ps!=null){
			try {
				ps.close();
				logger.info("Closing Prepared Statement");
			} catch (SQLException e) {
				logger.error("Error while closing prepared statement",e);
			}
		}
	}

	public void closeResultSet(ResultSet rs) {
		if(rs!=null){
			try {
				rs.close();
				logger.info("Closing Result Set");
			} catch (SQLException e) {
				logger.error("Error while closing result set",e);
			}
		}
	}
}
