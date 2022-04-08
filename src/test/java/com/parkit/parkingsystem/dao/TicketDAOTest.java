package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

class TicketDAOTest {

  private static TicketDAO ticketDAO;
  private static Ticket ticket;
  private static Date inTime;
  private static Date outTime;
  private static ParkingSpot parkingSpot;

  public static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
  Connection con = null;

  private static final Logger logger = LogManager.getLogger("TicketDAOTest");

  @BeforeAll
  private static void setUp() {
    ticketDAO = new TicketDAO();
    ticketDAO.setDataBaseConfig(dataBaseTestConfig);
  }

  @BeforeEach
  public void setUpPerTest() {
    ticket = new Ticket();
    try {
      con = dataBaseTestConfig.getConnection();
    } catch (Exception ex) {
      logger.error("Error connecting to data base", ex);

    }
  }

  @AfterEach
  private void tearDownPerTest() {
    dataBaseTestConfig.closeConnection(con);
  }

  @Test
  void saveTicketTest() throws Exception {
    inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    outTime = new Date();
    boolean saveTicket = ticketDAO.saveTicket(ticket);
    assertFalse(saveTicket);
  }

  @Test
  void getTicketTest() throws ClassNotFoundException, SQLException, IOException {
    String str = "ABCDEF";

    // GIVEN
    ticket.setParkingSpot(parkingSpot);
    ticket.setVehicleRegNumber(str);

    // WHEN
    boolean getTicket = ticketDAO.getTicket(str) != null;

    // THEN
    assertFalse(getTicket);

  }

  @Test
  void updateTicketTest() throws ClassNotFoundException, SQLException {
    outTime = new Date();
    ticket.setOutTime(outTime);
    ticket.setPrice(1.5);
    boolean updateTicket = ticketDAO.updateTicket(ticket);
    assertTrue(updateTicket);
  }

  // Check that a vehicle register number is for a recurring user
  @Test
  void isRecurringTest_forRecurringUser_shouldReturnTrue()
      throws ClassNotFoundException, SQLException {
    ticket.setVehicleRegNumber("ABCDEF");
    boolean isRecurring = ticketDAO.isRecurring(ticket.getVehicleRegNumber());
    assertFalse(isRecurring);
  }

  // Check if vehicle Reg Number is saved
  @Test
  void isSavedTest() {
    ticket.setVehicleRegNumber("ABCDEF");
    boolean isSaved = ticketDAO.isSaved(ticket.getVehicleRegNumber());
    assertFalse(isSaved);
  }

}