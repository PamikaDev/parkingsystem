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
  void closeConnectionOKShouldReturnTrue() throws SQLException {

    // GIVEN
    doThrow(SQLException.class).when(con).close();

    // WHEN
    dataBaseConfigTest.closeConnection(con);

    // THEN
    verify(con, times(1)).close();
    assertThat(logcaptor.getInfoLogs().contains("closing connection"));
  }

  @Test
  void closeConnectionKOShouldassertException() throws SQLException {

    // WHEN
    dataBaseConfigTest.closeConnection(null);

    // THEN
    assertThat(logcaptor.getErrorLogs().contains("Error while closing connection con"));
  }

  @Test
  void closePreparedStatementOKShouldReturnTrue() throws SQLException {

    // GIVEN
    doThrow(SQLException.class).when(ps).close();

    // WHEN
    dataBaseConfigTest.closePreparedStatement(ps);

    // THEN
    verify(ps, times(1)).close();
    assertThat(logcaptor.getInfoLogs().contains("closing prepared statement"));
  }

  @Test
  void closePreparedStatementKOShouldassertException() throws SQLException {

    // WHEN
    dataBaseConfigTest.closePreparedStatement(null);

    // THEN
    assertThat(logcaptor.getErrorLogs().contains("Error while closing prepared statement"));
  }

  @Test
  void closeResultSetOKShouldReturnTrue() throws SQLException {

    // GIVEN
    doThrow(SQLException.class).when(rs).close();

    // WHEN
    dataBaseConfigTest.closeResultSet(rs);

    // THEN
    verify(rs, times(1)).close();
    assertThat(logcaptor.getInfoLogs().contains("Closing Result Set"));
  }

  @Test
  void closeResultSetKOShouldassertException() throws SQLException {

    // WHEN
    dataBaseConfigTest.closeResultSet(null);

    // THEN
    assertThat(logcaptor.getErrorLogs().contains("Error while closing result set"));
  }

}
