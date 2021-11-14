package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static ParkingSpot parkingSpot;
	private static Ticket ticket;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		ticketDAO = new TicketDAO();
		new DataBasePrepareService();
		ticket = new Ticket();
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		parkingSpotDAO.setDataBaseConfig(dataBaseTestConfig);
		TicketDAO.setDataBaseConfig(dataBaseTestConfig);
	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	void testParkingACar() throws Exception {
		final ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		// check that a ticket is actualy saved in DB
		final boolean saved = ticketDAO.isSaved("ABCDEF");
		assertFalse(saved);

		// check that a Parking table is updated with availability
		final boolean available = parkingSpot.isAvailable();
		assertFalse(available);
	}

	@Test
	void testParkingLotExit() throws Exception {
		// testParkingACar();
		final ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		final Date date = new Date();
		final Date outTime = new Date();
		parkingService.processExitingVehicle();
		// EXO#2
		// check that the fare generated
		final double faregenerated = ticket.getPrice();
		assertThat(faregenerated).isEqualTo(0.0);

		// check that the out time are populated correctly in the database
		ticket.setOutTime(outTime);
		final Date generatedTime = ticket.getOutTime();
		assertThat(date).isEqualTo(generatedTime);
	}

}
