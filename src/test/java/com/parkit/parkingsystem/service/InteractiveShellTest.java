package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.util.InputReaderUtil;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
class InteractiveShellTest {

  private InteractiveShell interactiveShellTest;
  private static LogCaptor logcaptor;

  @Mock
  private ParkingService parkingService;

  @Mock
  private ParkingSpotDAO parkingSpotDAO;
  @Mock
  private InputReaderUtil inputReaderUtil;
  @Mock
  private TicketDAO ticketDAO;

  public InteractiveShell getInteractiveShellTest() {
    return interactiveShellTest;
  }

  public void setInteractiveShellTest(InteractiveShell interactiveShellTest) {
    this.interactiveShellTest = interactiveShellTest;
  }

  @BeforeAll
  private static void setUp() {
    new InteractiveShell();
  }

  @BeforeEach
  private void setUpPerTest() {
    setInteractiveShellTest(new InteractiveShell());
    logcaptor = LogCaptor.forName("ParkingService");
    logcaptor.setLogLevelToInfo();
  }

  @AfterEach
  public void tearDownPerTest() {
    setInteractiveShellTest(null);
  }

  @Test
  void loadInterfaceTest() {
    inputReaderUtil.readSelection();
    InteractiveShell.loadInterface();
  }

  @Test
  void loadInterfaceKoShouldassertException() {
    assertThat(logcaptor.getErrorLogs()
        .contains("Unsupported option. Please enter a number corresponding to the provided menu"));
  }

}
