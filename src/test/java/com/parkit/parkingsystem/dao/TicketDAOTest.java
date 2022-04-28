package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
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

  private static TicketDAO ticketDAOTest;
  private static Ticket ticket;
  private static Date inTime;
  private static Date outTime;
  private static ParkingSpot parkingSpot;

  public static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
  Connection con = null;

  private static final Logger logger = LogManager.getLogger("TicketDAOTest");

  @BeforeAll
  private static void setUp() {
    ticketDAOTest = new TicketDAO();
    ticketDAOTest.setDataBaseConfig(dataBaseTestConfig);
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
  void saveTicketTest() {

    // GIVEN
    inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    outTime = new Date();

    // WHEN
    boolean saveTicket = ticketDAOTest.saveTicket(ticket);

    // THEN
    assertFalse(saveTicket);
  }

  @Test
  void getTicketTest() {
    String str = "ABCDEF";

    // GIVEN
    ticket.setParkingSpot(parkingSpot);
    ticket.setVehicleRegNumber(str);

    // WHEN
    boolean getTicket = ticketDAOTest.getTicket(str) != null;

    // THEN
    assertFalse(getTicket);

  }

  @Test
  void updateTicketTest() {

    // GIVEN
    outTime = new Date();
    ticket.setOutTime(outTime);
    ticket.setPrice(1.5);

    // WHEN
    boolean updateTicket = ticketDAOTest.updateTicket(ticket);

    // THEN
    assertTrue(updateTicket);
  }

  /*
   * Check that a vehicle register number is for a recurring user
   */
  @Test
  void isRecurringTest() {

    // GIVEN
    ticket.setVehicleRegNumber("ABCDEF");

    // WHEN
    boolean isRecurring = ticketDAOTest.isRecurring(ticket.getVehicleRegNumber());

    // THEN
    assertFalse(isRecurring);
  }

  /*
   * Check if vehicle Reg Number is saved
   */
  @Test
  void isSavedTest() {

    // GIVEN
    ticket.setVehicleRegNumber("ABCDEF");

    // WHEN
    boolean isSaved = ticketDAOTest.isSaved(ticket.getVehicleRegNumber());

    // THEN
    assertFalse(isSaved);
  }

  /*
   * Check if vehicle is inside
   */
  @Test
  void vehicleInside() {

    // GIVEN
    ticket.setOutTime(null);

    // WHEN
    boolean vehicleInside = ticketDAOTest.vehicleInside(null);

    // THEN
    assertFalse(vehicleInside);
  }

  /*
   * Check if vehicle is outside
   */
  @Test
  void vehicleOutside() {

    // GIVEN
    outTime = new Date();
    ticket.setOutTime(outTime);

    // WHEN
    boolean vehicleOutside = ticketDAOTest.vehicleOutside(ticket.getVehicleRegNumber());

    // THEN
    assertFalse(vehicleOutside);
  }

}