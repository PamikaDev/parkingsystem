//package com.parkit.parkingsystem.util;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.util.Date;
//import java.util.Scanner;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import com.parkit.parkingsystem.model.ParkingSpot;
//import com.parkit.parkingsystem.model.Ticket;
//
//class InputReaderUtilTest {
//
//  private InputReaderUtil inputReaderUtilTest;
//  private static Scanner scan = new Scanner(System.in);
//  private ParkingSpot parkingSpot;
//  Ticket ticket = new Ticket();
//
//  @BeforeEach
//  private void setUpPerTest() {
//    inputReaderUtilTest = new InputReaderUtil();
//  }
//
//  @Test
//  void readSelectionTest() {
//
//    int input = Integer.parseInt(scan.nextLine());
//    Date outTime = new Date();
//    Date inTime = new Date();
//    ticket.setInTime(inTime);
//    ticket.setOutTime(outTime);
//    ticket.setParkingSpot(parkingSpot);
//    ticket.setPrice(ticket.getPrice());
//
//    // WHEN
//    inputReaderUtilTest.readSelection();
//
//    // THEN
//    assertThat(inputReaderUtilTest.readSelection()).isEqualTo(input);
//  }
//
//  @Test
//  void readVehicleRegistrationNumberTest() throws Exception {
//
//    // GIVEN
//
//    String vehicleRegNumber = scan.nextLine();
//    ticket.setVehicleRegNumber(vehicleRegNumber);
//
//    // WHEN
//    inputReaderUtilTest.readVehicleRegistrationNumber();
//
//    // THEN
//    assertThat(inputReaderUtilTest.readVehicleRegistrationNumber()).isEqualTo(vehicleRegNumber);
//  }
//
//}
