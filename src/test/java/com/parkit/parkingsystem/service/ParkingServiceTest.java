package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;

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

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

  private ParkingService parkingServiceTest;
  private static LogCaptor logcaptor;
  private Ticket ticket;

  @Mock
  private static FareCalculatorService fareCalculatorService = new FareCalculatorService();;
  @Mock
  private static InputReaderUtil inputReaderUtil;
  @Mock
  private static ParkingSpotDAO parkingSpotDAO;
  @Mock
  private static TicketDAO ticketDAO;
  @Mock
  private static ParkingSpot parkingSpot;

  @BeforeEach
  private void setUpPerTest() {
    logcaptor = LogCaptor.forName("ParkingService");
    logcaptor.setLogLevelToInfo();
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
  void processIncomingVehicleTest() throws Exception, IOException {

    // GIVEN
    String vehicleRegNumber = "ABCDEF";
    parkingSpotDAO.updateParking(parkingSpot);
    ticketDAO.saveTicket(ticket);
    ticketDAO.getVehicleInside(vehicleRegNumber);
    ticketDAO.isRecurring(vehicleRegNumber);
    inputReaderUtil.readVehicleRegistrationNumber();
    parkingSpot.setId(inputReaderUtil.readSelection());

    // WHEN
    parkingServiceTest.processIncomingVehicle();

    // THEN
    verify(ticketDAO, Mockito.times(1)).getVehicleInside(vehicleRegNumber);
    verify(ticketDAO, Mockito.times(1)).isRecurring(vehicleRegNumber);
    verify(inputReaderUtil, Mockito.times(1)).readVehicleRegistrationNumber();
    verify(parkingSpot, Mockito.times(1)).setId(inputReaderUtil.readSelection());
    verify(ticketDAO, Mockito.times(1)).saveTicket(ticket);
    verify(inputReaderUtil, Mockito.times(3)).readSelection();
    verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    assertFalse(parkingSpot.isAvailable());

  }

  @Test
  void getVehichleRegNumberTest() throws Exception {

    // GIVEN
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    ticket.setVehicleRegNumber(inputReaderUtil.readVehicleRegistrationNumber());

    // WHEN
    parkingServiceTest.getVehichleRegNumber();

    // THEN
    verify(inputReaderUtil, atLeast(1)).readVehicleRegistrationNumber();
    assertThat(ticket.getVehicleRegNumber()).isEqualTo("ABCDEF");
  }

  @Test
  void getNextParkingNumberIfAvailableTest() {

    // GIVEN
    when(inputReaderUtil.readSelection()).thenReturn(1);

    // WHEN
    parkingServiceTest.getNextParkingNumberIfAvailable();

    // THEN
    assertFalse(parkingSpot.isAvailable());

  }

  @Test
  void getNextParkingNumberIfAvailableKOShouldassertException() {

    // GIVEN
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(inputReaderUtil.readSelection()).thenThrow(IllegalArgumentException.class);
    // WHEN
    parkingServiceTest.getNextParkingNumberIfAvailable();

    // THEN
    assertFalse(parkingSpot.isAvailable());
    assertThat(logcaptor.getErrorLogs().contains("Error parsing user input for type of vehicle"));
  }

  @Test
  void getVehichleTypeTest() {

    // try {
    when(inputReaderUtil.readSelection()).thenReturn(1);
    parkingServiceTest.getVehichleType();
    assertThat(parkingServiceTest.getVehichleType()).isEqualTo(ParkingType.CAR);

    when(inputReaderUtil.readSelection()).thenReturn(2);
    parkingServiceTest.getVehichleType();
    assertThat(parkingServiceTest.getVehichleType()).isEqualTo(ParkingType.BIKE);

    when(inputReaderUtil.readSelection()).thenReturn(0);
    parkingServiceTest.processIncomingVehicle();
    parkingServiceTest.processExitingVehicle();
  }

  /**
   * Testing process exiting vehicle should update Ticket and Parking and set a parking spot as
   * available.
   *
   * @throws Exception
   */
  @Test
  void processExitingVehicleTest() throws Exception {

    // GIVEN
    String vehicleRegNumber = "ABCDEF";
    parkingSpot.setAvailable(true);
    parkingSpotDAO.updateParking(parkingSpot);
    ticketDAO.getTicket(vehicleRegNumber);
    when(ticketDAO.getVehicleOutside(vehicleRegNumber)).thenReturn(true);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    fareCalculatorService.checkDiscount(ticket);

    // WHEN
    parkingServiceTest.processExitingVehicle();

    // THEN
    verify(ticketDAO, Mockito.times(1)).getVehicleOutside(vehicleRegNumber);
    verify(inputReaderUtil, Mockito.times(1)).readVehicleRegistrationNumber();
    verify(ticketDAO, Mockito.times(1)).getTicket(vehicleRegNumber);
    verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    verify(ticketDAO, Mockito.times(1)).getTicket(vehicleRegNumber);
  }

  @Test
  void processExitingVehicleKoShouldassertException() throws Exception {

    // GIVEN
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenThrow(Exception.class);

    // WHEN
    parkingServiceTest.processExitingVehicle();

    // THEN
    assertFalse(parkingSpot.isAvailable());
    assertThat(logcaptor.getErrorLogs().contains("Unable to process exiting vehicle"));

  }

}
