//package com.parkit.parkingsystem.util;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.util.Date;
//import java.util.Scanner;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import com.parkit.parkingsystem.model.ParkingSpot;
//import com.parkit.parkingsystem.model.Ticket;
//
//class InputReaderUtilTest {
//
//  private int input;
//  private InputReaderUtil InputReaderUtilTest;
//  private static Scanner scan = new Scanner(System.in);
//  private ParkingSpot parkingSpot;
//  Ticket ticket = new Ticket();
//
//  @BeforeAll
//  static void setUpBeforeClass() throws Exception {
//    new InputReaderUtil();
//  }
//
//  @Test
//  void readSelectionTest() {
//
//    // GIVEN
//    input = Integer.parseInt(scan.nextLine());
//    Date outTime = new Date();
//    Date inTime = new Date();
//    // Ticket ticket = new Ticket();
//    ticket.setInTime(inTime);
//    ticket.setOutTime(outTime);
//    ticket.setParkingSpot(parkingSpot);
//    ticket.setPrice(ticket.getPrice());
//    // ticket.setVehicleRegNumber(vehicleRegNumber);
//
//    // WHEN
//    InputReaderUtilTest.readSelection();
//
//    // THEN
//    assertThat(Integer.parseInt(scan.nextLine())).isEqualTo(input);
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
//    InputReaderUtilTest.readVehicleRegistrationNumber();
//
//    // THEN
//    assertThat(scan.nextLine()).isEqualTo(vehicleRegNumber);
//  }
//
//}
