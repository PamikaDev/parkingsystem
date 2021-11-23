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
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAOTest {

	private static TicketDAO ticketDAO;
	private static Ticket ticket;
	private static Date inTime;
	private static Date outTime;

	public static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	Connection con = null;
	private static final Logger logger = LogManager.getLogger("TicketDAOTest");

	@BeforeAll
	private static void setUp() throws Exception {
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
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
		dataBaseTestConfig.closeConnection(null);
	}

	// check that this operation does not save the Ticket in DB
	@Test
	public void saveTicketTest() throws Exception {
		inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		outTime = new Date();
		ticketDAO.saveTicket(ticket);
		assertFalse(ticketDAO.saveTicket(ticket));
	}

	// return an Exception when the date of inTime and outTime are null
	@Test
	public void saveTicketTest_shouldReturnException() {
		try {
			inTime = new Date();
			inTime.setTime(0);
			outTime = new Date();
			outTime.setTime(0);

			ticketDAO.saveTicket(ticket);
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void updateTicketTest() {
		outTime = new Date();
		ticket.setOutTime(outTime);
		ticket.setPrice(1.5);
		boolean updateTicket = ticketDAO.updateTicket(ticket);
		assertTrue(updateTicket);
	}

	@Test
	public void updateTicketTest_False() {
		boolean updateTicket = ticketDAO.updateTicket(ticket);
		assertFalse(updateTicket);
	}

	// Check that a vehicle register number is for a recurring user
	@Test
	public void isRecurringTest_forRecurringUser_shouldReturnTrue() {
		ticket.setVehicleRegNumber("ABCDEF");
		boolean isRecurring = ticketDAO.isRecurring(ticket.getVehicleRegNumber());
		assertTrue(isRecurring);
	}

	// Check that a new vehicle register number is not for a recurring user
	@Test
	public void isRecurringTest_forNewUser_shouldReturnFalse() {
		ticket.setVehicleRegNumber("IMNEWUSER");
		boolean isRecurring = ticketDAO.isRecurring(ticket.getVehicleRegNumber());
		assertFalse(isRecurring);
	}

	// Check if vehicle Reg Number is saved
	@Test
	public void isSavedTest() {
		ticket.setVehicleRegNumber("ABCDEF");
		boolean isSaved = ticketDAO.isSaved(ticket.getVehicleRegNumber());
		assertTrue(isSaved);
		System.out.println(isSaved);
	}

	// return an Exception and should not save it in the DB
	@Test
	public void isSavedTest_shouldReturnException() {
		ticket.setVehicleRegNumber(null);
		try {
			ticketDAO.isSaved(ticket.getVehicleRegNumber());

		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
		assertFalse(ticketDAO.isSaved(ticket.getVehicleRegNumber()));

	}
}