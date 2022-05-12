package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
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

  private static ParkingSpot parkingSpot;
  private static Ticket ticket;

  private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
  private static ParkingSpotDAO parkingSpotDAO;
  private static TicketDAO ticketDAO;
  private static DataBasePrepareService dataBasePrepareService;

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
    // when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    dataBasePrepareService.clearDataBaseEntries();
  }

  @AfterAll
  private static void tearDown() {

  }

  @Test
  void testParkingACar() {

    final ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO,
        ticketDAO);
    parkingService.processIncomingVehicle();

    // Check that a ticket is actually saved in DB.
    final boolean saved = ticketDAO.isSaved("ABCDEF");
    assertFalse(saved);

    // Check that Parking table is updated with availability
    final boolean available = parkingSpot.isAvailable();
    assertFalse(available);
  }

  @Test
  void testParkingLotExit() throws FileNotFoundException, IOException {
    testParkingACar();
    final ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO,
        ticketDAO);

    parkingService.processExitingVehicle();
    final Date date = new Date();
    final Date outTime = new Date();

    // Check that the fare generated and out time are populated correctly in the database
    final double faregenerated = ticket.getPrice();
    ticket.setOutTime(outTime);
    final Date generatedTime = ticket.getOutTime();
    assertFalse(generatedTime.before(date));
    assertEquals(0.0, faregenerated);
  }
}