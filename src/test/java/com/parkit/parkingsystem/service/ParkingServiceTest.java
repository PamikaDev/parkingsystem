package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
  private Ticket ticket;
  private static LogCaptor logcaptor;
  @Mock
  private static ParkingSpot parkingSpot;

  @Mock
  private static FareCalculatorService fareCalculatorService;
  @Mock
  private static InputReaderUtil inputReaderUtil;
  @Mock
  private static ParkingSpotDAO parkingSpotDAO;
  @Mock
  private static TicketDAO ticketDAO;

  @BeforeEach
  private void setUpPerTest() {
    logcaptor = LogCaptor.forName("ParkingService");
    logcaptor.setLogLevelToInfo();
    parkingServiceTest = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ticket = new Ticket();
    ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
    ticket.setParkingSpot(parkingSpot);
    ticket.setVehicleRegNumber("ABCDEF");
    Date outTime = new Date();
    ticket.setOutTime(outTime);
  }

  @AfterEach
  public void tearDownPerTest() {
    parkingServiceTest = null;
  }

  @Test
  void processIncomingVehicleTest() throws Exception, IOException {

    // GIVEN
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(Mockito.any(ParkingType.class))).thenReturn(1);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(null);
    when(parkingSpotDAO.updateParking(Mockito.any(ParkingSpot.class))).thenReturn(true);
    when(ticketDAO.saveTicket(Mockito.any(Ticket.class))).thenReturn(true);

    // WHEN
    parkingServiceTest.processIncomingVehicle();

    // THEN
    verify(inputReaderUtil, Mockito.times(1)).readSelection();
    assertFalse(parkingSpot.isAvailable());

  }

  @Test
  void processIncomingVehicleKOShouldassertException() throws Exception, IOException {

    // GIVEN
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(Mockito.any(ParkingType.class))).thenReturn(1);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(null);
    when(parkingSpotDAO.updateParking(Mockito.any(ParkingSpot.class))).thenReturn(true);
    when(ticketDAO.saveTicket(Mockito.any(Ticket.class))).thenReturn(true);

    // WHEN
    parkingServiceTest.processIncomingVehicle();

    // THEN
    assertThat(logcaptor.getErrorLogs().contains("Unable to process exiting vehicle"));

  }

  @Test
  void processIncomingVehicleKOShouldReturnFalse() throws Exception, IOException {

    // GIVEN
    parkingSpot.setId(0);
    ticket.setParkingSpot(null);

    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(Mockito.any(ParkingType.class))).thenReturn(1);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(null);
    when(parkingSpotDAO.updateParking(Mockito.any(ParkingSpot.class))).thenReturn(false);
    when(ticketDAO.saveTicket(Mockito.any(Ticket.class))).thenReturn(false);

    // WHEN
    parkingServiceTest.processIncomingVehicle();

    // THEN
    assertFalse(parkingSpot.isAvailable());
    assertThat(logcaptor.getErrorLogs().contains("Unable to process exiting vehicle"));

  }

  @Test
  void processIncomingVehicleOKVehicleAlreadyInside() throws Exception, IOException {

    // GIVEN
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(Mockito.any(ParkingType.class))).thenReturn(1);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    when(ticketDAO.getVehicleInside(Mockito.anyString())).thenReturn(true);

    // WHEN
    parkingServiceTest.processIncomingVehicle();

    // THEN
    assertThat(logcaptor.getErrorLogs().contains("Le véhicule est déjà dans le parking"));

  }

  @Test
  void processIncomingVehicleKOVehicleAlreadyInside() throws Exception, IOException {

    // GIVEN
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(Mockito.any(ParkingType.class))).thenReturn(1);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(null);
    when(ticketDAO.getVehicleInside(Mockito.anyString())).thenReturn(false);

    // WHEN
    parkingServiceTest.processIncomingVehicle();

    // THEN
    assertThat(logcaptor.getErrorLogs().contains("Le véhicule n'est plus dans le parking"));

  }

  @Test
  void processIncomingVehicleOKVehicleReccuring() throws Exception, IOException {

    // GIVEN
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(Mockito.any(ParkingType.class))).thenReturn(1);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    when(ticketDAO.getVehicleInside(Mockito.anyString())).thenReturn(false);
    when(ticketDAO.isRecurring(Mockito.anyString())).thenReturn(true);
    when(parkingSpotDAO.updateParking(Mockito.any(ParkingSpot.class))).thenReturn(true);
    when(ticketDAO.saveTicket(Mockito.any(Ticket.class))).thenReturn(true);

    // WHEN
    parkingServiceTest.processIncomingVehicle();

    // THEN
    assertThat(logcaptor.getInfoLogs().contains("L'utilisateur est recurrent"));

  }

  @Test
  void processIncomingVehicleKOVehicleReccuring() throws Exception, IOException {

    // GIVEN
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(parkingSpotDAO.getNextAvailableSlot(Mockito.any(ParkingType.class))).thenReturn(1);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    when(ticketDAO.getVehicleInside(Mockito.anyString())).thenReturn(false);
    when(ticketDAO.isRecurring(Mockito.anyString())).thenReturn(false);
    when(parkingSpotDAO.updateParking(Mockito.any(ParkingSpot.class))).thenReturn(true);
    when(ticketDAO.saveTicket(Mockito.any(Ticket.class))).thenReturn(true);

    // WHEN
    parkingServiceTest.processIncomingVehicle();

    // THEN
    assertThat(logcaptor.getInfoLogs().contains("L'utilisateur n'est pas recurrent"));

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
    assertThat(logcaptor.getErrorLogs()
        .contains("Error fetching parking number from DB. Parking slots might be full"));
    assertThat(logcaptor.getErrorLogs().contains("Error parsing user input for type of vehicle"));
    assertThat(logcaptor.getErrorLogs().contains("Error fetching next available parking slot"));

  }

  @Test
  void getVehichleTypeTest() {

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
    Date outTime = new Date();
    ticket = new Ticket();
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    parkingSpot.setAvailable(true);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
    ticket.setVehicleRegNumber(inputReaderUtil.readVehicleRegistrationNumber());
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    when(ticketDAO.getVehicleOutside(Mockito.anyString())).thenReturn(true);
    when(ticketDAO.getTicket(vehicleRegNumber)).thenReturn(ticket);
    parkingSpot.setAvailable(true);

    // WHEN
    parkingServiceTest.processExitingVehicle();

    // THEN
    verify(inputReaderUtil, Mockito.times(2)).readVehicleRegistrationNumber();
    assertTrue(parkingSpot.isAvailable());

  }

  @Test
  void processExitingVehicleOKVehicleAlreadyOutsideShouldassertException()
      throws Exception, IOException {

    // GIVEN
    String vehicleRegNumber = "ABCDEF";
    ticket.setParkingSpot(parkingSpot);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
    ticket.setVehicleRegNumber(inputReaderUtil.readVehicleRegistrationNumber());
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    when(ticketDAO.getVehicleOutside(Mockito.anyString())).thenReturn(true);
    when(ticketDAO.getTicket(vehicleRegNumber)).thenReturn(ticket);
    parkingSpot.setAvailable(true);

    // WHEN
    parkingServiceTest.processExitingVehicle();

    // THEN
    assertThat(logcaptor.getErrorLogs().contains("The vehicle is already outside"));
    assertTrue(parkingSpot.isAvailable());

  }

  @Test
  void processExitingVehicleOKUpdateTicketTest() throws Exception, IOException {

    // GIVEN
    String vehicleRegNumber = "ABCDEF";
    ticket.setParkingSpot(parkingSpot);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
    ticket.setVehicleRegNumber(inputReaderUtil.readVehicleRegistrationNumber());
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    when(ticketDAO.getVehicleOutside(Mockito.anyString())).thenReturn(false);
    when(ticketDAO.getTicket(vehicleRegNumber)).thenReturn(ticket);
    when(ticketDAO.updateTicket(Mockito.any(Ticket.class))).thenReturn(true);

    // WHEN
    parkingServiceTest.processExitingVehicle();

    // THEN
    assertThat(logcaptor.getErrorLogs().contains("The vehicle is already outside"));

  }

  @Test
  void processExitingVehicleKOUpdateTicketTest() throws Exception {

    // GIVEN
    ticket.setParkingSpot(parkingSpot);
    parkingSpot.setAvailable(false);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenThrow(Exception.class);

    // WHEN
    parkingServiceTest.processExitingVehicle();

    // THEN
    assertThat(
        logcaptor.getErrorLogs().contains("Unable to update ticket information. Error occurred"));
  }

  @Test
  void processExitingVehicleOKUpdateParkingTest() throws Exception {

    // GIVEN
    String vehicleRegNumber = "ABCDEF";
    Date outTime = new Date();
    ticket = new Ticket();
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
    when(ticketDAO.getTicket(Mockito.anyString())).thenReturn(ticket);
    parkingSpot.setAvailable(false);

    // WHEN
    parkingServiceTest.processExitingVehicle();

    // WHEN
    assertFalse(parkingSpot.isAvailable());
    assertThat(
        logcaptor.getInfoLogs().contains("Please pay the parking fare:" + ticket.getPrice()));
    assertThat(logcaptor.getInfoLogs().contains(
        "Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime));

  }

}
