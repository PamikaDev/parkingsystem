package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.Ticket;

class TicketDAOTest {

  private static TicketDAO ticketDAO;
  private static Ticket ticket;
  private static Date inTime;
  private static Date outTime;

  public static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
  Connection con = null;

  @BeforeAll
  private static void setUp() {
    ticketDAO = new TicketDAO();
    ticketDAO.setDataBaseConfig(dataBaseTestConfig);
  }

  @BeforeEach
  public void setUpPerTest() throws ClassNotFoundException, SQLException, IOException {
    ticket = new Ticket();
    con = dataBaseTestConfig.getConnection();
  }

  @AfterEach
  private void tearDownPerTest() {
    dataBaseTestConfig.closeConnection(null);
  }

  // check that this operation does not save the Ticket in DB
  @Test
  void saveTicketTest() throws Exception {
    inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
    outTime = new Date();
    boolean saveTicket = ticketDAO.saveTicket(ticket);
    assertFalse(saveTicket);
  }

  @Test
  void updateTicketTest() {
    outTime = new Date();
    ticket.setOutTime(outTime);
    ticket.setPrice(1.5);
    boolean updateTicket = ticketDAO.updateTicket(ticket);
    assertTrue(updateTicket);
  }

  @Test
  void updateTicketTest_False() {
    outTime = new Date();
    ticket.setOutTime(outTime);
    ticket.setPrice(1.5);
    boolean updateTicket = ticketDAO.updateTicket(ticket);
    assertTrue(updateTicket);
  }

  // Check that a vehicle register number is for a recurring user
  @Test
  void isRecurringTest_forRecurringUser_shouldReturnTrue() {
    ticket.setVehicleRegNumber("ABCDEF");
    boolean isRecurring = ticketDAO.isRecurring(ticket.getVehicleRegNumber());
    assertTrue(isRecurring);
  }

  // Check if vehicle Reg Number is saved
  @Test
  void isSavedTest() {
    ticket.setVehicleRegNumber("ABCDEF");
    boolean isSaved = ticketDAO.isSaved(ticket.getVehicleRegNumber());
    assertTrue(isSaved);
  }

  // return an Exception and should not save it in the DB
  @Test
  void isSavedTest_shouldReturnException() {
    ticket.setVehicleRegNumber(null);
    try {
      ticketDAO.isSaved(ticket.getVehicleRegNumber());

    } catch (final IllegalArgumentException e) {
    }
    assertFalse(ticketDAO.isSaved(ticket.getVehicleRegNumber()));

  }
}