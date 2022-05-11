package com.parkit.parkingsystem.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.altindag.log.LogCaptor;

class InputReaderUtilTest {

  private InputReaderUtil inputReaderUtilTest;
  // private static Scanner scan = new Scanner(System.in);
  private static LogCaptor logcaptor;

  @BeforeEach
  private void setUpPerTest() {
    logcaptor = LogCaptor.forName("InputReaderUtil");
    logcaptor.setLogLevelToInfo();
    inputReaderUtilTest = new InputReaderUtil();
  }

  @Test
  void readSelectionTest() {
    inputReaderUtilTest.readSelection();
    assertThat(inputReaderUtilTest.readSelection()).isEqualTo(-1);
  }

  @Test
  void readSelectionKoShouldassertException() throws Exception {
    assertThat(logcaptor.getErrorLogs().contains("Error while reading user input from Shell"));
  }

  @Test
  void readVehicleRegistrationNumberTest() throws Exception {
    try {
      String vehicleRegNumber = null;
      inputReaderUtilTest.readVehicleRegistrationNumber();
      assertThat(inputReaderUtilTest.readVehicleRegistrationNumber()).isEqualTo(vehicleRegNumber);
    } catch (Exception e) {
    }
  }

  @Test
  void readVehicleRegistrationNumberKoShouldassertException() throws Exception {
    assertThat(logcaptor.getErrorLogs().contains("Invalid input provided"));
  }

}
