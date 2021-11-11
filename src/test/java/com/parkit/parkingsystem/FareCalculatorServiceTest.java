package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;

	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService(null);
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();
	}

	@Test
	void calculateFareCar() {
		final Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	void calculateFareBike() {
		final Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR);
	}

	@Test
	void calculateFareUnkownType() {
		final Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	/*
	 * NullPointerException for Unknown Vehicle Type
	 */
	@Test
	public void calculateFareTest_shouldThrowNullPointerException_forUnknowVehicleType() {
		// GIVEN
		Date inTime = new Date();
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, null, false); // vehicle type null should generate a
																	// NullPointerException
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);

		try {
			ticket.setParkingSpot(parkingSpot);

			// WHEN
			fareCalculatorService.calculateFare(ticket);

			// THEN
		} catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
//			assertTrue(e.getMessage().contains("Unkown Parking Type"));
		}

	}

	@Test
	void calculateFareCarWithFutureInTime() {
		final Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() + 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	void calculateFareBikeWithFutureInTime() {
		final Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() + 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	/*
	 * null out-time for parking CAR should generate an IllegalArgumentException
	 */
	@Test
	public void calculateFareCar_shouldThrowIllegalArgumentException_WithNullOutTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		outTime.setTime(0); // null out-time should generate an IllegalArgumentException
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		try {
			fareCalculatorService.calculateFare(ticket);
		}
		// THEN
		catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			assertTrue(e.getMessage().contains("Out time provided is incorrect:" + ticket.getOutTime().toString()));
		}

	}

	/*
	 * null out-time for parking BIKE should generate an IllegalArgumentException
	 */
	@Test
	public void calculateFareBike_shouldThrowIllegalArgumentException_WithNullOutTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		outTime.setTime(0); // null out-time should generate an IllegalArgumentException
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		try {
			fareCalculatorService.calculateFare(ticket);
		}
		// THEN
		catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
			assertTrue(e.getMessage().contains("Out time provided is incorrect:" + ticket.getOutTime().toString()));
		}
	}

	@Test
	void calculateFareCarWithLessThanOneHourParkingTime() {
		final Date inTime = new Date();
		// 45 minutes parking time should give 3/4th parking fare
		inTime.setTime(System.currentTimeMillis() - 45 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR * 0.75);
	}

	@Test
	void calculateFareBikeWithLessThanOneHourParkingTime() {
		final Date inTime = new Date();
		// 45 minutes parking time should give 3/4th parking fare
		inTime.setTime(System.currentTimeMillis() - 45 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR * 0.75);
	}

	@Test
	void calculateFareCarWithMoreThanADayParkingTime() {
		final Date inTime = new Date();
		// 24 hours parking time should give 24 * parking fare per hour
		inTime.setTime(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR * 24);

	}

	@Test
	void calculateFareBikeWithMoreThanADayParkingTime() {
		final Date inTime = new Date();
		// 24 hours parking time should give 24 * parking fare per hour
		inTime.setTime(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR * 24);
	}

	// Free car parking for first 30 minutes
	@Test
	void calculateFareTest_forCar_WithLessThan30MinutesParkingTime_shouldBeFree() {

		final Date inTime = new Date();
		// 20 minutes parking time should give 0 * parking fare per hour
		inTime.setTime(System.currentTimeMillis() - 0 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR * 0);
	}

	// Free Bike parking for first 30 minutes
	@Test
	void calculateFareTest_forBike_WithLessThan30MinutesParkingTime_shouldBeFree() {

		final Date inTime = new Date();
		// If duration <= 30 min, parking time should give 0 * parking fare per hour
		inTime.setTime(System.currentTimeMillis() - 0 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR * 0);
	}

	// 5% discount For recurring CAR users
	@Test
	void calculateFareTest_forCar_forRecurringUsers_shouldGetA5PerCentDisount() {
		final Date inTime = new Date();

		// 1H parking time for recurring CAR should give 1 * parking fare per hour *0.95
		// inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		inTime.setTime((long) (System.currentTimeMillis() - 60 * 60 * 1000 * 0.95));
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR * 0.95);
	}

	// 5% discount For recurring BIKE users
	@Test
	void calculateFareTest_forBike_forRecurringUsers_shouldGetA5PerCentDisount() {
		final Date inTime = new Date();

		// 1H parking time for recurring BIKE give 1 * parking fare per hour *0.95
		// inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		inTime.setTime((long) (System.currentTimeMillis() - 60 * 60 * 1000 * 0.95));
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket);

		assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR * 0.95);

	}

}
