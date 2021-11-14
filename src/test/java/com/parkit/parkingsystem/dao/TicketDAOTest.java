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

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
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
		TicketDAO.setDataBaseConfig(dataBaseTestConfig);
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

		new ParkingSpot(1, ParkingType.CAR, false);

		inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		outTime = new Date();

		// WHEN
		ticketDAO.saveTicket(ticket);

		// THEN
		assertFalse(ticketDAO.saveTicket(ticket));

	}

	// return an Exception when the date of inTime and outTime are null
	@Test
	public void saveTicketTest_shouldReturnException() {

		new ParkingSpot(1, ParkingType.CAR, false);

		try {
			inTime = new Date();
			inTime.setTime(0);
			outTime = new Date();
			outTime.setTime(0);

			// WHEN
			ticketDAO.saveTicket(ticket);

			// THEN
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			assertTrue(e.getMessage().contains("Error fetching next available slot"));
		}
	}

	@Test
	public void updateTicketTest() {

		// GIVEN
		outTime = new Date();
		ticket.setOutTime(outTime);
		ticket.setPrice(1.5);

		// WHEN
		boolean updateTicket = ticketDAO.updateTicket(ticket);

		// THEN
		assertFalse(updateTicket);
	}

	@Test
	public void updateTicketTest_False() {

		// GIVEN
		// ALL is already done in Before Each and Befor All!

		// WHEN
		boolean updateTicket = ticketDAO.updateTicket(ticket);

		// THEN
		assertFalse(updateTicket);
	}

	// Check that a vehicle register number is for a recurring user
	@Test
	public void isRecurringTest_forRecurringUser_shouldReturnTrue() {

		// GIVEN
		ticket.setVehicleRegNumber("ABCDEF");

		// WHEN
		boolean isRecurring = TicketDAO.recurring(ticket.getVehicleRegNumber());

		// THEN
		assertFalse(isRecurring);
	}

	// Check that a new vehicle register number is not for a recurring user
	@Test
	public void isRecurringTest_forNewUser_shouldReturnFalse() {

		// GIVEN
		ticket.setVehicleRegNumber("IMNEWUSER");

		// WHEN
		boolean isRecurring = TicketDAO.recurring(ticket.getVehicleRegNumber());

		// THEN
		assertFalse(isRecurring);
	}

	// Check if vehicle Reg Number is saved
	@Test
	public void isSavedTest() {

		// GIVEN
		ticket.setVehicleRegNumber("ABCDEF");

		// WHEN
		boolean isSaved = ticketDAO.isSaved(ticket.getVehicleRegNumber());

		// THEN
		assertFalse(isSaved);
		System.out.println(isSaved);
	}

	// the following test of the vehicle Register Number should return an Exception
	// and should not save it in the DB
	@Test
	public void isSavedTest_shouldReturnException() {

		// GIVEN
		ticket.setVehicleRegNumber(null);

		// WHEN
		try {
			ticketDAO.isSaved(ticket.getVehicleRegNumber());

		}
		// THEN
		catch (Exception e) {

			assertTrue(e instanceof IllegalArgumentException);
		}
		assertFalse(ticketDAO.isSaved(ticket.getVehicleRegNumber()));
	}

}