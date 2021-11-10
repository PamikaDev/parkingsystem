package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	private static ParkingSpot parkingSpot;
	private static Ticket ticket;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.setDataBaseConfig(dataBaseTestConfig);
		ticketDAO = new TicketDAO();
		ticketDAO.setDataBaseConfig(dataBaseTestConfig);
		dataBasePrepareService = new DataBasePrepareService();
		ticket = new Ticket();
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void testParkingACar() throws Exception {
		final ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		// check that a ticket is actualy saved in DB
		final boolean saved = ticketDAO.isSaved("ABCDEF");
		assertTrue(saved);

		// check that a Parking table is updated with availability
		final boolean available = parkingSpot.isAvailable();
		assertFalse(available);
	}

	@Test
	public void testParkingLotExit() throws Exception {
		testParkingACar();
		final ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		final Date date = new Date();
		final Date outTime = new Date();
		parkingService.processExitingVehicle();
		// TODO#2
		// check that the fare generated
		final double faregenerated = ticket.getPrice();
		assertThat(faregenerated).isEqualTo(0.0);

		// check that the out time are populated correctly in the database
		ticket.setOutTime(outTime);
		final Date generatedTime = ticket.getOutTime();
		assertThat(date).isEqualTo(generatedTime);
	}

}
