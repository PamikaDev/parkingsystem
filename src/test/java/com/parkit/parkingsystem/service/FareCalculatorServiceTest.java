package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

@ExtendWith(MockitoExtension.class)
class FareCalculatorServiceTest {

  private FareCalculatorService fareCalculatorServiceTest;
  private Ticket ticket;
  @Mock
  private TicketDAO ticketDAO;

  @BeforeEach
  private void setUpPerTest() {
    fareCalculatorServiceTest = new FareCalculatorService();
    ticket = new Ticket();
  }

  @AfterEach
  public void tearDownPerTest() {
    fareCalculatorServiceTest = null;
  }

  @Test
  void calculateFareCar() {
    final Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    final Date outTime = new Date();
    final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    fareCalculatorServiceTest.calculateFare(ticket);

    assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR * 0.5);
  }

  @Test
  void calculateFareBike() {
    final Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    final Date outTime = new Date();
    final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    fareCalculatorServiceTest.calculateFare(ticket);

    assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR * 0.5);
  }

  // Free car parking for first 30 minutes
  @Test
  void calculateFareTest_forCar_WithLessThan30MinutesParkingTime() {

    // 20 minutes parking time should give 0 * parking fare per hour
    final Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (0 * 60 * 1000));
    final Date outTime = new Date();
    final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    fareCalculatorServiceTest.calculateFare(ticket);

    assertThat(ticket.getPrice()).isZero();
  }

  // Free Bike parking for first 30 minutes
  @Test
  void calculateFareTest_forBike_WithLessThan30MinutesParkingTime() {

    // If duration <= 30 min, parking time should give 0 * parking fare per hour
    final Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (0 * 60 * 1000));
    final Date outTime = new Date();
    final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    fareCalculatorServiceTest.calculateFare(ticket);

    assertThat(ticket.getPrice()).isZero();
  }

  // 5% discount For recurring CAR users
  @Test
  void calculateFareCarForRecurringUser() {

    // 1H parking time for recurring CAR should give 1 * parking fare per hour *0.95
    final Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    final Date outTime = new Date();
    final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    fareCalculatorServiceTest.calculateFare(ticket);

    assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR * 0.95 * 0.5);
  }

  // 5% discount For recurring BIKE users
  @Test
  void calculateFareBikeForRecurringUser() {

    // 1H parking time for recurring BIKE give 1 * parking fare per hour *0.95
    final Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    final Date outTime = new Date();
    final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    fareCalculatorServiceTest.calculateFare(ticket);

    assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR * 0.95 * 0.5);

  }

  @Test
  public void calculateFareUnkownType() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    assertThrows(NullPointerException.class, () -> fareCalculatorServiceTest.calculateFare(ticket));
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
  public void calculateFareBikeWithLessThanOneHourParkingTime() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should
                                                                  // give 3/4th parking fare
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorServiceTest.calculateFare(ticket);
    assertThat(ticket.getPrice()).isEqualTo(0.25 * Fare.BIKE_RATE_PER_HOUR);
  }

  @Test
  public void calculateFareCarWithLessThanOneHourParkingTime() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should
                                                                  // give 3/4th parking fare
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorServiceTest.calculateFare(ticket);
    assertThat(ticket.getPrice()).isEqualTo(0.25 * Fare.CAR_RATE_PER_HOUR);
  }

  @Test
  public void calculateFareCarWithMoreThanADayParkingTime() {
    // 24 hours parking time should give 24 * parking fare per hour
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorServiceTest.calculateFare(ticket);
    assertThat(ticket.getPrice()).isEqualTo(23.5 * Fare.CAR_RATE_PER_HOUR);
  }

  @Test
  void calculateFareBikeWithMoreThanADayParkingTime() {

    // 24 hours parking time should give 24 * parking fare per hour
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);

    fareCalculatorServiceTest.calculateFare(ticket);

    assertThat(ticket.getPrice()).isEqualTo(23.5 * Fare.BIKE_RATE_PER_HOUR);
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
