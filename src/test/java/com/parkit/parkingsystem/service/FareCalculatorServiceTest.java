package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

	private static Instant startedAt;
	private FareCalculatorService fareCalculatorServiceUnderTest;
	private Ticket ticket;

	@BeforeAll
	private static void setUp() {
		startedAt = Instant.now();
	}

	@BeforeEach
	private void setUpPerTest() {
		fareCalculatorServiceUnderTest = new FareCalculatorService();
		ticket = new Ticket();
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
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFareCar(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR * 0.5);
	}

	@Test
	void calculateFareBike() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFareBike(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR * 0.5);
	}

	@Test
	void calculateFareUnkownType() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		assertThrows(NullPointerException.class, () -> fareCalculatorServiceUnderTest.calculateFare(ticket));
	}

	@ParameterizedTest(name = "{0} donne une IllegalArgumentException")
	@ValueSource(strings = { "calculateFareBike_shouldThrowIllegalArgumentException_WithNullOutTime",
			"calculateFareBikeWithFutureInTime" })
	void calculateFareBike_shouldThroNullPointerException(String arg) {
		assertThrows(NullPointerException.class,
				() -> fareCalculatorServiceUnderTest.calculateFare(ticket));
	}

	@ParameterizedTest(name = "{0} donne une IllegalArgumentException")
	@ValueSource(strings = { "calculateFareCar_shouldThrowIllegalArgumentException_WithNullOutTime",
			"calculateFareCarWithFutureInTime" })
	void calculateFareCar_shouldThroNullPointerException(String arg) {
		assertThrows(NullPointerException.class,
				() -> fareCalculatorServiceUnderTest.calculateFare(ticket));
	}

	@Test
	void calculateFareCarWithLessThanOneHourParkingTime() {

		// 45 minutes parking time should give 3/4th parking fare
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 45 * 60 * 1000);
		Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(0.25 * Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	void calculateFareBikeWithLessThanOneHourParkingTime() {

		// 45 minutes parking time should give 3/4th parking fare
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 45 * 60 * 1000);
		Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(0.25 * Fare.BIKE_RATE_PER_HOUR);
	}

	@Test
	void calculateFareCarWithMoreThanADayParkingTime() {

		// 24 hours parking time should give 24 * parking fare per hour
		Date inTime = new Date();
		inTime.setTime((System.currentTimeMillis() - 24 * 60 * 60 * 1000));
		Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(23.5 * Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	void calculateFareBikeWithMoreThanADayParkingTime() {

		// 24 hours parking time should give 24 * parking fare per hour
		Date inTime = new Date();
		inTime.setTime((System.currentTimeMillis() - 24 * 60 * 60 * 1000));
		Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(23.5 * Fare.BIKE_RATE_PER_HOUR);
	}

	// Free car parking for first 30 minutes
	@Test
	void calculateFareTest_forCar_WithLessThan30MinutesParkingTime_shouldBeFree() {

		// 20 minutes parking time should give 0 * parking fare per hour
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 0 * 60 * 1000);
		Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(0);
	}

	// Free Bike parking for first 30 minutes
	@Test
	void calculateFareTest_forBike_WithLessThan30MinutesParkingTime_shouldBeFree() {

		// If duration <= 30 min, parking time should give 0 * parking fare per hour
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 0 * 60 * 1000);
		Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(0);
	}

	// 5% discount For recurring CAR users
	@Test
	void calculateFareTest_forCar_forRecurringUsers_shouldGetA5PerCentDisount() {

		// 1H parking time for recurring CAR should give 1 * parking fare per hour *0.95
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFareTest_forCar_forRecurringUsers_shouldGetA5PerCentDisount(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR * 0.95 * 0.5);
	}

	// 5% discount For recurring BIKE users
	@Test
	void calculateFareTest_forBike_forRecurringUsers_shouldGetA5PerCentDisount() {

		// 1H parking time for recurring BIKE give 1 * parking fare per hour *0.95
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFareTest_forBike_forRecurringUsers_shouldGetA5PerCentDisount(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR * 0.95 * 0.5);

	}

}
