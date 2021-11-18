package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
@ExtendWith(LoggingExtension.class)
class ParkingServiceTest {

	private static ParkingService parkingServiceUnderTest;
	private Logger logger;

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	@BeforeEach
	private void setUpPerTest() {
		try {
			logger.info("Appel avant chaque test");
			parkingServiceUnderTest = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - 60 * 60 * 1000));
			ticket.setVehicleRegNumber("ABCDEF");
		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	@AfterEach
	public void tearDownPerTest() {
		parkingServiceUnderTest = null;

	}

	@Test
	public void processIncomingVehicleTest() {

		// GIVEN
		ParkingSpot parkingSpot = new ParkingSpot(0, null, false);

		parkingSpotDAO.updateParking(parkingSpot);

		Ticket ticket = new Ticket();
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber(null);
		ticket.setPrice(0);
		ticket.setOutTime(null);
		ticketDAO.saveTicket(ticket);

		// WHEN
		parkingServiceUnderTest.processIncomingVehicle();

		// THEN
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
		assertFalse(parkingSpot.isAvailable());
	}

	@Test
	void getVehichleRegNumberTest() throws Exception {

		// GIVEN
		Ticket ticket = new Ticket();

		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		// WHEN
		ticket.setVehicleRegNumber(inputReaderUtil.readVehicleRegistrationNumber());
		parkingServiceUnderTest.getVehichleRegNumber();

		// THEN
		verify(inputReaderUtil, atLeast(1)).readVehicleRegistrationNumber();
		assertThat("ABCDEF").isEqualTo(ticket.getVehicleRegNumber());

	}

	@Test
	void getNextParkingNumberIfAvailableTest() {

		// GIVEN
		ParkingSpot parkingSpot = new ParkingSpot(0, null, false);

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(2);

		// WHEN
		parkingServiceUnderTest.getNextParkingNumberIfAvailable();

		// THEN
		verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
		assertFalse(parkingSpot.isAvailable());
	}

	@Test
	public void getVehichleTypeTest() {

		try {

			when(inputReaderUtil.readSelection()).thenReturn(1);

			parkingServiceUnderTest.getVehichleType();

			assertThat(parkingServiceUnderTest.getVehichleType()).isEqualTo(ParkingType.CAR);

			// GIVEN
			when(inputReaderUtil.readSelection()).thenReturn(2);

			// WHEN
			parkingServiceUnderTest.getVehichleType();

			// THEN
			assertThat(parkingServiceUnderTest.getVehichleType()).isEqualTo(ParkingType.BIKE);

		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

//	Testing type of the unknown vehicle should generate an IllegalArgumentException
	@Test
	public void getVehichleTypeTest_shouldThrowIllegalArgumentException_forUnknowParkingType() {

		// GIVEN
		try {
			when(inputReaderUtil.readSelection()).thenReturn(0);

			// WHEN
			parkingServiceUnderTest.getVehichleTypeTest_shouldThrowIllegalArgumentException_forUnknowParkingType();

			// THEN
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

	}

	// Testing process exiting vehicle should update Ticket and Parking and set a
	// parking spot as available
	@Test
	public void processExitingVehicleTest() throws Exception {

		try {

			// GIVEN
			ParkingSpot parkingSpot = new ParkingSpot(0, null, false);
			parkingSpotDAO.updateParking(parkingSpot);

			// WHEN
			parkingServiceUnderTest.processExitingVehicle();

			// THEN
			verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
			assertFalse(parkingSpot.isAvailable());

		} catch (Exception e) {
			logger.error("Unable to process exiting vehicle", e);
		}

	}

}
