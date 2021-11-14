package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorServiceUnderTest;
	private Ticket ticket;
	private Date inTime;

	private static Instant startedAt;

	@BeforeAll
	private static void setUp() {
		startedAt = Instant.now();
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();
		inTime = new Date();
		ticket.setInTime(inTime);
		fareCalculatorServiceUnderTest = new FareCalculatorService(null);
	}

	@AfterEach
	public void tearDownPerTest() {
		fareCalculatorServiceUnderTest = null;
	}

	@AfterAll
	public static void tearDown() {
		Instant endedAt = Instant.now();
		long duration = Duration.between(startedAt, endedAt).toMillis();
		System.out.println(MessageFormat.format("Durée des tests : {0} ms", duration));
	}

	@Test
	void calculateFareCar() {
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFareCar(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	void calculateFareBike() {
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFareBike(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR);
	}

	@Test
	void calculateFareUnkownType() {

		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		assertThrows(NullPointerException.class, () -> fareCalculatorServiceUnderTest.calculateFare(ticket));
	}

	@Test
	void calculateFareCarWithFutureInTime() {

		inTime.setTime(System.currentTimeMillis() + 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		assertThrows(IllegalArgumentException.class, () -> fareCalculatorServiceUnderTest.calculateFare(ticket));
	}

	@Test
	void calculateFareBikeWithFutureInTime() {

		inTime.setTime(System.currentTimeMillis() + 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		assertThrows(IllegalArgumentException.class, () -> fareCalculatorServiceUnderTest.calculateFare(ticket));
	}

	@Test
	void calculateFareCar_shouldThrowIllegalArgumentException_WithNullOutTime() {
		// GIVEN
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		outTime.setTime(0); // null out-time should generate an IllegalArgumentException
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		try {
			fareCalculatorServiceUnderTest.calculateFare(ticket);
		}
		// THEN
		catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

	}

	@Test
	void calculateFareBike_shouldThrowIllegalArgumentException_WithNullOutTime() {
		// GIVEN
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		outTime.setTime(0); // null out-time should generate an IllegalArgumentException
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		try {
			fareCalculatorServiceUnderTest.calculateFare(ticket);
		}
		// THEN
		catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@ParameterizedTest(name = "{0} doit être égal à NullPointerException")
	@ValueSource(strings = { "WithNullOutTime", "WithFutureInTime" })
	void calculateFareCar_shouldThroNullPointerException(String arg) {

		assertThrows(NullPointerException.class, () -> fareCalculatorServiceUnderTest.calculateFare(ticket));
	}

	@Test
	void calculateFareCarWithLessThanOneHourParkingTime() {

		// 45 minutes parking time should give 3/4th parking fare
		inTime.setTime(System.currentTimeMillis() - 45 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR * 0.25);
	}

	@Test
	void calculateFareBikeWithLessThanOneHourParkingTime() {

		// 45 minutes parking time should give 3/4th parking fare
		inTime.setTime(System.currentTimeMillis() - 45 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR * 0.25);
	}

	@Test
	void calculateFareCarWithMoreThanADayParkingTime() {

		// 24 hours parking time should give 24 * parking fare per hour
		inTime.setTime((long) (System.currentTimeMillis() - 23.5 * 60 * 60 * 1000));
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(23.5 * Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	void calculateFareBikeWithMoreThanADayParkingTime() {

		// 24 hours parking time should give 24 * parking fare per hour
		inTime.setTime((long) (System.currentTimeMillis() - 23.5 * 60 * 60 * 1000));
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(23.5 * Fare.BIKE_RATE_PER_HOUR);
	}

	// Free car parking for first 30 minutes
	@Test
	void calculateFareTest_forCar_WithLessThan30MinutesParkingTime_shouldBeFree() {

		// 20 minutes parking time should give 0 * parking fare per hour
		inTime.setTime(System.currentTimeMillis() - 0 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(0);
	}

	// Free Bike parking for first 30 minutes
	@Test
	void calculateFareTest_forBike_WithLessThan30MinutesParkingTime_shouldBeFree() {

		// If duration <= 30 min, parking time should give 0 * parking fare per hour
		inTime.setTime(System.currentTimeMillis() - 0 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(0);
	}

	// 5% discount For recurring CAR users
	@Test
	void calculateFareTest_forCar_forRecurringUsers_shouldGetA5PerCentDisount() {

		// 1H parking time for recurring CAR should give 1 * parking fare per hour *0.95
		inTime.setTime((long) (System.currentTimeMillis() - 60 * 60 * 1000 * 0.95));
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.RECURRING_CAR_RATE_PER_HOUR);
	}

	// 5% discount For recurring BIKE users
	@Test
	void calculateFareTest_forBike_forRecurringUsers_shouldGetA5PerCentDisount() {

		// 1H parking time for recurring BIKE give 1 * parking fare per hour *0.95
		inTime.setTime((long) (System.currentTimeMillis() - 60 * 60 * 1000 * 0.95));
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.RECURRING_BIKE_RATE_PER_HOUR);

	}

}
