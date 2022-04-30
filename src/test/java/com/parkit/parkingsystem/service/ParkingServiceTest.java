package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
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
class ParkingServiceTest {

  private ParkingService parkingServiceTest;

  @Mock
  final Logger logger = LogManager.getLogger("ParkingService");
  @Mock
  private static InputReaderUtil inputReaderUtil;
  @Mock
  private static ParkingSpotDAO parkingSpotDAO;
  @Mock
  private static TicketDAO ticketDAO;
  @Mock
  private static FareCalculatorService fareCalculatorService;

  private Ticket ticket;

  @BeforeEach
  private void setUpPerTest() {
    parkingServiceTest = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    ticket = new Ticket();
    ticket.setInTime(new Date(System.currentTimeMillis() - 60 * 60 * 1000));
    ticket.setVehicleRegNumber("ABCDEF");
  }

  @AfterEach
  public void tearDownPerTest() {
    parkingServiceTest = null;
  }

  @Test
  void processIncomingVehicleTest() throws Exception {

    // GIVEN
    final ParkingSpot parkingSpot = new ParkingSpot(0, null, false);
    parkingSpotDAO.updateParking(parkingSpot);
    ticket.setParkingSpot(parkingSpot);
    ticket.setVehicleRegNumber(null);
    ticket.setPrice(0);
    ticket.setOutTime(null);
    ticketDAO.saveTicket(ticket);
    ticketDAO.vehicleInside(ticket.getVehicleRegNumber());
    ticketDAO.isRecurring(ticket.getVehicleRegNumber());
    logger.error("Unable to process incoming vehicle");

    // WHEN
    parkingServiceTest.processIncomingVehicle();

    // THEN
    verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    verify(ticketDAO, Mockito.times(1)).vehicleInside(any());
    verify(ticketDAO, Mockito.times(1)).isRecurring(any());
    verify(logger, Mockito.times(1)).error("Unable to process incoming vehicle");
    assertFalse(parkingSpot.isAvailable());
  }

  @Test
  void getVehichleRegNumberTest() throws Exception {

    // GIVEN
    final Ticket ticket = new Ticket();
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    ticket.setVehicleRegNumber(inputReaderUtil.readVehicleRegistrationNumber());
    ticketDAO.getTicket(ticket.getVehicleRegNumber());

    // WHEN
    parkingServiceTest.getVehichleRegNumber();

    // THEN
    verify(inputReaderUtil, atLeast(1)).readVehicleRegistrationNumber();
    verify(ticketDAO, Mockito.times(1)).getTicket(any());
    assertThat(ticket.getVehicleRegNumber()).isEqualTo("ABCDEF");
  }

  @Test
  void getNextParkingNumberIfAvailableTest() throws SQLException, Exception {

    // GIVEN
    final ParkingSpot parkingSpot = new ParkingSpot(0, null, false);
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(2);

    // WHEN
    parkingServiceTest.getNextParkingNumberIfAvailable();

    // THEN
    verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
    assertFalse(parkingSpot.isAvailable());
  }

  @Test
  void getVehichleTypeTest() {

    // GIVEN
    when(inputReaderUtil.readSelection()).thenReturn(1);

    // WHEN
    parkingServiceTest.getVehichleType();

    // THEN
    assertThat(parkingServiceTest.getVehichleType()).isEqualTo(ParkingType.CAR);

    // GIVEN
    when(inputReaderUtil.readSelection()).thenReturn(2);

    // WHEN
    parkingServiceTest.getVehichleType();

    // THEN
    assertThat(parkingServiceTest.getVehichleType()).isEqualTo(ParkingType.BIKE);

  }

  /*
   * Testing process exiting vehicle should update Ticket and Parking and set a parking spot as
   * available
   */
  @Test
  void processExitingVehicleTest() throws Exception {

    // GIVEN
    final ParkingSpot parkingSpot = new ParkingSpot(0, null, false);
    parkingSpotDAO.updateParking(parkingSpot);
    final Ticket ticket = new Ticket();
    ticket.setInTime(new Date(System.currentTimeMillis() - 60 * 60 * 1000));
    ticket.setParkingSpot(parkingSpot);
    ticket.setVehicleRegNumber("ABCDEF");
    fareCalculatorService.calculateFare(ticket);
    inputReaderUtil.readVehicleRegistrationNumber();
    when(ticketDAO.getTicket(any())).thenReturn(ticket);
    ticketDAO.updateTicket(ticket);
    logger.error("Unable to process exiting vehicle");

    // WHEN
    parkingServiceTest.processExitingVehicle();

    // THEN
    verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    verify(ticketDAO, Mockito.times(2)).updateTicket(any(Ticket.class));
    verify(fareCalculatorService, atLeast(1)).calculateFare(ticket);
    verify(inputReaderUtil, atLeast(1)).readVehicleRegistrationNumber();
    verify(logger, Mockito.times(1)).error("Unable to process exiting vehicle");
    assertThat(ticket.getVehicleRegNumber()).isEqualTo("ABCDEF");
    assertFalse(parkingSpot.isAvailable());
  }
}