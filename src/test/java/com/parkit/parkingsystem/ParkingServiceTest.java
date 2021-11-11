package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

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
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	@BeforeEach
	private void setUpPerTest() {
		try {
			final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			final Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - 60 * 60 * 1000));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");
//			when(ticketDAO.getTicket(any())).thenReturn(ticket);
//			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
//			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
//			when(inputReaderUtil.readSelection()).thenReturn(1);
//			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	// Get registered number of the vehicle
	@Test
	public void getVehichleRegNumberTest() throws Exception {
		// GIVEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		Ticket ticket = new Ticket();

		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		// WHEN
		ticket.setVehicleRegNumber(inputReaderUtil.readVehicleRegistrationNumber());
		parkingService.getVehichleRegNumber();

		// THEN
		verify(inputReaderUtil, atLeast(1)).readVehicleRegistrationNumber();
		assertThat("ABCDEF").isEqualTo(ticket.getVehicleRegNumber());
	}

	// check if the parking spot is available
	@Test
	void getNextParkingNumberIfAvailableTest() {
		// GIVEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		ParkingSpot parkingSpot = new ParkingSpot(0, null, false);

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(2);

		// WHEN
		parkingSpot = parkingService.getNextParkingNumberIfAvailable();

		// THEN
		verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
		assertTrue(parkingSpot.isAvailable());
	}

	// Testing type of the vehicle should return a CAR
	@Test
	public void getVehichleTypeTest_shouldReturnCAR() {
		// GIVEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		when(inputReaderUtil.readSelection()).thenReturn(1);

		// WHEN
		parkingService.getVehichleType();

		// THEN
		assertThat(parkingService.getVehichleType()).isEqualTo(ParkingType.CAR);
	}

	// Testing type of the vehicle should return a BIKE
	@Test
	public void getVehichleTypeTest_shouldReturnBIKE() {
		// GIVEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		when(inputReaderUtil.readSelection()).thenReturn(2);

		// WHEN
		parkingService.getVehichleType();

		// THEN
		assertThat(parkingService.getVehichleType()).isEqualTo(ParkingType.BIKE);
	}

//	Testing type of the unknown vehicle should generate an IllegalArgumentException
	@Test
	public void getVehichleTypeTest_shouldThrowIllegalArgumentException_forUnknowParkingType() {
		// GIVEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		try {
			when(inputReaderUtil.readSelection()).thenReturn(0);

			// WHEN
			parkingService.getVehichleType();

			// THEN
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			assertTrue(e.getMessage().contains("Entered input is invalid"));
		}

	}

	// Testing process exiting vehicle should update Ticket and Parking and set a
	// parking spot as available
	@Test
	public void processExitingVehicleTest() throws Exception {

		// GIVEN
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		Ticket ticket = new Ticket();
		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");

		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

		when(ticketDAO.getTicket(any())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

		// WHEN
		parkingService.processExitingVehicle();

		// THEN
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
		verify(ticketDAO, times(1)).updateTicket(any(Ticket.class));
		assertTrue(parkingSpot.isAvailable());
	}

}
