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

  /*
   * check that this operation does not save the Ticket in DB
   */
  @Test
  void saveTicketTest() throws Exception {

    // GIVEN
    inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    outTime = new Date();

    // WHEN
    boolean saveTicket = ticketDAO.saveTicket(ticket);

    // THEN
    assertFalse(saveTicket);
  }

  /*
   * Check that getTicket method take vehicle register number as parameter
   */
  @Test
  void getTicketTest() throws ClassNotFoundException, SQLException, IOException {
    String str = "ABCDEF";

    // GIVEN
    ticket.setParkingSpot(parkingSpot);
    ticket.setVehicleRegNumber(str);

    // WHEN
    boolean getTicket = ticketDAO.getTicket(str) != null;
    // Ticket ticketdaotest = ticketDAO.getTicket(str);

    // THEN
    assertFalse(getTicket);
    // assertThat(str).isEqualTo(ticketdaotest.getVehicleRegNumber());

  }

  @Test
  void updateTicketTest() throws ClassNotFoundException, SQLException {
    // GIVEN
    outTime = new Date();
    ticket.setOutTime(outTime);
    ticket.setPrice(1.5);

    // WHEN
    boolean updateTicket = ticketDAO.updateTicket(ticket);

    // THEN
    assertTrue(updateTicket);
  }

  /*
   * Check that a vehicle register number is for a recurring user
   */
  @Test
  void isRecurringTest() throws ClassNotFoundException, SQLException {
    // GIVEN
    ticket.setVehicleRegNumber("ABCDEF");

    // WHEN
    boolean isRecurring = ticketDAO.isRecurring(ticket.getVehicleRegNumber());

    // THEN
    assertTrue(isRecurring);
  }

  /*
   * Check if vehicle Reg Number is saved
   */
  @Test
  void isSavedTest() {
    // GIVEN
    ticket.setVehicleRegNumber("ABCDEF");

    // WHEN
    boolean isSaved = ticketDAO.isSaved(ticket.getVehicleRegNumber());

    // THEN
    assertFalse(isSaved);
  }

  // Check if vehicle is inside
  @Test
  void vehicleInside() {
    // GIVEN
    ticket.setOutTime(null);

    // WHEN
    boolean vehicleInside = ticketDAO.vehicleInside(null);

    // THEN
    assertFalse(vehicleInside);
  }

  // Check if vehicle is outside
  void vehicleOutside() {
    // GIVEN
    ticket.setOutTime(null);

    // WHEN
    boolean vehicleOutside = ticketDAO.vehicleOutside(null);

    // THEN
    assertFalse(vehicleOutside);
  }

}