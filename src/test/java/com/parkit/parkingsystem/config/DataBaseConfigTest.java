package com.parkit.parkingsystem.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
class DataBaseConfigTest {
  private DataBaseConfig dataBaseConfigTest;
  private static LogCaptor logcaptor;

  private DataBaseTestConfig dataBaseTestConfig;

  @Mock
  private Connection con;

  @Mock
  private PreparedStatement ps;

  @Mock
  private ResultSet rs;

  @BeforeEach
  void setUpPerTest() throws Exception {

    dataBaseConfigTest = new DataBaseConfig();
    logcaptor = LogCaptor.forName("DataBaseConfig");
    logcaptor.setLogLevelToInfo();
    dataBaseTestConfig = new DataBaseTestConfig();

  }

  @AfterEach
  void tearDownPerTest() throws Exception {

    dataBaseConfigTest = null;
    dataBaseTestConfig = null;
  }

  @Test
  void getConnectionTest() throws ClassNotFoundException, SQLException {
    dataBaseConfigTest.getConnection();
    dataBaseTestConfig.getConnection();
  }

  @Test
  void closeConnectionWhenConnectionisNotNull() throws SQLException {

    // WHEN
    dataBaseConfigTest.closeConnection(con);
    dataBaseTestConfig.closeConnection(con);

    // THEN
    verify(con, times(2)).close();
    assertThat(logcaptor.getInfoLogs().contains("Closing DB connection"));
  }

  @Test
  void closeConnectionShouldassertException() throws SQLException {

    // GIVEN
    doThrow(SQLException.class).when(con).close();

    // WHEN
    dataBaseConfigTest.closeConnection(con);
    dataBaseTestConfig.closeConnection(con);

    // THEN
    assertThat(logcaptor.getErrorLogs().contains("Error while closing connection con"));
  }

  @Test
  void closeConnectionWhenConnectionisNull() throws SQLException {

    // WHEN
    dataBaseConfigTest.closeConnection(null);
    dataBaseTestConfig.closeConnection(null);

    // THEN
    assertThat(logcaptor.getInfoLogs().contains("Connection is null"));
  }

  @Test
  void closePreparedStatementWhenPreparedStatementisNotNull() throws SQLException {

    // WHEN
    dataBaseConfigTest.closePreparedStatement(ps);
    dataBaseTestConfig.closePreparedStatement(ps);

    // THEN
    verify(ps, times(2)).close();
    assertThat(logcaptor.getInfoLogs().contains("Closing prepared statement"));
  }

  @Test
  void closePreparedStatementShouldassertException() throws SQLException {

    // GIVEN
    doThrow(SQLException.class).when(ps).close();

    // WHEN
    dataBaseConfigTest.closePreparedStatement(ps);
    dataBaseTestConfig.closePreparedStatement(ps);

    // THEN
    assertThat(logcaptor.getErrorLogs().contains("Error while closing prepared statement"));
  }

  @Test
  void closePreparedStatementWhenPreparedStatementisNull() throws SQLException {

    // WHEN
    dataBaseConfigTest.closePreparedStatement(null);
    dataBaseTestConfig.closePreparedStatement(null);

    // THEN
    assertThat(logcaptor.getInfoLogs().contains("Prepared statement is null"));
  }

  @Test
  void closeResultSetWhenResultSetisNotNull() throws SQLException {

    // WHEN
    dataBaseConfigTest.closeResultSet(rs);
    dataBaseTestConfig.closeResultSet(rs);

    // THEN
    verify(rs, times(2)).close();
    assertThat(logcaptor.getInfoLogs().contains("Closing Result Set"));
  }

  @Test
  void closeResultSetShouldassertException() throws SQLException {

    // GIVEN
    doThrow(SQLException.class).when(rs).close();

    // WHEN
    dataBaseConfigTest.closeResultSet(rs);
    dataBaseTestConfig.closeResultSet(rs);

    // THEN
    assertThat(logcaptor.getErrorLogs().contains("Error while closing result set"));
  }

  @Test
  void closeResultSetWhenResultSetisNull() throws SQLException {

    // WHEN
    dataBaseConfigTest.closeResultSet(null);
    dataBaseTestConfig.closeResultSet(null);

    // THEN
    assertThat(logcaptor.getInfoLogs().contains("Result set is null"));
  }

}
