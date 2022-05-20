package com.parkit.parkingsystem.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
class TicketDAOTest {

  // Ma classe à tester
  private TicketDAO ticketDAOTest;
  private Ticket ticket;
  private static LogCaptor logcaptor;
  private String vehicleRegNumber = "TOTO";

  @Mock
  private DataBaseConfig databaseConfig;
  @Mock
  private Connection con;
  @Mock
  private PreparedStatement ps;
  @Mock
  private ResultSet rs;
  @Mock
  private ParkingSpot parkingSpot;

  @BeforeEach
  public void setUpPerTest() {
    logcaptor = LogCaptor.forName("TicketDAO");
    logcaptor.setLogLevelToInfo();

    ticket = new Ticket();
    ticketDAOTest = new TicketDAO();
    ticket.setId(1);
    ticket.setVehicleRegNumber(vehicleRegNumber);
    ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, true));
    ticket.setPrice(2);
    ticket.setInTime(new Date());
    ticket.setOutTime(new Date());
    ticketDAOTest.setDataBaseConfig(databaseConfig);
  }

  @AfterEach
  private void tearDownPerTest() {
  }

  @Test
  public void saveTicketOkShouldReturnFalse() throws SQLException, ClassNotFoundException {

    // Given
    when(databaseConfig.getConnection()).thenReturn(con);
    when(con.prepareStatement(Mockito.anyString())).thenReturn(ps);
    doNothing().when(ps).setInt(1, ticket.getParkingSpot().getId());
    doNothing().when(ps).setString(2, ticket.getVehicleRegNumber());
    doNothing().when(ps).setDouble(3, ticket.getPrice());
    doNothing().when(ps).setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
    doNothing().when(ps).setTimestamp(5,
        (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
    when(ps.execute()).thenReturn(true);

    // When
    boolean result = ticketDAOTest.saveTicket(ticket);

    // Then
    assertFalse(result);
    verify(ps, times(1)).execute();
  }

  @Test
  public void saveTicketKoShouldassertException() throws SQLException, ClassNotFoundException {

    // Given
    when(databaseConfig.getConnection()).thenReturn(con);
    when(con.prepareStatement(Mockito.anyString())).thenReturn(ps);
    doNothing().when(ps).setInt(1, ticket.getParkingSpot().getId());
    doNothing().when(ps).setString(2, ticket.getVehicleRegNumber());
    doNothing().when(ps).setDouble(3, ticket.getPrice());
    doNothing().when(ps).setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
    doNothing().when(ps).setTimestamp(5,
        (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
    when(ps.execute()).thenThrow(SQLException.class);

    // When
    ticketDAOTest.saveTicket(ticket);

    // Then
    assertThat(logcaptor.getErrorLogs().contains("Error fetching next available slot"));

  }

  @Test
  public void getTicketOKTest() throws SQLException, ClassNotFoundException {

    // Given
    when(databaseConfig.getConnection()).thenReturn(con);
    when(con.prepareStatement(Mockito.anyString())).thenReturn(ps);
    when(ps.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true);
    doNothing().when(ps).setString(1, vehicleRegNumber);
    when(rs.getInt(Mockito.anyInt())).thenReturn(1);
    when(rs.getString(6)).thenReturn("CAR");
    when(rs.getDouble(3)).thenReturn(1.5);
    when(rs.getTimestamp(Mockito.anyInt())).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

    // When
    ticketDAOTest.getTicket(vehicleRegNumber);

    // Then
    assertThat("TOTO").isEqualTo(ticket.getVehicleRegNumber());
    verify(ps, times(1)).executeQuery();
    verify(rs, times(1)).next();
  }

  @Test
  public void getTicketKoShouldassertException() throws SQLException, ClassNotFoundException {

    // Given
    when(databaseConfig.getConnection()).thenReturn(con);
    when(con.prepareStatement(Mockito.anyString())).thenReturn(ps);
    when(ps.executeQuery()).thenThrow(SQLException.class);

    // When
    ticketDAOTest.getTicket(vehicleRegNumber);

    // Then
    assertThat(logcaptor.getErrorLogs().contains("Error fetching next available slot"));
  }

  @Test
  public void updateTicketOkShouldReturnFalse() throws SQLException, ClassNotFoundException {

    // Given
    when(databaseConfig.getConnection()).thenReturn(con);
    when(con.prepareStatement(Mockito.anyString())).thenReturn(ps);
    doNothing().when(ps).setDouble(1, ticket.getPrice());
    doNothing().when(ps).setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
    doNothing().when(ps).setInt(3, ticket.getId());
    when(ps.execute()).thenReturn(true);

    // When
    boolean result = ticketDAOTest.updateTicket(ticket);

    // Then
    assertTrue(result);
    verify(ps, times(1)).execute();
  }

  @Test
  public void updateTicketKoShouldassertException() throws SQLException, ClassNotFoundException {

    // Given
    when(databaseConfig.getConnection()).thenReturn(con);
    when(con.prepareStatement(Mockito.anyString())).thenReturn(ps);
    doNothing().when(ps).setDouble(1, ticket.getPrice());
    doNothing().when(ps).setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
    doNothing().when(ps).setInt(3, ticket.getId());
    when(ps.execute()).thenThrow(SQLException.class);

    // When
    ticketDAOTest.updateTicket(ticket);

    // Then
    assertThat(logcaptor.getErrorLogs().contains("Error updating ticket info"));
  }

  @Test
  public void isRecurringOkShouldReturnFalse() throws SQLException, ClassNotFoundException {

    // Given
    when(databaseConfig.getConnection()).thenReturn(con);
    when(con.prepareStatement(Mockito.anyString())).thenReturn(ps);
    when(ps.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true);
    doNothing().when(ps).setString(1, vehicleRegNumber);

    // When
    boolean result = ticketDAOTest.isRecurring(vehicleRegNumber);

    // Then
    assertTrue(result);
    verify(ps, times(1)).executeQuery();
    verify(rs, times(1)).next();
  }

  @Test
  public void isRecurringKoShouldassertException() throws SQLException, ClassNotFoundException {

    // Given
    when(databaseConfig.getConnection()).thenReturn(con);
    when(con.prepareStatement(Mockito.anyString())).thenReturn(ps);
    when(ps.executeQuery()).thenReturn(rs);
    doNothing().when(ps).setString(1, vehicleRegNumber);
    when(ps.executeQuery()).thenThrow(SQLException.class);

    // When
    ticketDAOTest.isRecurring(vehicleRegNumber);

    // Then
    assertThat(logcaptor.getErrorLogs()
        .contains("Error checking vehicle reg number is for a recurring user"));
  }

  @Test
  public void isSavedOkShouldReturnFalse() throws SQLException, ClassNotFoundException {

    // Given
    when(databaseConfig.getConnection()).thenReturn(con);
    when(con.prepareStatement(Mockito.anyString())).thenReturn(ps);
    when(ps.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true);
    doNothing().when(ps).setString(1, vehicleRegNumber);

    // When
    boolean result = ticketDAOTest.isSaved(vehicleRegNumber);

    // Then
    assertTrue(result);
    verify(ps, times(1)).executeQuery();
    verify(rs, times(1)).next();
  }

  @Test
  public void isSavedKoShouldassertException() throws SQLException, ClassNotFoundException {

    // Given
    when(databaseConfig.getConnection()).thenReturn(con);
    when(con.prepareStatement(Mockito.anyString())).thenReturn(ps);
    when(ps.executeQuery()).thenReturn(rs);
    doNothing().when(ps).setString(1, vehicleRegNumber);
    when(ps.executeQuery()).thenThrow(SQLException.class);

    // When
    ticketDAOTest.isSaved(vehicleRegNumber);

    // Then
    assertThat(logcaptor.getErrorLogs().contains("Error saving vehicle Reg Number"));

  }

  @Test
  public void vehicleInsideOkShouldReturnFalse() throws SQLException, ClassNotFoundException {

    // Given
    when(databaseConfig.getConnection()).thenReturn(con);
    when(con.prepareStatement(Mockito.anyString())).thenReturn(ps);
    when(ps.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true);
    doNothing().when(ps).setString(1, vehicleRegNumber);

    // When
    boolean result = ticketDAOTest.getVehicleInside(vehicleRegNumber);

    // Then
    assertTrue(result);
    verify(ps, times(1)).executeQuery();
    verify(rs, times(1)).next();
  }

  @Test
  public void vehicleInsideKoShouldassertException() throws SQLException, ClassNotFoundException {

    // Given
    when(databaseConfig.getConnection()).thenReturn(con);
    when(con.prepareStatement(Mockito.anyString())).thenReturn(ps);
    when(ps.executeQuery()).thenReturn(rs);
    doNothing().when(ps).setString(1, vehicleRegNumber);
    when(ps.executeQuery()).thenThrow(SQLException.class);

    // When
    ticketDAOTest.getVehicleInside(vehicleRegNumber);

    // Then
    assertThat(
        logcaptor.getErrorLogs().contains("Error checking vehicleRegNumber is already inside"));

  }

  @Test
  public void vehicleOutsideOkShouldReturnFalse() throws SQLException, ClassNotFoundException {

    // Given
    when(databaseConfig.getConnection()).thenReturn(con);
    when(con.prepareStatement(Mockito.anyString())).thenReturn(ps);
    when(ps.executeQuery()).thenReturn(rs);
    when(rs.next()).thenReturn(true);
    doNothing().when(ps).setString(1, vehicleRegNumber);

    // When
    boolean result = ticketDAOTest.getVehicleOutside(vehicleRegNumber);

    // Then
    assertTrue(result);
    verify(ps, times(1)).executeQuery();
    verify(rs, times(1)).next();
  }

  @Test
  public void vehicleOutsideKoShouldassertException() throws SQLException, ClassNotFoundException {

    // Given
    when(databaseConfig.getConnection()).thenReturn(con);
    when(con.prepareStatement(Mockito.anyString())).thenReturn(ps);
    when(ps.executeQuery()).thenReturn(rs);
    doNothing().when(ps).setString(1, vehicleRegNumber);
    when(ps.executeQuery()).thenThrow(SQLException.class);

    // When
    ticketDAOTest.getVehicleOutside(vehicleRegNumber);

    // Then
    assertThat(
        logcaptor.getErrorLogs().contains("Error checking vehicleRegNumber is already outside"));

  }

}