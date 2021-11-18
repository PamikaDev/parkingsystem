package com.parkit.parkingsystem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

@ExtendWith(LoggingExtension.class)
class FareCalculatorServiceTest {

	private static Instant startedAt;

	private FareCalculatorService fareCalculatorServiceUnderTest;
	private Ticket ticket;
	private Date inTime;

	private Logger logger;

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@BeforeAll
	private static void setUp() {
		startedAt = Instant.now();
	}

	@BeforeEach
	private void setUpPerTest() {
		logger.info("Appel avant chaque test");
		ticket = new Ticket();
		inTime = new Date();
		ticket.setInTime(inTime);
		fareCalculatorServiceUnderTest = new FareCalculatorService();
	}

	@AfterEach
	public void tearDownPerTest() {
		logger.info("Appel après chaque test");
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

		assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR / 2);
	}

	@Test
	void calculateFareBike() {
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFareBike(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR / 2);
	}

	@Test
	void calculateFareUnkownType() {

		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		assertThrows(NullPointerException.class, () -> fareCalculatorServiceUnderTest.calculateFareUnkownType(ticket));
	}

	@Test
	void calculateFareCarWithFutureInTime() {

		inTime.setTime(System.currentTimeMillis() + 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		assertThrows(NullPointerException.class,
				() -> fareCalculatorServiceUnderTest.calculateFareCarWithFutureInTime(ticket));
	}

	@Test
	void calculateFareBikeWithFutureInTime() {

		inTime.setTime(System.currentTimeMillis() + 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		assertThrows(NullPointerException.class,
				() -> fareCalculatorServiceUnderTest.calculateFareBikeWithFutureInTime(ticket));
	}

	@ParameterizedTest(name = "{0} donne une IllegalArgumentException")
	@ValueSource(strings = { "calculateFareBike_shouldThrowIllegalArgumentException_WithNullOutTime",
			"calculateFareBikeWithFutureInTime" })
	void calculateFareBike_shouldThroNullPointerException(String arg) {
		assertThrows(NullPointerException.class,
				() -> fareCalculatorServiceUnderTest.calculateFareBike_shouldThroNullPointerException(ticket));
	}

	@ParameterizedTest(name = "{0} donne une IllegalArgumentException")
	@ValueSource(strings = { "calculateFareCar_shouldThrowIllegalArgumentException_WithNullOutTime",
			"calculateFareCarWithFutureInTime" })
	void calculateFareCar_shouldThroNullPointerException(String arg) {
		assertThrows(NullPointerException.class,
				() -> fareCalculatorServiceUnderTest.calculateFareCar_shouldThroNullPointerException(ticket));
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

		assertThat(ticket.getPrice()).isEqualTo(0.25 * Fare.CAR_RATE_PER_HOUR);
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

		assertThat(ticket.getPrice()).isEqualTo(0.25 * Fare.BIKE_RATE_PER_HOUR);
	}

	@Test
	void calculateFareCarWithMoreThanADayParkingTime() {

		// 24 hours parking time should give 24 * parking fare per hour
		inTime.setTime((System.currentTimeMillis() - 24 * 60 * 60 * 1000));
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
		inTime.setTime((System.currentTimeMillis() - 24 * 60 * 60 * 1000));
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
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR / 2);
	}

	// 5% discount For recurring BIKE users
	@Test
	void calculateFareTest_forBike_forRecurringUsers_shouldGetA5PerCentDisount() {

		// 1H parking time for recurring BIKE give 1 * parking fare per hour *0.95
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorServiceUnderTest.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR / 2);

	}

}
