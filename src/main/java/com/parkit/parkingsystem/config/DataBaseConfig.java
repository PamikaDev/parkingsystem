package com.parkit.parkingsystem.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataBaseConfig {

  private static final Logger logger = LogManager.getLogger("DataBaseConfig");

  public Connection getConnection() throws SQLException, ClassNotFoundException, IOException {
    logger.info("Create DB connection");

    Properties props = new Properties();
    try (FileInputStream fis = new FileInputStream(("resources/conf.properties"))) {
      props.load(fis);
    }

    Class.forName(props.getProperty("jdbc.driver.class"));
    String url = props.getProperty("jdbc.url");
    String login = props.getProperty("jdbc.login");
    String password = props.getProperty("jdbc.password");

    return DriverManager.getConnection(url, login, password);

  }

  public void closeConnection(Connection con) {
    if (con != null) {
      try {
        con.close();
        logger.info("Closing DB connection");
      } catch (SQLException e) {
        logger.error("Error while closing connection", e);
      }
    }
  }

  public void closePreparedStatement(PreparedStatement ps) {
    if (ps != null) {
      try {
        ps.close();
        logger.info("Closing Prepared Statement");
      } catch (SQLException e) {
        logger.error("Error while closing prepared statement", e);
      }
    }
  }

  public void closeResultSet(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
        logger.info("Closing Result Set");
      } catch (SQLException e) {
        logger.error("Error while closing result set", e);
      }
    }
  }
}