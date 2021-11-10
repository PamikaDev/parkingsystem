package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;

	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();
	}

	@Test
	public void calculateFareCar() {
		final Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);

		assertThat(Fare.CAR_RATE_PER_HOUR).isEqualTo(ticket.getPrice());
	}

	@Test
	public void calculateFareBike() {
		final Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertThat(Fare.BIKE_RATE_PER_HOUR).isEqualTo(ticket.getPrice());
	}

	@Test
	public void calculateFareUnkownType() {
		final Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	public void calculateFareCarWithFutureInTime() {
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
	public void calculateFareBikeWithFutureInTime() {
		final Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() + 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	public void calculateFareCarWithLessThanOneHourParkingTime() {
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
	public void calculateFareBikeWithLessThanOneHourParkingTime() {
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
	public void calculateFareCarWithMoreThanADayParkingTime() {
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
	public void calculateFareBikeWithMoreThanADayParkingTime() {
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
	public void calculateFareTest_forCar_WithLessThan30MinutesParkingTime_shouldBeFree() {

		final Date inTime = new Date();
		// 20 minutes parking time should give 0 * parking fare per hour
		inTime.setTime(System.currentTimeMillis() - 20 * 60 * 1000);
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
	public void calculateFareTest_forBike_WithLessThan30MinutesParkingTime_shouldBeFree() {

		final Date inTime = new Date();
		// 20 minutes parking time should give 0 * parking fare per hour
		inTime.setTime(System.currentTimeMillis() - 20 * 60 * 1000);
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
	public void calculateFareTest_forCar_forRecurringUsers_shouldGetA5PerCentDisount() {

		final Date inTime = new Date();
		// For recurring CAR users 60 minutes parking time should give 1 * parking fare
		// per an hour * 5%-discount
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket);

		assertThat(ticket.getPrice() * 0.95).isEqualTo(Fare.CAR_RATE_PER_HOUR * 0.95);
	}

	// 5% discount For recurring BIKE users
	@Test
	public void calculateFareTest_forBike_forRecurringUsers_shouldGetA5PerCentDisount() {

		final Date inTime = new Date();
		// For recurring BIKE users 60 minutes parking time should give 1 * parking fare
		// per hour * 5%-discount
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		final Date outTime = new Date();
		final ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket);

		assertThat(ticket.getPrice() * 0.95).isEqualTo(Fare.BIKE_RATE_PER_HOUR * 0.95);

	}

}
