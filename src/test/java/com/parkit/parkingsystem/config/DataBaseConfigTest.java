package com.parkit.parkingsystem.config;

import static org.assertj.core.api.Assertions.assertThat;
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

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
class DataBaseConfigTest {

  private DataBaseConfig dataBaseConfigTest;
  private static LogCaptor logcaptor;

  @Mock
  private Connection con;
  @Mock
  private PreparedStatement ps;
  @Mock
  private ResultSet rs;

  @BeforeEach
  void setUpPerTest() throws Exception {
    dataBaseConfigTest = new DataBaseConfig();
    logcaptor = LogCaptor.forName("TicketDAO");
    logcaptor.setLogLevelToInfo();
  }

  @AfterEach
  void tearDownPerTest() throws Exception {
    dataBaseConfigTest = null;
  }

  @Test
  void getConnectionTest() throws ClassNotFoundException, SQLException {
    dataBaseConfigTest.getConnection();
  }

  @Test
  void getConnectionKOShouldassertException() {
  }

  @Test
  void closeConnectionTest() throws SQLException {
    dataBaseConfigTest.closeConnection(con);
    verify(con, times(1)).close();
  }

  @Test
  void closeConnectionKoShouldassertException() throws SQLException {
    dataBaseConfigTest.closeConnection(con);
    assertThat(logcaptor.getErrorLogs().contains("Error while closing connection"));
  }

  @Test
  void closePreparedStatementTest() throws SQLException {
    dataBaseConfigTest.closePreparedStatement(ps);
    verify(ps, times(1)).close();
  }

  @Test
  void closePreparedStatementKoShouldassertException() throws SQLException {
    assertThat(logcaptor.getErrorLogs().contains("Error while closing prepared statement"));
  }

  @Test
  void closeResultSetTest() throws SQLException {
    dataBaseConfigTest.closeResultSet(rs);
    verify(rs, times(1)).close();
  }

  @Test
  void closeResultSetKoShouldassertException() throws SQLException {
    assertThat(logcaptor.getErrorLogs().contains("Error while closing result set"));
  }

}
