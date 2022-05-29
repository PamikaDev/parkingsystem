//package com.parkit.parkingsystem.service;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import com.parkit.parkingsystem.dao.ParkingSpotDAO;
//import com.parkit.parkingsystem.dao.TicketDAO;
//import com.parkit.parkingsystem.util.InputReaderUtil;
//
//import nl.altindag.log.LogCaptor;
//
//@ExtendWith(MockitoExtension.class)
//class InteractiveShellTest {
//
//  private InteractiveShell interactiveShellTest;
//  private static LogCaptor logcaptor;
//
//  @Mock
//  private ParkingService parkingService;
//
//  @Mock
//  private ParkingSpotDAO parkingSpotDAO;
//  @Mock
//  private InputReaderUtil inputReaderUtil;
//  @Mock
//  private TicketDAO ticketDAO;
//
//  public InteractiveShell getInteractiveShellTest() {
//    return interactiveShellTest;
//  }
//
//  public void setInteractiveShellTest(InteractiveShell interactiveShellTest) {
//    this.interactiveShellTest = interactiveShellTest;
//  }
//
//  @BeforeAll
//  private static void setUp() {
//    new InteractiveShell();
//  mock = EasyMock.createMock(InteractiveShell.class);
//  }
//
//  @BeforeEach
//  private void setUpPerTest() {
//    setInteractiveShellTest(new InteractiveShell());
//    logcaptor = LogCaptor.forName("ParkingService");
//    logcaptor.setLogLevelToInfo();
//  }
//
//  @AfterEach
//  public void tearDownPerTest() {
//    setInteractiveShellTest(null);
//  }
//
//  @Test
//  void loadInterfaceTest() {
//
//    boolean continueApp = true;
//    while (continueApp) {
//      int option = inputReaderUtil.readSelection();
//      when(inputReaderUtil.readSelection()).thenReturn(option);
//
//      InteractiveShell.loadInterface();
//
//      assertThat(inputReaderUtil.readSelection()).isEqualTo(option);
//      continueApp = false;
//      break;
//    }
//
//  }
//
//  @Test
//  void loadInterfaceKoShouldassertException() {
//    assertThat(logcaptor.getErrorLogs()
//        .contains("Unsupported option. Please enter a number corresponding to the provided menu"));
//  }
//
//}
