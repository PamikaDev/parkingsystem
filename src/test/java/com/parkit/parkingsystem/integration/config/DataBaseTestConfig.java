package com.parkit.parkingsystem.integration.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;

public class DataBaseTestConfig extends DataBaseConfig {

  private static final Logger logger = LogManager.getLogger("DataBaseTestConfig");

  @Override
  public Connection getConnection()
      throws SQLException, ClassNotFoundException, FileNotFoundException, IOException {
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

  @Override
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

  @Override
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

  @Override
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