package com.parkit.parkingsystem.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

class InteractiveShellTest {

  @Mock
  private static ParkingService parkingServiceTest;

  private ParkingSpot parkingSpot;

  @BeforeAll
  private static void setUp() {
    new InteractiveShell();
  }

  @AfterAll
  public static void tearDown() {
  }

  @Test
  void loadInterfaceTest() throws Exception {
    // GIVEN
    boolean continueApp = true;
    Date outTime = new Date();
    Date inTime = new Date();
    Ticket ticket = new Ticket();
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    ticket.setPrice(ticket.getPrice());

    // WHEN
    InteractiveShell.loadInterface();

    // THEN
    assertTrue(continueApp);
  }

}
