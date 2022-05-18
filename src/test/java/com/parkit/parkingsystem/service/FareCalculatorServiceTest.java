package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
class FareCalculatorServiceTest {

  private FareCalculatorService fareCalculatorServiceTest;
  private Ticket ticket;
  private double discount;
  private static LogCaptor logcaptor;

  @Mock
  private static TicketDAO ticketDAO;
  @Mock
  private static ParkingSpot parkingSpot;

  @BeforeEach
  private void setUpPerTest() {
    fareCalculatorServiceTest = new FareCalculatorService();
    ticket = new Ticket();
    logcaptor = LogCaptor.forName("ParkingService");
    logcaptor.setLogLevelToInfo();
  }

  @AfterEach
  public void tearDownPerTest() {
    fareCalculatorServiceTest = null;
  }

  @Test
  void calculateFareCar() {

    // GIVEN
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    Date outTime = new Date();
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    when(parkingSpot.getParkingType()).thenReturn(ParkingType.CAR);

    // WHEN
    fareCalculatorServiceTest.calculateFare(ticket);

    // THEN
    assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR * 0.5);

  }

  @Test
  void calculateFareBike() {

    // GIVEN
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    Date outTime = new Date();
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    when(parkingSpot.getParkingType()).thenReturn(ParkingType.BIKE);

    // WHEN
    fareCalculatorServiceTest.calculateFare(ticket);

    // THEN
    assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR * 0.5);

  }

  // Free parking for first 30 minutes
  @Test
  void calculateFareTest_forCar_WithLessThan30MinutesParkingTime() {

    // GIVEN
    // If duration <= 30 min, parking time should give 0 * parking fare per hour
    final Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (0 * 60 * 1000));
    final Date outTime = new Date();
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    // WHEN
    fareCalculatorServiceTest.calculateFare(ticket);

    // THEN
    assertThat(ticket.getPrice()).isZero();

  }

  @Test
  public void calculateFareUnkownTypeKO() {

    // GIVEN
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    Date outTime = new Date();
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    when(parkingSpot.getParkingType()).thenReturn(null);

    // THEN
    assertThrows(NullPointerException.class, () -> fareCalculatorServiceTest.calculateFare(ticket));
    assertThat(logcaptor.getErrorLogs().contains("Unkown Type"));
  }

  /**
   * checkDiscount -5% discount For recurring users
   */
  @Test
  void checkDiscountTest() {

    // GIVEN
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    Date outTime = new Date();
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    String vehicleRegNumber = "ABCDEF";
    ticketDAO.isRecurring(vehicleRegNumber);
    when(ticketDAO.isRecurring(Mockito.anyString())).thenReturn(true);

    // WHEN
    fareCalculatorServiceTest.checkDiscount(ticket);

    // THEN
    verify(ticketDAO, Mockito.times(1)).isRecurring(Mockito.anyString());
    assertTrue(ticketDAO.isRecurring(vehicleRegNumber));
    // assertThat(fareCalculatorServiceTest.checkDiscount(ticket)).isEqualTo(à.95);

  }

  @Test
  void checkDiscountKOShouldassertException() {

    // GIVEN
    String vehicleRegNumber = "ABCDEF";
    ticketDAO.isRecurring(vehicleRegNumber);
    when(ticketDAO.isRecurring(Mockito.anyString())).thenReturn(false);

    // WHEN
    fareCalculatorServiceTest.checkDiscount(ticket);

    // THEN
    verify(ticketDAO, Mockito.times(1)).isRecurring(Mockito.anyString());
    assertFalse(ticketDAO.isRecurring(vehicleRegNumber));
    assertThat(fareCalculatorServiceTest.checkDiscount(ticket)).isEqualTo(1);

  }

  @Test
  public void calculateFareBikeWithLessThanOneHourParkingTime() {

    // GIVEN
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should
                                                                  // // // give 3/4th parking fare
    Date outTime = new Date();
    // ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    ticket.setPrice(ticket.getPrice() * discount);
    when(parkingSpot.getParkingType()).thenReturn(ParkingType.BIKE);

    // WHEN
    fareCalculatorServiceTest.calculateFare(ticket);

    // THEN
    assertThat(ticket.getPrice()).isEqualTo(0.25 * Fare.BIKE_RATE_PER_HOUR);
  }

  @Test
  public void calculateFareCarWithLessThanOneHourParkingTime() {

    // GIVEN
    Date inTime = new Date();
    // 45 minutes parking time shouldgive 3/4th parking fare
    inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    ticket.setPrice(ticket.getPrice() * discount);

    // WHEN
    fareCalculatorServiceTest.calculateFare(ticket);

    // THEN
    assertThat(ticket.getPrice()).isEqualTo(0.25 * Fare.CAR_RATE_PER_HOUR);
  }

  @Test
  public void calculateFareCarWithMoreThanADayParkingTime() {

    // GIVEN
    Date inTime = new Date();
    // 24 hours parking time should give 24 * parking fare per hour
    inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    // WHEN
    fareCalculatorServiceTest.calculateFare(ticket);

    // THEN
    assertThat(ticket.getPrice()).isEqualTo(23.5 * Fare.CAR_RATE_PER_HOUR);
  }

  @Test
  void calculateFareBikeWithMoreThanADayParkingTime() {

    // GIVEN
    // 24 hours parking time should give 24 * parking fare per hour
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
    Date outTime = new Date();
    parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    // WHEN
    fareCalculatorServiceTest.calculateFare(ticket);

    // THEN
    assertThat(ticket.getPrice()).isEqualTo(23.5 * Fare.BIKE_RATE_PER_HOUR);
  }

  @Test
  public void calculateFareBikeWithFutureInTime() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    assertThrows(IllegalArgumentException.class,
        () -> fareCalculatorServiceTest.calculateFare(ticket));
  }

  @Test
  public void calculateFareCarWithFutureInTime() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    assertThrows(IllegalArgumentException.class,
        () -> fareCalculatorServiceTest.calculateFare(ticket));
  }

  @ParameterizedTest(name = "{0} donne une IllegalArgumentException") @ValueSource(strings = {
      "calculateFareBike_shouldThrowIllegalArgumentException_WithNullOutTime",
      "calculateFareBikeWithFutureInTime" })
  void calculateFareBike_shouldThroNullPointerException(String arg) {
    assertThrows(NullPointerException.class, () -> fareCalculatorServiceTest.calculateFare(ticket));
  }

  @ParameterizedTest(name = "{0} donne une IllegalArgumentException") @ValueSource(strings = {
      "calculateFareCar_shouldThrowIllegalArgumentException_WithNullOutTime",
      "calculateFareCarWithFutureInTime" })
  void calculateFareCar_shouldThroNullPointerException(String arg) {
    assertThrows(NullPointerException.class, () -> fareCalculatorServiceTest.calculateFare(ticket));
  }
}